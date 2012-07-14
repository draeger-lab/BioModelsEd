/*
 * $Id$
 * $URL$
 * ---------------------------------------------------------------------
 * This file is part of SBML Editor.
 *
 * Copyright (C) 2012 by the University of Tuebingen, Germany.
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation. A copy of the license
 * agreement is provided in the file named "LICENSE.txt" included with
 * this software distribution and also available online as
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 * ---------------------------------------------------------------------
 */
package de.zbit.editor.control;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.ModifierSpeciesReference;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBO;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.layout.BoundingBox;
import org.sbml.jsbml.ext.layout.Dimensions;
import org.sbml.jsbml.ext.layout.ExtendedLayoutModel;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.LayoutConstants;
import org.sbml.jsbml.ext.layout.NamedSBaseGlyph;
import org.sbml.jsbml.ext.layout.Point;
import org.sbml.jsbml.ext.layout.ReactionGlyph;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceRole;
import org.sbml.jsbml.ext.layout.TextGlyph;
import org.sbml.jsbml.ext.render.AbstractRenderPlugin;
import org.sbml.jsbml.ext.render.ColorDefinition;
import org.sbml.jsbml.ext.render.GlobalRenderInformation;
import org.sbml.jsbml.ext.render.RenderConstants;
import org.sbml.jsbml.ext.render.RenderModelPlugin;
import org.sbml.jsbml.util.ValuePair;

import y.base.Node;
import y.view.Graph2D;
import de.zbit.editor.SBMLEditorConstants;
import de.zbit.editor.gui.GUIFactory;
import de.zbit.editor.gui.Resources;
import de.zbit.editor.gui.SBMLEditMode;

/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class CommandController implements PropertyChangeListener {

  /**
   * TODO maybe rethink states an hold SBOTerm instead this would simplify
   * species creation
   */
  private enum States {
    catalysis,
    emptySet,
    inhibition,
    macromolecule,
    mousePressedCatalysis,
    mousePressedInhibition,
    mousePressedReaction,
    normal,
    reaction,
    simpleMolecule,
    unknownMolecule
  }

  private FileManager fileManager;
  private Logger logger = Logger.getLogger(CommandController.class.getName());
  private States state;
  private boolean reversible;
  private SBMLView view;
  
  private boolean copyEnabled = false;
  
  private Node node = null;
    
  private List<Node> nodeList = new ArrayList<Node>();
  private List<NamedSBaseGlyph> nodeCopyList = new ArrayList<NamedSBaseGlyph>();
  
  /**
   * @param editorInstance
   */
  public CommandController(SBMLView editorInstance) {
    this.view = editorInstance;
    this.state = States.normal;
    this.fileManager = new FileManager(this);
    this.logger.setLevel(Level.CONFIG);
  }

  /**
   * @param evt
   * @param sboTerm
   */
  private void createSpecies(PropertyChangeEvent evt, int sboTerm) {
    OpenedSBMLDocument selectedDoc = (OpenedSBMLDocument) this.view
        .getCurrentLayout().getSBMLDocument()
        .getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);

    // generate generic id
    String speciesId = selectedDoc.nextGenericId(SBMLEditorConstants.genericId);
    String nameFromPopup = this.getEditorInstance().nameDialogue(Resources.getString("GENERIC_SPECIES_NAME"));
    if (nameFromPopup == null) {
      return;
    }
    logger.info("popup: " + nameFromPopup);

    @SuppressWarnings("unchecked")
    ValuePair<Double, Double> newMousePosition = (ValuePair<Double, Double>) evt.getNewValue();
    Double x = newMousePosition.getL();
    Double y = newMousePosition.getV();

    // layout and model references
    Layout layout = this.view.getCurrentLayout();
    Model model = layout.getModel();
    final int level = model.getLevel();
    final int version = model.getVersion();

    String glyphId = selectedDoc.nextGenericId(SBMLEditorConstants.genericGlyphIdPrefix);
    String textglyphId = selectedDoc.nextGenericId(SBMLEditorConstants.genericTextGlyphIdPrefix);

    String compartmentId = findCompartmentId(x, y);
    Species s = SBMLFactory.createSpecies(speciesId, nameFromPopup, sboTerm, level, version, compartmentId);
    SpeciesGlyph sGlyph = SBMLFactory.createSpeciesGlyph(glyphId , level, version, speciesId);
    TextGlyph tGlyph = SBMLFactory.createTextGlyph(textglyphId, level, version, sGlyph, speciesId);

    model.addSpecies(s);
    SBMLFactory.addSpeciesGlyphToLayout(layout, sGlyph, x, y, s.getName());
    layout.addTextGlyph(tGlyph);

    selectedDoc.setFileModified(true);
    view.refreshTitle(layout);

    // keep a list of all glyphs which are associated with the species
    List<String> glyphList = new ArrayList<String>();
    glyphList.add(sGlyph.getId());

    Map<String, List<String>> layoutGlyphMap = new HashMap<String, List<String>>();
    layoutGlyphMap.put(layout.getId(), glyphList);

    s.putUserObject(SBMLEditorConstants.GLYPH_LINK_KEY, layoutGlyphMap);

    //      GraphLayoutPanel panel = (GraphLayoutPanel) this.view.getTabManager().getSelectedComponent();
    //      s.addTreeNodeChangeListener(new ControllerViewSynchronizer(panel, this.view.getCurrentLayout()));

    this.state = States.normal;
  }
  
  /**
   * @param x
   * @param y
   * @return
   */
  private String findCompartmentId(Double x, Double y) {
    return view.findCompartmentId(x, y);
  }

  private ReactionGlyph createReaction(Node sourceNode, Node targetNode) {
      OpenedSBMLDocument selectedDoc = (OpenedSBMLDocument) this.view
        .getCurrentLayout().getSBMLDocument().getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);

      Layout layout = this.view.getCurrentLayout();
      ListOf<SpeciesGlyph> list = layout.getListOfSpeciesGlyphs();
      SpeciesGlyph source = null;
      SpeciesGlyph target = null;
     
      for (SpeciesGlyph glyph : list) {
        Node node = (Node) glyph.getUserObject(SBMLEditorConstants.GLYPH_NODE_KEY);
        if (node == sourceNode) {
          source = glyph;
        } else if (node == targetNode) {
          target = glyph;
        }
      }
      if ((source == null) || (target == null)) {
        return null;
      }
      
      Model model = layout.getModel();
    
      Reaction reaction = SBMLFactory.createReaction(selectedDoc, model.getSpecies(source.getSpecies()),
        model.getSpecies(target.getSpecies()), reversible, model.getLevel(), model.getVersion());
      ReactionGlyph reactionGlyph = SBMLFactory.createReactionGlyph(selectedDoc, reaction, source, target, model.getLevel(), model.getVersion());
      reaction.setName(reaction.getId());
      
      model.addReaction(reaction);
      layout.add(reactionGlyph);
      
      return reactionGlyph;
    }
  
  private void createModifier(Node sourceNode, Node targetNode) {
    OpenedSBMLDocument selectedDoc = (OpenedSBMLDocument) this.view
    .getCurrentLayout().getSBMLDocument().getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);

    Layout layout = this.view.getCurrentLayout();
    ListOf<SpeciesGlyph> speciesList = layout.getListOfSpeciesGlyphs();
    ListOf<ReactionGlyph> reactionList = layout.getListOfReactionGlyphs();
    SpeciesGlyph source = null;
    ReactionGlyph target = null;
  
    for (SpeciesGlyph glyph : speciesList) {
      Node node = (Node) glyph.getUserObject(SBMLEditorConstants.GLYPH_NODE_KEY);
      if (sourceNode == node) {
        source = glyph;
      }
    }
    for (ReactionGlyph glyph : reactionList) {
      Node node = (Node) glyph.getUserObject(SBMLEditorConstants.GLYPH_NODE_KEY);
      if (targetNode == node) {
        target = glyph;
      }
    }
    if ((source == null) || (target == null)) {
      return;
    }
    
    Model model = layout.getModel();
    Reaction targetReaction = (Reaction) target.getReactionInstance();
    
    //Creation of a modifier
    ModifierSpeciesReference modifier = new ModifierSpeciesReference();
    modifier.setId(selectedDoc.nextGenericId("mod"));
    modifier.setLevel(model.getLevel());
    modifier.setVersion(model.getVersion());
    modifier.setSBOTerm(source.getSpeciesInstance().getSBOTerm());
    modifier.setSpecies(source.getSpecies());
    modifier.setName(modifier.getId());
    targetReaction.addModifier(modifier);
    
    //Creation of a modifier glyph
    SpeciesReferenceGlyph modifierGlyph = new SpeciesReferenceGlyph();
    if (this.state == States.catalysis) {
      modifierGlyph.setRole(SpeciesReferenceRole.MODIFIER);
    } else if (this.state == States.inhibition) {
      modifierGlyph.setRole(SpeciesReferenceRole.INHIBITOR);
    }
    modifierGlyph.setId(selectedDoc.nextGenericId("modGlyph"));
    modifierGlyph.setLevel(model.getLevel());
    modifierGlyph.setVersion(model.getVersion());
    modifierGlyph.setSBOTerm(source.getSpeciesInstance().getSBOTerm());
    modifierGlyph.setSpeciesGlyph(source.getId());
    modifierGlyph.setName(modifierGlyph.getId());
    target.addSpeciesReferenceGlyph(modifierGlyph);
    
  }

  private void createEmptySet(PropertyChangeEvent evt) {
    createSpecies(evt, SBO.getEmptySet());
  }

  private void createMacromolecule(PropertyChangeEvent evt) {
    createSpecies(evt, SBO.getMacromolecule());

  }

  private void createSimpleMolecule(PropertyChangeEvent evt) {
    createSpecies(evt, SBO.getSimpleMolecule());

  }

  private void createUnknownMolecule(PropertyChangeEvent evt) {
    createSpecies(evt, SBO.getUnknownMolecule());
  }

  /**
   * opens empty sbml document
   * @param name of new file
   * @return true if successful
   */
  public boolean fileNew() {
    String name = view.askUserFileNew();
    if (name == null) {
      return false;
    }
    if (name.isEmpty()) {
      name = Resources.getString(SBMLEditorConstants.genericFileName);
    }

    /*
     * first, create a new SBMLDocument
     */
    SBMLDocument sbmlDocument = new SBMLDocument(
        SBMLView.DEFAULT_LEVEL_VERSION.getL(),
        SBMLView.DEFAULT_LEVEL_VERSION.getV());
    Model model = sbmlDocument.createModel(SBMLEditorConstants.modelDefaultName);
    model.setName(name);
    model.createCompartment(SBMLEditorConstants.compartmentDefaultName);

    /*
     * embed the new SBMLDocument in an OpenedSBMLDocument and tell the
     * fileManager about it
     */
    OpenedSBMLDocument doc = new OpenedSBMLDocument(sbmlDocument);
    doc.setFileModified(true);
    if(!this.fileManager.addDocument(doc)) {
      return false;
    }

    /*
     * create a new default layout and tell the view to display it
     */
    Layout layout = doc.createDefaultLayout();
    if (!this.view.addLayout(layout)) {
      return false;
    }
    
    return true;
  }

  /**
   * quits program
   */
  public void fileQuit() {
    if (this.fileManager.anyFileIsModified()) {
      int returnVal = GUIFactory.createQuestionClose(this.view.getFrame());
      if (returnVal == JOptionPane.YES_OPTION && this.view.getTabManager().closeAllTabs()) {
        System.exit(0);
      }
    } else {
      System.exit(0);
    }
  }

  /**
   * gets currently selected doc from view and forwards it to filemanager for saving
   * @return true if successful 
   */
  public boolean fileSave() {
    OpenedSBMLDocument selectedDoc = (OpenedSBMLDocument) this.view
        .getCurrentLayout().getSBMLDocument()
        .getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);
    return fileManager.fileSave(selectedDoc);
  }

  /**
   * gets currently selected doc from view and forwards it to filemanager for saving
   * @return true if successful 
   */
  public boolean fileSaveAs() {
    OpenedSBMLDocument selectedDoc = (OpenedSBMLDocument) this.view
        .getCurrentLayout().getSBMLDocument()
        .getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);
    return fileManager.fileSaveAs(selectedDoc);
  }
  
  /**
   * forwards fileOpen request to file manager
   * @return true if successful
   * @throws FileNotFoundException 
   */
  public boolean fileOpen() throws FileNotFoundException {
      return fileManager.fileOpen();
  }
  
  /**
   * forwards fileClose request to file manager
   * @return true if successful
   */
  public boolean fileClose() {
    OpenedSBMLDocument doc = (OpenedSBMLDocument) this.view
        .getCurrentLayout().getSBMLDocument()
        .getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);
    
    if (askUserSave(doc)) {
      return fileManager.fileClose(doc);
    }
    else {
      return false;
    }    
  }
  
  /**
   * forwards OpenDialog request to view
   * @return file chosen by user
   */
  public File askUserOpenDialog() {
    return view.askUserOpenDialog();
  }
  
  /**
   * forwards save dialog request to view
   * @return chosen filepath
   */
  public File askUserSaveDialog() {
    return view.askUserSaveDialog();
  }
  
  
  /**
   * @return the editorInstance
   */
  public SBMLView getEditorInstance() {
    return view;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent
   * )
   */
  @SuppressWarnings("unchecked")
  public void propertyChange(PropertyChangeEvent evt) {
    logger.info(evt.getPropertyName());

    if (evt.getPropertyName().equals(SBMLEditorConstants.openingDone)) {
      OpenedSBMLDocument doc = (OpenedSBMLDocument) evt.getNewValue();
      
      //TODO DefaultLayout isn't always empty.
      /*
       * add first or new default layout to view
       */
      boolean hasLayout = doc.hasLayout();
      Layout layout = doc.getFirstLayoutOrNew();
      if (!hasLayout) {
         int newInformation = this.view.askUserCreateLayoutInformation();
         if (newInformation == 0) {
           doc.createLayoutInformation();
         }
      }
      this.view.addLayout(layout);
      /*
       * notify fileManager about newly opened document
       */
      
      // FIXME Test to save and write Colors
      // this.fileManager.addDocument(doc);
      if (this.fileManager.addDocument(doc)) {
        // TODO createDefaultRenderInformation in OpenedSBMLDocument needed
        AbstractRenderPlugin absRenderPlugin = 
          (AbstractRenderPlugin) doc.getDocument().getModel().getExtension(RenderConstants.namespaceURI);
        
        if (absRenderPlugin == null) {
          GlobalRenderInformation renderInfo = new GlobalRenderInformation(
            "defaultGlobalRenderInformation", 3, 1);
          renderInfo.setListOfColorDefinitions(renderInfo.getListOfColorDefinitions());
          renderInfo.addColorDefinition(new ColorDefinition("RED", new Color(255, 0, 0)));
          renderInfo.addColorDefinition(new ColorDefinition("GREEN", new Color(0, 255, 0)));
          renderInfo.addColorDefinition(new ColorDefinition("BLUE", new Color(0, 0, 255)));
          ExtendedLayoutModel extendedLayoutModel = 
            (ExtendedLayoutModel) doc.getDocument().getModel().getExtension(LayoutConstants.namespaceURI);
          RenderModelPlugin renderPlugin = new RenderModelPlugin(extendedLayoutModel.getListOfLayouts());
          renderPlugin.addGlobalRenderInformation(renderInfo);
          doc.getDocument().getModel().addExtension(RenderConstants.namespaceURI, renderPlugin);
        }
      }
    }
    else if (evt.getPropertyName().equals(SBMLEditorConstants.EditModeMousePressedLeft)) {
      mousePressedLeft(evt);    
    }
    else if (evt.getPropertyName().equals(SBMLEditorConstants.EditModeMouseDraggedLeft)) {
      
    }
    else if (evt.getPropertyName().equals(SBMLEditorConstants.EditModeNodePressedLeft)) {
      nodePressedLeft(evt);      
    }
    else if (evt.getPropertyName().equals(SBMLEditorConstants.EditModeNodeReleasedLeft)) {
      
    }
    else if (evt.getPropertyName().equals(SBMLEditorConstants.EditModeNodePressedRight)) {
      nodePressedRight(evt);
    }
    else if (evt.getPropertyName().equals(SBMLEditorConstants.EditModeMousePressedRight)) {
      mousePressedRight(evt);
    }
    else if (evt.getPropertyName().equals(SBMLEditorConstants.EditModeSelectionChanged)) {
      this.nodeList = (List<Node>) evt.getNewValue();
     
      logger.info("Size of list: " + nodeList.size());
    }
    else if (evt.getPropertyName().equals(SBMLEditorConstants.EditModeUpdateNodes)) {
      updateNodes(evt);
    }
  }


  /**
   * @param evt
   */
  private void updateNodes(PropertyChangeEvent evt) {
    Graph2D graph = (Graph2D) evt.getNewValue();
    for (Node node : this.nodeList) {
      NamedSBaseGlyph glyph =  getGlyphFromNode(node);
      if (glyph == null) {
        logger.info("Couldn't find glyph for node");
      }
      else {
        double x = graph.getX(node);
        double y = graph.getY(node);
        double width = graph.getWidth(node);
        double height = graph.getHeight(node);
        glyph.createBoundingBox(width, height, SBMLEditorConstants.glyphDefaultDepth, x, y, SBMLEditorConstants.glyphDefaultZ);
        logger.info("Updating glyph information: " + 
            "Id: " + glyph.getId() + 
            " X: " + x +
            " Y:" + y +
            " Width: " + width +
            " Height: "+ height);
      }
    }
    
  }
  
  private NamedSBaseGlyph getGlyphFromNode(Node node) {
    
    Layout layout = this.view.getCurrentLayout();
    
    for (SpeciesGlyph glyph : layout.getListOfSpeciesGlyphs()) {
      Node n = (Node) glyph.getUserObject(SBMLEditorConstants.GLYPH_NODE_KEY);
      if (node == n) {
        return glyph;
      }
    }
    
    for (ReactionGlyph glyph : layout.getListOfReactionGlyphs()) {
      Node n = (Node) glyph.getUserObject(SBMLEditorConstants.GLYPH_NODE_KEY);
      if (node == n) {
        return glyph;
      }
    }
    
    return null;
  }
  

  /**
   * @param evt
   */
  private void mousePressedRight(PropertyChangeEvent evt) {
    JPopupMenu popup = GUIFactory.createPastePopupMenu(this, this.copyEnabled);
    SBMLEditMode editmode =  (SBMLEditMode) evt.getSource();
    MouseEvent e = editmode.getLastPressEvent();
    popup.show(e.getComponent(), e.getX(), e.getY());    
  }

  /**
   * @param evt
   */
  private void nodePressedRight(PropertyChangeEvent evt) {    
    JPopupMenu popup;
    Node node = (Node) evt.getNewValue();
    NamedSBaseGlyph glyph = getGlyphFromNode(node);
    if (glyph instanceof SpeciesGlyph) {
      popup = GUIFactory.createSpeciesGlyphPopupMenu(this);
    }
    else {
      popup = GUIFactory.createReactionGlyphPopupMenu(this);
    }
    SBMLEditMode editmode =  (SBMLEditMode) evt.getSource();
    MouseEvent e = editmode.getLastPressEvent();
    popup.show(e.getComponent(), e.getX(), e.getY());
  }
   


  
  /**
   * @param evt
   */
  private void mousePressedLeft(PropertyChangeEvent evt) {
    if (this.state == States.unknownMolecule) {
      createUnknownMolecule(evt);
    }
    else if (this.state == States.simpleMolecule) {
      createSimpleMolecule(evt);
    }
    else if (this.state == States.macromolecule) {
      createMacromolecule(evt);
    }
    else if (this.state == States.emptySet) {
      createEmptySet(evt);
    }    
  }

  /**
   * @param evt
   */
  private void nodePressedLeft(PropertyChangeEvent evt) {
    
    if (this.state == States.reaction) {
      if (this.node == null) {
        this.node = (Node) evt.getNewValue();
        logger.info("Source Node for Reaction set.");
      } else {
        ReactionGlyph rGlyph = createReaction(this.node, (Node) evt.getNewValue());  
        Layout layout = this.view.getCurrentLayout();
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(this.node);
        list.add(evt.getNewValue());
        list.add(rGlyph);
        layout.firePropertyChange("reactionCreated", null, list);
        logger.info("Target Node for Reaction set. Created Reaction");
        this.state = States.normal;
        this.node = null;
      }
    }
    if ((this.state == States.catalysis) || (this.state == States.inhibition)) {
      if (this.node == null) {
        this.node = (Node) evt.getNewValue();
        logger.info("Source Node for " + this.state + " set.");
      } else {
        createModifier(this.node, (Node) evt.getNewValue());
        Layout layout = this.view.getCurrentLayout();
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(this.node);
        list.add(evt.getNewValue());
        if (this.state == States.catalysis) {
          list.add("Catalysis");
        } else if (this.state == States.inhibition) {
          list.add("Inhibition");
        }
        
        layout.firePropertyChange("modifierCreated", null, list);
        logger.info("Target Node for " + this.state + " set.");
        this.state = States.normal;
        this.node = null;
      }
    }
    
  }

  public void stateCatalysis() {
    this.state = States.catalysis;
    logger.info(this.state.toString());
  }

  public void stateEmptySet() {
    this.state = States.emptySet;
    logger.info(this.state.toString());
  }

  public void stateInhibition() {
    this.state = States.inhibition;
    logger.info(this.state.toString());
  }

  public void stateMacromolecule() {
    this.state = States.macromolecule;
    logger.info(this.state.toString());
  }

  public void stateNormal() {
    this.state = States.normal;
    logger.info(this.state.toString());
  }

  public void stateReaction() {
    this.state = States.reaction;
    logger.info(this.state.toString());
  }

  public void stateSimpleMolecule() {
    this.state = States.simpleMolecule;
    logger.info(this.state.toString());
  }

  public void stateUnknownMolecule() {
    this.state = States.unknownMolecule;
    logger.info(this.state.toString());
  }
  
  public void changeReversible() {
    this.reversible = !this.reversible;
  }

  public boolean closeTab(Layout layout) {
    return this.view.closeTab(layout);
  }

  public Component getFrame() {
    return this.view.getFrame();
  }

  public void fileNotFound() {
    view.showError(SBMLEditorConstants.fileNotFound);
  }

  /**
   * @param layout
   */
  public boolean layoutClose(Layout layout) {
    if (view.getTabManager().isAnyOpenFromDocument(layout)) {
      return view.closeTab(layout);
    }
    else {
      return view.fileClose();
    }    
  }

  /**
   * @param currentLayout
   */
  public void layoutDelete(Layout layout) {
    OpenedSBMLDocument doc = (OpenedSBMLDocument) layout.getSBMLDocument()
        .getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);
    if(doc.getListOfLayouts().size() == 1) {
      logger.info("Document doesn't have 2 or more layouts");
    }
    else if (view.getTabManager().isAnyOpenFromDocument(layout)){
      logger.info("Try to delete Layout ID: " + layout.getId() + " Layout Name: " + layout.getName());
      doc.getListOfLayouts().remove(layout);
      view.closeTab(layout);
      //view.updateComboBox(doc.getListOfLayouts());
    }
    else {
        logger.info("No other layout opened");
      //TODO Ask user for Save
    }
  }
  
  /**
   * @param doc
   * @return if iser did not cancel saving progress
   */
  public boolean askUserSave(OpenedSBMLDocument doc) {
    if (doc.isFileModified()) {
      int returnVal = GUIFactory.createQuestionSave(this.view.getFrame(), doc.getAssociatedFilename());
      if (returnVal == JOptionPane.YES_OPTION) {
        logger.info("User chose to save file");
        fileSave();
        return true;
      }
      else if (returnVal == JOptionPane.NO_OPTION) {
        logger.info("User chose to not save file");
        return true;
      }
      else {
        logger.info("User canceled closing");
        return false;
      }
    }
    return true;
  }
    
  public void nodeDelete() {
    Layout layout = this.view.getCurrentLayout();
    OpenedSBMLDocument selectedDoc = (OpenedSBMLDocument) layout.getSBMLDocument()
    .getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);
    
    for (Node node : this.nodeList) {
      NamedSBaseGlyph glyph = getGlyphFromNode(node);
      if (glyph == null) {
        logger.info("Couldn't find glyph for node");
      }
      else if (glyph instanceof SpeciesGlyph) {
        //TODO: Delete all Reactions associated with glyph
        ((SpeciesGlyph) glyph).getSpecies();
        if (this.nodeCopyList.remove(glyph)){
          logger.info("Removed glyph from copylist");
        }
        layout.getListOfSpeciesGlyphs().remove(glyph);
        layout.firePropertyChange("nodeDelete", null, glyph.getUserObject(SBMLEditorConstants.GLYPH_NODE_KEY));
      }
      else if (glyph instanceof ReactionGlyph) {
        this.nodeCopyList.remove(glyph);
        layout.getListOfReactionGlyphs().remove(glyph);
        layout.firePropertyChange("nodeDelete", null, glyph.getUserObject(SBMLEditorConstants.GLYPH_NODE_KEY));
      }
    }
            
    logger.info("nodeDelete in CC");
    selectedDoc.setFileModified(true);
  }
    
  /**
   * 
   */
  public void editDelete() {
    this.nodeDelete();
  }
  
  /**
   * 
   */
  public void nodeCopy() {
    this.copyEnabled = true;
    this.nodeCopyList.clear();
    for(Node n : this.nodeList) {
      NamedSBaseGlyph glyph = getGlyphFromNode(n);
      if (glyph != null) {
        this.nodeCopyList.add(glyph);
      }
    }
    logger.info("Copy list has changed: Size: " + this.nodeCopyList.size());
  }
  
  /**
   * 
   */
  public void editCopy() {
    this.nodeCopy();
  }
  
  /**
   * 
   */
  public void nodeRename() {
    /*if (speciesSelected == true) {
      TextGlyph textGlyph = (TextGlyph) selectedSpeciesGlyph.getUserObject(SBMLEditorConstants.GRAPHOBJECT_TEXTGLYPH_KEY);
      // TODO
      JOptionPane.showInputDialog(Resources.getString("NEW_FILE"), Resources.getString("GENERIC_FILE_NAME"));
    }*/
  }
  
  /**
   * 
   */
  public void nodePaste() {
    logger.info("Pasting...");
    Layout layout = this.view.getCurrentLayout();
    OpenedSBMLDocument selectedDoc = getDocumentFromLayout(layout);
    
    for (NamedSBaseGlyph glyph : this.nodeCopyList) {
      
      if (glyph instanceof SpeciesGlyph) {
        SpeciesGlyph copySpeciesGlyph = (SpeciesGlyph) glyph;
        copySpeciesGlyph(layout, selectedDoc, copySpeciesGlyph);
      }
      else if (glyph instanceof ReactionGlyph) {
        SpeciesGlyph copyReactionGlyph = (SpeciesGlyph) glyph;
        copyReactionGlyph(layout, selectedDoc, copyReactionGlyph);
      }
    }
    selectedDoc.setFileModified(true);
    view.refreshTitle(layout);
  }
  
  /**
   * @param layout
   * @param selectedDoc
   * @param copyReactionGlyph
   */
  private void copyReactionGlyph(Layout layout, OpenedSBMLDocument selectedDoc,
      SpeciesGlyph copyReactionGlyph) {
    logger.info("Reactionglyph");    
  }

  /**
   * @param selectedDoc
   * @param layout
   * @param copySpeciesGlyph
   */
  private void copySpeciesGlyph(Layout layout, OpenedSBMLDocument selectedDoc, SpeciesGlyph copySpeciesGlyph) {
        
    String speciesId = copySpeciesGlyph.getSpecies();
    Species species = copySpeciesGlyph.getModel().getSpecies(speciesId);
    
    TextGlyph originalTextGlyph = (TextGlyph) copySpeciesGlyph.getUserObject(SBMLEditorConstants.GRAPHOBJECT_TEXTGLYPH_KEY);
    String glyphId = selectedDoc.nextGenericId(SBMLEditorConstants.genericGlyphIdPrefix);
    String textGlyphId = selectedDoc.nextGenericId(SBMLEditorConstants.genericTextGlyphIdPrefix);
    
    double x = copySpeciesGlyph.getBoundingBox().getPosition().getX();
    double y = copySpeciesGlyph.getBoundingBox().getPosition().getY();
    double width = copySpeciesGlyph.getBoundingBox().getDimensions().getWidth();
    double height = copySpeciesGlyph.getBoundingBox().getDimensions().getHeight();
    
    if(layout.getModel() == copySpeciesGlyph.getModel()) {
      logger.info("nodePaste: Same Model");  
      
      SpeciesGlyph speciesGlyph = SBMLFactory.createSpeciesGlyph(glyphId, SBMLView.DEFAULT_LEVEL_VERSION.getL(), SBMLView.DEFAULT_LEVEL_VERSION.getV(), x, y, width, height, speciesId);
      layout.add(speciesGlyph);
      
      logger.info("New Glyph: " + 
          "Id: " + speciesGlyph.getId() + 
          " X: " + x +
          " Y:" + y +
          " Width: " + width +
          " Height: "+ height);
      
      if (originalTextGlyph != null) {
        TextGlyph newTextGlyph = SBMLFactory.createTextGlyph(textGlyphId,
            SBMLView.DEFAULT_LEVEL_VERSION.getL(),
            SBMLView.DEFAULT_LEVEL_VERSION.getV(),
            speciesGlyph,
            originalTextGlyph.getNamedSBase());
        layout.addTextGlyph(newTextGlyph);
      }
    }
    else {
      logger.info("nodePaste: Different Model");
      String speciesIdNew = selectedDoc.nextGenericId(SBMLEditorConstants.genericId);
      Species s = SBMLFactory.createSpecies(speciesIdNew,
          copySpeciesGlyph.getName(),
          species.getSBOTerm(),
          SBMLView.DEFAULT_LEVEL_VERSION.getL(),
          SBMLView.DEFAULT_LEVEL_VERSION.getV(),
          selectedDoc.getDefaultCompartment());
      layout.getModel().addSpecies(s);
      SpeciesGlyph speciesGlyph = SBMLFactory.createSpeciesGlyph(glyphId, SBMLView.DEFAULT_LEVEL_VERSION.getL(), SBMLView.DEFAULT_LEVEL_VERSION.getV(), x, y, width, height, speciesIdNew);
      layout.add(speciesGlyph);
      
      logger.info("New Glyph: " + 
          "Id: " + speciesGlyph.getId() + 
          " X: " + x +
          " Y:" + y +
          " Width: " + width +
          " Height: "+ height);
      
      if (originalTextGlyph != null) {
        TextGlyph newTextGlyph = SBMLFactory.createTextGlyph(textGlyphId,
            SBMLView.DEFAULT_LEVEL_VERSION.getL(),
            SBMLView.DEFAULT_LEVEL_VERSION.getV(),
            speciesGlyph,
            originalTextGlyph.getText());
        layout.addTextGlyph(newTextGlyph);
      }
    }
    
  }

  /**
   * 
   */
  public void editPaste() {
    this.nodePaste();
  }
  
  private OpenedSBMLDocument getDocumentFromLayout(Layout layout) {
    return (OpenedSBMLDocument) layout.getSBMLDocument()
        .getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);
  }
}