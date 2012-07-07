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

import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFrame;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.util.ValuePair;

import de.zbit.editor.gui.TabManager;

/**
 * @author Eugen Netz
 * @since 1.0
 * @version $Rev$
 */
public interface SBMLView {

  public static final ValuePair<Integer, Integer> DEFAULT_LEVEL_VERSION = new ValuePair<Integer, Integer>(
      3, 1);

  // TODO: let all actions return a boolean value (success/failure)

  public boolean fileNew();

  public boolean fileOpen() throws FileNotFoundException;

  public boolean fileClose();

  public boolean fileSave();

  public boolean fileSaveAs();

  public void fileQuit();

  public boolean addLayout(Layout layout);

  public Layout getCurrentLayout();

  public String nameDialogue(String id);

  public TabManager getTabManager();


  public File askUserOpenDialog();
  public File askUserSaveDialog();

  public JFrame getFrame();

  public String askUserFileNew();

  public void showWarning(String warning);
  public void showError(String error);

  boolean closeTab(Layout layout);

  /**
   * @param layout
   */
  public void refreshTitle(Layout layout);

  /**
   * @param list
   */
  public void updateComboBox(ListOf<Layout> list);
  
  public void helpAbout();

  /**
   * 
   */
  public void layoutClone();

  /**
   * 
   */
  public void layoutDelete();

  /**
   * @param layout
   * @return
   */
  public boolean layoutClose(Layout layout);

  /**
   * @return 
   * 
   */
  public boolean layoutNew();

  /**
   * 
   */
  public void openLayoutInTab();

  /**
   * 
   */
  public void openLayoutInNewTab();
}
