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

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.layout.BoundingBox;
import org.sbml.jsbml.ext.layout.Dimensions;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.Point;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;

import y.base.Node;
import de.zbit.editor.SBMLEditorConstants;

/**
 * @author Andreas Dr&aum;ger
 * @since 1.0
 * @version $Rev$
 */
public class Layout2GraphML extends SB_2GraphML<Layout> {

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
		initSpeciesGlyphs(layout);
		initReactionGlyphs(layout);
		initTextGlyphs(layout);
	}

	/**
	 * @param layout
	 */
	private void initTextGlyphs(Layout layout) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param layout
	 */
	private void initReactionGlyphs(Layout layout) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param layout
	 */
	private void initSpeciesGlyphs(Layout layout) {
		ListOf<SpeciesGlyph> list = layout.getListOfSpeciesGlyphs();
		for(SpeciesGlyph glyph: list){
		
		String id = glyph.getSpecies();
		
		Species species = layout.getModel().getSpecies(id);
		
		String name = species.getName();
		BoundingBox bb = glyph.getBoundingBox();
		Dimensions dimensions = bb.getDimensions();
		Point point = bb.getPosition();
		Node n = createNode(id, name, species.getSBOTerm(), point.getX(), point.getY(), dimensions.getWidth(), dimensions.getHeight());
		glyph.putUserObject(SBMLEditorConstants.GLYPH_NODE_KEY, n);
		}
	}

	/**
	 * @param layout
	 */
	private void initCompartments(Layout layout) {
			
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
