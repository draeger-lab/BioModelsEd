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
import javax.swing.JToolBar;

/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class EditorToolbar extends JToolBar {

	private static final String SMALL_MOLECULE = "Small Molecule";

	public EditorToolbar() {
	    JButton buttonSmallMolecule = new JButton("Small Molecule");
	    buttonSmallMolecule.setActionCommand(SMALL_MOLECULE);
	    //buttonSmallMolecule.addActionListener(...);
		add(buttonSmallMolecule);
	}

}
