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

import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBO;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.layout.ExtendedLayoutModel;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.LayoutConstant;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.util.ValuePair;

import de.zbit.editor.gui.SBMLWritingTask;
import de.zbit.graph.gui.TranslatorSBMLgraphPanel;


/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class CommandController implements PropertyChangeListener {

  private SBMLView view;
  private int fileCounter;
  private states   state;
  private ValuePair<Double, Double> pos;

  private enum states {
    normal,
    unspecified,
    simpleChemical,
    macromolecule,
    sink,
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
    OpenedDocument doc = new OpenedDocument(sbmlDocument);
    view.addDocument(doc);
  }


  public void fileSave() {
    OpenedDocument od = this.view.getSelectedDoc();
    if (od.hasAssociatedFilepath()) {
      try {
        SBMLWritingTask task = new SBMLWritingTask(new File(
          od.getAssociatedFilepath()), od.getSbmlDocument());
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
    OpenedDocument od = this.view.getSelectedDoc();
    od.setAssociatedFilepath(file.getAbsolutePath());
    view.refreshTitle();
    try {
      SBMLWritingTask task = new SBMLWritingTask(new File(
        od.getAssociatedFilepath()), od.getSbmlDocument());
      task.addPropertyChangeListener(this);
      task.execute();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }


  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals("doneopening")) {
      OpenedDocument doc = (OpenedDocument) evt.getNewValue();
      view.addDocument(doc);
    }
    if (evt.getPropertyName().equals("donesaveing")) {
      System.out.println("Speichern fertig...");
    }
    if (evt.getPropertyName().equals("EditModeMouseClicked")) {
      if (this.state != states.normal) {
        ++this.fileCounter;
        String name = this.getEditorInstance().nameDialogue(this.fileCounter);
        if ((name != null) && (name.length() > 0)
          && !name.equalsIgnoreCase("undefined")) {
          Double x = (Double) evt.getOldValue();
          Double y = (Double) evt.getNewValue();
          Model model = this.view.getSelectedDoc().getSbmlDocument().getModel();
          //Species s = model.createSpecies("id" + this.fileCounter);
          Species s = new Species("id" + this.fileCounter);
          s.setName(name);
          s.setLevel(model.getLevel());
          s.setVersion(model.getVersion());
          //TranslatorSBMLgraphPanel panel = (TranslatorSBMLgraphPanel)this.view.getTabManager().getSelectedComponent();
          s.setSBOTerm(this.chooseSpecies(s));
          
          ExtendedLayoutModel extLayout = new ExtendedLayoutModel(model);
          Layout layout = extLayout.createLayout();
          SpeciesGlyph sGlyph = layout.createSpeciesGlyph(s.getId());
          sGlyph.createBoundingBox(50, 50, 0, x, y, 0);
          model.addExtension(LayoutConstant.namespaceURI, extLayout);

          model.addSpecies(s);
        }
      }
    }
    /*if (evt.getPropertyName().equals("EditModeMPLeft")) {
      if (this.state == states.reaction) {
        this.state = states.mousePressedReaction;
        this.pos.setL((Double) evt.getOldValue());
        this.pos.setV((Double) evt.getNewValue());
      }
    }
    if (evt.getPropertyName().equals("EditModeMRLeft")) {
      if(this.state == states.mousePressedReaction) {
        Model model = this.view.getSelectedDoc().getSbmlDocument().getModel();
        this.fileCounter++;
        model.createReaction("id" + this.fileCounter);
        model.getReaction("id" + this.fileCounter).addReactant(specref);
      }
    }*/
  }
  
  private int chooseSpecies(Species s) {
    Integer current = null;
    if (this.state == states.unspecified) {
      current = SBO.getUnknownMolecule();
    } else if (this.state == states.simpleChemical) {
      current = SBO.getSimpleMolecule();
    } else if (this.state == states.macromolecule) {
      current = SBO.getMacromolecule();
    } else if (this.state == states.sink) {
      current = SBO.getEmptySet();
    }
    this.state = states.normal;
    return current;
  }
  
  public void stateUnspecified() {
    this.state = states.unspecified;
  }


  public void stateSimpleChemical() {
    this.state = states.simpleChemical;
  }


  public void stateMacromolecule() {
    this.state = states.macromolecule;
  }


  public void stateSink() {
    this.state = states.sink;
  }


  public void stateNormal() {
    this.state = state.normal;
  }
  
  public void stateReaction() {
    this.state = states.reaction;
  }
  
  public void stateCatalysis() {
    this.state = states.catalysis;
  }
  
  public void stateInhibition() {
    this.state = states.inhibition;
  }
}
