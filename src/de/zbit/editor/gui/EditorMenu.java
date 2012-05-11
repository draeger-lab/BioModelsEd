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

import java.beans.EventHandler;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import java.awt.event.ActionListener;

/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class EditorMenu extends JMenuBar {
  /**
   * 
   */
  private static final long serialVersionUID = -3245574503778953826L;

  public EditorMenu(CommandController commandController) {
    JMenu menuFile = GUIFactory.createMenu(this, "File");
    GUIFactory.createMenuItem(menuFile, "New", "ctrl N",
        EventHandler.create(ActionListener.class, commandController, "fileNew"));
    menuFile.addSeparator();
    GUIFactory.createMenuItem(menuFile, "Open", "ctrl O");
    GUIFactory.createMenuItem(menuFile, "Close", "ctrl W");
    menuFile.addSeparator();
    GUIFactory.createMenuItem(menuFile, "Save", "ctrl S");
    GUIFactory.createMenuItem(menuFile, "Save as...");
    GUIFactory.createMenuItem(menuFile, "Export");
    menuFile.addSeparator();
    GUIFactory.createMenuItem(menuFile, "Quit", "ctrl Q");

    JMenu menuEdit = GUIFactory.createMenu(this, "Edit");
    GUIFactory.createMenuItem(menuEdit, "Undo", "ctrl Z");
    GUIFactory.createMenuItem(menuEdit, "Redo", "ctrl Y");
    GUIFactory.createMenuItem(menuEdit, "Delete", "DELETE");
    GUIFactory.createMenuItem(menuEdit, "Copy", "ctrl C");
    GUIFactory.createMenuItem(menuEdit, "Cut", "ctrl X");
    GUIFactory.createMenuItem(menuEdit, "Paste", "ctrl V");

    JMenu menuLayout = GUIFactory.createMenu(this, "Layout");
    GUIFactory.createMenuItem(menuLayout, "New", "alt N");
    GUIFactory.createMenuItem(menuLayout, "Clone", "alt C");
    GUIFactory.createMenuItem(menuLayout, "Delete", "alt D");

    JMenu menuHelp = GUIFactory.createMenu(this, "Help");
    GUIFactory.createMenuItem(menuHelp, "About");
  }

}
