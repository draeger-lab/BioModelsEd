/*
 * $Id:  TabManager.java 14:14:01 jakob $
 * $URL: TabManager.java $
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

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTabbedPane;

import de.zbit.editor.control.OpenedDocument;
import de.zbit.graph.gui.TranslatorSBMLgraphPanel;

/**
 * @author Jakob Matthes
 * @version $Rev$
 */
//TODO: Set SVN properties
public class TabManager extends JTabbedPane {

  private static final long serialVersionUID = -905908829761611472L;
  private SBMLEditor editorInstance;
  // TODO: Do not declare variables of type ArrayList -> not flexible enough.
  private ArrayList<OpenedDocument> tabMap = new ArrayList<OpenedDocument>();
  private HashMap<String, Integer> openedFilenames = new HashMap<String, Integer>();
  

  /**
   * @param editorInstance
   */
  public TabManager(SBMLEditor editorInstance) {
    this.editorInstance = editorInstance;
  }
  
  /**
   * @return the editorInstance
   */
  public SBMLEditor getEditorInstance() {
    return editorInstance;
  }

  /**
   * @param doc
   */
  private void closeTab(int index) {
	removeTabAt(index);
	tabMap.remove(index);
  }

  /**
   * @param doc
   */
  public void addTab(OpenedDocument doc) {
    tabMap.add(doc);
    String title;
    if (doc.hasAssociatedFilepath()) {
    	title = doc.getAssociatedFilename();
    } else {
      title = Resources.getString("UNSAVED_FILE");
    }
    if(openedFilenames.containsKey(title)){
    	int count = openedFilenames.get(title);
    	openedFilenames.put(title, ++count);
    	title += " (" + (count-1) + ")";
    } else {
    	openedFilenames.put(title, 1);
    }
    TranslatorSBMLgraphPanel panel = new TranslatorSBMLgraphPanel(doc.getSbmlDocument(), false);
    addTab(title, panel);
    setSelectedComponent(panel);
  }

  /**
   * Return the currently opened document.
   * @return
   */
  
  public OpenedDocument getCurrentDocument() {
	  return tabMap.get(getSelectedIndex());
  }

  /**
   * Close the currently visible tab.
   */
  public void closeCurrentTab() {
	  if(isAnySelected()){
		  closeTab(getSelectedIndex());
	  }
  }
    
  public boolean isAnySelected(){
	  return getSelectedIndex()!=-1;
  }

  /*
  public void fileSave() {
	  if(isAnySelected()){
		  getCurrentDocument().fileSave();
	  }
  }*/
  
  /*
  public void fileSaveAs(String filename) {
	  // TODO: Respect Java coding conventions.
	  if(isAnySelected()){
		  getCurrentDocument().fileSaveAs(filename);
		  setTitleAt(getSelectedIndex(), filename);
	  }
  }*/
  
  public boolean hasAssociatedFilepath() {
	  return getCurrentDocument().hasAssociatedFilepath();
  }
  
  public void refreshTitle(){
	  setTitleAt(getSelectedIndex(), tabMap.get(getSelectedIndex()).getAssociatedFilename());
  }
  
}
