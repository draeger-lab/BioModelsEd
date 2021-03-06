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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.SwingWorker;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLWriter;

import de.zbit.editor.Constants;
import de.zbit.io.OpenedFile;

/**
 * This class represents a file writing task for a SBMLDocument.
 * 
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Eugen Netz
 * @author Jan Rudolph
 * @version $Rev$
 */
public class SBMLWritingTask extends SwingWorker<Void, Void> {

  private OutputStream stream;
  private File file;
  private SBMLDocument doc;
  private OpenedFile<SBMLDocument> openedFile;

  /**
	 * @param doc2
   * @throws FileNotFoundException 
	 */
	public SBMLWritingTask(OpenedFile<SBMLDocument> openedFile) throws FileNotFoundException {
		this.openedFile = openedFile;
		this.file = openedFile.getFile();
		this.doc = openedFile.getDocument();
		this.stream = new FileOutputStream(this.file);
	}


	/**
   * Writes the document into a file using a SBMLWriter.
   */
  protected Void doInBackground() throws Exception {
    new SBMLWriter().write(doc, stream);
    return null;
  }


  /**
   * Fires a property change, when the writing task is done.
   */
  @Override
  protected void done() {
    firePropertyChange(Constants.savingDone, null, openedFile);
  }
}
