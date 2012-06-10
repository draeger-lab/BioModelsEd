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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBO;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.util.ValuePair;

import de.zbit.editor.SBMLEditorConstants;
import de.zbit.editor.gui.GUIFactory;

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
    String genericId = selectedDoc.getGenericId();
    String nameFromPopup = this.getEditorInstance().nameDialogue(genericId);
    logger.info("popup: " + nameFromPopup);
  
    // use name as id if possible
    String id = selectedDoc.isIdAvailable(nameFromPopup) ? nameFromPopup
        : genericId;
    logger.info("id: " + id);
  
    if ((nameFromPopup != null) && (nameFromPopup.length() > 0)) {
      // is there any possibility in java to correctly check types before
      // casting?
      ValuePair<Double, Double> newMousePosition =
          (ValuePair<Double, Double>) evt.getNewValue();
      Double x = newMousePosition.getL();
      Double y = newMousePosition.getV();
  
      /*
       * layout and model references
       */
      Layout layout = this.view.getCurrentLayout();
      Model model = layout.getModel();
  
      /*
       * create species (not in model)
       */
      Species s = new Species(id);
      s.setName(nameFromPopup);
      s.setLevel(model.getLevel());
      s.setVersion(model.getVersion());
      s.setSBOTerm(sboTerm);
  
      /*
       * create species glyph
       */
      SpeciesGlyph sGlyph = layout.createSpeciesGlyph("glyph_" + s.getId(),
          s.getId());
      sGlyph.setBoundingBox(sGlyph.createBoundingBox(
          SBMLEditorConstants.glyphDefaultWidth,
          SBMLEditorConstants.glyphDefaultHeight,
          SBMLEditorConstants.glyphDefaultDepth,
          x,
          y,
          SBMLEditorConstants.glyphDefaultZ));
      layout.add(sGlyph);
  
      /*
       * add created species
       */
      model.addSpecies(s);
  
      // TranslatorSBMLgraphPanel panel = (TranslatorSBMLgraphPanel)
      // this.view.getTabManager().getSelectedComponent();
      // s.addTreeNodeChangeListener(new ControllerViewSynchronizer(panel));
    }
  
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
    boolean success = true;
    /*
     * first, create a new SBMLDocument
     */
    SBMLDocument sbmlDocument = new SBMLDocument(
        SBMLView.DEFAULT_LEVEL_VERSION.getL(),
        SBMLView.DEFAULT_LEVEL_VERSION.getV());
    sbmlDocument.createModel(name);

    /*
     * embed the new SBMLDocument in an OpenedSBMLDocument and tell the
     * fileManager about it
     */
    OpenedSBMLDocument doc = new OpenedSBMLDocument(sbmlDocument);
    success &= this.fileManager.addDocument(doc);

    /*
     * create a new default layout and tell the view to display it
     */
    Layout layout = doc.createDefaultLayout();
    success &= this.view.addLayout(layout);
    
    return success;
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
    return fileManager.fileClose(doc);
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
       * notify fileManager about newly opened document
       */
      this.fileManager.addDocument(doc);

      /*
       * add first or new default layout to view
       */
      this.view.addLayout(doc.getFirstLayoutOrNew());
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
}
