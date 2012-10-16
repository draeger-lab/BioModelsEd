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
package de.zbit.graph;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.SBO;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.layout.ExtendedLayoutModel;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.LayoutConstants;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;

import de.zbit.editor.BioModelsEdConstants;


/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class GlyphTest {
  public SBMLDocument doc = new SBMLDocument(3, 1);

  private void test() {
    Model model = doc.createModel("myModel");

    ExtendedLayoutModel extendedLayoutModel = new ExtendedLayoutModel(model);
    model.addExtension(LayoutConstants.namespaceURI, extendedLayoutModel);
    Layout layout = extendedLayoutModel.createLayout("defaultLayout");
    
    Compartment compartment = model.createCompartment("c1");
    
    Species s = new Species("s1");
    s.setName("name");
    s.setLevel(model.getLevel());
    s.setVersion(model.getVersion());
    s.setSBOTerm(SBO.getIon());
    s.setCompartment("c1");

    SpeciesGlyph sGlyph = layout.createSpeciesGlyph("glyph_s1");
    sGlyph.setId("glyph_s1");
    sGlyph.setSpecies("s1");
    sGlyph.setBoundingBox(sGlyph.createBoundingBox(
        BioModelsEdConstants.glyphDefaultWidth,
        BioModelsEdConstants.glyphDefaultHeight,
        BioModelsEdConstants.glyphDefaultDepth,
        10,
        10,
        BioModelsEdConstants.glyphDefaultZ));
    layout.add(sGlyph);

    model.addSpecies(s);
  }
  
  public static void main(String[] args) throws SBMLException, XMLStreamException {
    GlyphTest testCase = new GlyphTest();
    testCase.test();
    SBMLWriter.write(testCase.doc, System.out, '\t', (short) 1);
  }
}
