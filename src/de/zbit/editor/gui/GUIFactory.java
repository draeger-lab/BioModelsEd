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

import java.awt.event.ActionListener;
import java.beans.EventHandler;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class GUIFactory {

  /**
   * Create a JMenu on given menubar.
   * 
   * @param menuBar
   * @param name
   * @return
   */
  public static JMenu createMenu(JMenuBar menuBar, String name) {
    JMenu menu = new JMenu(name);
    menuBar.add(menu);
    return menu;
  }


  /**
   * Add a new item with keystroke to given menu.
   * 
   * @param menu
   * @param label
   * @param key
   * @param modifier
   */
  public static JMenuItem createMenuItem(JMenu menu, String label,
    String keystroke, ActionListener... l) {
    JMenuItem item = createMenuItem(menu, label, l);
    item.setAccelerator(KeyStroke.getKeyStroke(keystroke));
    return item;
  }


  public static JMenuItem createMenuItem(JMenu menu, String label,
    Integer ctrl, int keyCode, ActionListener... l) {
    JMenuItem item = createMenuItem(menu, label, l);
    item.setAccelerator(KeyStroke.getKeyStroke(keyCode, ctrl));
    return item;
  }


  /**
   * Add a new item to given menu.
   * 
   * @param menu
   * @param label
   * @param key
   */
  public static JMenuItem createMenuItem(JMenu menu, String label,
    ActionListener... l) {
    JMenuItem item = new JMenuItem(label);
    if (l != null) {
      for (ActionListener listener : l) {
        item.addActionListener(listener);
      }
    }
    menu.add(item);
    return item;
  }


  /**
   * Add a button to given toolbar.
   * 
   * @param toolbar
   * @param name
   * @param l
   * @return
   */
  public static JButton addButton(JToolBar toolbar, String name,
    ActionListener... l) {
    JButton button = new JButton(name);
    if (l != null) {
      for (ActionListener listener : l) {
        button.addActionListener(listener);
      }
    }
    toolbar.add(button);
    return button;
  }


  public static JFileChooser createFileChooser() {
    JFileChooser fc = new JFileChooser();
    FileFilter filter = createFilterXML();
    fc.setAcceptAllFileFilterUsed(false);
    fc.setFileFilter(filter);
    // TODO: create also a file filter for extension *.sbml.
//  fc.addChoosableFileFilter(filter);
    return fc;
  }


  public static FileNameExtensionFilter createFilterXML() {
    return new FileNameExtensionFilter("XML-Datei", "xml", "XML");
  }


  public static int createQuestionClose(JFrame frame) {
    return JOptionPane.showConfirmDialog(frame,
      Resources.getString("DIALOG_QUIT_QUESTION"),
      Resources.getString("DIALOG_QUIT_TITLE"), JOptionPane.YES_NO_OPTION);
  }


  public static JPopupMenu createTabPopupMenu(TabComponent component) {
    JMenuItem menuItem;
    // Create the popup menu.
    JPopupMenu popup = new JPopupMenu();
    menuItem = new JMenuItem(Resources.getString("TAB_CLOSE"));
    menuItem.addActionListener(EventHandler.create(ActionListener.class,
      component, "close"));
    popup.add(menuItem);
    menuItem = new JMenuItem(Resources.getString("TAB_CLOSE_ALL"));
    menuItem.addActionListener(EventHandler.create(ActionListener.class,
      component, "closeAll"));
    popup.add(menuItem);
    return popup;
  }
}
