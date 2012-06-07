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

import org.sbml.jsbml.SBMLReader;

import de.zbit.editor.control.OpenedSBMLDocument;

/**
 * @author Eugen Netz
 * @since 1.0
 * @version $Rev$
 */
public class SBMLReadingTask extends SwingWorker<OpenedSBMLDocument, Void> {

  private ProgressMonitorInputStream stream;
  private File                       file;


  public SBMLReadingTask(File file, Component parent)
    throws FileNotFoundException {
    // TODO localize
    this.file = file;
    this.stream = new ProgressMonitorInputStream(parent, "Reading",
      new FileInputStream(file));
  }


  protected OpenedSBMLDocument doInBackground() throws Exception {
    return new OpenedSBMLDocument(SBMLReader.read(stream), file.getAbsolutePath());
  }


  @Override
  protected void done() {
    try {
      OpenedSBMLDocument doc = get();
      // TODO: Create an interface with public static final entries for these property keys.
      firePropertyChange("doneopening", null, doc);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
