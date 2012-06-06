/*
 * $$Id${file_name} ${time} ${user}$$
 * $$URL${file_name}$$
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

import org.sbml.jsbml.SBMLDocument;

import y.view.EditMode;
import de.zbit.editor.control.CommandController;
import de.zbit.graph.io.SB_2GraphML;

/**
 * @author Eugen Netz
 * @since 1.0
 * @version $Rev$
 */
public class SBMLEditMode extends EditMode  {

  private SB_2GraphML<SBMLDocument> converter;
  private CommandController controller;


  public SBMLEditMode(SB_2GraphML<SBMLDocument> converter, CommandController controller) {
    super();
    this.converter = converter;
    this.allowNodeCreation(true);
    this.setCreateEdgeMode(new SBMLCreateEdgeMode());
    this.allowEdgeCreation(true);
    this.controller = controller;
    this.addPropertyChangeListener(controller);
  }

   
  
  @Override
  public void mousePressedLeft(double x, double y) {
    //TODO set Source Node for edge
    firePropertyChange("EditModeMPLeft", x, y);
  }
  
  @Override
  public void mouseReleasedLeft(double x, double y) {
    //TODO set Target Node for edge, create edge
    firePropertyChange("EditModeMRLeft", x, y);
  } 
  
  @Override
  public void mouseClicked(double x, double y) {
    // TODO Left/Right Unterscheidung?
    firePropertyChange("EditModeMouseClicked", x, y);
  }
  
}
