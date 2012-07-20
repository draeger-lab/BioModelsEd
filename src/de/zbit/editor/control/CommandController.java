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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.NamedSBaseGlyph;
import org.sbml.jsbml.ext.layout.ReactionGlyph;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceRole;
import org.sbml.jsbml.ext.layout.TextGlyph;
import org.sbml.jsbml.util.ValuePair;

import y.base.Node;
import y.io.GIFIOHandler;
import y.io.ImageOutputHandler;
import y.io.JPGIOHandler;
import y.view.Graph2D;
import y.view.Graph2DView;
import de.zbit.editor.SBMLEditorConstants;
import de.zbit.editor.gui.GUIFactory;
import de.zbit.editor.gui.Resources;
import de.zbit.editor.gui.SBMLEditMode;

/**
 * Controlls the execution of all commands and conveys them to the View and the Document.
 * 
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Eugen Netz
 * @author Jan Rudolph
 * @version $Rev$
 */
public class CommandController implements PropertyChangeListener {

  /**
   * States, which determine, which action will be taken on the next mouse click.
   * 
   */
  private enum States {
    catalysis,
    emptySet,
    inhibition,
    macromolecule,
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
   * Constructor
   * @param editorInstance must implement SBMLView
   */
  public CommandController(SBMLView editorInstance) {
    this.view = editorInstance;
    this.state = States.normal;
    this.fileManager = new FileManager(this);
    this.logger.setLevel(Level.CONFIG);
  }

  /**
   * Creates a Species and adds it to the model.
   * Creates a SpeciesGlyph and adds it to the layout. Its Position is determined by the ValuePair in NewValue of evt.
   * @param evt
   * @param sboTerm the SBO-Term of the Species
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
   * Finds the innermost Compartment, in which the Position determined by the parameters lies.
   * @param x
   * @param y
   * @return The Id of the Compartment
   */
  private String findCompartmentId(Double x, Double y) {
    return view.findCompartmentId(x, y);
  }

  /**
   * Creates a ReactionGlyph with the SpeciesGlyph corresponding to the sourceNode as the Substrate
   * and the SpeciesGlyph corresponding to the targetNode as the Product and adds it to the layout.
   * @param sourceNode
   * @param targetNode
   * @return the ReactionGlyph
   */
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
  
  /**
   * Creates a ModifierSpeciesReference and adds it to the model.
   * Creates a SpeciesReferenceGlyph and adds it to the Layout.
   * @param sourceNode corresponding to the SpeciesGlyph
   * @param targetNode corresponding to the ReactionGlyph
   */
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
    modifier.setId(selectedDoc.nextGenericId(SBMLEditorConstants.genericModifierReferenceIdPrefix));
    modifier.setLevel(model.getLevel());
    modifier.setVersion(model.getVersion());
    int sbo = 0;
    if (this.state == States.catalysis) {
      sbo = SBO.getCatalyst();
    } else {
      sbo = SBO.getInhibitor();
    }
    modifier.setSBOTerm(sbo);
    modifier.setSpecies(source.getSpecies());
    modifier.setName(modifier.getId());
    targetReaction.addModifier(modifier);
    
    //Creation of a modifier glyph
    //SpeciesReferenceGlyph modifierGlyph = new SpeciesReferenceGlyph();
    SpeciesReferenceGlyph modifierGlyph = SBMLFactory.createSpeciesReferenceGlyph(selectedDoc, source, target, sbo);
    /*if (this.state == States.catalysis) {
      modifierGlyph.setRole(SpeciesReferenceRole.MODIFIER);
    } else if (this.state == States.inhibition) {
      modifierGlyph.setRole(SpeciesReferenceRole.INHIBITOR);
    }
    modifierGlyph.setId(selectedDoc.nextGenericId(SBMLEditorConstants.genericModifierReferenceGlyphIdPrefix));
    modifierGlyph.setLevel(model.getLevel());
    modifierGlyph.setVersion(model.getVersion());
    modifierGlyph.setSBOTerm(source.getSpeciesInstance().getSBOTerm());
    modifierGlyph.setSpeciesGlyph(source.getId());
    modifierGlyph.setName(modifierGlyph.getId()); */
    target.addSpeciesReferenceGlyph(modifierGlyph);
    
  }

  /**
   * Calls the {@link #createSpecies} method with the SBO-term for an EmptySet.
   * @param evt
   */
  private void createEmptySet(PropertyChangeEvent evt) {
    createSpecies(evt, SBO.getEmptySet());
  }

  /**
   * Calls the {@link #createSpecies} method with the SBO-term for a Macromolecule.
   * @param evt
   */
  private void createMacromolecule(PropertyChangeEvent evt) {
    createSpecies(evt, SBO.getMacromolecule());

  }

  /**
   * Calls the {@link #createSpecies} method with the SBO-term for a SimpleMolecule.
   * @param evt
   */
  private void createSimpleMolecule(PropertyChangeEvent evt) {
    createSpecies(evt, SBO.getSimpleMolecule());

  }

  /**
   * Calls the {@link #createSpecies} method with the SBO-term for an UnknownMolecule.
   * @param evt
   */
  private void createUnknownMolecule(PropertyChangeEvent evt) {
    createSpecies(evt, SBO.getUnknownMolecule());
  }

  /**
   * Opens an empty {@link #SBMLDocument}.
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
    if (!this.view.addLayout(layout, false)) {
      return false;
    }
    
    return true;
  }

  /**
   * Quits the program.
   * @return true, if succesful.
   */
  public boolean fileQuit() {
    if (this.fileManager.anyFileIsModified()) {
      int returnVal = GUIFactory.createQuestionClose(this.view.getFrame());
      if (returnVal == JOptionPane.YES_OPTION && this.view.getTabManager().closeAllTabs()) {
        System.exit(0);
      }
    } else {
      System.exit(0);
    }
    return true;
  }

  /**
   * Gets currently selected doc from view and forwards it to filemanager for saving.
   * @return true if successful. 
   */
  public boolean fileSave() {
    OpenedSBMLDocument selectedDoc = (OpenedSBMLDocument) this.view
        .getCurrentLayout().getSBMLDocument()
        .getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);
    return fileManager.fileSave(selectedDoc);
  }

  /**
   * Gets currently selected doc from view and forwards it to filemanager for saving.
   * @return true if successful. 
   */
  public boolean fileSaveAs() {
    OpenedSBMLDocument selectedDoc = (OpenedSBMLDocument) this.view
        .getCurrentLayout().getSBMLDocument()
        .getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);
    return fileManager.fileSaveAs(selectedDoc);
  }
  
  /**
   * Forwards fileOpen request to file manager.
   * @return true if successful.
   * @throws FileNotFoundException 
   */
  public boolean fileOpen() throws FileNotFoundException {
      return fileManager.fileOpen();
  }
  
  /**
   * Forwards fileClose request to file manager.
   * @return true if successful.
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
   * Forwards OpenDialog request to view.
   * @return file chosen by user
   */
  public File askUserOpenDialog() {
    return view.askUserOpenDialog();
  }
  
  /**
   * Forwards save dialog request to view.
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

  /**
   * Is called by a fired PropertyChange and determines the followed action.
   */
  @SuppressWarnings("unchecked")
  public void propertyChange(PropertyChangeEvent evt) {
    logger.info(evt.getPropertyName());

    if (evt.getPropertyName().equals(SBMLEditorConstants.openingDone)) {
      OpenedSBMLDocument doc = (OpenedSBMLDocument) evt.getNewValue();
      
      /*
       * add first or new default layout to view
       */
      boolean hasLayout = doc.hasLayout();
      boolean autoLayout = false;
      Layout layout = doc.getFirstLayoutOrNew();
      logger.info("Document has layout information: " + hasLayout);
      if (!hasLayout) {
         int newInformation = this.view.askUserCreateLayoutInformation();
         if (newInformation == JOptionPane.YES_OPTION) {
           doc.createLayoutInformation();
           autoLayout = true;
         }
      }
      this.view.addLayout(layout, autoLayout);
      /*
       * notify fileManager about newly opened document
       */
      this.fileManager.addDocument(doc);
      
      //Test to save and write Colors
      /*
        if (this.fileManager.addDocument(doc)) {
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
      }*/
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
   * Calls {@link #updateGlyphFromNode} for every node in {@link #nodeList}.
   * @param evt
   */
  private void updateNodes(PropertyChangeEvent evt) {
    Graph2D graph = (Graph2D) evt.getNewValue();
   
    for (Node node : this.nodeList) {
      updateGlyphFromNode(node, graph);
    }
  }
  
  /**
   * Updates the BoundingBox of the Glyph corresponding to the node with the size and position from the node.
   * @param node
   * @param graph
   * @return true, if the Glyph was updated. false, if the Glyph wasn't found.
   */
  public boolean updateGlyphFromNode(Node node, Graph2D graph) {
    NamedSBaseGlyph glyph =  getGlyphFromNode(node);
    if (glyph == null) {
      logger.info("Couldn't find glyph for node");
      return false;
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
      this.layoutModified(this.view.getCurrentLayout());
      return true;
    }
  }
  
  /**
   * Finds the SpeciesGlyph or ReactionGlyph corresponding to the node.
   * @param node
   * @return the Glyph or null, if it wasn't found within the SpeciesGlyph or ReactionGlyph lists.
   */
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
   * Creates and shows PopupMenu on right mouse click on empty space.
   * @param evt
   */
  private void mousePressedRight(PropertyChangeEvent evt) {
    JPopupMenu popup = GUIFactory.createPastePopupMenu(this, this.copyEnabled);
    SBMLEditMode editmode =  (SBMLEditMode) evt.getSource();
    MouseEvent e = editmode.getLastPressEvent();
    popup.show(e.getComponent(), e.getX(), e.getY());    
  }

  /**
   * Creates and shows PopupMenu on right mouse click on a node.
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
   * Calls a method, that creates a Species, determined by {@link #state}.
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
   * Creates either a Reaction or a Modifier, if the right conditions are met.
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
          list.add(SBO.getCatalyst());
        } else if (this.state == States.inhibition) {
          list.add(SBO.getInhibitor());
        }
        
        layout.firePropertyChange("modifierCreated", null, list);
        logger.info("Target Node for " + this.state + " set.");
        this.state = States.normal;
        this.node = null;
      }
    }
    
  }

  /**
   * Changes the {@link #state} to catalysis.
   */
  public void stateCatalysis() {
    this.state = States.catalysis;
    logger.info(this.state.toString());
  }

  /**
   *  Changes the {@link #state} to emptySet.
   */
  public void stateEmptySet() {
    this.state = States.emptySet;
    logger.info(this.state.toString());
  }

  /**
   *  Changes the {@link #state} to inhibition.
   */
  public void stateInhibition() {
    this.state = States.inhibition;
    logger.info(this.state.toString());
  }

  /**
   *  Changes the {@link #state} to macromolecule.
   */
  public void stateMacromolecule() {
    this.state = States.macromolecule;
    logger.info(this.state.toString());
  }

  /**
   *  Changes the {@link #state} to normal.
   */
  public void stateNormal() {
    this.state = States.normal;
    logger.info(this.state.toString());
  }

  /**
   *  Changes the {@link #state} to reaction.
   */
  public void stateReaction() {
    this.state = States.reaction;
    logger.info(this.state.toString());
  }

  /**
   *  Changes the {@link #state} to simpleMolecule
   */
  public void stateSimpleMolecule() {
    this.state = States.simpleMolecule;
    logger.info(this.state.toString());
  }

  /**
   *  Changes the {@link #state} to unknownMolecule
   */
  public void stateUnknownMolecule() {
    this.state = States.unknownMolecule;
    logger.info(this.state.toString());
  }
  
  /**
   * Toggles {@link #reversible}, which determines, whether a created Reaction is reversible.
   */
  public void changeReversible() {
    this.reversible = !this.reversible;
  }

  /**
   * Closes the tab, that shows the given layout.
   * @param layout
   * @return true, if a tab was found and closed. false otherwise.
   */
  public boolean closeTab(Layout layout) {
    return this.view.closeTab(layout);
  }

  /**
   * @return the Frame
   */
  public Component getFrame() {
    return this.view.getFrame();
  }

  /**
   * Shows a File-Not-Found error.
   */
  public void fileNotFound() {
    view.showError(SBMLEditorConstants.fileNotFound);
  }

  /**
   * Closes the tab, that shows the given layout.
   * If no other layout from the same document is shown in another tab, the document is closed as well.
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
   * Removes the given layout from the model and closes the corresponding tab.
   * @param currentLayout
   */
  public void layoutDelete(Layout layout) {
    OpenedSBMLDocument doc = (OpenedSBMLDocument) layout.getSBMLDocument()
        .getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);
    boolean anyopen = view.getTabManager().isAnyOpenFromDocument(layout);
    if(doc.getListOfLayouts().size() == 1) {
      logger.info("Document doesn't have 2 or more layouts");
      this.view.showError("ERROR_LAYOUT_DELETE");
    }
    else if (anyopen || !anyopen && GUIFactory.createQuestionDelete(this.view.getFrame()) == JOptionPane.YES_OPTION){
      logger.info("Try to delete Layout ID: " + layout.getId() + " Layout Name: " + layout.getName());
      doc.getListOfLayouts().remove(layout);
      doc.setFileModified(true);
      view.closeTab(layout);
    }
  }
  
  /**
   * Asks the user, if the file should be saved and carries out the task.
   * @param doc
   * @return if user did not cancel saving progress
   */
  private boolean askUserSave(OpenedSBMLDocument doc) {
    if (doc.isFileModified()) {
      int returnVal = GUIFactory.createQuestionSave(this.view.getFrame(), doc.getAssociatedFilename());
      if (returnVal == JOptionPane.YES_OPTION) {
        logger.info("User chose to save file");
        return fileSave();
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
   * Deletes all currently selected nodes.
   */
  public void nodeDelete() {
    Layout layout = this.view.getCurrentLayout();
    OpenedSBMLDocument selectedDoc = getDocumentFromLayout(layout);
    
    for (Node node : this.nodeList) {
      NamedSBaseGlyph glyph = getGlyphFromNode(node);
      if (glyph == null) {
        logger.info("Couldn't find glyph for node");
      }
      else if (glyph instanceof SpeciesGlyph) {
        String speciesId = ((SpeciesGlyph) glyph).getSpecies();
        deleteReactionGlyphs((SpeciesGlyph) glyph, layout);
        
        if (this.nodeCopyList.remove(glyph)){
          logger.info("Removed glyph from copylist");
        }
        layout.getListOfSpeciesGlyphs().remove(glyph);
        layout.firePropertyChange("nodeDelete", null, glyph.getUserObject(SBMLEditorConstants.GLYPH_NODE_KEY));
        if (!selectedDoc.hasAnySpeciesGlyphForSpeciesId(speciesId)) {
          logger.info("No glyph left for species: Deleting species id: " + speciesId);
          layout.getModel().removeSpecies(speciesId);
        }
      }
      else if (glyph instanceof ReactionGlyph) {
        this.nodeCopyList.remove(glyph);
        layout.getListOfReactionGlyphs().remove(glyph);
        layout.firePropertyChange("nodeDelete", null, glyph.getUserObject(SBMLEditorConstants.GLYPH_NODE_KEY));
      }
    }
            
    logger.info("nodeDelete in CC");
    layoutModified(layout);
  }
  
  /**
   * Finds the ReactionGlyphs, that hold SpeciesReferenceGlyphs of the given SpeciesGlyph.
   * @param speciesGlyph
   * @param layout
   * @return a list of the found ReactionGlyphs.
   */
  public List<ReactionGlyph> findReactionGlyphs(SpeciesGlyph speciesGlyph, Layout layout) {
    ArrayList<ReactionGlyph> list = new ArrayList<ReactionGlyph>();
    
    for (ReactionGlyph rGlyph : layout.getListOfReactionGlyphs()) {
      List<SpeciesReferenceGlyph> sRefList = rGlyph.getListOfSpeciesReferenceGlyphs();
      
      for (int i = 0; i < sRefList.size(); i++) {
        if((speciesGlyph.getId().equals(sRefList.get(i).getSpeciesGlyph())) && 
          ((sRefList.get(i).getSpeciesReferenceRole() == SpeciesReferenceRole.PRODUCT) ||
           (sRefList.get(i).getSpeciesReferenceRole() == SpeciesReferenceRole.SUBSTRATE))){
          logger.info("Reaktionen löschen: Reaction"+ rGlyph.getId() + " zur Liste hinzugefügt.");
          list.add(rGlyph);
        }
      }
    }
    logger.info("Reaktionen löschen: Liste der Reaktionen wurde erstellt");
    return list;
  }
  
  /**
   * Deletes the ReactionGlyphs, that hold SpeciesReferenceGlyphs of the given SpeciesGlyph.
   * @param speciesGlyph
   * @param layout
   */
  private void deleteReactionGlyphs(SpeciesGlyph speciesGlyph, Layout layout) {
    List<ReactionGlyph> list = findReactionGlyphs(speciesGlyph, layout);
    
    for (ReactionGlyph rGlyph : list) {
      layout.getListOfReactionGlyphs().remove(rGlyph);
      layout.firePropertyChange("nodeDelete", null, rGlyph.getUserObject(SBMLEditorConstants.GLYPH_NODE_KEY));
      
      if (this.nodeCopyList.remove(rGlyph)){
        logger.info("Removed glyph from copylist");
      }
      logger.info("Reaktionen löschen: Reaction gelöscht.");
    }
  }
    
  /**
   * 
   */
  public void editDelete() {
    this.nodeDelete();
  }
  
  /**
   * Memorizes the selected nodes to paste them later with {@link #nodePaste}.
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
   * Pops up a dialogue and renames the SpeciesGlyph and Species corresponding to the selected node.
   */
  public void nodeRename() {
    Node nodeToRename = this.nodeList.get(0);
    SpeciesGlyph selectedGlyph = (SpeciesGlyph) getGlyphFromNode(nodeToRename);
    TextGlyph textGlyph = (TextGlyph) selectedGlyph.getUserObject(SBMLEditorConstants.GRAPHOBJECT_TEXTGLYPH_KEY);
    Species species = selectedGlyph.getModel().getSpecies(textGlyph.getNamedSBase());
    String oldName = species != null ? species.getName() : "";
    String newName = JOptionPane.showInputDialog(Resources.getString("NEW_NODE_NAME"),
        oldName);
    if (newName != null) {
      // set name
      species.setName(newName);
      
      // set file as modified
      Layout layout = view.getCurrentLayout();
      layoutModified(layout);
      
      // update Graph2D node
      // Property change does not work
      // this.view.getCurrentLayout().firePropertyChange("nodeRenamed", null, species);
      // 
      Graph2D graph2d = this.view.getTabManager().getPanelFromLayout(layout).getGraph2DView().getGraph2D();
      graph2d.setLabelText(nodeToRename, newName);
      graph2d.updateViews();
    }
  }
  
  /**
   * Pastes the nodes memorized with {@link #nodeCopy}.
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
        ReactionGlyph copyReactionGlyph = (ReactionGlyph) glyph;
        copyReactionGlyph(layout, selectedDoc, copyReactionGlyph);
      }
    }
    layoutModified(layout);
  }
  
  /**
   * Not implemented.
   * @param layout
   * @param selectedDoc
   * @param copyReactionGlyph
   */
  private void copyReactionGlyph(Layout layout, OpenedSBMLDocument selectedDoc,
      ReactionGlyph copyReactionGlyph) {
    logger.info("Reactionglyph");    
  }

  /**
   * Copies a SpeciesGlyph
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
          species.getName(),
          species.getSBOTerm(),
          SBMLView.DEFAULT_LEVEL_VERSION.getL(),
          SBMLView.DEFAULT_LEVEL_VERSION.getV(),
          selectedDoc.getDefaultCompartment());
      layout.getModel().addSpecies(s);
      SpeciesGlyph speciesGlyph = SBMLFactory.createSpeciesGlyph(glyphId,
          SBMLView.DEFAULT_LEVEL_VERSION.getL(),
          SBMLView.DEFAULT_LEVEL_VERSION.getV(),
          x,
          y,
          width,
          height,
          speciesIdNew);
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
            speciesIdNew);
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
  
  /**
   * Finds the document, that holds the given layout.
   * @param layout
   * @return the found document.
   */
  private OpenedSBMLDocument getDocumentFromLayout(Layout layout) {
    return (OpenedSBMLDocument) layout.getSBMLDocument()
        .getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);
  }
  
  private void layoutModified(Layout layout) {
    OpenedSBMLDocument doc = getDocumentFromLayout(layout);
    doc.setFileModified(true);
    this.view.refreshTitle(layout);
  }

  /**
   * Renames the given layout.
   * @param currentLayout
   * @param name
   */
  public boolean layoutRename(Layout layout, String name) {
    layout.setName(name);
    layoutModified(layout);
    this.view.updateComboBox(getDocumentFromLayout(layout).getListOfLayouts());
    return true;
  }

  /**
   * Exports the current view as a JPED or GIF.
   * @return true, if file was exported succesfully. false otherwise.
   */
  public boolean fileExport(File file) {
    Graph2DView view = this.view.getTabManager().getPanelFromLayout(this.view.getCurrentLayout()).getGraph2DView();
    Graph2D graph = view.getGraph2D();
       
    if(GUIFactory.createFilterGIF().accept(file)) {
      logger.info("Exporting gif file: " + file.getAbsolutePath());
      exportGraphToImageFileFormat(graph, new GIFIOHandler(), file.getAbsolutePath());
    }
    else if (GUIFactory.createFilterJPEG().accept(file)) {
      logger.info("Exporting jpeg file: " + file.getAbsolutePath());
      exportGraphToImageFileFormat(graph, new JPGIOHandler(), file.getAbsolutePath());      
    }
    return false;
  }
  
  /**
   * Exports the graph in the format specified by ioh.
   * @param graph
   * @param ioh
   * @param outFile
   */
  private void exportGraphToImageFileFormat(Graph2D graph, ImageOutputHandler ioh, String outFile) {  

    // Save the currently active view.   
    Graph2DView originalView = (Graph2DView)graph.getCurrentView();  

    // Create a new Graph2DView instance with the graph. This will be the   
    // dedicated view for image export.   
    Graph2DView exportView = ioh.createDefaultGraph2DView(graph);  

    Rectangle box = exportView.getGraph2D().getBoundingBox();  
    Dimension dim = box.getSize();  

    // Set the view's width and height, in turn this also sets the image's size.   
    exportView.setSize(dim);  
    // The clipping should show the entire graph. (The graph's bounding is a   
    // little enlarged.)   
    exportView.zoomToArea(box.getX() - 10, box.getY() - 10,   
        box.getWidth() + 20, box.getHeight() + 20);  

    // Set the detail threshold so that it is never switched to less detail mode.   
    exportView.setPaintDetailThreshold(0.0); 

    // Replace the currently active view containing the graph with the "export" view.   
    graph.setCurrentView(exportView);  

    try {
      ioh.write(graph, outFile);
    } catch (IOException e) {
      logger.info(e.getMessage());
    }

    // Remove the "export" view from the graph.   
    graph.removeView(graph.getCurrentView());  
    // Reset the current view to the originally active view.   
    graph.setCurrentView(originalView);    
  }  
}