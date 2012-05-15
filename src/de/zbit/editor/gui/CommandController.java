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
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.SBMLWriter;


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
    OpenedDocument currentDocument = editorInstance.getTabManager().getCurrentDocument();
    if (currentDocument.hasAssociatedFilepath()) {
      try {
        new SBMLWriter().write(currentDocument.getSbmlDocument(),
            currentDocument.getAssociatedFilepath());
      } catch (SBMLException e) {
        e.printStackTrace();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (XMLStreamException e) {
        e.printStackTrace();
      }
    }
    else {
      fileSaveAs();
    }
  }
  
  public void fileSaveAs() {
    JFileChooser fc = new JFileChooser();
    int returnVal = fc.showSaveDialog(editorInstance.getFrame());
    
    if(returnVal == JFileChooser.APPROVE_OPTION) {
      try {
        OpenedDocument currentDocument = editorInstance.getTabManager().getCurrentDocument();
        SBMLDocument doc = currentDocument.getSbmlDocument();
        new SBMLWriter().write(doc, fc.getSelectedFile());  
      } catch (XMLStreamException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
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
