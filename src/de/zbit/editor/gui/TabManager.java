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
/**
 * @author Jan Rudolph
 * @version $Rev$
 */
/**
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
   * FIXME add comment
   * @param file
   * @param layoutId
   * @return
   */
  public boolean addTab(OpenedFile<SBMLDocument> file, String layoutId) {
    if (file != null) {
    	if (openedFiles.contains(file)) {
    		BioModelsEdPanel panel = isOpen(file, layoutId);
    		if (panel != null) {
    			switchTo(panel);
    		}
    	}
    	else {
    		openedFiles.add(file);
    	}
    }
    Layout layout = (layoutId != null) ?
    		SBMLTools.getLayout(file, layoutId) : SBMLTools.getOrCreateDefaultLayout(file);
    String title = createTitle(file, layout);
		BioModelsEdPanel panel = createPanelFromLayout(layout, file);
		super.addTab(title, panel);
		setSelectedComponent(panel);
    return true;
  }
  

  /**
   * all tab changes need to be processed by switchTo for GUI elements
   * to be enabled/disabled and titles to be updated
   * @param panel
   */
  private void switchTo(BioModelsEdPanel panel) {
  	if (panel == null) {
  		BioModelsEdGUITools.setGuiStart(true, menuBar, toolBar);
  	}
  	else {
  		BioModelsEdGUITools.setGuiStart(false, menuBar, toolBar);
  		
  		OpenedFile<SBMLDocument> file = panel.getFile();
  		if ((file == null) || file.isChanged()) {
  			BioModelsEdGUITools.setGuiFileChanged(true, menuBar, toolBar);
  		}
  		setSelectedComponent(panel);
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
	private BioModelsEdPanel isOpen(OpenedFile<SBMLDocument> file, String layoutId) {
		Layout layout = SBMLTools.getLayout(file, layoutId);
		Component[] components = getComponents();
		for (int i = 0; i < getComponentCount(); i++) {
			if (components[i] instanceof BioModelsEdPanel) {
				BioModelsEdPanel panel = (BioModelsEdPanel) components[i];
				if (panel.getDocument().equals(layout)) {
					return panel;
				}
			}
		}
		return null;
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
    boolean autoLayout = false; //TODO read from options 
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
		// TODO improve by changing to next component, not first
		switchTo((BioModelsEdPanel) this.getComponent(0));
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
					int index = getTabIndexByComponent(component);
					String title = createTitle(file, panel.getDocument());
					setTitleAt(index, title);
				}
			}
		}
	}


	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent arg0) {
		logger.info("state changed");
		// invoked when tabs are removed by mouseClicked in super.addCloseIconToTabComponentAt
		if (getTabCount() > 0) {
			switchTo((BioModelsEdPanel) getSelectedComponent());
		}
		else {
			switchTo(null);
		}
	}
}
