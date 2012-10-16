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
package de.zbit.editor.control;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.swing.JFrame;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.util.ValuePair;

import de.zbit.editor.gui.TabManager;
import de.zbit.io.OpenedFile;

/**
 * The Interface, that a view of this program must implement.
 * 
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Eugen Netz
 * @author Jan Rudolph
 * @version $Rev$
 */
public interface SBMLView {

  /**
   * The SBML Level and Version supported by this program
   */
  public static final ValuePair<Integer, Integer> DEFAULT_LEVEL_VERSION = new ValuePair<Integer, Integer>(
      3, 1);

  /**
   * Opens the layout in a new tab.
   * @param layout, the layout to open
   * @param autoLayout, if true the autoLayout Algorithm is used on the layout
   * @return true if successful
   */
  public boolean addTab(OpenedFile<SBMLDocument> file, String layoutId, boolean autoLayout);

  /**
   * Forwards a getCurrentLayout request to the TabManager.
   * @return the current layout from the TabManager
   */
  public Layout getCurrentLayout();

  /**
   * Creates pop-up for the input of a Species name.
   * @param s, the default name
   * @return the name
   */
  public String nameDialogue(String id);

  /**
   * @return the tabManager
   */
  public TabManager getTabManager();

  /**
   * Creates pop-up to request File Open Input.
   * @return the file to open
   */
  public File askUserOpenDialog();
  
  /**
   * Creates pop-up to request File Save Input.
   * @return the file to save
   */
  public File askUserSaveDialog();
  
  /**
   * Asks user, if Glyphs should be created for all Structures in the model.
   * @return the Integer representing the JOptionPane Option
   */
  public int askUserCreateLayoutInformation();

  /**
   * @return the frame
   */
  public Component getFrame();

  /**
   * Creates popup for the input of a filename.
   * @eturn the filename
   */
  public String askUserFileNew();

  /**
   * Shows a warning message corresponding to the given String.
   * @param warning
   */
  public void showWarning(String warning);
  
  /**
   * Shows an error message corresponding to the given String.
   * @param error
   */
  public void showError(String error);

  /**
   * Forwards a closeTab request to the TabManager, that closes the tab, that shows the layout.
   * @param layout, the layout to be closed
   * @return true if succesful
   */
  public boolean closeTab(Layout layout);

  /**
   * Refreshes the title of the layout shown in the tab.
   * @param layout 
   */
  public void refreshTitle(Layout layout);

  /**
   * Updates the ComboBox for choice of layout.
   * @param list
   */
  public void updateComboBox(List<Layout> list);
  
  /**
   * Shows the "About"-message.
   */
  public void helpAbout();

  /**
   * Clones the layout shown in the current tab and opens it in a new tab.
   */
  public void layoutClone();

  /**
   * Deletes the layout shown in the current tab.
   */
  public void layoutDelete();

  /**
   * Closes the tab, that shows the given layout.
   * @param layout
   */
  public boolean layoutClose(Layout layout);

  /**
   * Creates a new empty layout and opens it in a new tab.
   */
  public boolean layoutNew();
  
  /**
   * Renames the current layout.
   */
  public boolean layoutRename();
  
  /**
   * Applies an algorithm for an automated layout to the current layout.
   */
  public boolean layoutAuto();

  /**
   * Opens the layout selected in the ComboBox in the current tab.
   */
  public void openLayoutInTab();

  /**
   * Opens the layout selected in the ComboBox in a new tab.
   */
  public void openLayoutInNewTab();


  /**
   * Returns associated CommandController
   * @return
   */
  public CommandController getController();

  /**
   * Use to unable control buttons when no document is opened
   * @param b
   */
  public void setControlsOn(boolean b);

}
