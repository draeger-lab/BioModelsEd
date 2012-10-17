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

import java.awt.Component;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.ext.layout.Layout;

import y.layout.organic.OrganicLayouter;
import y.view.Graph2DView;
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
  private List<OpenedFile<SBMLDocument>> openedFiles;
  
  private static final ResourceBundle MESSAGES = ResourceManager.getBundle("de.zbit.locales.Messages");

  /**
   * Constructor.
   * @param view
   */
  public TabManager(SBMLView view) {
    this.view = view;
    openedFiles = new LinkedList<OpenedFile<SBMLDocument>>();
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
    if (openedFiles.contains(file)) {
    	int index = isOpen(file, layoutId);
			if (index != -1) {
    		logger.info("Layout already opened, switching to right tab");
    		setSelectedIndex(index);
    		return false;
    	}
    }
    else {
    	openedFiles.add(file);
    }
    Layout layout = SBMLTools.getLayout(file, layoutId);
    GraphLayoutPanel panel = createPanelFromLayout(layout, autoLayout);
    String title = createTitle(file, layout);
    addTab(title, panel);
    setSelectedComponent(panel);
    view.setControlsOn(true);
    return true;
  }
  
  /**
	 * @param file
	 * @param layout
	 * @return
	 */
	private String createTitle(OpenedFile<SBMLDocument> file, Layout layout) {
		return file.getFile().getName() + ":" 
				+ (layout.isSetName() ? layout.getName() : layout.getId());
	}

	/**
	 * checks if a layout identified by id and the corresponding file
	 * is already shown in some tab
	 * @param file
	 * @param layoutId
	 * @return index where layout is shown or -1 if not opened
	 */
	private int isOpen(OpenedFile<SBMLDocument> file, String layoutId) {
		Layout layout = SBMLTools.getLayout(file, layoutId);
		Component[] components = getComponents();
		for (int i = 0; i < getComponentCount(); i++) {
			GraphLayoutPanel panel = (GraphLayoutPanel) components[i];
			if (panel.getDocument().equals(layout)) {
				return i;
			}
		}
		return -1;
	}
  
  /**
   * Closes all tabs except the focused one
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
    GraphLayoutPanel selected = (GraphLayoutPanel) getSelectedComponent();
    Layout layout = selected.getDocument();
    OpenedFile<SBMLDocument> openedFile = getFile(layout);
    openedFiles.clear();
    openedFiles.add(openedFile);
    setComponentAt(0, selected);
    for (int i = 1; i < this.getComponentCount(); i ++) {
    	removeTabAt(i);
    }
    return true;
  }

  /**
   * retrieves a sbml document from layout and the associated
   * opened file from openedFiles
	 * @param layout
	 * @return
	 */
	private OpenedFile<SBMLDocument> getFile(Layout layout) {
		SBMLDocument sbmlDoc = layout.getSBMLDocument();
		for (OpenedFile<SBMLDocument> file : openedFiles) {
			if (file.getDocument().equals(sbmlDoc)) {
				return file;
			}
		}
		throw new AssertionError("No file for layout " + layout + " opened");
	}
  
  /**
	 * @return the openedFiles
	 */
	public List<OpenedFile<SBMLDocument>> getOpenedFiles() {
		return openedFiles;
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
}
