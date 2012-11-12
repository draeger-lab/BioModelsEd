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
import java.util.List;
import java.util.logging.Logger;

import javax.swing.tree.TreeNode;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBO;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.layout.BoundingBox;
import org.sbml.jsbml.ext.layout.GraphicalObject;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.ReactionGlyph;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceRole;
import org.sbml.jsbml.util.TreeNodeChangeListener;
import org.sbml.jsbml.util.TreeNodeRemovedEvent;

import y.base.Node;
import y.view.GenericEdgeRealizer;
import de.zbit.editor.Constants;


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
        speciesGlyph.putUserObject(Constants.GLYPH_NODE_KEY, n);
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
      	switch (role) {
      		case SUBSTRATE: source = getNode(glyph.getSpeciesGlyphInstance()); break;
      		case PRODUCT: target = getNode(glyph.getSpeciesGlyphInstance()); break;
      	}
      }
      if (source == null || target == null) {
      	logger.info("reaction creation failed, source: " + source + " , target: " + target);
      	return;
      }
      SBMLCreateEdgeMode createEdgeMode = (SBMLCreateEdgeMode) editMode.getCreateEdgeMode();
			Node reactionNode = createEdgeMode.createEdgeNode(panel.getGraph2DView().getGraph2D(), 
      	source, target, new GenericEdgeRealizer(), reaction.getReversible());
      reactionGlyph.putUserObject(Constants.GLYPH_NODE_KEY, reactionNode);
      logger.info("CVS : Reaction Drawn");
    }
    else if (node instanceof ListOf<?>) {
    	//TODO
    }
    else if (node instanceof SpeciesReferenceGlyph) {
    	SpeciesReferenceGlyph referenceGlyph = (SpeciesReferenceGlyph) node;
    	
    	// Navigating ReactionGlyph <- ListOf<SpeciesReferenceGlyph> <- SpeciesReferenceGlyph
    	ReactionGlyph reactionGlyph = (ReactionGlyph) referenceGlyph.getParentSBMLObject().getParentSBMLObject();
    	//TODO get role from SpeciesReference 
    	int sbo = getSBO(referenceGlyph.getSpeciesReferenceRole());
    	
    	Node source = getNode(referenceGlyph.getSpeciesGlyphInstance());
    	Node target = getNode(reactionGlyph);
      SBMLCreateEdgeMode createEdgeMode = (SBMLCreateEdgeMode) editMode.getCreateEdgeMode();
      createEdgeMode.createEdge(panel.getGraph2DView().getGraph2D(), source, target,
        new GenericEdgeRealizer(), sbo);
      logger.info("CVS : Modifier Drawn"); 
    }
  }

  /**
   * wraps Node extraction from graphical objects
	 * @param GraphicalObject
	 * @return
	 */
	private Node getNode(GraphicalObject g) {
		return (Node) g.getUserObject(Constants.GLYPH_NODE_KEY);
	}

	/**
	 * @param speciesReferenceRole
	 * @return
	 */
	private int getSBO(SpeciesReferenceRole role) {
		switch (role) {
			// TODO catalyst vs. catalytic activator, changes affect SBMLCreateEdgeNode
			case ACTIVATOR : return SBO.getCatalyst();
			case INHIBITOR : return SBO.getInhibitor(); 
			case MODIFIER : return SBO.getModifier(); 
			case PRODUCT : return SBO.getProduct(); 
			case SIDEPRODUCT : return SBO.getProduct(); 
			case SIDESUBSTRATE : return SBO.getReactant(); 
			case SUBSTRATE : return SBO.getReactant();
			// TODO not sure about default value
			case UNDEFINED : return SBO.getParticipant();
		}
		return 0;
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
          Node n = getNode(glyph);
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
