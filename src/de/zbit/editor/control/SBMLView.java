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
//TODO: let all actions return a boolean value (success/failure)

  public void fileNew();


  public boolean fileOpen();


  public void fileClose();


  public void fileSave();


  public void fileSaveAs();

// TODO: modifier public
  void fileQuit();


  public void addDocument(OpenedDocument doc);


  public OpenedDocument getSelectedDoc();


  public void refreshTitle();
  
  
  public String nameDialogue(int counter);
  
  
  public void refresh(String id, String name, int sboTerm, double x, double y);
  
  
  public TabManager getTabManager();
}
