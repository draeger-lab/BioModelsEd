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
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import de.zbit.editor.control.OpenedSBMLDocument;

/**
 * This class represents a single tab of the tab bar.
 * 
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Eugen Netz
 * @author Jan Rudolph
 * @version $Rev$
 */
public class TabComponent extends JPanel {

  private static final long serialVersionUID = -8179219783406520882L;
  private TabManager        tabManager;
  private JLabel            label;
  private static Logger logger = Logger.getLogger(OpenedSBMLDocument.class.toString());

  /**
   * Constructor.
   * @param tabManager
   */
  public TabComponent(TabManager tabManager) {
	super(new BorderLayout());
    this.tabManager = tabManager;
    setOpaque(false);
    String title = tabManager.getTitleAt(tabManager.getSelectedIndex());
    this.label = new JLabel(title);
    add(this.label, BorderLayout.CENTER);
    
    label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
    JButton button = GUIFactory.createButtonIcon(Resources.iconButtonClose, "Close this tab", 16, 16, 
        EventHandler.create(ActionListener.class, this, "close"));
    
    add(button, BorderLayout.EAST);
    MouseListener tabListener = new TabListener(
      GUIFactory.createTabPopupMenu(this));
    addMouseListener(tabListener);
  }

  /**
   * Sets the title shown in the tab.
   * @param title
   */
  public void setTitle(String title) {
    this.label.setText(title);
  }

  /**
   * Closes this tab.
   */
  public void close() {
    GraphLayoutPanel panel = (GraphLayoutPanel) tabManager.getComponentAt(tabManager.indexOfTabComponent(this));
    tabManager.showTab(panel.getDocument());
    tabManager.getEditorInstance().layoutClose(panel.getDocument());
  }

  /**
   * Closes all tabs.
   */
  public void closeAll() {
    tabManager.closeAllTabs();
  }
  
  /**
   * Runs the autoLayout algorithm on the layout shown in this tab.
   */
  public void autoLayout() {
    GraphLayoutPanel panel = (GraphLayoutPanel) tabManager.getComponentAt(tabManager.indexOfTabComponent(this));
    tabManager.layoutAuto(panel.getDocument());
  }
  
  /**
   * 
   */
  class TabListener extends MouseAdapter {

    JPopupMenu popup;

    /**
     * Constructor.
     * @param popupMenu
     */
    TabListener(JPopupMenu popupMenu) {
      popup = popupMenu;
    }

    /**
     * Determines the action for a dragged mouse.
     */
    public void mouseDragged(MouseEvent e) {
      logger.info("Dragged");
    }
    
    /**
     * Calls maybeShowPopup on a pressed mouse button.
     */
    public void mousePressed(MouseEvent e) {
      maybeShowPopup(e);
    }

    /**
     * Calls maybeShowPopup on a released mouse button.
     */
    public void mouseReleased(MouseEvent e) {
      maybeShowPopup(e);
    }

    /**
     * Determines whether the mouse event should trigger a popup and triggers it when it should.
     * @param e
     */
    private void maybeShowPopup(MouseEvent e) {
      if (e.isPopupTrigger()) {
        popup.show(e.getComponent(), e.getX(), e.getY());
      } else {
        TabComponent tabComponent = ((TabComponent) e.getComponent());
        GraphLayoutPanel panel = (GraphLayoutPanel) tabManager.getComponentAt(tabManager.indexOfTabComponent(tabComponent));
        tabManager.showTab(panel.getDocument());
      }
    }
  }
}
