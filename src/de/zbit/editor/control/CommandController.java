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

import org.sbml.jsbml.JSBML;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBO;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.test.gui.JSBMLvisualizer;

import de.zbit.editor.gui.SBMLWritingTask;


/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class CommandController implements PropertyChangeListener {

  private SBMLView view;
  private int fileCounter;
  private states   state;

  // TODO: Use upper-case first letter (Java convention)
  private enum states {
    normal,
    unspecified,
    simpleChemical,
    macromolecule,
    sink,
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
    	// TODO: Just for tests:
    	new JSBMLvisualizer(od.getSbmlDocument());
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
    if (evt.getPropertyName().equals("EditModeMPLeft")) {
      if (this.state != states.normal) {
        ++this.fileCounter;
        String name = this.getEditorInstance().nameDialogue(this.fileCounter);
        if ((name != null) && (name.length() > 0)
            && !name.equalsIgnoreCase("undefined")) {
            Model model = this.view.getSelectedDoc().getSbmlDocument().getModel();
            Species s = model.createSpecies("id" + this.fileCounter);
            s.setName(name);
            //this.chooseSpecies(s);
            //TODO How to add new species to model and set coordinates?
            model.addSpecies(s);
            //TODO How to refresh the view, using the changed model?
            //this.view.refresh();
            this.state = states.normal;
          }
      }
    }
  }
  
  private Integer chooseSpecies() {
	  // TODO: better use switch statement here
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

  public void addNode(double x, double y) {
	  if (this.state != states.normal) {
	        ++this.fileCounter;
	        String name = this.getEditorInstance().nameDialogue(this.fileCounter);
	        if ((name != null) && (name.length() > 0)
	            && !name.equalsIgnoreCase("undefined")) {
	            OpenedDocument od = this.view.getSelectedDoc();
	            String id = "id" + this.fileCounter;
	            Integer sboTerm = this.chooseSpecies();
	            od.addNode(id,name, sboTerm, x, y);
	            
	           // s.setName(name);
	            //this.chooseSpecies(s);
	            //TODO How to add new species to model and set coordinates?
	            //model.addSpecies(s);
	            //TODO How to refresh the view, using the changed model?
	           this.view.refresh(id, name, sboTerm, x, y);
	           
	            this.state = states.normal;
	          }
	      }
  }
}
