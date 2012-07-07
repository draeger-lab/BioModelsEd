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
import java.util.logging.Logger;

import javax.swing.JTabbedPane;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.ext.layout.Layout;

import y.view.Graph2DView;
import de.zbit.editor.SBMLEditorConstants;
import de.zbit.editor.control.OpenedSBMLDocument;

/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class TabManager extends JTabbedPane {

  private static final long serialVersionUID = -905908829761611472L;
  private static Logger logger = Logger.getLogger(OpenedSBMLDocument.class.toString());
  private SBMLEditor editorInstance;
  private ArrayList<Layout> listOfLayouts = new ArrayList<Layout>();
  
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
  public boolean addTab(Layout layout) {
    if(listOfLayouts.contains(layout)) {
      logger.info("List contains layout: ID: "+ layout.getId() + " Name: " +layout.getName());
    }
    listOfLayouts.add(layout);
      
    GraphLayoutPanel panel = createPanelFromLayout(layout);
    
    addTab("",panel);
    setSelectedComponent(panel);
    setTabComponentAt(getSelectedIndex(), new TabComponent(this));

    refreshTitle(layout);
    showTab(layout);
    return true;
  }
  
  /**
   * @param layout
   * @return
   */
  public boolean closeTab(Layout layout) {
    if (isLayoutOpen(layout)) {
      int index = this.listOfLayouts.indexOf(layout);
      remove(index);
      this.listOfLayouts.remove(index);
      logger.info("ID: " + layout.getId() + " Name: " + layout.getName()
        + " Tabindex: " + index);
      showTab(getCurrentLayout());
      return true;
    }
    return false;
  }

  /**
   * @return
   */
  public Layout getCurrentLayout() {
    if (isAnySelected()) {
      return this.listOfLayouts.get(getSelectedIndex());
    }
    else {
      return null;
    }
  }
  
  /**
   * @param layout
   */
  public void showTab(Layout layout) {
    if(layout == null) {
      this.editorInstance.updateComboBox(new ListOf<Layout>());
    }
    else {
      setSelectedIndex(this.listOfLayouts.indexOf(layout));
      OpenedSBMLDocument doc = (OpenedSBMLDocument) layout.getModel().getSBMLDocument().getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);
      this.editorInstance.updateComboBox(doc.getListOfLayouts());
    }
  }

  /**
   * 
   */
  public void closeAllTabs() {
    while (isAnySelected()) {
      if(this.editorInstance.layoutClose(getCurrentLayout()) == false) {
        break;
      }
    }
  }

  /**
   * @param currentLayout
   */
  public void refreshTitle(Layout layout) {
    OpenedSBMLDocument doc = (OpenedSBMLDocument) layout.getModel().getSBMLDocument().getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);

    for(Layout l : doc.getListOfLayouts()) {
      if(isLayoutOpen(l)) {
        TabComponent component = (TabComponent) getTabComponentAt(this.listOfLayouts.indexOf(l));
        String title = doc.getAssociatedFilename()+": "+ l.getName();
        if(doc.isFileModified()) {
          title = "*"+title;
        }
        component.setTitle(title);
      }
    }
  }

  /**
   * @param l
   * @return
   */
  public boolean isLayoutOpen(Layout layout) {
    return this.listOfLayouts.contains(layout);
  }  
  
  public boolean isAnySelected() {
    return getSelectedIndex() != -1;
  }
  
  public boolean isAnyOpenFromDocument(Layout layout) {
    OpenedSBMLDocument doc = (OpenedSBMLDocument) layout.getSBMLDocument()
        .getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);
    ListOf<Layout> list = doc.getListOfLayouts();
    
    boolean anyopen = false;
    
    for (Layout l : list) {
      if (l.getId() != layout.getId()) {
        anyopen |= isLayoutOpen(l);
      }
    }
    return anyopen;
  }

  /**
   * @param currentLayout
   * @param layout
   */
  public void changeTab(Layout oldLayout, Layout newLayout) {
    int index = listOfLayouts.indexOf(oldLayout);
    
    listOfLayouts.set(index, newLayout);

    GraphLayoutPanel panel = createPanelFromLayout(newLayout);

    setComponentAt(index, panel);
    setSelectedComponent(panel);
    setTabComponentAt(getSelectedIndex(), new TabComponent(this));
    refreshTitle(newLayout);
    showTab(newLayout);

  }
  
  public GraphLayoutPanel createPanelFromLayout (Layout layout) { 
    SBMLEditMode editMode = new SBMLEditMode(this.editorInstance.getController());
    GraphLayoutPanel panel = new GraphLayoutPanel(layout, editMode);
    Graph2DView view = panel.getGraph2DView();
    
    //FIXME Not sure if necessery
    //view.removeViewMode((ViewMode) view.getViewModes().next());
    //ViewMode viewMode = (ViewMode) view.getViewModes().next();
 
    view.addViewMode(editMode);
    
    
    layout.addTreeNodeChangeListener(new ControllerViewSynchronizer(this, panel, layout, editMode));
    return panel;
  }
}
