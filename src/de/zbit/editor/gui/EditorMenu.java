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

import de.zbit.editor.BioModelsEdConstants;
import de.zbit.editor.control.CommandController;
import de.zbit.editor.control.SBMLView;

/**
 * Represents the menu bar.
 * 
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Eugen Netz
 * @author Jan Rudolph
 * @version $Rev$
 */
public class EditorMenu extends JMenuBar {

  /**
   * 
   */
  private static final long serialVersionUID = -3245574503778953826L;
  
  /**
   * Constructor.
   * @param commandcontroller
   * @param parent
   */
  public EditorMenu(CommandController commandcontroller, SBMLView parent) {
    JMenu menuFile = GUIFactory.createMenu(this,
      Resources.getString(BioModelsEdConstants.MENU_FILE));
    int ctrl = GUITools.getControlKey();
    GUIFactory.createMenuItem(menuFile, Resources.getString(BioModelsEdConstants.MENU_FILE_NEW), Resources.iconButtonNew,
      ctrl, KeyEvent.VK_N, EventHandler.create(ActionListener.class, parent,
        BioModelsEdConstants.fileNew));
    menuFile.addSeparator();
    GUIFactory.createMenuItem(menuFile, Resources.getString(BioModelsEdConstants.MENU_FILE_OPEN), Resources.iconButtonOpen,
      ctrl, KeyEvent.VK_O, EventHandler.create(ActionListener.class, parent,
          BioModelsEdConstants.fileOpen));
    GUIFactory.createMenuItem(menuFile, Resources.getString(BioModelsEdConstants.MENU_FILE_CLOSE), Resources.iconButtonDelete,
      ctrl, KeyEvent.VK_W, EventHandler.create(ActionListener.class, parent,
          BioModelsEdConstants.fileClose));
    menuFile.addSeparator();
    GUIFactory.createMenuItem(menuFile, Resources.getString(BioModelsEdConstants.MENU_FILE_SAVE), Resources.iconButtonSave,
      ctrl, KeyEvent.VK_S, EventHandler.create(ActionListener.class, parent,
          BioModelsEdConstants.fileSave));
    GUIFactory.createMenuItem(menuFile, Resources.getString(BioModelsEdConstants.MENU_FILE_SAVEAS), Resources.iconButtonSave,
        EventHandler.create(ActionListener.class, parent, BioModelsEdConstants.fileSaveAs));
    GUIFactory.createMenuItem(menuFile, Resources.getString(BioModelsEdConstants.MENU_FILE_EXPORT), Resources.iconButtonExport,
        EventHandler.create(ActionListener.class, parent, BioModelsEdConstants.fileExport));
    menuFile.addSeparator();
    GUIFactory.createMenuItem(menuFile, Resources.getString(BioModelsEdConstants.MENU_FILE_QUIT), Resources.iconButtonQuit,
      ctrl, KeyEvent.VK_Q, EventHandler.create(ActionListener.class, parent,
          BioModelsEdConstants.fileQuit));
    JMenu menuEdit = GUIFactory.createMenu(this,
      Resources.getString(BioModelsEdConstants.MENU_EDIT));
    GUIFactory.createMenuItem(menuEdit, Resources.getString(BioModelsEdConstants.MENU_EDIT_UNDO), Resources.iconButtonUndo,
      ctrl, KeyEvent.VK_Z, EventHandler.create(ActionListener.class,
        parent, BioModelsEdConstants.editUndo));
    GUIFactory.createMenuItem(menuEdit, Resources.getString(BioModelsEdConstants.MENU_EDIT_REDO), Resources.iconButtonRedo,
      ctrl, KeyEvent.VK_Y, EventHandler.create(ActionListener.class, parent, BioModelsEdConstants.editRedo));
    GUIFactory.createMenuItem(menuEdit,  Resources.getString(BioModelsEdConstants.MENU_EDIT_DELETE), Resources.iconButtonDelete, 
        "DELETE", EventHandler.create(ActionListener.class, commandcontroller, BioModelsEdConstants.editDelete));
    GUIFactory.createMenuItem(menuEdit, Resources.getString(BioModelsEdConstants.MENU_EDIT_COPY), Resources.iconButtonCopy,
      ctrl, KeyEvent.VK_C, EventHandler.create(ActionListener.class,
        commandcontroller, BioModelsEdConstants.editCopy));
    GUIFactory.createMenuItem(menuEdit, Resources.getString(BioModelsEdConstants.MENU_EDIT_CUT), Resources.iconButtonCut,
      ctrl, KeyEvent.VK_X, EventHandler.create(ActionListener.class,
        parent, BioModelsEdConstants.editCut));
    GUIFactory.createMenuItem(menuEdit, Resources.getString(BioModelsEdConstants.MENU_EDIT_PASTE), Resources.iconButtonPaste,
      ctrl, KeyEvent.VK_V, EventHandler.create(ActionListener.class,
        commandcontroller, BioModelsEdConstants.editPaste));
    JMenu menuLayout = GUIFactory.createMenu(this,
      Resources.getString(BioModelsEdConstants.MENU_LAYOUT));
    GUIFactory.createMenuItem(menuLayout, Resources.getString(BioModelsEdConstants.MENU_LAYOUT_NEW), Resources.iconButtonNew,
        BioModelsEdConstants.MENU_LAYOUT_NEW_KEYSTROKE, null, EventHandler.create(ActionListener.class, parent, BioModelsEdConstants.layoutNew));
    
    GUIFactory.createMenuItem(menuLayout, Resources.getString(BioModelsEdConstants.MENU_LAYOUT_RENAME), Resources.iconButtonRename,
      BioModelsEdConstants.MENU_LAYOUT_RENAME_KEYSTROKE, null, EventHandler.create(ActionListener.class, parent, BioModelsEdConstants.layoutRename));
    
    GUIFactory.createMenuItem(menuLayout, Resources.getString(BioModelsEdConstants.MENU_LAYOUT_CLONE), Resources.iconButtonCopy,
        BioModelsEdConstants.MENU_LAYOUT_CLONE_KEYSTROKE, null, EventHandler.create(ActionListener.class, parent, BioModelsEdConstants.layoutClone));
    
    GUIFactory.createMenuItem(menuLayout, Resources.getString(BioModelsEdConstants.MENU_LAYOUT_DELETE), Resources.iconButtonDelete, 
        BioModelsEdConstants.MENU_LAYOUT_DELETE_KEYSTROKE, null, EventHandler.create(ActionListener.class, parent, BioModelsEdConstants.layoutDelete));
    
    GUIFactory.createMenuItem(menuLayout, Resources.getString(BioModelsEdConstants.MENU_LAYOUT_AUTO), Resources.iconButtonAuto,
      BioModelsEdConstants.MENU_LAYOUT_AUTO_KEYSTROKE, null, EventHandler.create(ActionListener.class, parent, BioModelsEdConstants.layoutAuto));
    
    JMenu menuHelp = GUIFactory.createMenu(this, Resources.getString(BioModelsEdConstants.MENU_HELP));
    GUIFactory.createMenuItem(menuHelp, Resources.getString(BioModelsEdConstants.MENU_HELP_ABOUT), Resources.iconButtonAbout,
                EventHandler.create(ActionListener.class, parent, BioModelsEdConstants.helpAbout));
    
    setEnableState(false);
        
  }
  
  /**
   * Enables or disables several options in the menu bar, depending on the input.
   * Enables, if anyDocumentsOpen is true.
   * @param anyDocumentsOpen
   */
  public void setEnableState(boolean anyDocumentsOpen) {
    
    this.getMenu(0).getItem(3).setEnabled(anyDocumentsOpen);
    this.getMenu(0).getItem(5).setEnabled(anyDocumentsOpen);
    this.getMenu(0).getItem(6).setEnabled(anyDocumentsOpen);
    this.getMenu(0).getItem(7).setEnabled(anyDocumentsOpen);
    this.getMenu(1).setEnabled(anyDocumentsOpen);
    this.getMenu(2).setEnabled(anyDocumentsOpen);
  }
  
  /**
   * Enables or disables the Save option.
   * Enables if enabled is true.
   * @param enabled
   */
  public void setSaveState(boolean enabled) {
    this.getMenu(0).getItem(5).setEnabled(enabled);
  }
}
