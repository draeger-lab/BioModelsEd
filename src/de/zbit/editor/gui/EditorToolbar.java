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

import javax.swing.JComboBox;
import javax.swing.JToolBar;


/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class EditorToolbar extends JToolBar {

  private static final long serialVersionUID = 4238837776010510727L;


  /**
   * @param commandController
   */
  public EditorToolbar(SBMLEditor parent) {
    // TODO: Create a very simple icon for each button, use Tooltips, remove the
    // String Label.
    GUIFactory.addButton(this, Resources.getString("UNKNOWN_MOLECULE"),
      EventHandler.create(ActionListener.class, parent, "addUnknownMolecule"));
    GUIFactory.addButton(this, Resources.getString("SIMPLE_MOLECULE"),
      EventHandler.create(ActionListener.class, parent, "addSimpleMolecule"));
    GUIFactory.addButton(this, Resources.getString("MACROMOLECULE"),
      EventHandler.create(ActionListener.class, parent, "addMacromolecule"));
    GUIFactory.addButton(this, Resources.getString("EMPTY_SET"),
      EventHandler.create(ActionListener.class, parent, "addEmptySet"));
    GUIFactory.addButton(this, Resources.getString("REACTION"),
      EventHandler.create(ActionListener.class, parent, "addReaction"));
    GUIFactory.addButton(this, Resources.getString("CATALYSIS"),
      EventHandler.create(ActionListener.class, parent, "addCatalysis"));
    GUIFactory.addButton(this, Resources.getString("INHIBITION"),
      EventHandler.create(ActionListener.class, parent, "addInhibition"));
     String[] layoutArray = { "A", "B", "C" };
     JComboBox layoutComboBox = new JComboBox(layoutArray);
     layoutComboBox.setSelectedIndex(0);
     add(layoutComboBox);
    GUIFactory.addButton(this,  Resources.getString("MENU_TAB_OPEN"));
    GUIFactory.addButton(this,  Resources.getString("MENU_TAB_OPEN_NEW"));
  }
}
