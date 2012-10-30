/*
 * $Id$
 * $URL$
 * ---------------------------------------------------------------------
 * This file is part of BioModelsEd.
 *
 * Copyright (C) 20012-2012 by the University of Tuebingen, Germany.
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
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.ext.layout.Layout;

import de.zbit.editor.control.CommandController;
import de.zbit.editor.control.SBMLView;
import de.zbit.gui.BaseFrame;
import de.zbit.gui.GUIOptions;
import de.zbit.gui.GUITools;
import de.zbit.gui.actioncommand.ActionCommand;
import de.zbit.io.OpenedFile;
import de.zbit.io.filefilter.SBFileFilter;
import de.zbit.util.ResourceManager;
import de.zbit.util.prefs.SBPreferences;

/**
 * @author Jan Rudoplph
 * @version $Rev$
 */
public class BioModelsEdGUI extends BaseFrame implements SBMLView, ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8556637237458666164L;
	private TabManager tabManager;
	private CommandController controller;
	private JToolBar toolBar;
	private final static Logger logger = Logger.getLogger(BioModelsEdGUI.class.getName());
	//TODO rename package
	private final static ResourceBundle MESSAGES = ResourceManager.getBundle("de.zbit.locales.Messages");
	
	/**
	 * Constructor
	 */
	public BioModelsEdGUI() {
		super();
	}

	@Override
	public boolean closeFile() {
		logger.info("closeFile");
		return controller.fileClose();
	}

	@Override
	protected JToolBar createJToolBar() {
		logger.info("creating toolbar");
		tabManager = new TabManager(this);
		toolBar = new EditorToolBar(this, tabManager);
		return toolBar;
	}

	@Override
	protected Component createMainComponent() {
		logger.info("creating main component");
		//FIXME creating Tabmanager before Toolbar
		return tabManager;
	}

	@Override
	public URL getURLAboutMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getURLLicense() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getURLOnlineHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected File[] openFile(File... files) {
		SBPreferences prefs = SBPreferences.getPreferencesFor(GUIOptions.class);
		if ((files == null) || (files.length == 0)) {
			files = GUITools.openFileDialog(
				this,
				prefs.get(GUIOptions.OPEN_DIR),
				false,
				true,
				JFileChooser.FILES_ONLY, 
				SBFileFilter.createSBMLFileFilter()
			);
		}
		logger.info("openFile" + Arrays.toString(files));
		return controller.openFile(files);
	}

	@Override
	public File saveFile() {
		return null;
		//return controller.saveFile();
	}

	@Override
	public Layout getCurrentLayout() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String nameDialogue(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TabManager getTabManager() {
		return this.tabManager;
	}


	@Override
	public File askUserSaveDialog() {
		return GUITools.saveFileDialog(this);
	}

	@Override
	public int askUserCreateLayoutInformation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Component getFrame() {
		logger.info("getFrame");
		return this.tabManager;
	}

	@Override
	public File askUserFileNew() {
		return GUITools.saveFileDialog(this);
	}

	@Override
	public void showWarning(String warning) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showError(String error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean closeTab(Layout layout) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refreshTitle(Layout layout) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateComboBox(List<Layout> list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void helpAbout() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void layoutClone() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void layoutDelete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean layoutClose(Layout layout) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean layoutNew() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean layoutRename() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean layoutAuto() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void openLayoutInTab() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openLayoutInNewTab() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CommandController getController() {
		return this.controller;
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		logger.info("Property changed: " + evt.getPropertyName());
	} 

	@Override
	public File saveFileAs() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param controller
	 */
	public void setController(CommandController controller) {
		this.controller = controller;
	}

	/* (non-Javadoc)
	 * @see de.zbit.editor.control.SBMLView#addTab(de.zbit.io.OpenedFile, java.lang.String, boolean)
	 */
	@Override
	public boolean addTab(OpenedFile<SBMLDocument> file, String layoutId,
		boolean autoLayout) {
		return tabManager.addTab(file, layoutId, autoLayout);
	}

	/* (non-Javadoc)
	 * @see de.zbit.gui.BaseFrame#additionalEditMenuItems()
	 */
	@Override
	protected JMenuItem[] additionalEditMenuItems() {
		return new JMenuItem[] {
				GUITools.createJMenuItem(this, Command.UNDO),
				GUITools.createJMenuItem(this, Command.REDO),
				GUITools.createJMenuItem(this, Command.COPY),
				GUITools.createJMenuItem(this, Command.CUT),
				GUITools.createJMenuItem(this, Command.PASTE),
				GUITools.createJMenuItem(this, Command.DELETE),
				GUITools.createJMenuItem(this, Command.SELECT_ALL),
		};
	}

	/* (non-Javadoc)
	 * @see de.zbit.gui.BaseFrame#additionalFileMenuItems()
	 */
	@Override
	protected JMenuItem[] additionalFileMenuItems() {
		return new JMenuItem[] {
				GUITools.createJMenuItem(this, Command.NEW),
				GUITools.createJMenuItem(this, Command.CLOSE),
		};
	}

	/* (non-Javadoc)
	 * @see de.zbit.gui.BaseFrame#additionalMenus()
	 */
	@Override
	protected JMenu[] additionalMenus() {
		return new JMenu[] {
			GUITools.createJMenu(
				MESSAGES.getString("LAYOUT_MENU"), 
				MESSAGES.getString("LAYOUT_MENU_TOOLTIP"), 
				GUITools.createJMenuItem(this, Command.NEW_LAYOUT),
				GUITools.createJMenuItem(this, Command.RENAME_LAYOUT),
				GUITools.createJMenuItem(this, Command.DELETE_LAYOUT),
				GUITools.createJMenuItem(this, Command.CLONE_LAYOUT),
				GUITools.createJMenuItem(this, Command.AUTOMATIC_LAYOUT))
		};
	}
	

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		logger.info("action performed: " + evt.getActionCommand());
		switch (Command.valueOf(evt.getActionCommand())) {
			case NEW :
				controller.fileNew(); 
				break;
			case CLOSE :
				controller.fileClose();
				break;
		}
	}

	/* (non-Javadoc)
	 * @see de.zbit.editor.control.SBMLView#getToolBar()
	 */
	@Override
	public JToolBar getToolBar() {
		return toolBar;
	}
	
	public void addUnknownMolecule() {
		controller.stateUnknownMolecule();
	}
}
