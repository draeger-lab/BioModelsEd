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

import org.sbml.jsbml.SBMLDocument;

import de.zbit.editor.gui.SBMLWritingTask;


/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class CommandController implements PropertyChangeListener {

  private SBMLView view;


  /**
   * @param editorInstance
   */
  public CommandController(SBMLView editorInstance) {
    this.view = editorInstance;
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
  }
}
