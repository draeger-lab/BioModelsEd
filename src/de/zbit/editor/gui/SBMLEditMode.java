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

import java.awt.event.MouseEvent;

import org.sbml.jsbml.util.ValuePair;

import y.base.Node;
import y.view.EditMode;
import y.view.HitInfo;
import de.zbit.editor.SBMLEditorConstants;
import de.zbit.editor.control.CommandController;

/**
 * @author Eugen Netz
 * @since 1.0
 * @version $Rev$
 */
public class SBMLEditMode extends EditMode  {

  private ValuePair<Double, Double> lastPositionMouseClicked;


  public SBMLEditMode(CommandController controller) {
    super();
    this.allowNodeCreation(true);
    this.setCreateEdgeMode(new SBMLCreateEdgeMode());
    this.allowEdgeCreation(true);
    this.addPropertyChangeListener(controller);
  }

   
  
  @Override
  public void mousePressedLeft(double x, double y) {
	  //TODO set Source Node for edge
	ValuePair<Double, Double> newPositionMouseClicked = new ValuePair<Double, Double>(x, y);
    firePropertyChange(SBMLEditorConstants.EditModeMousePressedLeft, lastPositionMouseClicked, newPositionMouseClicked);
    lastPositionMouseClicked = newPositionMouseClicked;
  }
  
  @Override
  public void mouseReleasedLeft(double x, double y) {
    //TODO set Target Node for edge, create edge
		ValuePair<Double, Double> positionMouseReleased = new ValuePair<Double, Double>(x, y);
    firePropertyChange(SBMLEditorConstants.EditModeMouseReleasedLeft, lastPositionMouseClicked, positionMouseReleased);
  } 
  
  @Override
  public void mouseClicked(double x, double y) {
    // TODO Left/Right Unterscheidung?
		ValuePair<Double, Double> newPositionMouseClicked = new ValuePair<Double, Double>(x, y);
    firePropertyChange(SBMLEditorConstants.EditModeMouseClicked, lastPositionMouseClicked, newPositionMouseClicked);
    lastPositionMouseClicked = newPositionMouseClicked;
  }
  
  @Override
  protected void nodeClicked(Node node) {
    System.out.print("X : " + getGraph2D().getCenterX(node));
  }
  
  @Override
  public void mouseClicked(MouseEvent evt) {
    //FIXME Test, no Function
    if (evt.getButton() == MouseEvent.BUTTON1) {
      for (int i = 0; i < this.getGraph2D().getNodeArray().length; i++) {
        Node node = this.getGraph2D().getNodeArray()[i];
        double x = this.getGraph2D().getX(node);
        double y = this.getGraph2D().getY(node);
        System.out.print("\nX : " + x + ", Y : " + y);        
      }
      HitInfo info = this.getGraph2D().getHitInfo(evt.getX(), evt.getY());
      info.getFirstHit();
    }
    else if (evt.getButton() == MouseEvent.BUTTON3) {
      System.out.print("Rechts");
    }
  }
  
}
