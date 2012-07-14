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
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.tree.TreeNode;

import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.layout.BoundingBox;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.ReactionGlyph;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.util.TreeNodeChangeListener;
import org.sbml.jsbml.util.TreeNodeRemovedEvent;

import y.base.Node;
import y.view.GenericEdgeRealizer;
import de.zbit.editor.SBMLEditorConstants;


/**
 * @author Eugen Netz
 * @since 1.0
 * @version $Rev$
 */
public class ControllerViewSynchronizer implements TreeNodeChangeListener {

  private TabManager tabmanager;
  private GraphLayoutPanel panel;
  private SBMLEditMode editMode;
  private Layout layout;
  private Logger logger = Logger.getLogger(ControllerViewSynchronizer.class.getName());
  
  public ControllerViewSynchronizer(TabManager tabmanager, GraphLayoutPanel panel, Layout layout, SBMLEditMode editMode) {
    this.tabmanager = tabmanager;
    this.panel = panel;
    this.layout = layout;
    this.editMode = editMode;
  }
  
  /* (non-Javadoc)
   * @see org.sbml.jsbml.util.TreeNodeChangeListener#nodeAdded(javax.swing.tree.TreeNode)
   */
  @Override
  public void nodeAdded(TreeNode node) {
    // React only if *Glyphs are added
    if (node instanceof SpeciesGlyph) {
      SpeciesGlyph speciesGlyph = (SpeciesGlyph) node;

      /*if (this.layout != this.tabmanager.getCurrentLayout()) {
        logger.info("glyph not relevant to this layout");
        return;
      }*/
      
      BoundingBox boundingBox = speciesGlyph.getBoundingBox();
      Species s = layout.getModel().getSpecies(speciesGlyph.getSpecies());
      //FIXME speciesGlyph hat kein Model
      //Species s = speciesGlyph.getModel().getSpecies(speciesGlyph.getSpecies());
      
      if (boundingBox != null) {
        Node n = panel.getConverter().createNode(speciesGlyph.getId(),
            s.getName(),
            s.getSBOTerm(),
            boundingBox.getPosition().getX(),
            boundingBox.getPosition().getY(),
            boundingBox.getDimensions().getWidth(),
            boundingBox.getDimensions().getHeight());
        panel.getGraph2DView().updateView();
        speciesGlyph.putUserObject(SBMLEditorConstants.GLYPH_NODE_KEY, n);
      }
    }
  }

  /* (non-Javadoc)
   * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    logger.info(evt.getPropertyName());
    if(evt.getPropertyName().equals("nodeDelete")){
      Node node = (Node) evt.getNewValue();
      this.panel.getGraph2DView().getGraph2D().removeNode(node);
      panel.getGraph2DView().updateView();
      logger.info("CVS : Removed node");
      
    } else if (evt.getPropertyName().equals("reactionCreated")) {
     
      ArrayList<Object> list = (ArrayList<Object>) evt.getNewValue();
      Node source = (Node) list.get(0);
      Node target = (Node) list.get(1);
      ReactionGlyph reactionGlyph = (ReactionGlyph) list.get(2);
      
      Reaction reaction = (Reaction) reactionGlyph.getReactionInstance();
      
      SBMLCreateEdgeMode createEdgeMode = (SBMLCreateEdgeMode) editMode.getCreateEdgeMode();
      Node reactionNode = createEdgeMode.createEdgeNode(panel.getGraph2DView().getGraph2D(), source, target,
         new GenericEdgeRealizer(), reaction.getReversible());
      reactionGlyph.putUserObject(SBMLEditorConstants.GLYPH_NODE_KEY, reactionNode);
      logger.info("CVS : Reaction Drawn");
      
    } else if (evt.getPropertyName().equals("modifierCreated")) {
      ArrayList<Object> list = (ArrayList<Object>) evt.getNewValue();
      Node source = (Node) list.get(0);
      Node target = (Node) list.get(1);
      String state = (String) list.get(2);
      SBMLCreateEdgeMode createEdgeMode = (SBMLCreateEdgeMode) editMode.getCreateEdgeMode();
      createEdgeMode.createEdge(panel.getGraph2DView().getGraph2D(), source, target,
        new GenericEdgeRealizer(), state);
      logger.info("CVS : Modifier Drawn");
    }
    
    else {
      logger.info("CVS : Unknown property change event");
    }
  }

  @Override
  public void nodeRemoved(TreeNodeRemovedEvent evt) {
	  logger.info("Node Removed Event in CVS");

  }


}
