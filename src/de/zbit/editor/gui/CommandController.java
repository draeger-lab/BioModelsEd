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
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;


/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class CommandController {
  private SBMLEditor editorInstance;

  /**
   * @param editorInstance
   */
  public CommandController(SBMLEditor editorInstance) {
    this.editorInstance = editorInstance;
  }
  
  /**
   * @return the editorInstance
   */
  public SBMLEditor getEditorInstance() {
    return editorInstance;
  }

  public void fileNew() {
    SBMLDocument sbmlDocument = new SBMLDocument(SBMLEditor.sbmlLevel, SBMLEditor.sbmlVersion);
    // TODO name of model
    sbmlDocument.createModel("untitled");
    OpenedDocument doc = new OpenedDocument(sbmlDocument);
    editorInstance.addDocument(doc);
    editorInstance.getTabManager().addTab(doc);
  }
  
  public void fileOpen() {
	  // TODO: Create a factory method for creating JFileChoosers in GUIFactory. This is too simple.
    JFileChooser fc = new JFileChooser();
    int returnVal = fc.showOpenDialog(editorInstance.getFrame());
    
    if(returnVal == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      try {
        //Read file
        SBMLDocument sbmlDoc = SBMLReader.read(file);
        OpenedDocument doc = new OpenedDocument(sbmlDoc, file.getPath());
        editorInstance.addDocument(doc);
        editorInstance.getTabManager().addTab(doc);
        // TODO: Find a way to display user messages not on the command-line interface.
      } catch (XMLStreamException e) {
        //e.printStackTrace();
        System.err.println( e );
      } catch (IOException e) {
        //e.printStackTrace();
        System.err.println( e );
     }
    }
  }
  
  public void fileSave() {
	  TabManager tabmanager = editorInstance.getTabManager();
	  if(tabmanager.isAnySelected()){
		  if(tabmanager.hasAssociatedFilepath()){
			  tabmanager.fileSave();
		  }
		  else{
			  fileSaveAs();
		  }
	  }
  }
  
  public void fileSaveAs() {
	  TabManager tabmanager = editorInstance.getTabManager(); 
	if(tabmanager.isAnySelected()){
    JFileChooser fc = new JFileChooser();
    int returnVal = fc.showSaveDialog(editorInstance.getFrame());
    // TODO: respect standard Java code convention
    if(returnVal == JFileChooser.APPROVE_OPTION) {
    	
    	tabmanager.fileSaveAs(fc.getSelectedFile().getAbsolutePath());
    }
	}
  }
  
  public void fileClose() {
    editorInstance.getTabManager().closeCurrentTab();
  }
  
  public void fileQuit() {
    System.exit(0);
  }
  
}
