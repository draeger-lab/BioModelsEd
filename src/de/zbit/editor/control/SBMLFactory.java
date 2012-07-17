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

import java.util.List;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBO;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.ext.layout.BoundingBox;
import org.sbml.jsbml.ext.layout.CompartmentGlyph;
import org.sbml.jsbml.ext.layout.Dimensions;
import org.sbml.jsbml.ext.layout.GraphicalObject;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.Point;
import org.sbml.jsbml.ext.layout.ReactionGlyph;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceRole;
import org.sbml.jsbml.ext.layout.TextGlyph;

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
  public static Species createSpecies(String id, String name,
    int sboTerm, int level, int version, String compartmentId) {
    Species s = new Species(id, name, level, version);
    s.setSBOTerm(sboTerm);
    s.setCompartment(compartmentId);
    return s;
  }


  
  
  /**
   * adds Species Glyph to layout defining a new Boundingbox  by x and y
   * @param layout
   * @param sGlyph
   * @param x
   * @param y
   * @param name
   * @return
   */
  public static SpeciesGlyph addSpeciesGlyphToLayout(Layout layout, SpeciesGlyph sGlyph, double x, double y, String name) {
    return addSpeciesGlyphToLayout(layout, sGlyph, x, y, SBMLEditorConstants.glyphDefaultDepth, 
      SBMLEditorConstants.glyphDefaultWidth, SBMLEditorConstants.glyphDefaultHeight, 
      SBMLEditorConstants.glyphDefaultDepth, name);
  }
  
  /**
   * adds Species Glyph to layout defining a new Boundingbox  by x and y
   * @param layout
   * @param sGlyph
   * @param x
   * @param y
   * @param z
   * @param width
   * @param height
   * @param depth
   * @param name
   * @return
   */
  public static SpeciesGlyph addSpeciesGlyphToLayout(Layout layout, SpeciesGlyph sGlyph, double x, double y, double z,
    double width, double height, double depth, String name) {
    sGlyph.setName(name);
    sGlyph.createBoundingBox(width, height, depth, x, y, z);
    layout.addSpeciesGlyph(sGlyph);
    return sGlyph;
  }
  
  /**
   * @param id
   * @param level
   * @param version
   * @param graphicalObject
   * @param speciesId
   * @return
   */
  public static TextGlyph createTextGlyph(String id, int level, int version, GraphicalObject graphicalObject, String speciesId) {
    TextGlyph tg = new TextGlyph(id, level, version);
    tg.setGraphicalObject(graphicalObject);
    tg.setOriginOfText(speciesId);
    graphicalObject.putUserObject(SBMLEditorConstants.GRAPHOBJECT_TEXTGLYPH_KEY, tg);
    return tg;
  }
  
  /**
   * @param selectedDoc
   * @param source
   * @param target
   * @param reversible
   * @param level
   * @param version
   * @return
   */
  public static Reaction createReaction(OpenedSBMLDocument selectedDoc, Species source, Species target, boolean reversible, int level, int version){
    String id = selectedDoc.nextGenericId(SBMLEditorConstants.genericReactionIdPrefix);
    Reaction reaction = new Reaction(id, level, version);
    SpeciesReference sourceRef = new SpeciesReference(source);
    SpeciesReference targetRef = new SpeciesReference(target);
    reaction.addReactant(sourceRef);
    reaction.addProduct(targetRef);
    reaction.setReversible(reversible);
    reaction.setSBOTerm(SBO.getStateTransition());
    return reaction;
  }
  
  /**
   * @param selectedDoc
   * @param reaction
   * @param source
   * @param target
   * @param level
   * @param version
   * @return
   */
  public static ReactionGlyph createReactionGlyph(OpenedSBMLDocument selectedDoc, Reaction reaction, SpeciesGlyph source, SpeciesGlyph target, int level, int version) {
    String id = selectedDoc.nextGenericId(SBMLEditorConstants.genericReactionGlyphIdPrefix);
    ReactionGlyph reactionGlyph = new ReactionGlyph(id, level, version);
    if (source.isSetBoundingBox() && target.isSetBoundingBox()) {
      double x = source.getBoundingBox().getPosition().getX()
        + target.getBoundingBox().getPosition().getX() / 2d;
      double y = source.getBoundingBox().getPosition().getY()
        + target.getBoundingBox().getPosition().getY() / 2d;
      BoundingBox bb = new BoundingBox();
      bb.setLevel(source.getLevel());
      bb.setVersion(source.getVersion());
      bb.setDimensions(new Dimensions(10, 10, 0, source.getLevel(),
        source.getVersion()));
      bb.setPosition(new Point(x, y, 0, source.getLevel(), source.getVersion()));
      reactionGlyph.setBoundingBox(bb);
    }
    
    id = selectedDoc.nextGenericId(SBMLEditorConstants.genericSpeciesReferenceGlyphIdPrefix);
    SpeciesReferenceGlyph sourceRef = new SpeciesReferenceGlyph(id, level, version);
    sourceRef.setSpeciesGlyph(source.getId());
    sourceRef.setRole(SpeciesReferenceRole.SUBSTRATE);
    id = selectedDoc.nextGenericId(SBMLEditorConstants.genericSpeciesReferenceGlyphIdPrefix);
    SpeciesReferenceGlyph targetRef = new SpeciesReferenceGlyph(id, level, version);
    targetRef.setSpeciesGlyph(target.getId());
    targetRef.setRole(SpeciesReferenceRole.PRODUCT);
    
    
    reactionGlyph.addSpeciesReferenceGlyph(sourceRef);
    reactionGlyph.addSpeciesReferenceGlyph(targetRef);
    reactionGlyph.setSBOTerm(SBO.getStateTransition());
    reactionGlyph.setReaction(reaction);
    
    return reactionGlyph;
  }
  
  public static SpeciesReferenceGlyph createSpeciesReferenceGlyph(OpenedSBMLDocument doc, SpeciesGlyph source, ReactionGlyph target, int sbo) {
    SpeciesReferenceGlyph modifierGlyph = new SpeciesReferenceGlyph();
    if (sbo == SBO.getCatalyst()) {
      modifierGlyph.setRole(SpeciesReferenceRole.MODIFIER);
    } else if (sbo == SBO.getInhibitor()) {
      modifierGlyph.setRole(SpeciesReferenceRole.INHIBITOR);
    }
    Model model = doc.getDocument().getModel();
    
    modifierGlyph.setId(doc.nextGenericId(SBMLEditorConstants.genericModifierReferenceGlyphIdPrefix));
    modifierGlyph.setLevel(model.getLevel());
    modifierGlyph.setVersion(model.getVersion());
    if (sbo >= 0) {
      modifierGlyph.setSBOTerm(sbo);
    }
    modifierGlyph.setSpeciesGlyph(source.getId());
    modifierGlyph.setName(modifierGlyph.getId());
    
    return modifierGlyph;
  }
  
  /**
   * creates a CompartmentGlyph with BoundingBox
   * sets assoc. Compartment
   * @param id
   * @param level
   * @param version
   * @param compartment
   * @param x
   * @param y
   * @param width
   * @param height
   * @return
   */
  public static CompartmentGlyph createCompartmentGlyph(String id, int level, int version, 
    Double x, Double y, Double width, Double height, String compartment) {
    CompartmentGlyph cGlyph = createCompartmentGlyph(id, level, version, compartment);
    cGlyph.createBoundingBox(width, height, 0, x, y, 0);
    return cGlyph;
  }
  /**
   * creates a CompartmentGlyph with BoundingBox parsing string values
   * sets assoc. Compartment
   * @param id
   * @param level
   * @param version
   * @param x
   * @param y
   * @param width
   * @param height
   * @param compartment
   * @return
   */
  public static CompartmentGlyph createCompartmentGlyph(String id, int level, int version,
    String x, String y, String width, String height, String compartment) {
    if(x != null && y != null && width != null && height != null) {
      return createCompartmentGlyph(id, level, version, Double.parseDouble(x), 
        Double.parseDouble(y), Double.parseDouble(width), Double.parseDouble(height), compartment);
    }
    return createCompartmentGlyph(id, level, version, compartment);
  }
  
  /**
   * creates a CompartmentGlyph without BoundingBox
   * sets assoc. Compartment
   * @param id
   * @param level
   * @param version
   * @param compartment
   * @return
   */
  public static CompartmentGlyph createCompartmentGlyph(String id, int level, int version, 
    String compartment) {
    CompartmentGlyph cGlyph = new CompartmentGlyph(id, level, version);
    cGlyph.setCompartment(compartment);
    return cGlyph;
  }
  
  /**
   * Creates a ReactionGlyph without Boundingbox, 
   * adds SpeciesReferences, assoc. reaction and reaction type
   * TODO field ReactionGlyph.curve never used
   * @param id
   * @param level
   * @param version
   * @param listOfSpeciesReferenceGlyphs
   * @param x
   * @param y
   * @param width
   * @param height
   * @param reaction
   * @param reactionType
   * @return
   */
  public static ReactionGlyph createReactionGlyph(String id, int level, int version, 
    List<SpeciesReferenceGlyph> listOfSpeciesReferenceGlyphs, String reaction, int reactionType) {
    ReactionGlyph rGlyph = new ReactionGlyph(id, level, version);
    for(SpeciesReferenceGlyph glyph : listOfSpeciesReferenceGlyphs) {
      rGlyph.addSpeciesReferenceGlyph(glyph);
    }
    rGlyph.setReaction(reaction);
    rGlyph.setSBOTerm(reactionType);
    return rGlyph;
  }
  
  /**
   * creates a SpeciesReferenceGlyph with BoundingBox,
   * sets SpeciesReferenceRole
   * sets SpeciesReference (assoc. SpeciesGlyph)
   * @param id
   * @param level
   * @param version
   * @param x
   * @param y
   * @param width
   * @param height
   * @param role
   * @param speciesReference
   * @return
   */
  public static SpeciesReferenceGlyph createSpeciesReferenceGlyph(String id, int level, int version,
    double x, double y, double width, double height, SpeciesReferenceRole role, String speciesReference) {
    SpeciesReferenceGlyph srGlyph = createSpeciesReferenceGlyph(id, level, version, role, speciesReference);
    srGlyph.createBoundingBox(width, height, 0, x, y, 0);
    return srGlyph;
  }
  /**
   * creates a SpeciesReferenceGlyph with BoundingBox if valid values are given, 
   * parsing coordinates from String
   * sets SpeciesReferenceRole
   * sets SpeciesReference (assoc. SpeciesGlyph)
   * @param id
   * @param level
   * @param version
   * @param x
   * @param y
   * @param width
   * @param height
   * @param role
   * @param speciesReference
   * @return
   */
  public static SpeciesReferenceGlyph createSpeciesReferenceGlyph(String id, int level, int version,
    String x, String y, String width, String height, SpeciesReferenceRole role, String speciesReference) {
    if(x != null && y != null && width != null && height != null) {
      return createSpeciesReferenceGlyph(id, level, version, Double.parseDouble(x), 
        Double.parseDouble(y), Double.parseDouble(width), Double.parseDouble(height), role, speciesReference);
    }
    return createSpeciesReferenceGlyph(id, level, version, role, speciesReference);
  }
  /**
   * creates a SpeciesReferenceGlyph without BoundinBox
   * sets SpeciesReferenceRole
   * sets SpeciesReference (assoc. SpeciesGlyph)
   * @param id
   * @param level
   * @param version
   * @param role
   * @param speciesReference
   * @return
   */
  public static SpeciesReferenceGlyph createSpeciesReferenceGlyph(String id, int level, int version,
    SpeciesReferenceRole role, String speciesReference) {
    SpeciesReferenceGlyph srGlyph = new SpeciesReferenceGlyph(id, level, version);
    srGlyph.setRole(role);
    srGlyph.setSpeciesReference(speciesReference);
    return srGlyph;
  }
  
  /**
   * creates a SpeciesGlyph with BoundingBox
   * sets assoc. Species
   * @param id
   * @param level
   * @param version
   * @param x
   * @param y
   * @param width
   * @param height
   * @param species
   * @return
   */
  public static SpeciesGlyph createSpeciesGlyph(String id, int level,
    int version, double x, double y, double width, double height, String species) {
    SpeciesGlyph sg = createSpeciesGlyph(id, level, version, species);
    sg.createBoundingBox(width, height, 0, x, y, 0);
    return sg;
  }
  /**
   * creates a SpeciesGlyph with BoundingBox if given coordinates
   * as String are not null
   * sets assoc. Species
   * @param id
   * @param level
   * @param version
   * @param x
   * @param y
   * @param width
   * @param height
   * @param species
   * @return
   */
  public static SpeciesGlyph createSpeciesGlyph(String id, int level,
    int version, String x, String y, String width, String height, String species) {
    if(x != null && y != null && width != null && height != null) {
      return createSpeciesGlyph(id, level, version, Double.parseDouble(x), 
        Double.parseDouble(y), Double.parseDouble(width), Double.parseDouble(height), species);
    }
    return createSpeciesGlyph(id, level, version, species);
  }

  /**
   * creates a SpeciesGlyph without Boundingbox
   * sets species
   * @param id
   * @param level
   * @param version
   * @param species
   * @return
   */
  public static SpeciesGlyph createSpeciesGlyph(String id,
    int level, int version, String species) {
    SpeciesGlyph sg = new SpeciesGlyph(id, level, version);
    sg.setSpecies(species);
    return sg;
  }
}
