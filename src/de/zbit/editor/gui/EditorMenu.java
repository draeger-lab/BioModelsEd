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
    JMenu menuFile = GUIFactory.createMenu(this, Resources.getString("MENU_FILE"));
    GUIFactory.createMenuItem(menuFile, Resources.getString("MENU_FILE_NEW"), "ctrl N",
        EventHandler.create(ActionListener.class, commandController, "fileNew"));
    menuFile.addSeparator();
    GUIFactory.createMenuItem(menuFile, Resources.getString("MENU_FILE_OPEN"), "ctrl O",
    	EventHandler.create(ActionListener.class, commandController, "fileOpen"));
    GUIFactory.createMenuItem(menuFile,Resources.getString("MENU_FILE_CLOSE"), "ctrl W");
    menuFile.addSeparator();
    GUIFactory.createMenuItem(menuFile, Resources.getString("MENU_FILE_SAVE"), "ctrl S",
    	EventHandler.create(ActionListener.class, commandController, "fileSave"));
    GUIFactory.createMenuItem(menuFile, Resources.getString("MENU_FILE_SAVEAS"));
    GUIFactory.createMenuItem(menuFile, Resources.getString("MENU_FILE_EXPORT"));
    menuFile.addSeparator();
    GUIFactory.createMenuItem(menuFile, Resources.getString("MENU_FILE_QUIT"), "ctrl Q");

    JMenu menuEdit = GUIFactory.createMenu(this, Resources.getString("MENU_EDIT"));
    GUIFactory.createMenuItem(menuEdit, Resources.getString("MENU_EDIT_UNDO"), "ctrl Z");
    GUIFactory.createMenuItem(menuEdit, Resources.getString("MENU_EDIT_REDO"), "ctrl Y");
    GUIFactory.createMenuItem(menuEdit, Resources.getString("MENU_EDIT_DELETE"), "DELETE");
    GUIFactory.createMenuItem(menuEdit, Resources.getString("MENU_EDIT_COPY"), "ctrl C");
    GUIFactory.createMenuItem(menuEdit, Resources.getString("MENU_EDIT_CUT"), "ctrl X");
    GUIFactory.createMenuItem(menuEdit, Resources.getString("MENU_EDIT_PASTE"), "ctrl V");

    JMenu menuLayout = GUIFactory.createMenu(this, Resources.getString("MENU_LAYOUT"));
    GUIFactory.createMenuItem(menuLayout,  Resources.getString("MENU_LAYOUT_NEW"), "alt N");
    GUIFactory.createMenuItem(menuLayout, Resources.getString("MENU_LAYOUT_CLONE"), "alt C");
    GUIFactory.createMenuItem(menuLayout, Resources.getString("MENU_LAYOUT_DELETE"), "alt D");

    JMenu menuHelp = GUIFactory.createMenu(this, Resources.getString("MENU_HELP"));
    GUIFactory.createMenuItem(menuHelp, Resources.getString("MENU_HELP_ABOUT"));
  }

}
