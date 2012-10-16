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

import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.ext.layout.ExtendedLayoutModel;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.LayoutConstants;

import de.zbit.editor.BioModelsEdConstants;
import de.zbit.editor.gui.Resources;
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
	List<OpenedFile<?>> listOfOpenedFiles = new ArrayList<OpenedFile<?>>();
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
	public boolean addDocument(OpenedFile<?> openedFile) {
	  if (!openedFile.isSetFile()) {
	    int i = 0;
	    String name;
      do {
        logger.info("Filename: " + Resources.getString(BioModelsEdConstants.genericFileName) +" (" + i + ")" + " not availible.");
	      i+=1;
	      name = Resources.getString(BioModelsEdConstants.genericFileName) +" (" + i + ")";
	    }while(isFileNameUsed(name));
      
      openedFile.setFile(new File(name));
	  }
		if (listOfOpenedFiles.contains(openedFile)) {
		  logger.info("Failed to add: List already contains document");
			return false;
		}
		else {
			this.listOfOpenedFiles.add(openedFile);
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
		for (OpenedFile<?> doc : listOfOpenedFiles) {
			if (doc.isSetFile() && doc.getFile().getAbsolutePath().equals(filePath)) {
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
    for (OpenedFile<?> doc : listOfOpenedFiles) {
      anyModified = anyModified || doc.isChanged();
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
        SBMLReadingTask task = new SBMLReadingTask(file, commandController.getFrame());
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
  public boolean fileClose(OpenedFile<SBMLDocument> doc) {
    boolean success = true;
    logger.info(doc.getFile().getName());
    // unwrap opened file
    Model model = doc.getDocument().getModel();
    ExtendedLayoutModel layoutModel = (ExtendedLayoutModel) model.getExtension(
    	LayoutConstants.getNamespaceURI(model.getVersion(), model.getVersion()));
    
    for (Layout layout : layoutModel.getListOfLayouts()) {
      boolean s = commandController.closeTab(layout);
      logger.info(layout.getName() + " closing succes? : " + s);
      success |= s;
    }
    if(success) {
      this.listOfOpenedFiles.remove(doc);
    }
    
    return success;
  }

  /**
   * Saves the given document to a file.
   * @param doc
   * @return true, if succesful
   */
  public boolean fileSave(OpenedFile<SBMLDocument> doc) {
    try {
      if(!doc.isSetFile()) {
        return fileSaveAs(doc);
      }
      SBMLWritingTask task = new SBMLWritingTask(doc.getFile(), doc.getDocument());
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
  public boolean fileSaveAs(OpenedFile<SBMLDocument> doc) {
    File file = commandController.askUserSaveDialog();
    if (file != null) {
      doc.setFile(file);
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
    for (OpenedFile<?> doc : listOfOpenedFiles) {
      if(doc.getFile().getName().equals(name)){
        return true;
      }
    }
    return false;
  }
}
