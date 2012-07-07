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

import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBO;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.ReactionGlyph;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceRole;

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
  
  public static Reaction createReaction(OpenedSBMLDocument selectedDoc, Species source, Species target, boolean reversible, int level, int version){
    String id = selectedDoc.nextGenericId("r");
    Reaction reaction = new Reaction(id, level, version);
    SpeciesReference sourceRef = new SpeciesReference(source);
    SpeciesReference targetRef = new SpeciesReference(target);
    reaction.addReactant(sourceRef);
    reaction.addProduct(targetRef);
    reaction.setReversible(reversible);
    reaction.setSBOTerm(SBO.getStateTransition());
    return reaction;
  }
  
  public static ReactionGlyph createReactionGlyph(OpenedSBMLDocument selectedDoc, Reaction reaction, SpeciesGlyph source, SpeciesGlyph target, int level, int version) {
    String id = selectedDoc.nextGenericId("rGlyph");
    ReactionGlyph reactionGlyph = new ReactionGlyph(id, level, version);
    id = selectedDoc.nextGenericId("rGlyphRef");
    SpeciesReferenceGlyph sourceRef = new SpeciesReferenceGlyph(id, level, version);
    sourceRef.setSpeciesGlyph(source.getId());
    sourceRef.setRole(SpeciesReferenceRole.SUBSTRATE);
    id = selectedDoc.nextGenericId("rGlyphRef");
    SpeciesReferenceGlyph targetRef = new SpeciesReferenceGlyph(id, level, version);
    targetRef.setSpeciesGlyph(target.getId());
    targetRef.setRole(SpeciesReferenceRole.PRODUCT);
    
    reactionGlyph.addSpeciesReferenceGlyph(sourceRef);
    reactionGlyph.addSpeciesReferenceGlyph(targetRef);
    reactionGlyph.setSBOTerm(SBO.getStateTransition());
    reactionGlyph.setReaction(reaction);
    
    return reactionGlyph;
  }
}
