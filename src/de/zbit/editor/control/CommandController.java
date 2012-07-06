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
import java.awt.event.ActionEvent;
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
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBO;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.layout.ExtendedLayoutModel;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.LayoutConstants;
import org.sbml.jsbml.ext.layout.Point;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.ext.render.AbstractRenderPlugin;
import org.sbml.jsbml.ext.render.ColorDefinition;
import org.sbml.jsbml.ext.render.GlobalRenderInformation;
import org.sbml.jsbml.ext.render.RenderConstants;
import org.sbml.jsbml.ext.render.RenderModelPlugin;
import org.sbml.jsbml.util.TreeNodeChangeListener;
import org.sbml.jsbml.util.TreeNodeRemovedEvent;
import org.sbml.jsbml.util.ValuePair;

import y.base.Node;

import de.zbit.editor.SBMLEditorConstants;
import de.zbit.editor.gui.GUIFactory;
import de.zbit.editor.gui.GraphLayoutPanel;
import de.zbit.editor.gui.Resources;
import de.zbit.editor.gui.SBMLEditMode;
import de.zbit.sbml.util.SBMLtools;

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
  private Node node;
  private SpeciesGlyph glyph;
  private boolean nodeCopy = false;
  private ValuePair<Integer, Integer> pos;
  
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
    String genericId = selectedDoc.nextGenericId(SBMLEditorConstants.genericId);
    String nameFromPopup = this.getEditorInstance().nameDialogue(genericId);
    logger.info("popup: " + nameFromPopup);
  
    /* use name as id if possible
    String id = selectedDoc.isIdAvailable(nameFromPopup) ? nameFromPopup
        : genericId;
    logger.info("id: " + id);
  
    if ((nameFromPopup != null) && (nameFromPopup.length() > 0)) {
      // is there any possibility in java to correctly check types before
      // casting?
       * */
       
      @SuppressWarnings("unchecked")
      ValuePair<Double, Double> newMousePosition =
          (ValuePair<Double, Double>) evt.getNewValue();
      Double x = newMousePosition.getL();
      Double y = newMousePosition.getV();
  
      /*
       * layout and model references
       */
      Layout layout = this.view.getCurrentLayout();
      Model model = layout.getModel();
      String glyphId = selectedDoc.nextGenericId(SBMLEditorConstants.genericGlyphIdPrefix);
      Species s = SBMLFactory.createSpecies(genericId, nameFromPopup, sboTerm, model.getLevel(), model.getVersion());
      SpeciesGlyph sGlyph = SBMLFactory.createSpeciesGlyph(glyphId , model.getLevel(), model.getVersion(), s.getId());
  
      model.addSpecies(s);
      //TODO sGlyph.setNamedSBase(); ???
      sGlyph = SBMLFactory.addSpeciesGlyphToLayout(layout, sGlyph, x, y, s.getName());

      selectedDoc.setFileModified(true);
      view.refreshTitle(layout);

      /*
       * keep a list of all glyphs which are associated with the species
       */
      List<String> glyphList = new ArrayList<String>();
      glyphList.add(sGlyph.getId());

      Map<String, List<String>> layoutGlyphMap = new HashMap<String, List<String>>();
      layoutGlyphMap.put(layout.getId(), glyphList);

      s.putUserObject(SBMLEditorConstants.GLYPH_LINK_KEY, layoutGlyphMap);
  
//      GraphLayoutPanel panel = (GraphLayoutPanel) this.view.getTabManager().getSelectedComponent();
//      s.addTreeNodeChangeListener(new ControllerViewSynchronizer(panel, this.view.getCurrentLayout()));
    
  
    this.state = States.normal;
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
    Model model = sbmlDocument.createModel(Resources.createValidID("m"));
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
      if (returnVal == JOptionPane.YES_OPTION) {
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

      //FIXME Changed order, so that a Layout definitely exists when adding RenderInformation. Does that make a difference?
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
      ValuePair<Double, Double> pos = (ValuePair<Double, Double>) evt.getNewValue();
      logger.info("New glyph information: " + "Node X: " + pos.getL() + " Y: " + pos.getV());
    }
    else if (evt.getPropertyName().equals(SBMLEditorConstants.EditModeNodePressedLeft)) {
      if (this.state == States.normal) {
        this.node = (Node) evt.getNewValue();
        this.glyph = getSpeciesGlyphFromNode();
        logger.info("Glyph selected ID: " + this.glyph.getId() + " Name: " +this.glyph.getName() + 
          " belongs to Species: " + this.glyph.getSpecies());
      }
    }
    else if (evt.getPropertyName().equals(SBMLEditorConstants.EditModeNodeReleasedLeft)) {
      if (this.state == States.normal) {
        
        ValuePair<Double, Double> pos = (ValuePair<Double, Double>) evt.getNewValue();
        SpeciesGlyph glyph = getSpeciesGlyphFromNode();
        glyph.getBoundingBox().setPosition(new Point(pos.getL(), pos.getV(), SBMLEditorConstants.glyphDefaultZ, 3, 1));
        logger.info("New glyph information: " + "Node X: " + pos.getL() + " Y: " + pos.getV());        
      }
    }
    else if (evt.getPropertyName().equals(SBMLEditorConstants.EditModeNodePressedRight)) {
      this.node = (Node) evt.getNewValue();
      this.glyph = getSpeciesGlyphFromNode();
      JPopupMenu popup = GUIFactory.createNodePopupMenu(this);
      SBMLEditMode editmode =  (SBMLEditMode) evt.getSource();
      MouseEvent e = editmode.getLastPressEvent();
      popup.show(e.getComponent(), e.getX(), e.getY());
      logger.info("Glyph selected ID: " + this.glyph.getId() + " Name: " +this.glyph.getName() + 
        "belongs to Species: " + this.glyph.getSpecies());
    }
    else if (evt.getPropertyName().equals(SBMLEditorConstants.EditModeMousePressedRight)) {
      JPopupMenu popup = GUIFactory.createPastePopupMenu(this, this.nodeCopy);
      SBMLEditMode editmode =  (SBMLEditMode) evt.getSource();
      MouseEvent e = editmode.getLastPressEvent();
      popup.show(e.getComponent(), e.getX(), e.getY());
      this.pos = new ValuePair<Integer, Integer>( e.getX(), e.getY()); 
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
  
  //Returns if User did not cancel saving progress 
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
  
  public SpeciesGlyph getSpeciesGlyphFromNode() {
    
    Layout layout = this.view.getCurrentLayout();
    ListOf<SpeciesGlyph> list = layout.getListOfSpeciesGlyphs();
    for (SpeciesGlyph glyph : list) {
      Node node = (Node) glyph.getUserObject(SBMLEditorConstants.GLYPH_NODE_KEY);
      if (node == this.node) {
        return glyph;
      }
    }
    return null;
  }
  
  public void nodeDelete() {
    Layout layout = this.view.getCurrentLayout();
    OpenedSBMLDocument selectedDoc = (OpenedSBMLDocument) layout.getSBMLDocument()
    .getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);
    
    layout.getListOfSpeciesGlyphs().remove(glyph);
    layout.firePropertyChange("nodeDelete", null, this.node);
    logger.info("nodeDelete in CC");
    selectedDoc.setFileModified(true);
  }
  
  public void nodeCopy() {
    this.nodeCopy = true;
  }
  
  public void nodePaste() {
    Layout layout = this.view.getCurrentLayout();
    OpenedSBMLDocument selectedDoc = (OpenedSBMLDocument) layout.getSBMLDocument()
    .getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);
        
    Double x = Double.valueOf(Integer.toString(this.pos.getL()));
    Double y = Double.valueOf(Integer.toString(this.pos.getV()));
    String speciesId = this.glyph.getSpecies();
    Species species = this.glyph.getModel().getSpecies(speciesId);  
    String id = selectedDoc.nextGenericId(SBMLEditorConstants.genericGlyphIdPrefix);
    
    if(layout.getModel() == this.glyph.getModel()) {
      logger.info("nodePaste: Same Model");
      
      SpeciesGlyph sGlyph = SBMLFactory.createSpeciesGlyph(id, SBMLView.DEFAULT_LEVEL_VERSION.getL(),
        SBMLView.DEFAULT_LEVEL_VERSION.getV(), speciesId);
      
      SBMLFactory.addSpeciesGlyphToLayout(layout, sGlyph, x, y, this.glyph.getName());
    }
    else {
      logger.info("nodePaste: Different Model");
      String speciesIdNew = selectedDoc.nextGenericId(SBMLEditorConstants.genericId);
      Species s = SBMLFactory.createSpecies(speciesIdNew, this.glyph.getName(), species.getSBOTerm(), SBMLView.DEFAULT_LEVEL_VERSION.getL(), SBMLView.DEFAULT_LEVEL_VERSION.getV());
      layout.getModel().addSpecies(s);
      SpeciesGlyph sGlyph = SBMLFactory.createSpeciesGlyph(id, SBMLView.DEFAULT_LEVEL_VERSION.getL(),
        SBMLView.DEFAULT_LEVEL_VERSION.getV(), speciesIdNew);
      SBMLFactory.addSpeciesGlyphToLayout(layout, sGlyph, x, y, this.glyph.getName());
    }
    selectedDoc.setFileModified(true);
  }
}