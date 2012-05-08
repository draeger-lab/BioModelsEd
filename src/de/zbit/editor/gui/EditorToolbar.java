/*
 * $Id:  EditorToolbar.java 22:02:21 jakob $
 * $URL: EditorToolbar.java $
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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;

/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class EditorToolbar extends JToolBar {

	public EditorToolbar() {
		addButton("Unspecified");
	    addButton("Simple Chemical");	    
	    addButton("Macromolecule");
	    addButton("Sink");
	    addSeparator();
	    addButton("Reaction");
	    addButton("Catalysis");
	    addButton("Inhibition");
	    addSeparator();
	    
	    String[] layoutArray = {"A", "B","C"};
	    JComboBox layoutComboBox = new JComboBox(layoutArray);
	    layoutComboBox.setSelectedIndex(0);
	    //layoutComboBox.addActionListener(l);
	    add(layoutComboBox);
	    addButton("open");
	    addButton("open in new tab");
	}

	private void addButton(String name) {
		JButton button = new JButton(name);
		add(button);
	    //buttonSmallMolecule.setActionCommand(...);
	    //buttonSmallMolecule.addActionListener(...);
	}
}
