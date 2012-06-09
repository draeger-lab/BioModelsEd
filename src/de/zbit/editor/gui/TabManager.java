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
import java.util.List;

import javax.swing.JTabbedPane;

import org.sbml.jsbml.ext.layout.Layout;

import y.view.Graph2D;
import y.view.Graph2DView;
import de.zbit.editor.control.OpenedSBMLDocument;
import de.zbit.graph.gui.TranslatorSBMLgraphPanel;
import de.zbit.graph.io.SBML2GraphML;

/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class TabManager extends JTabbedPane {

  private static final long         serialVersionUID = -905908829761611472L;
  private SBMLEditor                editorInstance;
  /**
   * we keep a list of all viewable graphs
   */
  private List<Layout> tabList           = new ArrayList<Layout>();

  
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
    tabList.remove(index);
  }


  public void closeAllTabs() {
    removeAll();
    tabList.clear();
  }


  /**
   * @param doc
   */
  public void addTab(Layout layout) {
    tabList.add(layout);
    String title = layout.getName();
    TranslatorSBMLgraphPanel panel = new TranslatorSBMLgraphPanel(
      layout.getSBMLDocument(), false);
    SBMLEditMode editMode = new SBMLEditMode(this.editorInstance.getController());
    Graph2DView view = panel.getGraph2DView();
    view.addViewMode(editMode);
    layout.getSBMLDocument().getModel().addTreeNodeChangeListener(new ControllerViewSynchronizer(panel));
    addTab(title, panel);
    setSelectedComponent(panel);
    setTabComponentAt(getSelectedIndex(), new TabComponent(this));
  }


  /**
   * Return the currently opened document.
   * 
   * @return
   */
  public Layout getCurrentLayout() {
    return tabList.get(getSelectedIndex());
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


  public void refreshTitle() {
    String title = tabList.get(getSelectedIndex()).getName();
    ((TabComponent) getTabComponentAt(getSelectedIndex())).setTitle(title);
  }
  
  public void refresh(String id, String name, int sboTerm, double x, double y) {	  
    
    TranslatorSBMLgraphPanel panel = (TranslatorSBMLgraphPanel) getComponentAt(getSelectedIndex());
    SBML2GraphML converter = panel.getConverter();
    converter.createNode(id, name, sboTerm, x, y);   
  }
}
