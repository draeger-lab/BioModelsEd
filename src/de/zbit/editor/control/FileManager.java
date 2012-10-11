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
import org.sbml.jsbml.ext.layout.Layout;

import de.zbit.editor.SBMLEditorConstants;
import de.zbit.editor.gui.Resources;
import de.zbit.editor.gui.SBMLWritingTask;
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
	List<OpenedDocument<?>> listOfOpenedDocuments = new ArrayList<OpenedDocument<?>>();
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
	public boolean addDocument(OpenedDocument<?> openedDocument) {
	  if (openedDocument.getAssociatedFilename() == null) {
	    int i = 0;
	    String name;
      do {
        logger.info("Filename: " + Resources.getString(SBMLEditorConstants.genericFileName) +" (" + i + ")" + " not availible.");
	      i+=1;
	      name = Resources.getString(SBMLEditorConstants.genericFileName) +" (" + i + ")";
	    }while(isFileNameUsed(name));
      
      openedDocument.setAssociatedFilename(name);
	  }
		if (listOfOpenedDocuments.contains(openedDocument)) {
		  logger.info("Failed to add: List already contains document");
			return false;
		}
		else {
			this.listOfOpenedDocuments.add(openedDocument);
			logger.info("Succes");
			return true;
		}
	}
	
	/**
	 * open Document
	 * @return returns true if document was added successfully
	 */
	//TODO: Not used
	
	/*
	public boolean openDocument(String filePath) {
		if (isFilePathUsed(filePath)) {
			return false;
		}
		else {
			File file = commandController.askUserOpenDialog();
			Frame frame = commandController.getEditorInstance().getFrame();
			if(file != null) {
				try {
			        SBMLReadingTask task = new SBMLReadingTask(file, frame);
			        task.addPropertyChangeListener(commandController);
			        task.execute();
			        return true;
			      } catch (FileNotFoundException e) {
			        e.printStackTrace();
			      }
			}
			return true;
		}
	}*/
	
	/**
	 * Check if filePath is already in use.
	 * @param filePath
	 * @return true, if it is used. false otherwise.
	 */
	private boolean isFilePathUsed(String filePath) {
		for (OpenedDocument<?> doc : listOfOpenedDocuments) {
			if (doc.hasAssociatedFilepath() && doc.getAssociatedFilepath().equals(filePath)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if any file has been modified.
	 * @return true, if any file is modified. false otherwise.
	 */
	public boolean anyFileIsModified() {
	  boolean anyModified = false;
    for (OpenedDocument<?> doc : listOfOpenedDocuments) {
      anyModified = anyModified || doc.isFileModified();
    }
    return anyModified;
	}
	
	/**
	 * Open File.
	 * @return true if successful
	 */
  public boolean fileOpen(File file) {
  	//FIXME Check for right filetype
    if (file == null || isFilePathUsed(file.getAbsolutePath())) {
      return false;
    }
    else {
      try {
        SBMLReadingTask task = new SBMLReadingTask(file, this.commandController.getFrame());
        task.addPropertyChangeListener(commandController);
        task.execute();
        return true;
      } catch (FileNotFoundException e) {
        commandController.fileNotFound();
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
  public boolean fileClose(OpenedSBMLDocument doc) {
    boolean success = true;
    logger.info(doc.getAssociatedFilename());
    for (Layout layout : doc.getListOfLayouts()) {
      boolean s = commandController.closeTab(layout);
      logger.info(layout.getName() + " closing succes? : " + s);
      success |= s;
    }
    if(success) {
      this.listOfOpenedDocuments.remove(doc);
    }
    
    return success;
  }

  /**
   * Saves the given document to a file.
   * @param doc
   * @return true, if succesful
   */
  public boolean fileSave(OpenedSBMLDocument doc) {
    try {
      
      if(!doc.hasAssociatedFilepath()) {
        return fileSaveAs(doc);
      }
      
      File file = new File(doc.getAssociatedFilepath());
      
      SBMLWritingTask task = new SBMLWritingTask(file, (SBMLDocument) doc.getDocument());
      task.addPropertyChangeListener(commandController);
      task.execute();
      doc.setFileModified(false);
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
  public boolean fileSaveAs(OpenedSBMLDocument doc) {
    File file = commandController.askUserSaveDialog();
    if (file != null) {
      doc.setAssociatedFilepath(file.getAbsolutePath());
      return fileSave(doc);
    }
    return false;
  }
  
  /**
   * Checks, if the name is already used as a filename.
   * @param name
   * @return true, if it is used
   */
  public boolean isFileNameUsed(String name) { 
    for (OpenedDocument<?> doc : listOfOpenedDocuments) {
      if(doc.getAssociatedFilename().equals(name)){
        return true;
      }
    }
    return false;
  }
}
