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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.ext.layout.Layout;

import y.layout.organic.OrganicLayouter;
import y.view.Graph2DView;
import de.zbit.editor.Constants;
import de.zbit.editor.control.SBMLTools;
import de.zbit.editor.control.SBMLView;
import de.zbit.gui.BaseFrame.BaseAction;
import de.zbit.gui.GUITools;
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
public class TabManager extends JTabbedPaneDraggableAndCloseable implements ChangeListener, ActionListener, PropertyChangeListener{

  private static final long serialVersionUID = -905908829761611472L;
  private static Logger logger = Logger.getLogger(TabManager.class.getName());
  private SBMLView view;
  private List<OpenedFile<SBMLDocument>> openedFiles;
  private JMenuBar menuBar;
  private JToolBar toolBar;
  private static final ResourceBundle MESSAGES = ResourceManager.getBundle("de.zbit.editor.locales.Messages");

  /**
   * Constructor.
   * menuBar and toolBar need to be set seperately!
   * @param view
   */
  public TabManager(SBMLView view) {
    this.view = view;
    openedFiles = new LinkedList<OpenedFile<SBMLDocument>>();
    this.addChangeListener(this);
  }

  /**
	 * @param menuBar the menuBar to set
	 */
	public void setMenuBar(JMenuBar menuBar) {
		this.menuBar = menuBar;
	}

	/**
	 * @param toolBar the toolBar to set
	 */
	public void setToolBar(JToolBar toolBar) {
		this.toolBar = toolBar;
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
  public boolean addTab(OpenedFile<SBMLDocument> file, String layoutId) {
    if (openedFiles.isEmpty()) {
    	// FIXME use GUITools.setEnabled (squeezer)
    	BioModelsEdGUITools.setEnabled(toolBar,true);
    }
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
    Layout layout = (layoutId != null) ?
    		SBMLTools.getLayout(file, layoutId) : SBMLTools.getOrCreateDefaultLayout(file);
    BioModelsEdPanel panel = createPanelFromLayout(layout, file);
    String title = createTitle(file, layout);
    addTab(title, panel);
    switchTo(panel);
    return true;
  }
  

  /**
   * all tab changes need to be processed by switchTo for GUI elements
   * to be enabled/disabled and titles to be updated
   * @param panel
   */
  private void switchTo(BioModelsEdPanel panel) {
  	if (panel == null) {
  		logger.info("nothing to show");
  		BioModelsEdGUITools.setEnabled(toolBar, false);
  	}
  	else {
  		setSelectedComponent(panel);
  		OpenedFile<SBMLDocument> file = panel.getFile();
  		if ((file == null) || file.isChanged()) {
  			panel.setName(createTitle(file, panel.getDocument()));
  			GUITools.setEnabled(true, menuBar, toolBar, 
  				BaseAction.FILE_SAVE_AS,
  				BaseAction.FILE_CLOSE);
  			if (file.isChanged()) {
  				GUITools.setEnabled(true, menuBar, toolBar,
  					BaseAction.FILE_SAVE);
  			}
  		}
  	}
  }
  /**
	 * @param file
	 * @param layout
	 * @return
	 */
	private String createTitle(OpenedFile<SBMLDocument> file, Layout layout) {
		String title = "";
		title += file.isChanged() ? "*" : "";
		title += file.isSetFile() ? file.getFile().getName() : Constants.genericFileName; 
		title += ":" + (layout.isSetName() ? layout.getName() : layout.getId());
		logger.info("new tab title: " + title);
		return title;
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
			if (components[i] instanceof BioModelsEdPanel) {
				BioModelsEdPanel panel = (BioModelsEdPanel) components[i];
				if (panel.getDocument().equals(layout)) { return i; }
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
    BioModelsEdPanel selected = (BioModelsEdPanel) getSelectedComponent();
    OpenedFile<SBMLDocument> openedFile = selected.getFile();
    openedFiles.clear();
    openedFiles.add(openedFile);
    setComponentAt(0, selected);
    for (int i = 1; i < this.getComponentCount(); i ++) {
    	removeTabAt(i);
    }
    return true;
  }
	
	/**
	 * @return
	 */
	public OpenedFile<SBMLDocument> getCurrentFile() {
		return ((BioModelsEdPanel) getSelectedComponent()).getFile();
	}

	/**
	 * @return
	 */
	public Layout getCurrentLayout() {
		return ((BioModelsEdPanel) getSelectedComponent()).getDocument();
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
  public BioModelsEdPanel createPanelFromLayout (Layout layout, OpenedFile<SBMLDocument> file) { 
    SBMLEditMode editMode = new SBMLEditMode(this);
    BioModelsEdPanel panel = new BioModelsEdPanel(layout, editMode, view.getController(), file);
    Graph2DView view = panel.getGraph2DView();
    boolean autoLayout = true; //TODO read from options 
    if (autoLayout) {
      view.applyLayout(new OrganicLayouter());
    }
 
    view.addViewMode(editMode);
        
    layout.addTreeNodeChangeListener(new ControllerViewSynchronizer(panel, layout, editMode));
    return panel;
  }

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		BioModelsEdPanel panel = (BioModelsEdPanel) this.getSelectedComponent();
		panel.receive(e);
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		BioModelsEdPanel panel = (BioModelsEdPanel) this.getSelectedComponent();
		if(panel != null) {
			panel.receive(evt);
		}
	}

	/**
	 * closes all tabs associated with file
	 * @param file
	 */
	public void closeFile(OpenedFile<SBMLDocument> file) {
		logger.info("closing " + file.getFile().getName());
		for (Component component : this.getComponents()) {
			if (component instanceof BioModelsEdPanel) {
				BioModelsEdPanel panel = (BioModelsEdPanel) component;
				if (panel.getFile().equals(file)) {
					this.remove(panel);
				}
			}
		}
//		if (this.getComponentCount() > 0) {
//			switchTo((BioModelsEdPanel) this.getComponent(0));
//		}
//		else {
//			
//		}
	}

	/**
	 * updated tab titles, invoked after saving
	 * @param file
	 */
	public void updateTitle(OpenedFile<SBMLDocument> file) {
		for (Component component : this.getComponents()) {
			if (component instanceof BioModelsEdPanel) {
				BioModelsEdPanel panel = (BioModelsEdPanel) component;
				if (panel.getFile().equals(file)) {
					panel.setName(createTitle(file, panel.getDocument()));
				}
			}
		}
	}


	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent arg0) {
		// invoked when tabs are removed by mouseClicked in super.addCloseIconToTabComponentAt
		if (getTabCount() > 0) {
			switchTo((BioModelsEdPanel) getSelectedComponent());
		}
		else {
			switchTo(null);
		}
	}
}
