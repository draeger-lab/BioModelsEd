/*
 * $Id:  ActionCreateSBO.java 10:40:35 AM jrudolph $
 * $URL: ActionCreateSBO.java $
 * ---------------------------------------------------------------------
 * This file is part of BioModelsEd.
 *
 * Copyright (C) 20012-2012 by the University of Tuebingen, Germany.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.ext.layout.TextGlyph;
import org.sbml.jsbml.util.ValuePair;

import de.zbit.editor.BioModelsEdConstants;
import de.zbit.io.OpenedFile;

/**
 * @author Jan Rudolph
 * @version $Rev$
 */
public class ActionCreateSBO extends Action {
	
	Layout layout;
	Integer sboTerm;
	ValuePair<Double, Double> mousePosition;
	
	/**
	 * @param file
	 * @param layout
	 * @param sboTerm
	 * @param mousePosition
	 */
	public ActionCreateSBO(OpenedFile<SBMLDocument> file, Layout layout,
		Integer sboTerm, ValuePair<Double, Double> mousePosition) {
		super(file);
		this.layout = layout;
		this.sboTerm = sboTerm;
		this.mousePosition = mousePosition;
	}



	/* (non-Javadoc)
	 * @see de.zbit.editor.control.Action#isReady()
	 */
	@Override
	public boolean isReady() {
		return super.isReady() 
				&& (layout != null) 
				&& (sboTerm != null) 
				&& (mousePosition != null);
	}



	/**
	 * @param layout the layout to set
	 */
	public void setLayout(Layout layout) {
		this.layout = layout;
		execute();
	}



	/**
	 * @param sboTerm the sboTerm to set
	 */
	public void setSboTerm(Integer sboTerm) {
		this.sboTerm = sboTerm;
		execute();
	}



	/**
	 * @param mousePosition the mousePosition to set
	 */
	public void setMousePosition(ValuePair<Double, Double> mousePosition) {
		this.mousePosition = mousePosition;
		execute();
	}



	/* (non-Javadoc)
	 * @see de.zbit.editor.control.Action#execute()
	 */
	@Override
	public void execute() {
		if (isReady()) {
	    // generate generic id
	    String speciesId = SBMLTools.getNextGenericId(file, BioModelsEdConstants.genericId);

	    Double x = mousePosition.getL();
	    Double y = mousePosition.getV();

	    // layout and model references
	    Model model = layout.getModel();
	    final int level = model.getLevel();
	    final int version = model.getVersion();

	    String glyphId = SBMLTools.getNextGenericId(file, BioModelsEdConstants.genericGlyphIdPrefix);
	    String textglyphId = SBMLTools.getNextGenericId(file, BioModelsEdConstants.genericTextGlyphIdPrefix);

	    String compartmentId = SBMLTools.findCompartmentId(x, y);
	    Species s = SBMLFactory.createSpecies(speciesId, "s", sboTerm, level, version, compartmentId);
	    SpeciesGlyph sGlyph = SBMLFactory.createSpeciesGlyph(glyphId , level, version, speciesId);
	    TextGlyph tGlyph = SBMLFactory.createTextGlyph(textglyphId, level, version, sGlyph, speciesId);

	    model.addSpecies(s);
	    SBMLFactory.addSpeciesGlyphToLayout(layout, sGlyph, x, y, s.getName());
	    layout.addTextGlyph(tGlyph);

	    file.setChanged(true);

	    // keep a list of all glyphs which are associated with the species
	    List<String> glyphList = new ArrayList<String>();
	    glyphList.add(sGlyph.getId());

	    Map<String, List<String>> layoutGlyphMap = new HashMap<String, List<String>>();
	    layoutGlyphMap.put(layout.getId(), glyphList);

	    s.putUserObject(BioModelsEdConstants.GLYPH_LINK_KEY, layoutGlyphMap);
		}
	}
	
}
