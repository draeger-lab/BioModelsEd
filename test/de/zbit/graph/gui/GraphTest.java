/* $Id$
 * $URL$
 * ---------------------------------------------------------------------
 * This file is part of the SysBio API library.
 *
 * Copyright (C) 2011 by the University of Tuebingen, Germany.
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation. A copy of the license
 * agreement is provided in the file named "LICENSE.txt" included with
 * this software distribution and also available online as
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 * ---------------------------------------------------------------------
 */
package de.zbit.graph.gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.SBO;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.layout.ExtendedLayoutModel;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.LayoutConstants;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.ext.render.RenderConstants;
import org.sbml.jsbml.ext.render.RenderModelPlugin;

import y.view.EditMode;
import y.view.Graph2DView;
import y.view.ViewMode;

/**
 * @author Andreas Dr&auml;ger
 * @date 16:03:47
 * @since 1.1
 * @version $Rev$
 */
public class GraphTest {
	
	/**
	 * @param args
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Throwable {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		JFrame f = new JFrame("Graph Test");
		
		int level = 3, version = 1;
		
		SBMLDocument doc = new SBMLDocument(level, version);
		
		Model model = doc.createModel("m1");
		Compartment c = model.createCompartment("c1");
		Species s1 = model.createSpecies("s1", "Species 1", c);
		s1.setSBOTerm(SBO.getComplex());
		Species s2 = model.createSpecies("s2", "Species 2", c);
		Reaction r1 = model.createReaction("r1");
		r1.setReversible(false);
		r1.createReactant(s1).setStoichiometry(1f);
		r1.createProduct(s2).setStoichiometry(2d);
		
		ExtendedLayoutModel extLayout = new ExtendedLayoutModel(model);
		Layout layout = extLayout.createLayout();
		
		RenderModelPlugin render = new RenderModelPlugin(extLayout.getListOfLayouts());
		extLayout.getListOfLayouts().addExtension(RenderConstants.namespaceURI, render);
		SpeciesGlyph sGlyph = layout.createSpeciesGlyph("glyph_" + s1.getId(), s1.getId());
		sGlyph.createBoundingBox(60, 60, 10);
		model.addExtension(LayoutConstants.namespaceURI, extLayout);
		
		SBMLWriter.write(doc, System.out, ' ', (short) 2);
		
		TranslatorSBMLgraphPanel panel = new TranslatorSBMLgraphPanel(doc, false);
		Graph2DView view = panel.getGraph2DView();
		view.removeViewMode((ViewMode) view.getViewModes().next());
		EditMode editMode = new SBGNEditMode<SBMLDocument>(panel.getConverter());
		editMode.showNodeTips(true);
		view.addViewMode(editMode);
		f.getContentPane().add(panel);
		f.setMinimumSize(new Dimension(640, 480));
		f.pack();
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
}
