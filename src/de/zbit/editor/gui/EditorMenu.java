/*
 * $Id:  EditorMenu.java 22:02:21 jakob $
 * $URL: EditorMenu.java $
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

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class EditorMenu extends JMenuBar  {

	public EditorMenu() {
		JMenu menuFile = new JMenu("File");
		add(menuFile);
		menuFile.add(new JMenuItem("Quit"));
		

		JMenu menuEdit = new JMenu("Edit");
		add(menuEdit);
		
		JMenu menuLayout = new JMenu("Layout");
		add(menuLayout);
		
		JMenu menuHelp = new JMenu("Help");
		add(menuHelp);
	}
	
}
