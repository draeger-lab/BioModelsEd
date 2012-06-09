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

import java.awt.Frame;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.sbml.jsbml.SBMLDocument;


import de.zbit.editor.gui.SBMLReadingTask;
import de.zbit.editor.gui.SBMLWritingTask;

/**
 * @author Jan Rudolph
 *	
 * @version $Rev
 */
public class FileManager {
	/**
	 * List of all opened Documents
	 */
	List<OpenedDocument<?>> listOfOpenedDocuments = new ArrayList<OpenedDocument<?>>();
	CommandController commandController;

	/**
	 * @param listOfOpenedDocuments
	 */
	public FileManager() {
	}
	
	/**
	 * @param listOfOpenedDocuments
	 */
	public FileManager(CommandController commandController) {
		this.commandController = commandController;
	}
	
	/**
	 * add Document only if not already added
	 * @return returns true if document was added successfully
	 */
	public boolean addDocument(OpenedDocument<?> openedDocument) {
		if (listOfOpenedDocuments.contains(openedDocument)) {
			return false;
		}
		else {
			this.listOfOpenedDocuments.add(openedDocument);
			return true;
		}
	}
	
	/**
	 * open Document
	 * @return returns true if document was added successfully
	 */
	public boolean openDocument(String filePath) {
		if (isFilePathUsed(filePath)) {
			return false;
		}
		else {
			File file = commandController.getSelectedFile();
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
	}
	
	/**
	 * save file
	 */
	public boolean save(OpenedDocument<?> doc) {
		try {
			String associatedFilename = doc.getAssociatedFilename();
			// check for Filename set, if not ask user
			File file = doc.getAssociatedFilename() == null ? 
					commandController.getSelectedFile() : new File(associatedFilename);
			SBMLWritingTask task = new SBMLWritingTask(file, (SBMLDocument) doc.getDocument());
			task.addPropertyChangeListener(commandController);
			task.execute();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * check if filePath is already in use
	 * @param filePath
	 * @return
	 */
	private boolean isFilePathUsed(String filePath) {
		for (OpenedDocument<?> doc : listOfOpenedDocuments) {
			if (doc.hasAssociatedFilepath() && doc.getAssociatedFilepath() == filePath) {
				return true;
			}
		}
		return false;
	}
	
	
	
}
