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

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;


/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class CommandController {
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
    SBMLDocument sbmlDocument = new SBMLDocument(SBMLView.DEFAULT_LEVEL_VERSION.getL(), SBMLView.DEFAULT_LEVEL_VERSION.getV() );
    // TODO name of model
    sbmlDocument.createModel("untitled");
    OpenedDocument doc = new OpenedDocument(sbmlDocument);
    view.addDocument(doc);
  }
  
  public boolean fileOpen(File file) {
	  // TODO: Create a factory method for creating JFileChoosers in GUIFactory. This is too simple.
	  if(file != null){
      try {
        //Read file
        SBMLDocument sbmlDoc = SBMLReader.read(file);
        OpenedDocument doc = new OpenedDocument(sbmlDoc, file.getPath());
        view.addDocument(doc);
        return true;
        // TODO: Find a way to display user messages not on the command-line interface.
      } catch (XMLStreamException e) {
        //e.printStackTrace();
        System.err.println( e );
      } catch (IOException e) {
        //e.printStackTrace();
        System.err.println( e );
     }      
    }
	  return false;
  }
  
  public void fileSave() {
	  OpenedDocument od = this.view.getSelectedDoc();
	  if(od.hasAssociatedFilepath()){
		  od.fileSave();
	  } else {
		  view.fileSaveAs();
	  }
  }
  
  public void fileSaveAs(File file) {
	  this.view.getSelectedDoc().fileSaveAs(file);	
  }
  
  public void fileClose() {
    view.getTabManager().closeCurrentTab();
  }
  
  public void fileQuit() {
    System.exit(0);
  }
  
}
