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
package de.zbit.editor;

import java.awt.Window;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import de.zbit.AppConf;
import de.zbit.Launcher;
import de.zbit.editor.control.CommandController;
import de.zbit.editor.gui.BioModelsEdGUI;
import de.zbit.util.prefs.KeyProvider;


/**
 * Launcher class for BioModelsEd
 * @author Jan Rudoplph
 * @version $Rev$
 */
public class BioModelsEd extends Launcher {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7695361498192546438L;
	BioModelsEdGUI gui;
	CommandController controller;
	
	/**
	 * 
	 */
	public BioModelsEd() {
		super();
		controller = new CommandController();
		gui = new BioModelsEdGUI();
		controller.setView(gui);
		gui.setController(controller);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new BioModelsEd().run();
	}

	@Override
	public void commandLineMode(AppConf appConf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Class<? extends KeyProvider>> getCmdLineOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Class<? extends KeyProvider>> getInteractiveOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getURLlicenseFile() {
		try {
			return new URL("http://www.gnu.org/licenses/gpl-3.0-standalone.html");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public URL getURLOnlineUpdate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVersionNumber() {
		return "0.1";
	}

	@Override
	public short getYearOfProgramRelease() {
		return 2012;
	}

	@Override
	public short getYearWhenProjectWasStarted() {
		return 2012;
	}

	@Override
	public Window initGUI(AppConf appConf) {
		return gui;
	}

}
