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
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JTabbedPane;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.ReactionGlyph;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;

import y.base.Node;
import y.layout.organic.OrganicLayouter;
import y.view.Graph2DView;
import de.zbit.editor.SBMLEditorConstants;
import de.zbit.editor.control.OpenedSBMLDocument;
import de.zbit.editor.control.SBMLView;
import de.zbit.gui.JTabbedPaneDraggableAndCloseable;

/**
 * Manages all the TabComponents and the tab bar.
 * 
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Eugen Netz
 * @author Jan Rudolph
 * @version $Rev$
 */
public class TabManager extends JTabbedPaneDraggableAndCloseable {

  private static final long serialVersionUID = -905908829761611472L;
  private static Logger logger = Logger.getLogger(OpenedSBMLDocument.class.toString());
  private SBMLView editorInstance;
  private List<Layout> listOfLayouts = new ArrayList<Layout>();
  
  /**
   * Constructor.
   * @param editorInstance
   */
  public TabManager(SBMLView editorInstance) {
    this.editorInstance = editorInstance;
  }

  /**
   * @return the editorInstance
   */
  public SBMLView getEditorInstance() {
    return editorInstance;
  }

  /**
   * Adds a tab for the given layout and runs the autoLayout algorithm on it, when autoLayout is true.
   * @param layout
   * @param autolayout
   * return true if successful
   */
  public boolean addTab(Layout layout, boolean autoLayout) {
    if(listOfLayouts.contains(layout)) {
      logger.warning("List contains layout: ID: "+ layout.getId() + " Name: " +layout.getName());
    }
    
    listOfLayouts.add(layout);
    GraphLayoutPanel panel = createPanelFromLayout(layout, autoLayout);
    addTab("tab", panel);
    refreshTitle(layout);
    showTab(layout);
    this.editorInstance.setEnableState(true);
    return true;
  }
  
  /**
   * Closes the tab, that shows the given layout
   * @param layout
   * @return true if succesful
   */
  public boolean closeTab(Layout layout) {
    if (isLayoutOpen(layout)) {
      int index = getIndexFromLayout(layout);
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
   * Gets the layout shown i the currently active tab.
   * 
   * @return the layout
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
   * Sets the active tab to the tab that shows the given layout.
   * 
   * @param layout
   */
  public void showTab(Layout layout) {
    if(layout == null) {
      this.editorInstance.updateComboBox(new ListOf<Layout>());
      this.editorInstance.setEnableState(false);
    }
    else {
      setSelectedIndex(getIndexFromLayout(layout));
      OpenedSBMLDocument doc = (OpenedSBMLDocument) layout.getModel().getSBMLDocument().getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);
      this.editorInstance.updateComboBox(doc.getListOfLayouts());
    }
  }

  /**
   * Closes all tabs.
   */
  public boolean closeAllTabs() {
    while (isAnySelected()) {
      if(this.editorInstance.layoutClose(getCurrentLayout()) == false) {
        return false;
      }
    }
    return true;
  }

  /**
   * Refreshes the title of the tab, that shows the given layout.
   * @param layout
   */
  public void refreshTitle(Layout layout) {
    OpenedSBMLDocument doc = (OpenedSBMLDocument) layout.getModel().getSBMLDocument().getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);

    for(Layout l : doc.getListOfLayouts()) {
      if(isLayoutOpen(l)) {
        int index = getIndexFromLayout(l);
        String title = doc.getAssociatedFilename()+": "+ l.getName();
        if(doc.isFileModified()) {
          title = "*"+title;
        }
        this.setTitleAt(index, title);
        //FIXME setTitleAt for JTBDAC does not work
        logger.info("Tab title changed: Index: " + index +" Name: " + getTitleAt(index));
      }
    }
  }
  

  /**
   * Checks whether the given layout is open in any tab.
   * @param layout
   * @return true if it is shown
   */
  public boolean isLayoutOpen(Layout layout) {
    for(Layout l : this.listOfLayouts) {
      if (layout == l) {
        return true;
      }
    }
    return false;
  }  
  
  /**
   * Checks whether any tab is selected.
   * @return true if one is selected
   */
  public boolean isAnySelected() {
    return getSelectedIndex() != -1;
  }
  
  /**
   * Gets the tab index of the tab, that shows the given layout.
   * @param layout
   * @return the index if the layout is shown, -1 if the layout isn't shown in any tab
   */
  private int getIndexFromLayout(Layout layout) {
    int index = 0;
    for(Layout l : this.listOfLayouts) {
      if (layout == l) {
        return index;
      }
      index+=1;
    }
    return -1;
  }
  
  /**
   * @param layout
   * @return true if any other layout from the same document is open 
   */
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
   * Change the the tab with oldLayout to newLayout
   * @param oldLayout
   * @param newLayout
   */
  public void changeTab(Layout oldLayout, Layout newLayout) {
    int index = getIndexFromLayout(oldLayout);
    
    listOfLayouts.set(index, newLayout);

    GraphLayoutPanel panel = createPanelFromLayout(newLayout, false);

    setComponentAt(index, panel);
    setSelectedComponent(panel);
    setTabComponentAt(getSelectedIndex(), new TabComponent(this));
    refreshTitle(newLayout);
    showTab(newLayout);

  }
  
  /**
   * Creates a panel, that shows the given layout and runs the autoLayout algorithm on it, if autoLayout is true.
   * @param layout
   * @param autoLayout
   * @return the created panel
   */
  public GraphLayoutPanel createPanelFromLayout (Layout layout, boolean autoLayout) { 
    SBMLEditMode editMode = new SBMLEditMode(this.editorInstance.getController());
    GraphLayoutPanel panel = new GraphLayoutPanel(layout, editMode);
    Graph2DView view = panel.getGraph2DView();
    if (autoLayout) {
      view.applyLayout(new OrganicLayouter());
    }
 
    view.addViewMode(editMode);
        
    layout.addTreeNodeChangeListener(new ControllerViewSynchronizer(panel, layout, editMode));
    return panel;
  }

  /**
   * Runs the OrganicLayouter on the given layout.
   * @param layout
   * @return
   */
  public boolean layoutAuto(Layout layout) {
    GraphLayoutPanel panel = getPanelFromLayout(layout);
    Graph2DView view = panel.getGraph2DView();
    view.applyLayout(new OrganicLayouter());
    view.updateView();
    
    for(SpeciesGlyph glyph : layout.getListOfSpeciesGlyphs()) {
      Node node = (Node) glyph.getUserObject(SBMLEditorConstants.GLYPH_NODE_KEY);
      this.editorInstance.getController().updateGlyphFromNode(node, view.getGraph2D());
    }
    for(ReactionGlyph glyph : layout.getListOfReactionGlyphs()) {
      Node node = (Node) glyph.getUserObject(SBMLEditorConstants.GLYPH_NODE_KEY);
      this.editorInstance.getController().updateGlyphFromNode(node, view.getGraph2D());
    }
    
    return true;
  }
  
  /**
   * Gets the panel, that shows the given layout.
   * @param layout
   * @return the panel
   */
  public GraphLayoutPanel getPanelFromLayout(Layout layout) {
    return (GraphLayoutPanel) getComponentAt(getIndexFromLayout(layout));
  }
}
