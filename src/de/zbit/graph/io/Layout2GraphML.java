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
package de.zbit.graph.io;

import java.util.List;
import java.util.logging.Logger;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.layout.BoundingBox;
import org.sbml.jsbml.ext.layout.CompartmentGlyph;
import org.sbml.jsbml.ext.layout.Dimensions;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.Point;
import org.sbml.jsbml.ext.layout.ReactionGlyph;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceRole;
import org.sbml.jsbml.ext.layout.TextGlyph;

import y.base.Node;
import y.view.GenericEdgeRealizer;
import de.zbit.editor.SBMLEditorConstants;
import de.zbit.editor.gui.SBMLCreateEdgeMode;

/**
 * @author Andreas Dr&aum;ger
 * @since 1.0
 * @version $Rev$
 */
public class Layout2GraphML extends SB_2GraphML<Layout> {
  
  private Logger logger = Logger.getLogger(Layout2GraphML.class.getName());

	/**
	 * 
	 */
	public Layout2GraphML() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.zbit.graph.io.SB_2GraphML#createNodesAndEdges(java.lang.Object)
	 */
	@Override
	protected void createNodesAndEdges(Layout layout) {
    if (layout == null
        || layout.getSBMLDocument() == null
        || !layout.getSBMLDocument().isSetModel()) {
      return;
    }
		initCompartments(layout);
		initTextGlyphs(layout);
		initSpeciesGlyphs(layout);
		//TODO Initialize Reaction Modifiers
		initReactionGlyphs(layout);
	}

	/**
	 * @param layout
	 */
	private void initTextGlyphs(Layout layout) {
	  ListOf<TextGlyph> list = layout.getListOfTextGlyphs();
	  for (TextGlyph textGlyph : list) {
	    SpeciesGlyph s = layout.getSpeciesGlyph(textGlyph.getGraphicalObject());
	    if (s != null) {
	      s.putUserObject(SBMLEditorConstants.GRAPHOBJECT_TEXTGLYPH_KEY, textGlyph);
	    }
	  }
	}

	/**
	 * @param layout
	 */
	private void initReactionGlyphs(Layout layout) {
	  ListOf<ReactionGlyph> list = layout.getListOfReactionGlyphs();
	  for (ReactionGlyph r : list) {
	    Node source = null;
	    Node target = null;
	    ListOf<SpeciesReferenceGlyph> refList = r.getListOfSpeciesReferenceGlyphs();
	    for (SpeciesReferenceGlyph sRef : refList) {
	      if (sRef.getSpeciesReferenceRole() == SpeciesReferenceRole.SUBSTRATE) {
	        source = (Node) sRef.getSpeciesGlyphInstance().getUserObject(SBMLEditorConstants.GLYPH_NODE_KEY);
	      }
	      if (sRef.getSpeciesReferenceRole() == SpeciesReferenceRole.PRODUCT) {
	        target = (Node) sRef.getSpeciesGlyphInstance().getUserObject(SBMLEditorConstants.GLYPH_NODE_KEY);
	      }
	    }
	    SBMLCreateEdgeMode createEdgeMode = (SBMLCreateEdgeMode) this.editMode.getCreateEdgeMode();
	    createEdgeMode.createEdgeNode(this.simpleGraph, source, target, new GenericEdgeRealizer(), null);	
	  }
	}

	/**
	 * @param layout
	 */
	private void initSpeciesGlyphs(Layout layout) {
	  ListOf<SpeciesGlyph> list = layout.getListOfSpeciesGlyphs();
	  for (SpeciesGlyph glyph : list) {

	    String speciesId = glyph.getSpecies();
	    Species species = layout.getModel().getSpecies(speciesId);
	    if (species == null) {
	      continue;
	    }

	    String name = "(" + species.getName() + ")";
	    TextGlyph textGlyph = (TextGlyph) glyph.getUserObject(SBMLEditorConstants.GRAPHOBJECT_TEXTGLYPH_KEY);
	    if (textGlyph != null) {
	      name = textGlyph.getText();
	    }
	    
	    Node n;
	    if (glyph.isSetBoundingBox() && glyph.getBoundingBox().isSetPosition()
	        && glyph.getBoundingBox().isSetDimensions()) {
	      BoundingBox bb = glyph.getBoundingBox();
	      Dimensions dimensions = bb.getDimensions();
	      Point point = bb.getPosition();
	      n = createNode(speciesId, name, species.getSBOTerm(), point.getX(), point.getY(), dimensions.getWidth(), dimensions.getHeight());
	    }
	    else {
	      n = createNode(speciesId, name, species.getSBOTerm());
	    }
	    glyph.putUserObject(SBMLEditorConstants.GLYPH_NODE_KEY, n);
	  }
	}

	/**
	 * @param layout
	 */
	private void initCompartments(Layout layout) {
			List<CompartmentGlyph> compartments = layout.getListOfCompartmentGlyphs();
			if (compartments.size() == 1) {
			  // found one compartment, the default compartment, do not draw it
			  return;
			}
			else {
			  // TODO
			  // String outmostCompartmentId = getOutmostCompartmentId(compartments);
			  String outmostCompartmentId = "outmost";
			  for (CompartmentGlyph c : compartments) {
			    // TODO order: outmost compartment -> innermost compartment
			    if (c.getId().equals(outmostCompartmentId)) {
			      continue;
			    }
			    Node n;
			    if (c.isSetBoundingBox() && c.getBoundingBox().isSetDimensions()
			        && c.getBoundingBox().isSetPosition()) {
			      BoundingBox bb = c.getBoundingBox();
			      Dimensions dimensions = bb.getDimensions();
			      Point point = bb.getPosition();
			      n = createNode(c.getId(), c.getName(), c.getSBOTerm(),
			          point.getX(), point.getY(), dimensions.getWidth(), dimensions.getHeight());
			    }
			    else {
			      n = createNode(c.getId(), c.getName(), c.getSBOTerm());
			    }
			    c.putUserObject(SBMLEditorConstants.GLYPH_NODE_KEY, n);
			  }
			}
	}
	
	/**
	 * @param a
	 * @param b
	 * @return whether {@link BoundingBox} a contains {@link BoundingBox} b 
	 */
	private static boolean containsBoundingBox(BoundingBox a, BoundingBox b) {
	  // TODO
	  return false;
	}

	/* (non-Javadoc)
	 * @see de.zbit.graph.io.SB_2GraphML#isAnyLayoutInformationAvailable()
	 */
	@Override
	protected boolean isAnyLayoutInformationAvailable() {
		// TODO Auto-generated method stub
		return false;
	}

}
