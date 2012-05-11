/*
 * $Id:  GUIFactory.java 23:46:16 jakob $
 * $URL: GUIFactory.java $
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

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class GUIFactory {
  
  /**
   * Create a JMenu on given menubar. 
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
   * Adding new item with keystroke to given menu.
   * @param menu
   * @param label
   * @param key
   * @param modifier
   */
  public static JMenuItem createMenuItem(JMenu menu, String label, String keystroke, ActionListener ... l) {
    JMenuItem item = createMenuItem(menu, label, l);
    item.setAccelerator(KeyStroke.getKeyStroke(keystroke));
    return item;
  }

  /**
   * Adding new item to given menu.
   * @param menu
   * @param label
   * @param key
   */
  public static JMenuItem createMenuItem(JMenu menu, String label, ActionListener ... l) {
    JMenuItem item = new JMenuItem(label);
    if (l != null) {
      for (ActionListener listener : l){
        item.addActionListener(listener);        
      }
    }
    menu.add(item);
    return item;
  }
}
