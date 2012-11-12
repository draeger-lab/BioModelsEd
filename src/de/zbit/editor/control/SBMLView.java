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

import org.sbml.jsbml.SBMLDocument;
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
   * @return true if successful
   */
  public boolean show(OpenedFile<SBMLDocument> file);


  /**
   * @return the tabManager
   */
  public TabManager getTabManager();

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
   * Returns associated CommandController
   * @return
   */
  public CommandController getController();

}
