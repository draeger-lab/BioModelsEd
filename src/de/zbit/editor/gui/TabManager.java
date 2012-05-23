/*
 * $Id: TabManager.java 31 2012-05-22 12:10:55Z se-ss-12.netz$
 * $URL: https://cis.informatik.uni-tuebingen.de/svn/R4f8845abdec88/trunk/src/de/zbit/editor/gui/TabManager.java$
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

import y.view.Graph2DView;
import de.zbit.editor.control.OpenedDocument;
import de.zbit.graph.gui.TranslatorSBMLgraphPanel;
import de.zbit.graph.io.SBML2GraphML;

/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class TabManager extends JTabbedPane {

  private static final long         serialVersionUID = -905908829761611472L;
  private SBMLEditor                editorInstance;
  // TODO: Do not declare variables of type ArrayList -> not flexible enough.
  private ArrayList<OpenedDocument> tabMap           = new ArrayList<OpenedDocument>();
  private HashMap<String, Integer>  openedFilenames  = new HashMap<String, Integer>();


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
  public void closeTab(int index) {
    removeTabAt(index);
    tabMap.remove(index);
  }


  public void closeAllTabs() {
    removeAll();
    tabMap.clear();
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
    if (openedFilenames.containsKey(title)) {
      int count = openedFilenames.get(title);
      openedFilenames.put(title, ++count);
      title += " (" + (count - 1) + ")";
    } else {
      openedFilenames.put(title, 1);
    }
    TranslatorSBMLgraphPanel panel = new TranslatorSBMLgraphPanel(
      doc.getSbmlDocument(), false);
    SBMLEditMode editMode = new SBMLEditMode(panel.getConverter(), this.editorInstance.getController());
    Graph2DView view = panel.getGraph2DView();
    view.addViewMode(editMode);
    //this.addPropertyChangeListener(editMode);
    addTab(title, panel);
    setSelectedComponent(panel);
    setTabComponentAt(getSelectedIndex(), new TabComponent(this));
  }


  /**
   * Return the currently opened document.
   * 
   * @return
   */
  public OpenedDocument getCurrentDocument() {
    return tabMap.get(getSelectedIndex());
  }


  /**
   * Close the currently visible tab.
   */
  public void closeCurrentTab() {
    if (isAnySelected()) {
      closeTab(getSelectedIndex());
    }
  }


  public boolean isAnySelected() {
    return getSelectedIndex() != -1;
  }


  public boolean hasAssociatedFilepath() {
    return getCurrentDocument().hasAssociatedFilepath();
  }


  public void refreshTitle() {
    String title = tabMap.get(getSelectedIndex()).getAssociatedFilename();
    ((TabComponent) getTabComponentAt(getSelectedIndex())).setTitle(title);
  }
  
  public void refresh(String id, String name, int sboTerm, double x, double y) {	  
    
    TranslatorSBMLgraphPanel panel = (TranslatorSBMLgraphPanel) getComponentAt(getSelectedIndex());
    SBML2GraphML converter = panel.getConverter();
    converter.createNode(id, name, sboTerm, x, y);   
  }
}
