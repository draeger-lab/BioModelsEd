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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.ReactionGlyph;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.util.ValuePair;

import y.base.Node;
import y.layout.organic.OrganicLayouter;
import y.view.Graph2DView;
import de.zbit.editor.BioModelsEdConstants;
import de.zbit.editor.control.SBMLTools;
import de.zbit.editor.control.SBMLView;
import de.zbit.gui.JTabbedPaneDraggableAndCloseable;
import de.zbit.io.OpenedFile;
import de.zbit.util.ResourceManager;

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
  private static Logger logger = Logger.getLogger(TabManager.class.getName());
  private SBMLView view;
  private Map<OpenedFile<SBMLDocument>, List<String>> openedLayouts;
  private Map<String, Layout> layoutTitles;
  
  private static final ResourceBundle MESSAGES = ResourceManager.getBundle("de.zbit.locales.Messages");

  /**
   * Constructor.
   * @param view
   */
  public TabManager(SBMLView view) {
    this.view = view;
    openedLayouts = new HashMap<OpenedFile<SBMLDocument>, List<String>>();
    layoutTitles = new HashMap<String, Layout>();
  }

  /**
   * @return the view
   */
  public SBMLView getView() {
    return view;
  }
  
  /**
   * Adds a tab for the given layout and runs the autoLayout algorithm on it, when autoLayout is true.
   * @param layout
   * @param autolayout
   * return true if successful
   */
  public boolean addTab(OpenedFile<SBMLDocument> file, String layoutId, boolean autoLayout) {
    List<String> layouts = openedLayouts.get(file);
    String title = generateTitle(file, layoutId);
    if (layouts == null) {
    	// initialize
    	layouts = new LinkedList<String>();
    	openedLayouts.put(file, layouts);
    }
    if (layouts.contains(layoutId)) {
    	logger.info("Layout already opened, switching to right tab");
    	showTab(title);
    	return false;
    }
    layouts.add(layoutId);
    
    Layout layout = SBMLTools.getLayout(file, layoutId);
    layoutTitles.put(title, layout);
		GraphLayoutPanel panel = createPanelFromLayout(layout, autoLayout);
    addTab(title, panel);
    refreshTitle(title);
    showTab(title);
    view.setControlsOn(true);
    return true;
  }
  
  /**
	 * @param title
	 */
	private void showTab(String title) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param title
	 */
	private void refreshTitle(String title) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param file
	 * @param layoutId
	 * @return
	 */
	private String generateTitle(OpenedFile<SBMLDocument> file, String layoutId) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param file
	 * @param layoutId
	 */
	private void switchTo(OpenedFile<SBMLDocument> file, String layoutId) {
		// TODO Auto-generated method stub
		
	}
  

  /**
   * Closes all tabs.
   */
  public boolean closeAllTabsExceptOne() {
    String message = MESSAGES.getString("SAVE_BEFORE_CLOSE");
		String title = MESSAGES.getString("SAVE_BEFORE_CLOSE_TITLE");
		int choice = JOptionPane.showConfirmDialog(this, message, title, 
    	JOptionPane.YES_NO_CANCEL_OPTION,
    	JOptionPane.QUESTION_MESSAGE);
    if (choice == JOptionPane.CANCEL_OPTION) {
    	logger.info("closing tabs canceled");
    	return false;
    }
    else if (choice == JOptionPane.NO_OPTION) {
    	logger.info("closing tabs without saving");
    }
    else if (choice == JOptionPane.YES_OPTION) {
    	logger.info("saving befor closing tabs");
    	view.getController().fileSaveAll();
    }
    int selectedIndex = getSelectedIndex();
    setComponentAt(0, getComponent(selectedIndex));
    
    return true;
  }

  /**
   * Refreshes the title of the tab, that shows the given layout.
   * @param layout
   */
	public void refreshTitle(Layout layout) {
    // TODO
  }
  

  /**
   * Checks whether the given layout is open in any tab.
   * @param layout
   * @return true if it is shown
   */
  public boolean isLayoutOpen(String title) {
    return indexOfTab(title) != -1;
  }  
  
  /**
   * Checks whether any tab is selected.
   * @return true if one is selected
   */
  public boolean isAnySelected() {
    return getSelectedIndex() != -1;
  }
  
  
  /**
   * Creates a panel, that shows the given layout and runs the autoLayout algorithm on it, if autoLayout is true.
   * @param layout
   * @param autoLayout
   * @return the created panel
   */
  public GraphLayoutPanel createPanelFromLayout (Layout layout, boolean autoLayout) { 
    SBMLEditMode editMode = new SBMLEditMode(this.view.getController());
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
 /* public boolean layoutAuto(Layout layout) {
    GraphLayoutPanel panel = getPanelFromLayout(layout);
    Graph2DView view = panel.getGraph2DView();
    view.applyLayout(new OrganicLayouter());
    view.updateView();
    
    for(SpeciesGlyph glyph : layout.getListOfSpeciesGlyphs()) {
      Node node = (Node) glyph.getUserObject(SBMLEditorConstants.GLYPH_NODE_KEY);
      this.view.getController().updateGlyphFromNode(node, view.getGraph2D());
    }
    for(ReactionGlyph glyph : layout.getListOfReactionGlyphs()) {
      Node node = (Node) glyph.getUserObject(SBMLEditorConstants.GLYPH_NODE_KEY);
      this.view.getController().updateGlyphFromNode(node, view.getGraph2D());
    }
    
    return true;
  }*/
  
  /**
   * return all opened files
   */
  public Set<OpenedFile<SBMLDocument>> getOpenedFiles() {
  	return openedLayouts.keySet();
  }
}
