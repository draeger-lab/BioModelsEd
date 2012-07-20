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

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.beans.EventHandler;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.zbit.editor.SBMLEditorConstants;
import de.zbit.editor.control.CommandController;

/**
 * Offers static methods for creation of Java Swing objects.
 * 
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Eugen Netz
 * @author Jan Rudolph
 * @version $Rev$
 */
public class GUIFactory {

  /**
   * Create a JMenu on given menubar.
   * 
   * @param menuBar
   * @param name
   * @return the created JMenu
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
   * @param icon
   * @param keystroke
   * @param l
   * @return the created item
   */
  public static JMenuItem createMenuItem(JMenu menu, String label, ImageIcon icon,
    String keystroke, ActionListener... l) {
    JMenuItem item = createMenuItem(menu, label, icon, l);
    item.setAccelerator(KeyStroke.getKeyStroke(keystroke));
    return item;
  }

  /**
   * Add a new item with keystroke to given menu.
   * 
   * @param menu
   * @param label
   * @param icon
   * @param ctrl
   * @param keyCode
   * @param l
   * @return the created item
   */
  public static JMenuItem createMenuItem(JMenu menu, String label, ImageIcon icon,
    Integer ctrl, int keyCode, ActionListener... l) {
    JMenuItem item = createMenuItem(menu, label, icon, l);
    item.setAccelerator(KeyStroke.getKeyStroke(keyCode, ctrl));
    return item;
  }


  /**
   * Add a new item to given menu.
   * 
   * @param menu
   * @param label
   * @param icon
   * @param l
   * @return the created item
   */
  public static JMenuItem createMenuItem(JMenu menu, String label, ImageIcon icon,
    ActionListener... l) {
    JMenuItem item = new JMenuItem(label, icon);
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
   * @param tooltip
   * @param icon 
   * @param width
   * @param height
   * @param l
   * @return the created button
   */
  public static JButton addButton(JToolBar toolbar, String tooltip, ImageIcon icon, int width, int height,
    ActionListener... l) {
    JButton button = createButtonIcon(icon, tooltip, width, height, l);
    
    toolbar.add(button);
    return button;
  }
  
  /**
   * Add a button to given toolbar.
   * 
   * @param toolbar
   * @param name
   * @param l
   * @return the created button
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
  
  /**
   * Add a checkbox to a given toolbar
   * 
   * @param toolbar
   * @param name
   * @param l
   * @return the created checkbox
   */
  public static JCheckBox addCheckbox(JToolBar toolbar, String name,
    ActionListener... l) {
    JCheckBox checkbox = new JCheckBox(name);
    if (l != null) {
      for (ActionListener listener : l) {
        checkbox.addActionListener(listener);
      }
    }
    toolbar.add(checkbox);
    return checkbox;
  }

  /**
   * Creates a filechooser for xml and sbml files.
   * @return the created filechooser
   */
  public static JFileChooser createFileChooser() {
    JFileChooser fileChooser = new JFileChooser();
    
    /*
     * no filtering
     */
    fileChooser.setAcceptAllFileFilterUsed(true);
    
    /*
     * *.sbml and *.xml filter
     */
    FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter(
        Resources.getString("FILENAME_FILTER"), "xml", "sbml");
    fileChooser.addChoosableFileFilter(extensionFilter);
    fileChooser.setFileFilter(extensionFilter);
    
    return fileChooser;
  }

  /**
   * Creates a filechooser for exportable image files.
   * @return the created filechooser
   */
  public static JFileChooser createFileChooserExport() {
    JFileChooser fileChooser = new JFileChooser();

    /*
     * no filtering
     */
    fileChooser.setAcceptAllFileFilterUsed(true);

    /*
     * *.gif and *.jpeg filter
     */
    FileNameExtensionFilter extensionFilter =  createFilterGIF();
    
    fileChooser.addChoosableFileFilter(extensionFilter);
    
    extensionFilter =  createFilterJPEG();
    fileChooser.addChoosableFileFilter(extensionFilter);
    
    fileChooser.setFileFilter(extensionFilter);

    return fileChooser;
  }
  
  /**
   * Creates a filename extension filter for JPEG files.
   * @return the created filter
   */
  public static FileNameExtensionFilter createFilterJPEG() {
    return new FileNameExtensionFilter("JPEG", "jpeg", "JPEG", "jpg");
  }

  /**
   * Creates a filename extension filter for GIF files.
   * @return the created filter
   */
  public static FileNameExtensionFilter createFilterGIF() {
    return new FileNameExtensionFilter("GIF", "gif", "GIF");
  }
  
  /**
   * Shows an option dialogue for closing a tab in the given frame. 
   * @param frame
   * @return the Integer corresponding to the JOptionPane option
   */
  public static int createQuestionClose(JFrame frame) {
    return JOptionPane.showConfirmDialog(frame,
      Resources.getString("DIALOG_QUIT_QUESTION"),
      Resources.getString("DIALOG_QUIT_TITLE"), JOptionPane.YES_NO_OPTION);
  }
  
  /**
   * Shows an option dialogue for saving a file in the given frame. 
   * @param frame
   * @param title
   * @return the Integer corresponding to the JOptionPane option
   */
  public static int createQuestionSave(JFrame frame, String title) {
    return JOptionPane.showConfirmDialog(frame,
      Resources.getString("DIALOG_SAVE_QUESTION"),
      title, JOptionPane.YES_NO_CANCEL_OPTION);
  }

  /**
   * Shows an option dialogue for deleting a layout in the given frame.
   * @param frame
   * @return the Integer corresponding to the JOptionPane option
   */
  public static int createQuestionDelete(JFrame frame) {
    return JOptionPane.showConfirmDialog(frame,
      Resources.getString("DIALOG_DELETE_LAYOUT_QUESTION"),
      Resources.getString("DIALOG_DELETE_LAYOUT_TITLE"), JOptionPane.YES_NO_OPTION);
  }

  /**
   * Creates a popup menu for tab component.
   * @param component
   * @return the created menu
   */
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
    
    menuItem = new JMenuItem("Autolayout");
    menuItem.addActionListener(EventHandler.create(ActionListener.class,
      component, "autoLayout"));
    popup.add(menuItem);
    
    return popup;
  }
  
  /**
   * Creates a popup menu for a SpeciesGlyph.
   * @param controller
   * @return the created menu
   */
  public static JPopupMenu createSpeciesGlyphPopupMenu(CommandController controller) {
    JMenuItem menuItem;
    // Create the popup menu.
    JPopupMenu popup = new JPopupMenu();

    menuItem = new JMenuItem(Resources.getString("NODE_RENAME"));
    menuItem.addActionListener(EventHandler.create(ActionListener.class,
      controller, SBMLEditorConstants.nodeRename));
    popup.add(menuItem);
    
    menuItem = new JMenuItem(Resources.getString("NODE_COPY"));
    menuItem.addActionListener(EventHandler.create(ActionListener.class,
      controller, SBMLEditorConstants.nodeCopy));
    popup.add(menuItem);
    
    menuItem = new JMenuItem(Resources.getString("NODE_DELETE"));
    menuItem.addActionListener(EventHandler.create(ActionListener.class,
      controller, SBMLEditorConstants.nodeDelete));
    popup.add(menuItem);
    
    return popup;
  }
  
  /**
   * Creates a popup menu for a ReactionGlyph.
   * @param controller
   * @return the created menu
   */
  public static JPopupMenu createReactionGlyphPopupMenu(CommandController controller) {
    JMenuItem menuItem;
    // Create the popup menu.
    JPopupMenu popup = new JPopupMenu();
       
    menuItem = new JMenuItem(Resources.getString("NODE_DELETE"));
    menuItem.addActionListener(EventHandler.create(ActionListener.class,
      controller, SBMLEditorConstants.nodeDelete));
    popup.add(menuItem);
    
    return popup;
  }
  
  /**
   * Creates a popup menu for pasting nodes.
   * @param controller
   * @param nodeCopy
   * @return the created menu
   */
  public static JPopupMenu createPastePopupMenu(CommandController controller, boolean nodeCopy) {
    JMenuItem menuItem;
    
    JPopupMenu popup = new JPopupMenu();
    
    menuItem = new JMenuItem(Resources.getString("NODE_PASTE"));
    menuItem.addActionListener(EventHandler.create(ActionListener.class,
      controller, "nodePaste"));
    menuItem.setEnabled(nodeCopy);
    popup.add(menuItem);
    return popup;
  }
  
  /**
   * Creates a button.
   * @param icon
   * @param tooltip
   * @param width
   * @param height
   * @param l
   * @return the created button
   */
  public static JButton createButtonIcon(ImageIcon icon, String tooltip, int width, int height, ActionListener... l) {
    JButton button = new JButton(icon);
    if (l != null) {
      for (ActionListener listener : l) {
        button.addActionListener(listener);
      }
    }
    if (width !=0 || height !=0) {
      button.setPreferredSize(new Dimension(width, height));
    }
    button.setFocusPainted(false);
    button.setFocusable(false);
    button.setBorderPainted(false);
    button.setRolloverEnabled(true);
    button.setToolTipText(tooltip);
    return button;
  }
}
