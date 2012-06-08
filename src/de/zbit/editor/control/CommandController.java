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
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBO;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.layout.ExtendedLayoutModel;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.LayoutConstant;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.util.ValuePair;

import de.zbit.editor.SBMLEditorConstants;
import de.zbit.editor.gui.ControllerViewSynchronizer;
import de.zbit.editor.gui.SBMLWritingTask;
import de.zbit.graph.gui.TranslatorSBMLgraphPanel;


/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class CommandController implements PropertyChangeListener {

  public static final Object LAYOUT_LINK_KEY = null;
  private SBMLView view;
  private states   state;
  private Logger logger = Logger.getLogger(CommandController.class.getName());

  /**
   *  TODO maybe rethink states an hold SBOTerm instead
   *  this would simplify species creation
   */
  private enum states {
    normal,
    unknownMolecule,
    simpleMolecule,
    macromolecule,
    emptySet,
    reaction,
    catalysis,
    inhibition,
    mousePressedReaction,
    mousePressedCatalysis,
    mousePressedInhibition,
  }

  /**
   * @param editorInstance
   */
  public CommandController(SBMLView editorInstance) {
    this.view = editorInstance;
    this.state = states.normal;
    this.logger.setLevel(Level.CONFIG);
  }


  /**
   * @return the editorInstance
   */
  public SBMLView getEditorInstance() {
    return view;
  }


  public void fileNew(String name) {
    SBMLDocument sbmlDocument = new SBMLDocument(
      SBMLView.DEFAULT_LEVEL_VERSION.getL(),
      SBMLView.DEFAULT_LEVEL_VERSION.getV());
    sbmlDocument.createModel(name);
    OpenedSBMLDocument doc = new OpenedSBMLDocument(sbmlDocument);
    view.addDocument(doc);
  }


  public void fileSave() {
    OpenedSBMLDocument od = this.view.getSelectedDoc();
    if (od.hasAssociatedFilepath()) {
      try {
        SBMLWritingTask task = new SBMLWritingTask(new File(
          od.getAssociatedFilepath()), (SBMLDocument) od.getDocument());
        task.addPropertyChangeListener(this);
        task.execute();
      } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      view.fileSaveAs();
    }
  }


  public void fileSaveAs(File file) {
    OpenedSBMLDocument od = this.view.getSelectedDoc();
    od.setAssociatedFilepath(file.getAbsolutePath());
    view.refreshTitle();
    try {
      SBMLWritingTask task = new SBMLWritingTask(new File(
        od.getAssociatedFilepath()), (SBMLDocument) od.getDocument());
      task.addPropertyChangeListener(this);
      task.execute();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }


  public void propertyChange(PropertyChangeEvent evt) {
    logger.info(evt.getPropertyName());
    if (evt.getPropertyName().equals("doneopening")) {
      OpenedSBMLDocument doc = (OpenedSBMLDocument) evt.getNewValue();
      view.addDocument(doc);
    }
    if (evt.getPropertyName().equals(SBMLEditorConstants.EditModeMousePressedLeft)) {
      if (this.state == states.unknownMolecule) {
        createUnknownMolecule(evt);
      } else if (this.state == states.simpleMolecule) {
        createSimpleMolecule(evt);
      } else if (this.state == states.macromolecule) {
        createMacromolecule(evt);
      } else if (this.state == states.emptySet) {
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
    createSpecies(evt, SBO.getEmptySet());
  }


  /**
   * @param evt
   * @param sboTerm TODO
   */
  private void createSpecies(PropertyChangeEvent evt, int sboTerm) {
    OpenedSBMLDocument selectedDoc = this.view.getSelectedDoc();

    // generate generic id
    String genericId = selectedDoc.getGenericId();
    String nameFromPopup = this.getEditorInstance().nameDialogue(genericId);
    // use name as id if possible
    String id = selectedDoc.isIdAvailable(nameFromPopup) ? nameFromPopup : genericId;

    if ((nameFromPopup != null) && (nameFromPopup.length() > 0)) {
      createSpecies(evt, sboTerm);
    }
    // is there any possibility in java to correctly check types before casting?
    ValuePair<Double, Double> newMousePosition = (ValuePair<Double, Double>) evt.getNewValue();
    Double x = newMousePosition.getL();
    Double y = newMousePosition.getV();
    Model model = selectedDoc.getDocument().getModel();
    Species s = new Species(id);
    s.setName(nameFromPopup);
    s.setLevel(model.getLevel());
    s.setVersion(model.getVersion());
    //TranslatorSBMLgraphPanel panel = (TranslatorSBMLgraphPanel)this.view.getTabManager().getSelectedComponent();
    s.setSBOTerm(sboTerm);

    ExtendedLayoutModel extLayout = new ExtendedLayoutModel(model);
    //TODO wont register id
    Layout layout = extLayout.createLayout();
    SpeciesGlyph sGlyph = layout.createSpeciesGlyph("glyph_" + s.getId(), s.getId());
    sGlyph.setBoundingBox(sGlyph.createBoundingBox(100, 100, 0, x, y, 0));
    layout.add(sGlyph);
    model.addExtension(LayoutConstant.namespaceURI, extLayout);
    TranslatorSBMLgraphPanel panel = (TranslatorSBMLgraphPanel) this.view.getTabManager().getSelectedComponent();

    s.addTreeNodeChangeListener(new ControllerViewSynchronizer(panel));
    model.addSpecies(s);
  }
  
  public void stateUnknownMolecule() {
    this.state = states.unknownMolecule;
    logger.info(this.state.toString());
  }


  public void stateSimpleMolecule() {
    this.state = states.simpleMolecule;
    logger.info(this.state.toString());
  }


  public void stateMacromolecule() {
    this.state = states.macromolecule;
    logger.info(this.state.toString());
  }


  public void stateEmptySet() {
    this.state = states.emptySet;
    logger.info(this.state.toString());
  }


  public void stateNormal() {
    this.state = states.normal;
    logger.info(this.state.toString());
  }
  
  public void stateReaction() {
    this.state = states.reaction;
    logger.info(this.state.toString());
  }
  
  public void stateCatalysis() {
    this.state = states.catalysis;
    logger.info(this.state.toString());
  }
  
  public void stateInhibition() {
    this.state = states.inhibition;
    logger.info(this.state.toString());
  }
}
