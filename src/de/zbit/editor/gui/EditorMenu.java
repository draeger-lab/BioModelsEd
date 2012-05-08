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

import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import de.zbit.kegg.io.KEGGtranslator;

/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class EditorMenu extends JMenuBar  {

	public EditorMenu() {
		JMenu menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);
		add(menuFile);
		addItem(menuFile, "New", KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK);
		menuFile.addSeparator();
		addItem(menuFile, "Open", KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK);
		addItem(menuFile, "Close", KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK);
		addItem(menuFile, "Save", KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);
		addItem(menuFile, "Save as...");
		addItem(menuFile, "Export");
		addItem(menuFile, "Quit", KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK);		

		JMenu menuEdit = new JMenu("Edit");
		add(menuEdit);
		addItem(menuEdit, "Undo", KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK);
		addItem(menuEdit, "Redo", KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK);
		addItem(menuEdit, "Delete", KeyEvent.VK_DELETE);
		addItem(menuEdit, "Copy", KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK);
		addItem(menuEdit, "Cut", KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK);
		addItem(menuEdit, "Paste", KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK);		
		
		JMenu menuLayout = new JMenu("Layout");
		add(menuLayout);
		addItem(menuLayout, "New", KeyEvent.VK_N, KeyEvent.ALT_DOWN_MASK);
		addItem(menuLayout, "Clone", KeyEvent.VK_C, KeyEvent.ALT_DOWN_MASK);
		addItem(menuLayout, "Delete", KeyEvent.VK_D, KeyEvent.ALT_DOWN_MASK);
		
		JMenu menuHelp = new JMenu("Help");
		add(menuHelp);
		addItem(menuHelp,"About");
		
	}

	/**
	 * Adding new item to given menu
	 * @param menu
	 * @param label
	 * @param key
	 * @param modifier
	 */
	private void addItem(JMenu menu, String label, int key, int modifier) {
		JMenuItem item = new JMenuItem(label);
		item.setAccelerator(KeyStroke.getKeyStroke(key, modifier));
		menu.add(item);
	}
	
	/**
	 * Adding new item to given menu
	 * @param menu
	 * @param label
	 * @param key
	 */
	private void addItem(JMenu menu, String label, int key) {
		addItem(menu, label, key, 0);
	}
	
	/**
	 * Adding new item to given menu
	 * @param menu
	 * @param label
	 */
	private void addItem(JMenu menu, String label) {
		JMenuItem item = new JMenuItem(label);
		menu.add(item);
	}
	
}
