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


import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

/**
 * @author Alexander Diamantikos
 * @author Eugen Netz
 * @since 1.0
 * @version $Rev$
 */
//TODO: Set SVN properties.
public class TabComponent extends JPanel {

  private static final long serialVersionUID = -8179219783406520882L;
  private TabManager        tabManager;
  private JLabel            label;


  public TabComponent(TabManager tabManager) {
	super(new BorderLayout());
    this.tabManager = tabManager;
    setOpaque(false);
    String title = tabManager.getTitleAt(tabManager.getSelectedIndex());
    this.label = new JLabel(title);
    add(this.label, BorderLayout.CENTER);
    JButton button = new JButton("x");
    button.addActionListener(EventHandler.create(ActionListener.class, this,
      "close"));
//    button.setSize(10, 10);
//    button.setOpaque(false);
    button.setBorderPainted(false);
    add(button, BorderLayout.EAST);
    MouseListener tabListener = new TabListener(
      GUIFactory.createTabPopupMenu(this));
    addMouseListener(tabListener);
  }


  public void setTitle(String title) {
    this.label.setText(title);
  }


  public void close() {
    tabManager.closeTab(tabManager.indexOfTabComponent(this));
  }


  public void closeAll() {
    tabManager.closeAllTabs();
  }

  class TabListener extends MouseAdapter {

    JPopupMenu popup;


    TabListener(JPopupMenu popupMenu) {
      popup = popupMenu;
    }


    public void mousePressed(MouseEvent e) {
      maybeShowPopup(e);
    }


    public void mouseReleased(MouseEvent e) {
      maybeShowPopup(e);
    }


    private void maybeShowPopup(MouseEvent e) {
      if (e.isPopupTrigger()) {
        popup.show(e.getComponent(), e.getX(), e.getY());
      } else {
        TabComponent tabComponent = ((TabComponent) e.getComponent());
        tabManager.showTab(tabManager.indexOfTabComponent(tabComponent));       
      }
    }
  }
}
