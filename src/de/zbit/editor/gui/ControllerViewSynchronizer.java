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
import java.util.List;
import java.util.logging.Logger;

import javax.swing.tree.TreeNode;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.ext.layout.BoundingBox;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.ReactionGlyph;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceRole;
import org.sbml.jsbml.util.TreeNodeChangeListener;
import org.sbml.jsbml.util.TreeNodeRemovedEvent;

import y.base.Node;
import y.view.GenericEdgeRealizer;
import de.zbit.editor.BioModelsEdConstants;


/**
 * Updates the view on changes in the model.
 * 
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Eugen Netz
 * @author Jan Rudolph
 * @version $Rev$
 */
public class ControllerViewSynchronizer implements TreeNodeChangeListener {

  private GraphLayoutPanel panel;
  private SBMLEditMode editMode;
  private Layout layout;
  private Logger logger = Logger.getLogger(ControllerViewSynchronizer.class.getName());
  
  /**
   * Constructor.
   * @param panel
   * @param layout
   * @param editMode
   */
  public ControllerViewSynchronizer(GraphLayoutPanel panel, Layout layout, SBMLEditMode editMode) {
    this.panel = panel;
    this.layout = layout;
    this.editMode = editMode;
  }
  
  /**
   * Adds a node to the view after the corresponding Glyph was added to the model.
   * @param the added node in the model
   */
  @Override
  public void nodeAdded(TreeNode node) {
  	logger.info("node added: " + node.getClass().getName());
    // React only if *Glyphs are added
    if (node instanceof SpeciesGlyph) {
      SpeciesGlyph speciesGlyph = (SpeciesGlyph) node;
      BoundingBox boundingBox = speciesGlyph.getBoundingBox();
      Species s = layout.getModel().getSpecies(speciesGlyph.getSpecies());
      if (boundingBox != null) {
        Node n = panel.getConverter().createNode(speciesGlyph.getId(),
            s.getName(),
            s.getSBOTerm(),
            boundingBox.getPosition().getX(),
            boundingBox.getPosition().getY(),
            boundingBox.getDimensions().getWidth(),
            boundingBox.getDimensions().getHeight());
        panel.getGraph2DView().updateView();
        speciesGlyph.putUserObject(BioModelsEdConstants.GLYPH_NODE_KEY, n);
      }
    }
    else if (node instanceof ReactionGlyph) {
      ReactionGlyph reactionGlyph = (ReactionGlyph) node;
      Reaction reaction = (Reaction) reactionGlyph.getReactionInstance();
      ListOf<SpeciesReferenceGlyph> listOfSpeciesReferenceGlyphs = reactionGlyph.getListOfSpeciesReferenceGlyphs();
      Node source = null;
      Node target = null;
      for (SpeciesReferenceGlyph glyph : listOfSpeciesReferenceGlyphs) {
      	SpeciesReferenceRole role = glyph.getSpeciesReferenceRole();
      	if (role.equals(SpeciesReferenceRole.SUBSTRATE)) {
      		source = (Node) glyph.getSpeciesGlyphInstance().getUserObject(BioModelsEdConstants.GLYPH_NODE_KEY);
      	}
      	if (role.equals(SpeciesReferenceRole.PRODUCT)) {
      		target = (Node) glyph.getSpeciesGlyphInstance().getUserObject(BioModelsEdConstants.GLYPH_NODE_KEY);
      	}
      }
      if (source == null || target == null) {
      	logger.info("reaction creation failed, source: " + source + " , target: " + target);
      	return;
      }
      SBMLCreateEdgeMode createEdgeMode = (SBMLCreateEdgeMode) editMode.getCreateEdgeMode();
			Node reactionNode = createEdgeMode.createEdgeNode(panel.getGraph2DView().getGraph2D(), 
      	source, 
      	target,
         new GenericEdgeRealizer(), reaction.getReversible());
      reactionGlyph.putUserObject(BioModelsEdConstants.GLYPH_NODE_KEY, reactionNode);
      logger.info("CVS : Reaction Drawn");
    }
    else if (node instanceof ListOf<?>) {
    	//TODO
    }
    else if (node instanceof SpeciesReferenceGlyph) {
    	/*SpeciesReferenceGlyph referenceGlyph = (SpeciesReferenceGlyph) node;
    	// Navigating ReactionGlyph <- ListOf<SpeciesReferenceGlyph> <- SpeciesReferenceGlyph
    	ReactionGlyph reactionGlyph = (ReactionGlyph) referenceGlyph.getParentSBMLObject().getParentSBMLObject();
    	SpeciesReferenceRole role = referenceGlyph.getSpeciesReferenceRole();
    	
    	Node source = referenceGlyph.getUserObject(BioModelsEdConstants.GLYPH_NODE_KEY);
      SBMLCreateEdgeMode createEdgeMode = (SBMLCreateEdgeMode) editMode.getCreateEdgeMode();
      createEdgeMode.createEdge(panel.getGraph2DView().getGraph2D(), source, target,
        new GenericEdgeRealizer(), reactionGlyph.getS);
      logger.info("CVS : Modifier Drawn"); */
    }
  }

  /**
   * Updates the view on several changes.
   */
  @SuppressWarnings("unchecked")
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    logger.info(evt.getPropertyName());
    if (evt.getPropertyName().equals("modifierCreated")) {
      
    }
    
    else if (evt.getPropertyName().equals("nodeRenamed")) {
      Species species = (Species) evt.getNewValue();
      
      logger.info("rename species with id " + species.getId() + " to " + species.getName());
      
      List<SpeciesGlyph> listOfGlyphs = layout.getListOfSpeciesGlyphs();
      for (SpeciesGlyph glyph : listOfGlyphs) {
        if (glyph.isSetSpecies() && glyph.getSpecies().equals(species.getId())) {
          Node n = (Node) glyph.getUserObject(BioModelsEdConstants.GLYPH_NODE_KEY);
          this.panel.getGraph2DView().getGraph2D().setLabelText(n, species.getName());
        }
      }

      this.panel.getGraph2DView().updateView();
    }
    
    else {
      logger.info("not handled");
    }
  }

  /**
   * Determines the actions taken, after a node was removed from the model.
   */
  @Override
  public void nodeRemoved(TreeNodeRemovedEvent evt) {
    logger.info("..not yet implemented, source: " + evt.getSource().getClass());
  }
}
