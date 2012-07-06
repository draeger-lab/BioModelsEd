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
import y.view.Graph2D;
import y.view.HitInfo;
import y.view.NodePort;
import de.zbit.editor.SBMLEditorConstants;
import de.zbit.editor.control.CommandController;

/**
 * @author Eugen Netz
 * @since 1.0
 * @version $Rev$
 */
public class SBMLEditMode extends EditMode  {

  private ValuePair<Double, Double> lastPositionMouseClicked;
  private ValuePair<Double, Double> oldNodePosition;
  private Node node;


  public SBMLEditMode(CommandController controller) {
    super();
    this.allowNodeCreation(true);
    this.allowMoveSelection(false);
    this.setCreateEdgeMode(new SBMLCreateEdgeMode());
    this.allowEdgeCreation(true);
    this.addPropertyChangeListener(controller);
    this.allowNodeEditing(false);
  }
  
  @Override
  public void mousePressedLeft(double x, double y) {
	  //TODO set Source Node for edge
    
    HitInfo info = this.getGraph2D().getHitInfo(x, y);
    Object hit = info.getFirstHit();
    if (hit instanceof Node) {
      this.node = (Node) hit;
      double nodeX = this.getGraph2D().getX(node);
      double nodeY = this.getGraph2D().getY(node);
      
      firePropertyChange(SBMLEditorConstants.EditModeNodePressedLeft, null, this.node);      
      oldNodePosition = new ValuePair<Double, Double>(nodeX, nodeY);      
    }
    else {
      ValuePair<Double, Double> newPositionMouseClicked = new ValuePair<Double, Double>(x, y);
      firePropertyChange(SBMLEditorConstants.EditModeMousePressedLeft, lastPositionMouseClicked, newPositionMouseClicked);
      lastPositionMouseClicked = newPositionMouseClicked;
    }  
    
  }
  
  @Override
  public void mouseReleasedLeft(double x, double y) {
    //TODO set Target Node for edge, create edge
    
    HitInfo info = this.getGraph2D().getHitInfo(x, y);
    Object hit = info.getFirstHit();
    if (hit instanceof Node) {
      this.node =  (Node) hit;
      double nodeX = this.getGraph2D().getX(node);
      double nodeY = this.getGraph2D().getY(node);
      
      ValuePair<Double, Double> newNodePosition = new ValuePair<Double, Double>(nodeX, nodeY);
      firePropertyChange(SBMLEditorConstants.EditModeNodeReleasedLeft, oldNodePosition, newNodePosition);      
      oldNodePosition = newNodePosition;
      
    }
    else {
    		ValuePair<Double, Double> positionMouseReleased = new ValuePair<Double, Double>(x, y);
    		firePropertyChange(SBMLEditorConstants.EditModeMouseReleasedLeft, lastPositionMouseClicked, positionMouseReleased);
    }
  } 
  
  @Override
  public void mouseClicked(double x, double y) {
    // TODO Left/Right Unterscheidung?
		ValuePair<Double, Double> newPositionMouseClicked = new ValuePair<Double, Double>(x, y);
	
    firePropertyChange(SBMLEditorConstants.EditModeMouseClicked, lastPositionMouseClicked, newPositionMouseClicked);
    lastPositionMouseClicked = newPositionMouseClicked;
  }
  
  @Override
  public void mouseClicked(MouseEvent evt) {
    if (evt.getButton() == MouseEvent.BUTTON1) {
      double x = evt.getX();
      double y = evt.getY();
      HitInfo info = this.getGraph2D().getHitInfo(x, y);
      Object hit = info.getFirstHit();
      
      if (hit instanceof Node) {
        this.node = (Node) hit;
        ValuePair<Double, Double> newPositionMouseClicked = new ValuePair<Double, Double>(x, y);
        firePropertyChange(SBMLEditorConstants.EditModeNodeClickedLeft,
          null, this.node);
        lastPositionMouseClicked = newPositionMouseClicked;
      } 
      /*else {
        ValuePair<Double, Double> newPositionMouseClicked = new ValuePair<Double, Double>(x, y);
        firePropertyChange(SBMLEditorConstants.EditModeMouseClicked,
          lastPositionMouseClicked, newPositionMouseClicked);
        lastPositionMouseClicked = newPositionMouseClicked;
      }*/
    }
  }
    
  /*@Override
  public void mouseDraggedLeft(double x, double y) {
    HitInfo info = this.getGraph2D().getHitInfo(x, y);
    Object hit = info.getFirstHit();
    info.
    if (hit instanceof Node) {
      this.node =  (Node) hit;
      double nodeX = this.getGraph2D().getX(node);
      double nodeY = this.getGraph2D().getY(node);
      
      ValuePair<Double, Double> newNodePosition = new ValuePair<Double, Double>(nodeX, nodeY);
      firePropertyChange(SBMLEditorConstants.EditModeMouseDraggedLeft, null, newNodePosition);      
      //oldNodePosition = newNodePosition;
    }
  }*/
  
  @Override
  public void mousePressedRight(double x, double y) {
    HitInfo info = this.getGraph2D().getHitInfo(x, y);
    Object hit = info.getFirstHit();
    if (hit instanceof Node) {
      this.node = (Node) hit;
      double nodeX = this.getGraph2D().getX(node);
      double nodeY = this.getGraph2D().getY(node);
      
      firePropertyChange(SBMLEditorConstants.EditModeNodePressedRight, null, this.node);      
      oldNodePosition = new ValuePair<Double, Double>(nodeX, nodeY);      
    }
    else {
      ValuePair<Double, Double> newPositionMouseClicked = new ValuePair<Double, Double>(x, y);
      firePropertyChange(SBMLEditorConstants.EditModeMousePressedRight, lastPositionMouseClicked, newPositionMouseClicked);
      lastPositionMouseClicked = newPositionMouseClicked;
    }
  }
  
  public void nodeDelete(Node node) {
    this.getGraph2D().removeNode(node);
  }
  
}
