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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;

/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class EditorToolbar extends JToolBar {

  /**
   * 
   */
  private static final long serialVersionUID = 6956292884109141097L;
  private ToolbarListener toolbarListener = new ToolbarListener();

  public EditorToolbar(CommandController commandController) {
    addButton("Unspecified");
    addButton("Simple Chemical");
    addButton("Macromolecule");
    addButton("Sink");
    addSeparator();
    addButton("Reaction");
    addButton("Catalysis");
    addButton("Inhibition");
    addSeparator();

    String[] layoutArray = { "A", "B", "C" };
    JComboBox layoutComboBox = new JComboBox(layoutArray);
    layoutComboBox.setSelectedIndex(0);
    layoutComboBox.addActionListener(toolbarListener);
    add(layoutComboBox);
    addButton("open");
    addButton("open in new tab");
  }

  private void addButton(String name) {
    JButton button = new JButton(name);
    button.addActionListener(toolbarListener);
    add(button);
  }
}
