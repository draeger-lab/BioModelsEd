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
 * agreement is provided in the file named LICENSE.txt included with
 * this software distribution and also available online as
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 * ---------------------------------------------------------------------
 */

package de.zbit.editor.gui;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.EventHandler;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import de.zbit.editor.SBMLEditorConstants;
import de.zbit.editor.control.CommandController;
import de.zbit.editor.control.SBMLView;

/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class EditorMenu extends JMenuBar {

  /**
   * 
   */
  private static final long serialVersionUID = -3245574503778953826L;


  public EditorMenu(CommandController commandcontroller, SBMLView parent) {
    JMenu menuFile = GUIFactory.createMenu(this,
      Resources.getString(SBMLEditorConstants.MENU_FILE));
    int ctrl = GUITools.getControlKey();
    GUIFactory.createMenuItem(menuFile, Resources.getString(SBMLEditorConstants.MENU_FILE_NEW),
      ctrl, KeyEvent.VK_N, EventHandler.create(ActionListener.class, parent,
        SBMLEditorConstants.fileNew));
    menuFile.addSeparator();
    GUIFactory.createMenuItem(menuFile, Resources.getString(SBMLEditorConstants.MENU_FILE_OPEN),
      ctrl, KeyEvent.VK_O, EventHandler.create(ActionListener.class, parent,
          SBMLEditorConstants.fileOpen));
    GUIFactory.createMenuItem(menuFile, Resources.getString(SBMLEditorConstants.MENU_FILE_CLOSE),
      ctrl, KeyEvent.VK_W, EventHandler.create(ActionListener.class, parent,
          SBMLEditorConstants.fileClose));
    menuFile.addSeparator();
    GUIFactory.createMenuItem(menuFile, Resources.getString(SBMLEditorConstants.MENU_FILE_SAVE),
      ctrl, KeyEvent.VK_S, EventHandler.create(ActionListener.class, parent,
          SBMLEditorConstants.fileSave));
    GUIFactory.createMenuItem(menuFile,
      Resources.getString(SBMLEditorConstants.MENU_FILE_SAVEAS), EventHandler.create(
        ActionListener.class, parent, SBMLEditorConstants.fileSaveAs));
    GUIFactory.createMenuItem(menuFile,
      Resources.getString(SBMLEditorConstants.MENU_FILE_EXPORT), EventHandler.create(
        ActionListener.class, parent, SBMLEditorConstants.fileExport));
    menuFile.addSeparator();
    GUIFactory.createMenuItem(menuFile, Resources.getString(SBMLEditorConstants.MENU_FILE_QUIT),
      ctrl, KeyEvent.VK_Q, EventHandler.create(ActionListener.class, parent,
          SBMLEditorConstants.fileQuit));
    JMenu menuEdit = GUIFactory.createMenu(this,
      Resources.getString(SBMLEditorConstants.MENU_EDIT));
    GUIFactory.createMenuItem(menuEdit, Resources.getString(SBMLEditorConstants.MENU_EDIT_UNDO),
      ctrl, KeyEvent.VK_Z, EventHandler.create(ActionListener.class,
        parent, SBMLEditorConstants.editUndo));
    GUIFactory.createMenuItem(menuEdit, Resources.getString(SBMLEditorConstants.MENU_EDIT_REDO),
      ctrl, KeyEvent.VK_Y, EventHandler.create(ActionListener.class,
        parent, SBMLEditorConstants.editRedo));
    GUIFactory.createMenuItem(menuEdit,
      Resources.getString(SBMLEditorConstants.MENU_EDIT_DELETE), "DELETE", EventHandler.create(
        ActionListener.class, parent, SBMLEditorConstants.editDelete));
    GUIFactory.createMenuItem(menuEdit, Resources.getString(SBMLEditorConstants.MENU_EDIT_COPY),
      ctrl, KeyEvent.VK_C, EventHandler.create(ActionListener.class,
        parent, SBMLEditorConstants.editCopy));
    GUIFactory.createMenuItem(menuEdit, Resources.getString(SBMLEditorConstants.MENU_EDIT_CUT),
      ctrl, KeyEvent.VK_X, EventHandler.create(ActionListener.class,
        parent, SBMLEditorConstants.editCut));
    GUIFactory.createMenuItem(menuEdit, Resources.getString(SBMLEditorConstants.MENU_EDIT_PASTE),
      ctrl, KeyEvent.VK_V, EventHandler.create(ActionListener.class,
        parent, SBMLEditorConstants.editPaste));
    JMenu menuLayout = GUIFactory.createMenu(this,
      Resources.getString(SBMLEditorConstants.MENU_LAYOUT));
    GUIFactory.createMenuItem(menuLayout,
      Resources.getString(SBMLEditorConstants.MENU_LAYOUT_NEW), SBMLEditorConstants.MENU_LAYOUT_NEW_KEYSTROKE, EventHandler.create(
        ActionListener.class, parent, SBMLEditorConstants.layoutNew));
    GUIFactory.createMenuItem(menuLayout,
      Resources.getString(SBMLEditorConstants.MENU_LAYOUT_CLONE), SBMLEditorConstants.MENU_LAYOUT_CLONE_KEYSTROKE, EventHandler.create(
        ActionListener.class, parent, SBMLEditorConstants.layoutClone));
    GUIFactory.createMenuItem(menuLayout,
      Resources.getString(SBMLEditorConstants.MENU_LAYOUT_DELETE), SBMLEditorConstants.MENU_LAYOUT_DELETE_KEYSTROKE, EventHandler.create(
        ActionListener.class, parent, SBMLEditorConstants.layoutDelete));
    JMenu menuHelp = GUIFactory.createMenu(this,
      Resources.getString(SBMLEditorConstants.MENU_HELP));
    GUIFactory
              .createMenuItem(menuHelp, Resources.getString(SBMLEditorConstants.MENU_HELP_ABOUT),
                EventHandler.create(ActionListener.class, parent,
                    SBMLEditorConstants.helpAbout));
  }
}
