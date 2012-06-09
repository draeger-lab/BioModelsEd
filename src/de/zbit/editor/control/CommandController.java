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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
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

  private SBMLView view;
  private States state;
  private FileManager fileManager;

  private Logger logger = Logger.getLogger(CommandController.class.getName());

  /**
   * TODO maybe rethink states an hold SBOTerm instead this would simplify
   * species creation
   */
  private enum States {
    normal, unknownMolecule, simpleMolecule, macromolecule, emptySet, reaction, catalysis, inhibition, mousePressedReaction, mousePressedCatalysis, mousePressedInhibition,
  }

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
   * @return the editorInstance
   */
  public SBMLView getEditorInstance() {
    return view;
  }

  public void fileNew(String name) {
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
    this.fileManager.addDocument(doc);

    /*
     * create a new default layout and tell the view to display it
     */
    Layout layout = doc.createDefaultLayout();
    this.view.addLayout(layout);
  }

  /**
   * 
   */
  public void fileSave() {
    OpenedSBMLDocument selectedDoc = (OpenedSBMLDocument) this.view
        .getCurrentLayout().getSBMLDocument()
        .getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);
    fileManager.save(selectedDoc);
    /*
     * if (od.hasAssociatedFilepath()) { try { SBMLWritingTask task = new
     * SBMLWritingTask(new File( od.getAssociatedFilepath()), (SBMLDocument)
     * od.getDocument()); task.addPropertyChangeListener(this); task.execute();
     * } catch (FileNotFoundException e) { e.printStackTrace(); } } else {
     * view.fileSaveAs(); }
     */
  }

  /**
   * @param file
   */
  public void fileSaveAs() {
    fileSave();
    /*
     * OpenedSBMLDocument od = this.view.getCurrentLayout();
     * od.setAssociatedFilepath(file.getAbsolutePath()); view.refreshTitle();
     * try { SBMLWritingTask task = new SBMLWritingTask(new File(
     * od.getAssociatedFilepath()), (SBMLDocument) od.getDocument());
     * task.addPropertyChangeListener(this); task.execute(); } catch
     * (FileNotFoundException e) { e.printStackTrace(); }
     */
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
      Layout layout = selectedDoc.createDefaultLayout();
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

  public void stateUnknownMolecule() {
    this.state = States.unknownMolecule;
    logger.info(this.state.toString());
  }

  public void stateSimpleMolecule() {
    this.state = States.simpleMolecule;
    logger.info(this.state.toString());
  }

  public void stateMacromolecule() {
    this.state = States.macromolecule;
    logger.info(this.state.toString());
  }

  public void stateEmptySet() {
    this.state = States.emptySet;
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

  public void stateCatalysis() {
    this.state = States.catalysis;
    logger.info(this.state.toString());
  }

  public void stateInhibition() {
    this.state = States.inhibition;
    logger.info(this.state.toString());
  }

  public File getSelectedFile() {
    return view.getSelectedFile();
  }

  /**
   * 
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
}
