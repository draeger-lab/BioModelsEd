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
package de.zbit.editor.gui;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;

import javax.swing.ProgressMonitorInputStream;
import javax.swing.SwingWorker;

import de.zbit.editor.SBMLEditorConstants;
import de.zbit.editor.control.OpenedSBMLDocument;
import de.zbit.graph.io.CellDesignerAnnotationsParser.CellDesignerAnnotationParser;

/**
 * This class represents a file reading task for a SBMLDocument.
 * 
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Eugen Netz
 * @author Jan Rudolph
 * @version $Rev$
 */
public class SBMLReadingTask extends SwingWorker<OpenedSBMLDocument, Void> {

  private ProgressMonitorInputStream stream;
  private File file;

  
  /**
   * Constructor.
   * 
   * @param file
   * @param parent
   * @throws FileNotFoundException
   */
  public SBMLReadingTask(File file, Component parent)
      throws FileNotFoundException {
    this.file = file;
    this.stream = new ProgressMonitorInputStream(parent, Resources.getString("READING"),
        new FileInputStream(file));
  }


  /**
   * Runs the CellDesignerAnnotationParser on the file and creates an OpenedSBMLDocument with the modified file.
   */
  protected OpenedSBMLDocument doInBackground() throws Exception {
    //return new OpenedSBMLDocument(SBMLReader.read(stream), file.getAbsolutePath());
    CellDesignerAnnotationParser parser = new CellDesignerAnnotationParser(file);
    parser.run();
    return new OpenedSBMLDocument(parser.getSBMLDocument(), file.getAbsolutePath());
  }


  /**
   * Fires a property change with the created document, when the reding task is done.
   */
  @Override
  protected void done() {
    try {
      OpenedSBMLDocument doc = get();
      firePropertyChange(SBMLEditorConstants.openingDone, null, doc);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
  }
}
