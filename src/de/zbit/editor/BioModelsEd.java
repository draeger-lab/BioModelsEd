/*
 * $Id$
 * $URL$
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
package de.zbit.editor;

import java.awt.Window;
import java.net.URL;
import java.util.List;

import de.zbit.AppConf;
import de.zbit.Launcher;
import de.zbit.util.prefs.KeyProvider;

/**
 * @author Andreas Dr&auml;ger
 * @version $Rev$
 */
public class BioModelsEd extends Launcher {

	/**
	 * Generated serial version identifier.
	 */
	private static final long serialVersionUID = -6865051670739523143L;

	/* (non-Javadoc)
	 * @see de.zbit.Launcher#commandLineMode(de.zbit.AppConf)
	 */
	@Override
	public void commandLineMode(AppConf appConf) {
	}

	/* (non-Javadoc)
	 * @see de.zbit.Launcher#getCmdLineOptions()
	 */
	@Override
	public List<Class<? extends KeyProvider>> getCmdLineOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.zbit.Launcher#getInteractiveOptions()
	 */
	@Override
	public List<Class<? extends KeyProvider>> getInteractiveOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.zbit.Launcher#getURLlicenseFile()
	 */
	@Override
	public URL getURLlicenseFile() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.zbit.Launcher#getURLOnlineUpdate()
	 */
	@Override
	public URL getURLOnlineUpdate() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.zbit.Launcher#getVersionNumber()
	 */
	@Override
	public String getVersionNumber() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.zbit.Launcher#getYearOfProgramRelease()
	 */
	@Override
	public short getYearOfProgramRelease() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see de.zbit.Launcher#getYearWhenProjectWasStarted()
	 */
	@Override
	public short getYearWhenProjectWasStarted() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see de.zbit.Launcher#initGUI(de.zbit.AppConf)
	 */
	@Override
	public Window initGUI(AppConf appConf) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
