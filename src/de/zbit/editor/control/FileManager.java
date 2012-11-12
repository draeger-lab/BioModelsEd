/*
 * $Id
 * $URL
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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.sbml.jsbml.SBMLDocument;

import de.zbit.editor.gui.SBMLWritingTask;
import de.zbit.io.OpenedFile;
import de.zbit.sbml.gui.SBMLReadingTask;

/**
 * Manages the opened Documents.
 * 
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Eugen Netz
 * @author Jan Rudolph
 * @version $Rev$
 */
public class FileManager {
	/**
	 * List of all opened Documents
	 */
	List<OpenedFile<SBMLDocument>> listOfOpenedFiles = new ArrayList<OpenedFile<SBMLDocument>>();
	CommandController commandController;
	Logger logger = Logger.getLogger(FileManager.class.getName());


	/**
	 * Constructor.
	 * @param commandController
	 */
	public FileManager(CommandController commandController) {
		this.commandController = commandController;
	}
	
	/**
	 * Add Document only if not already added.
	 * @return returns true if document was added successfully
	 */
	public boolean addDocument(OpenedFile<SBMLDocument> openedFile) {
	  return listOfOpenedFiles.add(openedFile);
	}
	
	/**
	 * @return the listOfOpenedFiles
	 */
	public List<OpenedFile<SBMLDocument>> getListOfOpenedFiles() {
		return listOfOpenedFiles;
	}
	
	/**
	 * Open File.
	 * @return true if successful
	 */
  public boolean fileOpen(File file) {
  	//FIXME Check for right filetype
    if (file == null) {
      return false;
    }
    else {
      try {
      	// FIXME frame for reading task
        SBMLReadingTask task = new SBMLReadingTask(file, null, commandController);
        task.execute();
        return true;
      } catch (FileNotFoundException e) {
        return false;
      }
    }
  }
  
  /**
   * Open files and returns all successful for history
   */
  public File[] openFile(File... arg0) {
  	logger.info("openFile");
  	ArrayList<File> list = new ArrayList<File>();
  	for (File f : arg0) {
  		if(fileOpen(f)) {
  			list.add(f);
  		}
  	}
  	File[] successful = new File[list.size()];
  	list.toArray(successful);
  	return successful;
  }

  /**
   * Closes all tabs associated to this doc.
   * @param doc 
   * @return true, if succesful.
   */
  public boolean fileClose(OpenedFile<?> doc) {
    return this.listOfOpenedFiles.remove(doc);
  }

  /**
   * Saves the given document to a file.
   * @param doc
   * @return true, if succesful
   */
  public boolean saveFile(OpenedFile<SBMLDocument> doc) {
    try {
      if(!doc.isSetFile()) {
        return false;
      }
      SBMLWritingTask task = new SBMLWritingTask(doc);
      task.addPropertyChangeListener(commandController);
      task.execute();
      doc.setChanged(false);
      return true;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Asks user, where to save the file and calls {@link #fileSave}.
   * @param doc
   * @return true, if succesful
   */
  public boolean saveFileAs(File file, OpenedFile<SBMLDocument> openedFile) {
  	if (file == null) {
			return false;
		}
		if (openedFile.isSetFile()) {
			OpenedFile<SBMLDocument> newOpenedFile = new OpenedFile<SBMLDocument>();
			newOpenedFile.setDocument(new SBMLDocument(openedFile.getDocument()));
			newOpenedFile.setFile(file);
			return saveFile(newOpenedFile);
		}
		else {
			openedFile.setFile(file);
			return saveFile(openedFile);
		}
  }
}
