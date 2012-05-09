/*
 * $Id:  MenuListener.java 13:41:23 jan $
 * $URL: MenuListener.java $
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * @author Jan Rudolph
 * @version $Rev$
 */
public class MenuListener implements ActionListener {

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "Quit") {
			System.exit(0);
		}
		if (e.getActionCommand() == "About") {
			new JOptionPane().showMessageDialog(new JFrame("SBML Editor"), "Universität Tübingen, Softwareprojekt SS 2012\nDiamantikos,Matthes,Netz,Rudolph");
		}
		System.out.println(e);
	}

}
