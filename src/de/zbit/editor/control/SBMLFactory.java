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
package de.zbit.editor.control;

import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;

import de.zbit.editor.SBMLEditorConstants;

/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class SBMLFactory {

  /**
   * @param id
   * @param name
   * @param sboTerm
   * @param level
   * @param version
   * @return
   */
	
	//TODO Species only with Compartment
  public static Species createSpecies(String id, String name,
    int sboTerm, int level, int version) {
    Species s = new Species(id, name, level, version);
    s.setSBOTerm(sboTerm);
    return s;
  }


  public static SpeciesGlyph createSpeciesGlyph(String id, int level,
    int version, String speciesId) {
    SpeciesGlyph sg = new SpeciesGlyph(id, level, version);
    sg.setSpecies(speciesId);
    return sg;
  }
  
  public static SpeciesGlyph addSpeciesGlyphToLayout(Layout layout, SpeciesGlyph sGlyph, double x, double y, String name) {
    sGlyph.setName(name);
    sGlyph.setBoundingBox(sGlyph.createBoundingBox(
      SBMLEditorConstants.glyphDefaultWidth,
      SBMLEditorConstants.glyphDefaultHeight,
      SBMLEditorConstants.glyphDefaultDepth,
      x,
      y,
      SBMLEditorConstants.glyphDefaultZ));
    layout.addSpeciesGlyph(sGlyph);
    return sGlyph;
  }
}
