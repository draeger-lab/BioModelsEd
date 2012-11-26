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
package de.zbit.editor.gui;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import de.zbit.gui.GUITools;
import de.zbit.gui.BaseFrame.BaseAction;

/**
 * Offers static methods for several GUI actions.
 * 
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Eugen Netz
 * @author Jan Rudolph
 * @version $Rev$
 */
public class BioModelsEdGUITools {
    
  /**
   * set components enabled recursively
   * @param container
   * @param enable
   */
  public static void setEnabled(Container container, boolean enable) {
		Component[] components = container.getComponents();
		for (Component component : components) {
			component.setEnabled(enable);
			if (component instanceof Container) {
				setEnabled((Container)component, enable);
			}
		}
	}

	/**
	 * @param b
	 * @param menuBar
	 * @param toolBar
	 */
	public static void setGuiFileChanged(boolean b, JMenuBar menuBar,
		JToolBar toolBar) {
		GUITools.setEnabled(b, menuBar, toolBar, 
			BaseAction.FILE_SAVE,
			BaseAction.FILE_SAVE_AS,
			BaseAction.FILE_CLOSE);
	}

	/**
	 * @param b
	 * @param menuBar
	 * @param toolBar
	 */
	public static void setGuiStart(boolean b, JMenuBar menuBar, JToolBar toolBar) {
		BioModelsEdGUITools.setEnabled(toolBar, !b);
		GUITools.setEnabled(!b, menuBar, toolBar,
			BaseAction.FILE_SAVE,
			BaseAction.FILE_SAVE_AS,
			BaseAction.FILE_CLOSE,
			Command.CLONE_LAYOUT,
			Command.DELETE_LAYOUT,
			Command.NEW_LAYOUT,
			Command.RENAME_LAYOUT,
			Command.AUTOMATIC_LAYOUT);

	}
}
