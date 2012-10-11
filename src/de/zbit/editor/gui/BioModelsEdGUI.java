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
import java.awt.HeadlessException;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.ext.layout.Layout;

import de.zbit.AppConf;
import de.zbit.editor.control.CommandController;
import de.zbit.editor.control.SBMLView;
import de.zbit.gui.BaseFrame;
import de.zbit.gui.BaseFrameTab;
import de.zbit.gui.GUIOptions;
import de.zbit.gui.GUITools;
import de.zbit.io.filefilter.SBFileFilter;
import de.zbit.util.prefs.SBPreferences;

/**
 * @author Jan Rudoplph
 * @version $Rev$
 */
public class BioModelsEdGUI extends BaseFrame implements SBMLView {
	
	private TabManager tabManager;
	private CommandController controller;
	private final static Logger logger = Logger.getLogger(BioModelsEdGUI.class.getName());
	
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
		EditorToolbar toolbar = new EditorToolbar(this);
		return toolbar;
	}

	@Override
	protected Component createMainComponent() {
		logger.info("creating main component");
		tabManager = new TabManager(this);
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
	public boolean addLayout(Layout layout, boolean autoLayout) {
		return tabManager.addTab(layout, autoLayout);
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
	public File askUserOpenDialog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File askUserSaveDialog() {
		// TODO Auto-generated method stub
		return null;
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
	public String askUserFileNew() {
		// TODO Auto-generated method stub
		return null;
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
	public void updateComboBox(ListOf<Layout> list) {
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
	public void setEnableState(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		
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
}
