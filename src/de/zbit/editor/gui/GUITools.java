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

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JMenuBar;
import javax.swing.JToolBar;

/**
 * Offers static methods for several GUI actions.
 * 
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Eugen Netz
 * @author Jan Rudolph
 * @version $Rev$
 */
public class GUITools {

  /**
   * Detect if system is a Mac OS
   * 
   * @return
   */
  public static boolean onMac() {
    return (System.getProperty("os.name").toLowerCase().contains("mac") ||
        System.getProperty("mrj.version") != null);
  }


  public static int getControlKey() {
    if (onMac()) {
      return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    } else {
      return KeyEvent.CTRL_MASK;
    }
  }
  
  /**
   * Enable Button/MenuItem for user interaction
   * @param isEnabled
   * @param command
   * @param menuBar
   * @param toolBars
   */
  public static void setEnabled(boolean isEnabled, String command, 
    JMenuBar menuBar, JToolBar... toolBars) {
  }
}
