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
import org.sbml.jsbml.ext.layout.ExtendedLayoutModel;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.LayoutConstants;
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
    catalysis, emptySet, inhibition, macromolecule, mousePressedCatalysis, mousePressedInhibition, mousePressedReaction, normal, reaction, simpleMolecule, unknownMolecule,
  }
//  private enum States {
//    /*
//     * normal mode, view graph
//     */
//    MODE_VIEW,
//    /*
//     * insert a SBOTerm object
//     */
//    MODE_SBO,
//    /*
//     * insert a render object
//     */
//    MODE_RENDER
//  }
  private FileManager fileManager;
  private Logger logger = Logger.getLogger(CommandController.class.getName());
  private States state;
  private SBMLView view;
  
  private boolean nodeSelected = false;
  private boolean reactionSelected = false;
  private boolean nodeCopy = false;
  
  private SpeciesGlyph selectedGlyph;
  private SpeciesGlyph copyGlyph; 
  private ReactionGlyph selectedReactionGlyph;
  private Node node = null;
  
  private ValuePair<Double, Double> pos;
  
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

    Species s = SBMLFactory.createSpecies(speciesId, nameFromPopup, sboTerm, level, version);
    SpeciesGlyph sGlyph = SBMLFactory.createSpeciesGlyph(glyphId , level, version, speciesId);
    TextGlyph tGlyph = SBMLFactory.createTextGlyph(textglyphId, level, version, sGlyph, nameFromPopup);

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
  
  private ReactionGlyph createReaction(Node sourceNode, Node targetNode) {
      OpenedSBMLDocument selectedDoc = (OpenedSBMLDocument) this.view
        .getCurrentLayout().getSBMLDocument().getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);

      //FIXME Name needed?
      //String nameFromPopup = this.getEditorInstance().nameDialogue(genericId);
      //logger.info("popup: " + nameFromPopup);
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
      //TODO Let user decide, if the Reaction is reversible 
      boolean reversible = false;
    
      Reaction reaction = SBMLFactory.createReaction(selectedDoc, model.getSpecies(source.getSpecies()),
        model.getSpecies(target.getSpecies()), reversible, model.getLevel(), model.getVersion());
      ReactionGlyph reactionGlyph = SBMLFactory.createReactionGlyph(selectedDoc, reaction, source, target, model.getLevel(), model.getVersion());
      
      model.addReaction(reaction);
      layout.add(reactionGlyph);
      
      return reactionGlyph;
    }
  
  private void createModifier(Node sourceNode, Node targetNode) {
    OpenedSBMLDocument selectedDoc = (OpenedSBMLDocument) this.view
    .getCurrentLayout().getSBMLDocument().getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);

    //FIXME Name needed?
    //String nameFromPopup = this.getEditorInstance().nameDialogue(genericId);
    //logger.info("popup: " + nameFromPopup);
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
    if ((source == null) && (target == null)) {
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
  public void propertyChange(PropertyChangeEvent evt) {
    logger.info(evt.getPropertyName());

    if (evt.getPropertyName().equals(SBMLEditorConstants.openingDone)) {
      OpenedSBMLDocument doc = (OpenedSBMLDocument) evt.getNewValue();
      /*
       * add first or new default layout to view
       */
      this.view.addLayout(doc.getFirstLayoutOrNew());
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
    else if (evt.getPropertyName().equals(
        SBMLEditorConstants.EditModeMousePressedLeft)) {
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
    else if (evt.getPropertyName().equals(SBMLEditorConstants.EditModeMouseDraggedLeft)) {
      @SuppressWarnings("unchecked")
      ValuePair<Double, Double> pos = (ValuePair<Double, Double>) evt.getNewValue();
      logger.info("New glyph information: " + "Node X: " + pos.getL() + " Y: " + pos.getV());
    }
    else if (evt.getPropertyName().equals(SBMLEditorConstants.EditModeNodePressedLeft)) {
      if (this.state == States.normal) {
        
        
        this.selectedGlyph = getSpeciesGlyphFromNode((Node) evt.getNewValue());
        if (this.selectedGlyph != null) {
          this.nodeSelected = true;
          this.reactionSelected = false;
          logger.info("Glyph selected ID: " + this.selectedGlyph.getId() + " Name: " +this.selectedGlyph.getName() + 
            " belongs to Species: " + this.selectedGlyph.getSpecies());
        } else {
          this.selectedReactionGlyph = getReactionGlyphFromNode((Node) evt.getNewValue());
          logger.info("Glyph selected ID: " + this.selectedReactionGlyph.getId());
          this.reactionSelected = true;
          this.nodeSelected = false;
        }
      }
      if (this.state == States.reaction) {
        if (this.node == null) {
          this.node = (Node) evt.getNewValue();
          logger.info("Source Node for Reaction set.");
        } else {
          ReactionGlyph rGlyph = createReaction(this.node, (Node) evt.getNewValue());  
          Layout layout = this.view.getCurrentLayout();
          //FIXME use something like ValuePair, but VP needs Comparables
          //ValuePair<Node, Node> nodes = new ValuePair<Node, Node>(this.node, (Node) evt.getNewValue());
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
    else if (evt.getPropertyName().equals(SBMLEditorConstants.EditModeNodeReleasedLeft)) {
      if ((this.state == States.normal) && (this.nodeSelected)) {     
          @SuppressWarnings("unchecked")
          ValuePair<Double, Double> pos = (ValuePair<Double, Double>) evt.getNewValue();
          selectedGlyph.getBoundingBox().setPosition(new Point(pos.getL(), pos.getV(), SBMLEditorConstants.glyphDefaultZ, 3, 1));
          logger.info("New glyph information: " + "Node X: " + pos.getL() + " Y: " + pos.getV());  
      } else if ((this.state == States.normal) && (this.reactionSelected)) {
         ValuePair<Double, Double> pos = (ValuePair<Double, Double>) evt.getNewValue();
         this.selectedReactionGlyph.getBoundingBox().getPosition().setX(pos.getL());
         this.selectedReactionGlyph.getBoundingBox().getPosition().setY(pos.getV());
         logger.info("New glyph information: " + "Node X: " + pos.getL() + " Y: " + pos.getV());
      }
    }
    else if (evt.getPropertyName().equals(SBMLEditorConstants.EditModeNodePressedRight)) {
      
      this.nodeSelected = true;
      this.selectedGlyph = getSpeciesGlyphFromNode((Node) evt.getNewValue());
      JPopupMenu popup = GUIFactory.createNodePopupMenu(this);
      SBMLEditMode editmode =  (SBMLEditMode) evt.getSource();
      MouseEvent e = editmode.getLastPressEvent();
      popup.show(e.getComponent(), e.getX(), e.getY());
      logger.info("Glyph selected ID: " + this.selectedGlyph.getId() + " Name: " +this.selectedGlyph.getName() + 
        "belongs to Species: " + this.selectedGlyph.getSpecies());
    }
    else if (evt.getPropertyName().equals(SBMLEditorConstants.EditModeMousePressedRight)) {
      JPopupMenu popup = GUIFactory.createPastePopupMenu(this, this.nodeCopy);
      SBMLEditMode editmode =  (SBMLEditMode) evt.getSource();
      MouseEvent e = editmode.getLastPressEvent();
      popup.show(e.getComponent(), e.getX(), e.getY());
           
      this.pos = new ValuePair<Double, Double>(Double.parseDouble(new Integer(e.getX()).toString()) , Double.parseDouble(new Integer(e.getY()).toString())); 
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
  
  /**
   * @param selectedNode
   * @return
   */
  public SpeciesGlyph getSpeciesGlyphFromNode(Node selectedNode) {
    Layout layout = this.view.getCurrentLayout();
    ListOf<SpeciesGlyph> list = layout.getListOfSpeciesGlyphs();
    for (SpeciesGlyph glyph : list) {
      Node node = (Node) glyph.getUserObject(SBMLEditorConstants.GLYPH_NODE_KEY);
      if (node == selectedNode) {
        return glyph;
      }
    }
    return null;
  }
  
  /**
   * 
   */
  public ReactionGlyph getReactionGlyphFromNode(Node selectedNode) {
    Layout layout = this.view.getCurrentLayout();
    ListOf<ReactionGlyph> list = layout.getListOfReactionGlyphs();
    for (ReactionGlyph glyph : list) {
      Node node = (Node) glyph.getUserObject(SBMLEditorConstants.GLYPH_NODE_KEY);
      if (node == selectedNode) {
        return glyph;
      } 
    }
    return null;
  }
  
  public void nodeDelete() {
    Layout layout = this.view.getCurrentLayout();
    OpenedSBMLDocument selectedDoc = (OpenedSBMLDocument) layout.getSBMLDocument()
    .getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);
    
    layout.getListOfSpeciesGlyphs().remove(selectedGlyph);
    layout.firePropertyChange("nodeDelete", null, this.selectedGlyph.getUserObject(SBMLEditorConstants.GLYPH_NODE_KEY));
    logger.info("nodeDelete in CC");
    selectedDoc.setFileModified(true);
    this.nodeSelected = false;
  }
  
  /**
   * 
   */
  public void editDelete() {
    if(this.nodeSelected) {
      this.nodeDelete();
    }
  }
  
  /**
   * 
   */
  public void nodeCopy() {
    this.nodeCopy = true;
    this.copyGlyph = this.selectedGlyph;
  }
  
  /**
   * 
   */
  public void editCopy() {
    if(this.nodeSelected) {
      this.nodeCopy();
    }
  }
  
  /**
   * 
   */
  public void nodeRename() {
    if (nodeSelected == true) {
      TextGlyph textGlyph = (TextGlyph) selectedGlyph.getUserObject(SBMLEditorConstants.GRAPHOBJECT_TEXTGLYPH_KEY);
      // TODO
      JOptionPane.showInputDialog(Resources.getString("NEW_FILE"), Resources.getString("GENERIC_FILE_NAME"));
    }
  }
  
  /**
   * 
   */
  public void nodePaste() {
    Layout layout = this.view.getCurrentLayout();
    OpenedSBMLDocument selectedDoc = (OpenedSBMLDocument) layout.getSBMLDocument()
        .getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);
        
    Double x = this.pos.getL();
    Double y = this.pos.getV();
    String speciesId = this.copyGlyph.getSpecies();
    Species species = this.copyGlyph.getModel().getSpecies(speciesId);  
    TextGlyph originalTextGlyph = (TextGlyph) this.copyGlyph.getUserObject(SBMLEditorConstants.GRAPHOBJECT_TEXTGLYPH_KEY);
    String glyphId = selectedDoc.nextGenericId(SBMLEditorConstants.genericGlyphIdPrefix);
    String textGlyphId = selectedDoc.nextGenericId(SBMLEditorConstants.genericTextGlyphIdPrefix);
    
    if(layout.getModel() == this.copyGlyph.getModel()) {
      logger.info("nodePaste: Same Model");
      
      SpeciesGlyph sGlyph = SBMLFactory.createSpeciesGlyph(glyphId, SBMLView.DEFAULT_LEVEL_VERSION.getL(),
        SBMLView.DEFAULT_LEVEL_VERSION.getV(), speciesId);
      SBMLFactory.addSpeciesGlyphToLayout(layout, sGlyph, x, y, this.copyGlyph.getName());

      if (originalTextGlyph != null) {
        TextGlyph newTextGlyph = SBMLFactory.createTextGlyph(textGlyphId,
            SBMLView.DEFAULT_LEVEL_VERSION.getL(),
            SBMLView.DEFAULT_LEVEL_VERSION.getV(),
            sGlyph,
            originalTextGlyph.getText());
        layout.addTextGlyph(newTextGlyph);
      }
    }
    else {
      logger.info("nodePaste: Different Model");
      String speciesIdNew = selectedDoc.nextGenericId(SBMLEditorConstants.genericId);
      Species s = SBMLFactory.createSpecies(speciesIdNew, this.copyGlyph.getName(), species.getSBOTerm(), SBMLView.DEFAULT_LEVEL_VERSION.getL(), SBMLView.DEFAULT_LEVEL_VERSION.getV());
      layout.getModel().addSpecies(s);
      SpeciesGlyph sGlyph = SBMLFactory.createSpeciesGlyph(glyphId, SBMLView.DEFAULT_LEVEL_VERSION.getL(),
        SBMLView.DEFAULT_LEVEL_VERSION.getV(), speciesIdNew);
      SBMLFactory.addSpeciesGlyphToLayout(layout, sGlyph, x, y, this.copyGlyph.getName());
      
      if (originalTextGlyph != null) {
        TextGlyph newTextGlyph = SBMLFactory.createTextGlyph(textGlyphId,
            SBMLView.DEFAULT_LEVEL_VERSION.getL(),
            SBMLView.DEFAULT_LEVEL_VERSION.getV(),
            sGlyph,
            originalTextGlyph.getText());
        layout.addTextGlyph(newTextGlyph);
      }
    }
    selectedDoc.setFileModified(true);
    view.refreshTitle(layout);
  }
  
  /**
   * 
   */
  public void editPaste() {
    if(this.nodeSelected) {
      Point point = this.copyGlyph.getBoundingBox().getPosition();
      this.pos = new ValuePair<Double, Double>(point.getX() + 50, point.getY() + 50);
      this.nodePaste();
    }
  }
}