/*
 * $Id:  ControllerViewSynchronizer.java 14:34:32 Eugen Netz$
 * $URL: ControllerViewSynchronizer.java$
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

import java.beans.PropertyChangeEvent;

import javax.swing.tree.TreeNode;

import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.layout.BoundingBox;
import org.sbml.jsbml.ext.layout.ExtendedLayoutModel;
import org.sbml.jsbml.ext.layout.LayoutConstant;
import org.sbml.jsbml.util.TreeNodeChangeEvent;
import org.sbml.jsbml.util.TreeNodeChangeListener;

import de.zbit.graph.gui.TranslatorSBMLgraphPanel;


/**
 * @author Eugen Netz
 * @since 1.0
 * @version $Rev$
 */
public class ControllerViewSynchronizer implements TreeNodeChangeListener {

  private TranslatorSBMLgraphPanel panel;
  
  public ControllerViewSynchronizer(TranslatorSBMLgraphPanel panel){
    this.panel = panel;
  }
  
  /* (non-Javadoc)
   * @see org.sbml.jsbml.util.TreeNodeChangeListener#nodeAdded(javax.swing.tree.TreeNode)
   */
  @Override
  public void nodeAdded(TreeNode node) {
    //TODO
    if (node instanceof Species){
      Species s = (Species) node;
      //panel.getConverter().createNode(s.getId(), s.getName(), s.getSBOTerm());
      
      
      ExtendedLayoutModel extLayout = (ExtendedLayoutModel) s.getExtension(LayoutConstant.namespaceURI);
      BoundingBox b = extLayout.getListOfLayouts().get(0).getListOfSpeciesGlyphs().get(s.getId()).getBoundingBox();
      double x = b.getPosition().getX();
      double y = b.getPosition().getY();
      double width = b.getDimensions().getWidth();
      double height = b.getDimensions().getHeight();
      
      panel.getConverter().createNode(s.getId(), s.getName(), s.getSBOTerm(), x, y, width, height);
      panel.getGraph2DView().updateView();
      
    }  
  }
  

  @Override
  public void nodeRemoved(TreeNode node) {
   // TODO
    
  }

  /* (non-Javadoc)
   * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    //TreeNodeChangeEvent event = (TreeNodeChangeEvent) evt;
    //TODO Paint anew with new Doc?
    //panel.getConverter().createGraph(panel.getDocument());
    
  }


}
