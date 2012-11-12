/*
 * $Id$
 * $URL$
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

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.ModifierSpeciesReference;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.layout.ExtendedLayoutModel;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.LayoutConstants;
import org.sbml.jsbml.ext.layout.ReactionGlyph;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceGlyph;
import org.sbml.jsbml.ext.layout.TextGlyph;

import de.zbit.editor.Constants;
import de.zbit.io.OpenedFile;

/**
 * This class provides Methods for extracting information from SBMLDocuments
 * @author Jan Rudoplph
 * @version $Rev$
 */
public class SBMLTools {
	private static Logger logger = Logger.getLogger(SBMLTools.class.getName());

	/**
	 * @param selectedDoc
	 * @return
	 */
	public static String getNextGenericId(OpenedFile<SBMLDocument> selectedDoc,
		String genericId) {
		// TODO make it fancy
		Random random = new Random();
		return genericId + random.nextInt(100000);
	}

	/**
	 * returning first layout if existing, otherwise creating new layout
	 * @param doc
	 * @return
	 */
	public static Layout getOrCreateDefaultLayout(OpenedFile<SBMLDocument> doc) {
		SBMLDocument sbmlDoc = doc.getDocument();
		if (sbmlDoc == null) return null;
		Model model = getOrCreateModel(sbmlDoc);
		ExtendedLayoutModel layoutModel = getOrCreateExtendedLayoutModel(model);
		List<Layout> listOfLayouts = getOrCreateListOfLayouts(layoutModel);
		logger.info("Layout(s) found, returning first");
		return listOfLayouts.get(0);
	}

	/**
	 * @param doc
	 * @return
	 */
	public static boolean hasLayout(OpenedFile<SBMLDocument> doc) {
		SBMLDocument sbmlDoc = doc.getDocument();
		if (sbmlDoc == null)  {
			return false;
		}
		Model model = sbmlDoc.getModel();
		if (model == null) {
			return false;
		}
		ExtendedLayoutModel extLayoutModel = getExtendedLayoutModel(model);
		if (extLayoutModel == null) {
			return false;
		}
		return extLayoutModel.getLayoutCount() > 0;
	}

	/**
	 * Extracts extended Layout Model
	 * @param model
	 * @return
	 */
	private static ExtendedLayoutModel getExtendedLayoutModel(Model model) {
		ExtendedLayoutModel extLayoutModel = (ExtendedLayoutModel)model.getExtension(
			LayoutConstants.getNamespaceURI(model.getLevel(), model.getVersion()));
		return extLayoutModel;
	}

	/**
	 * gets List of Layouts, adds Layout if List null or empty
	 * @param layoutModel
	 * @return
	 */
	private static List<Layout> getOrCreateListOfLayouts(ExtendedLayoutModel layoutModel) {
		List<Layout> listOfLayouts = layoutModel.getListOfLayouts();
		if (listOfLayouts == null || listOfLayouts.isEmpty()) {
			logger.info("creating Layout");
			layoutModel.createLayout();
		}
		return listOfLayouts;
	}

	/**
	 * extracts ExtendedLayoutModel, creates one if not present
	 * @param model
	 * @return
	 */
	private static ExtendedLayoutModel getOrCreateExtendedLayoutModel(Model model) {
		ExtendedLayoutModel layoutModel = getExtendedLayoutModel(model);
		if (layoutModel == null) {
			logger.info("creating ExtendedLayoutModel");
			ExtendedLayoutModel newExtLayout = new ExtendedLayoutModel(model);
			model.addExtension(
				LayoutConstants.getNamespaceURI(model.getLevel(), model.getVersion()),
				newExtLayout);
			layoutModel = newExtLayout;
		}
		return layoutModel;
	}

	/**
	 * extracts Model from SBMLDocument, creates one if not present
	 * @param sbmlDoc
	 * @return
	 */
	private static Model getOrCreateModel(SBMLDocument sbmlDoc) {
		Model model = sbmlDoc.getModel();
		if (model == null) {
			logger.info("creating Model");
			sbmlDoc.createModel(Constants.genricModelId);
			model = sbmlDoc.getModel();
		}
		return model;
	}

	/**
	 * creates glyphs for all species in the document
	 * @param doc
	 */
	public static void createLayoutInformation(OpenedFile<SBMLDocument> doc) {
		Layout layout = getOrCreateDefaultLayout(doc);
		Model model = layout.getModel();
		List<Compartment> compartments = model.getListOfCompartments();
    for (Compartment c : compartments) {
      layout.createCompartmentGlyph(getNextGenericId(doc, Constants.genericGlyphIdPrefix), c.getId());
      logger.info("CompartmentGlyph created.");
    }
    
    List<Species> species = model.getListOfSpecies();
    for (Species s : species) {
      SpeciesGlyph sGlyph = SBMLFactory.createSpeciesGlyph(getNextGenericId(doc, Constants.genericGlyphIdPrefix),
        s.getLevel(), s.getVersion(), s.getId());
      layout.add(sGlyph);
      TextGlyph tGlyph = SBMLFactory.createTextGlyph(getNextGenericId(doc, Constants.genericTextGlyphIdPrefix),
        model.getLevel(), model.getVersion(), sGlyph, s.getId());
      layout.addTextGlyph(tGlyph);
      logger.info("SpeciesGlyph created.");
    }
    
    List<Reaction> reactions = model.getListOfReactions();
    for (Reaction r : reactions) {
      SpeciesGlyph source = null;
      SpeciesGlyph target = null;
      logger.info("Reaction read.");
      List<SpeciesGlyph> sGlyphs = layout.getListOfSpeciesGlyphs();
      for (SpeciesGlyph sGlyph : sGlyphs) {
        if (sGlyph.getSpecies().equals(r.getListOfReactants().get(0).getSpecies())) {
          source = sGlyph;
          r.getListOfReactants().get(sGlyph.getSpecies());
          logger.info("Source Glyph for ReactionGlyph set.");
        } else if (sGlyph.getSpecies().equals(r.getListOfProducts().get(0).getSpecies())) {
          target = sGlyph;
          logger.info("Target Glyph for ReactionGlyph set.");
        }
        if ((source != null) && (target != null)) {
          break;
        }
      }
      if ((source != null) && (target != null)) {
        ReactionGlyph rGlyph = SBMLFactory.createReactionGlyph(doc, r, source,
          target, r.getLevel(), r.getVersion());
        ListOf<ModifierSpeciesReference> MSRList = r.getListOfModifiers();
        for (ModifierSpeciesReference sR : MSRList) {
          SpeciesGlyph modSource = null;
          for (SpeciesGlyph sGlyph : layout.getListOfSpeciesGlyphs()) {
            if (sR.getSpecies().equals(sGlyph.getSpecies())) {
              modSource = sGlyph;
              break;
            }
          }
          if (modSource != null) {
            SpeciesReferenceGlyph srGlyph = SBMLFactory.createSpeciesReferenceGlyph(
                                              doc, modSource, rGlyph, sR.getSBOTerm());
            rGlyph.addSpeciesReferenceGlyph(srGlyph);
            logger.info("SpeciesReferenceGlyph created.");
          } else {
            logger.info("SpeciesReferenceGlyph not created.");
          }
        }
        
        layout.add(rGlyph);
        logger.info("ReactionGlyph created.");
      }
    }
	}

	/**
	 * @param selectedDoc
	 * @param speciesId
	 * @return
	 */
	public static boolean hasAnySpeciesGlyphForSpeciesId(
		OpenedFile<SBMLDocument> selectedDoc, String speciesId) {
		SBMLDocument doc = selectedDoc.getDocument();
		if (doc == null) return false;
		boolean hasAnyGlyph = false;
    for(Layout layout : getListOfLayouts(doc)) {
      for(SpeciesGlyph glyph : layout.getListOfSpeciesGlyphs()) {
        hasAnyGlyph |= glyph.getSpecies().equals(speciesId);
      }
    }
    return hasAnyGlyph;
	}

	/**
	 * @param selectedDoc
	 * @return
	 */
	public static String getDefaultCompartmentId(
		OpenedFile<SBMLDocument> selectedDoc) {
		SBMLDocument sbmlDoc = selectedDoc.getDocument();
		Model model = getOrCreateModel(sbmlDoc);
		Compartment c1 = model.getCompartment(0);
		if (c1 == null) {
			model.createCompartment(Constants.defaultCompartmentId);
			return Constants.defaultCompartmentId;
		}
		else {
			return c1.getId();
		}
	}

	/**
	 * @param documentFromLayout
	 * @return
	 */
	public static List<Layout> getListOfLayouts(SBMLDocument documentFromLayout) {
		Model model = documentFromLayout.getModel();
		if (hasLayout(model)) {
			return getOrCreateListOfLayouts(getOrCreateExtendedLayoutModel(model));
		}
		else {
			logger.info("No Layouts set for " + documentFromLayout);
			return null;
		}
	}

	/**
	 * checks if Layout extension is set for Model model
	 * @param model
	 * @return
	 */
	private static boolean hasLayout(Model model) {
		ExtendedLayoutModel extension = (ExtendedLayoutModel) model.getExtension(
			LayoutConstants.getNamespaceURI(model.getLevel(), model.getVersion()));
		return extension != null && extension.getLayout(0) != null;
	}


	/**
	 * returns the layout from {@code file} with id {@code layoutId}
	 * @param file
	 * @param layoutId
	 * @return
	 */
	public static Layout getLayout(OpenedFile<SBMLDocument> file, String layoutId) {
		SBMLDocument sbmlDoc = file.getDocument();
		Model model = sbmlDoc.getModel();
		return (Layout) model.findNamedSBase(layoutId);
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	public static String findCompartmentId(Double x, Double y) {
		// TODO implement this
		return Constants.defaultCompartmentId;
	}
	
}
