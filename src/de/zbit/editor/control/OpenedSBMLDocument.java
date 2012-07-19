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

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.tree.TreeNode;

import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.ModifierSpeciesReference;
import org.sbml.jsbml.NamedSBase;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.layout.CompartmentGlyph;
import org.sbml.jsbml.ext.layout.ExtendedLayoutModel;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.LayoutConstants;
import org.sbml.jsbml.ext.layout.ReactionGlyph;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceGlyph;
import org.sbml.jsbml.ext.layout.TextGlyph;
import org.sbml.jsbml.util.TreeNodeChangeListener;
import org.sbml.jsbml.util.TreeNodeRemovedEvent;

import de.zbit.editor.SBMLEditorConstants;

/**
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Eugen Netz
 * @author Jan Rudolph
 * @version $Rev$
 */
public class OpenedSBMLDocument extends OpenedDocument<SBMLDocument> implements TreeNodeChangeListener {

  private static Logger logger = Logger.getLogger(OpenedSBMLDocument.class.toString());
	List<String> listOfUsedIds = new ArrayList<String>();

	
	/**
	 * Constructor.
	 * @param document
	 * @param associatedFilepath
	 */
	public OpenedSBMLDocument(SBMLDocument document, String associatedFilepath) {
		super(document, associatedFilepath);
		document.putUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument, this);
		this.initialize();
	}
	
	
	/**
	 * Constructor.
	 * @param document
	 */
	public OpenedSBMLDocument(SBMLDocument document) {
		super(document);
    document.putUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument, this);
		this.initialize();
	}
	
	/**
	 * Do all necessary initialisations.
	 */
	private void initialize() {
	  Model model = this.document.getModel();
	  if (model != null) {
	    model.addTreeNodeChangeListener(this);
	  }
		initializeIds();
	}

	/**
	 * Keep track of ids to enable automatic naming.
	 */
	private void initializeIds() {
		Model model = this.document.getModel();
		List<Layout> listOfLayouts = getListOfLayouts();

		List<NamedSBase> list = new LinkedList<NamedSBase>();
		list.addAll(model.getListOfCompartments());
		list.addAll(model.getListOfReactions());
		list.addAll(model.getListOfSpecies());
		list.addAll(listOfLayouts);
		for (Layout l : listOfLayouts) {
		  list.addAll(l.getListOfAdditionalGraphicalObjects());
		  list.addAll(l.getListOfCompartmentGlyphs());
		  list.addAll(l.getListOfReactionGlyphs());
		  list.addAll(l.getListOfSpeciesGlyphs());
		  list.addAll(l.getListOfTextGlyphs());
		}
		for (NamedSBase s : list) {
			if (s.isSetId()) {
				listOfUsedIds.add(s.getId());
			}
		}
	}
	
	/**
	 * Get next generic unused id with a given prefix.
	 * @param prefix
	 * @return an id
	 */
	public String nextGenericId(String prefix) {
		int count = 0;
		String s = prefix + count;
		// search for first avalible id
		while(listOfUsedIds.contains(s)) {
      count += 1;
      s = prefix+count;
    }
    listOfUsedIds.add(s);
    return s;
	}
	
		
	/**
	 * Check if given id is available.
	 */
	public boolean isIdAvailable(String id) {
	  return !this.listOfUsedIds.contains(id);
	}

	/**
	 * Add id of added species to usedIds.
	 */
	@Override
	public void nodeAdded(TreeNode node) {
		if (node instanceof Species) {
			Species s = (Species) node;
			this.listOfUsedIds.add(s.getId());
		}
	}

	/**
	 * Remove id of removed species from usedIds.
	 */
	@Override
	public void nodeRemoved(TreeNodeRemovedEvent evt) {
	  TreeNode removedNode = evt.getSource();
	  if (removedNode instanceof Species) {
	    Species s = (Species) removedNode;
	    this.listOfUsedIds.remove(s.getId());
	  }
	}

	
	/**
	 * Creates a default layout and adds it to the model.
	 * @return the created layout
	 */
	public Layout createDefaultLayout() {
	  Model model = getDocument().getModel();
    ExtendedLayoutModel extendedLayoutModel = new ExtendedLayoutModel(model);
    Layout layout = extendedLayoutModel.createLayout(nextGenericId(SBMLEditorConstants.genericLayoutIdPrefix));
    layout.setName(SBMLEditorConstants.layoutDefaultName);
    model.addExtension(LayoutConstants.namespaceURI, extendedLayoutModel);
    return layout; 
	}


  /**
   * Gets the first layout in the document, if it exists. Otherwise calls {@link #createDefaultLayout}.
   * @return the first layout or a new layout.
   */
  public Layout getFirstLayoutOrNew() {
    ExtendedLayoutModel extendedLayoutModel =
        (ExtendedLayoutModel) this.document.getModel().getExtension(LayoutConstants.namespaceURI);

    if (this.hasLayout()) {
      logger.info("opening first layout");
      return extendedLayoutModel.getLayout(0);
    }
    else {
      logger.info("creating new layout");
      return this.createDefaultLayout();
    }
  }
  
  /**
   * Check, if this Document has a Layout
   * @return true, if Document has a Layout
   */
  public boolean hasLayout() {
    boolean hasLayout = false;
    ExtendedLayoutModel extendedLayoutModel =
      (ExtendedLayoutModel) this.document.getModel().getExtension(LayoutConstants.namespaceURI);
    if (extendedLayoutModel != null) {
      ListOf<Layout> listOfLayouts = extendedLayoutModel.getListOfLayouts();
      hasLayout = (listOfLayouts != null && !listOfLayouts.isEmpty());
    }
    return hasLayout;
  }
  
  /**
   * Creates a new empty layout.
   * @param name
   * @return the created layout
   */
  public Layout createNewLayout(String name) {
    Model model = this.document.getModel();
    ExtendedLayoutModel extendedLayoutModel =
        (ExtendedLayoutModel) model.getExtension(LayoutConstants.namespaceURI);
    
    Layout layout = extendedLayoutModel.createLayout(nextGenericId(SBMLEditorConstants.genericLayoutIdPrefix));
    layout.setName(name);
    logger.info("Created Layout in Model: " + model.getId() + " Layout ID: " + layout.getId() + " Layout Name: " + layout.getName());
    setFileModified(true);
    return layout;
  }
  
  /**
   * Clones the given layout and adds it to the model.
   * @param layout
   * @return the clone of the layout
   */
  public Layout addLayout (Layout layout) {
    Model model = this.document.getModel();
    ExtendedLayoutModel extendedLayoutModel =
        (ExtendedLayoutModel) model.getExtension(LayoutConstants.namespaceURI);
    
    Layout newLayout = new Layout(nextGenericId(SBMLEditorConstants.genericLayoutIdPrefix),
        model.getLevel(),
        model.getVersion());
    
    newLayout.setName(layout.getName());
    
    for (CompartmentGlyph cg : layout.getListOfCompartmentGlyphs()) {
      CompartmentGlyph cgClone = cg.clone();
      cgClone.setId(nextGenericId(SBMLEditorConstants.genericCompartmentGlyphIdPrefix));
      cgClone.setCompartment(cg.getCompartment());
      newLayout.addCompartmentGlyph(cgClone);
    }
    HashMap<String, String> sGlyphMap = new HashMap<String, String>();
    for (SpeciesGlyph sg : layout.getListOfSpeciesGlyphs()) {
      SpeciesGlyph sgClone = sg.clone();
      sgClone.setId(nextGenericId(SBMLEditorConstants.genericSpeciesGlyphIdPrefix));
      sGlyphMap.put(sg.getId(), sgClone.getId());
      sgClone.setSpecies(sg.getSpecies());
      newLayout.addSpeciesGlyph(sgClone);
    }
    for (ReactionGlyph rg : layout.getListOfReactionGlyphs()) {
      ReactionGlyph rgClone = rg.clone();
      rgClone.setId(nextGenericId(SBMLEditorConstants.genericReactionGlyphIdPrefix));
      rgClone.setReaction(rg.getReaction());
      
      ListOf<SpeciesReferenceGlyph> list = new ListOf<SpeciesReferenceGlyph>();
      list.setLevel(model.getLevel());
      list.setVersion(model.getVersion());
      for (SpeciesReferenceGlyph sRefGlyph : rg.getListOfSpeciesReferenceGlyphs()) {
        SpeciesReferenceGlyph sRefGlyphClone = sRefGlyph.clone();
        sRefGlyphClone.setId(nextGenericId(SBMLEditorConstants.genericSpeciesReferenceGlyphIdPrefix));
        sRefGlyphClone.setSpeciesGlyph(sGlyphMap.get(sRefGlyph.getSpeciesGlyphInstance().getId()));
        list.add(sRefGlyphClone);
      }
      rgClone.setListOfSpeciesReferencesGlyph(list);
      newLayout.addReactionGlyph(rgClone);
    }
    
    for (TextGlyph tg : layout.getListOfTextGlyphs()) {
      TextGlyph tgClone = tg.clone();
      tgClone.setId(nextGenericId(SBMLEditorConstants.genericTextGlyphIdPrefix));
      newLayout.addTextGlyph(tgClone);
    }
    
    extendedLayoutModel.addLayout(newLayout);
    
    
    logger.info("Added Layout in Model: " + model.getId() + " Layout ID: " + newLayout.getId() + " Layout Name: " + newLayout.getName());
    setFileModified(true);
    return newLayout;
  }
  
  /**
   * Create layout information for every element of the model.
   * @return true
   */
  public boolean createLayoutInformation() {
    Model model = this.document.getModel();
    ExtendedLayoutModel extendedLayoutModel =
        (ExtendedLayoutModel) model.getExtension(LayoutConstants.namespaceURI);
    Layout layout = extendedLayoutModel.getLayout(0);
    

    List<Compartment> compartments = model.getListOfCompartments();
    for (Compartment c : compartments) {
      layout.createCompartmentGlyph(this.nextGenericId(SBMLEditorConstants.genericGlyphIdPrefix), c.getId());
      logger.info("CompartmentGlyph created.");
    }
    
    List<Species> species = model.getListOfSpecies();
    for (Species s : species) {
      SpeciesGlyph sGlyph = SBMLFactory.createSpeciesGlyph(this.nextGenericId(SBMLEditorConstants.genericGlyphIdPrefix),
        s.getLevel(), s.getVersion(), s.getId());
      layout.add(sGlyph);
      TextGlyph tGlyph = SBMLFactory.createTextGlyph(this.nextGenericId(SBMLEditorConstants.genericTextGlyphIdPrefix),
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
        ReactionGlyph rGlyph = SBMLFactory.createReactionGlyph(this, r, source,
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
                                              this, modSource, rGlyph, sR.getSBOTerm());
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
    
    return true;
  }
  
  /**
   * @return the list of layouts from this document, if it exists. An empty list otherwise.
   */
  public ListOf<Layout> getListOfLayouts() {
    ExtendedLayoutModel extendedLayoutModel =
        (ExtendedLayoutModel) this.document.getModel().getExtension(LayoutConstants.namespaceURI);
    if (extendedLayoutModel != null) {
      return extendedLayoutModel.getListOfLayouts();
    }
    else {
      return new ListOf<Layout>();
    }
  }


  /**
   * @return the id of the first Compartment. Returns an empty String if it doesn't exist.
   */
  public String getDefaultCompartment() {
    Model model = this.document.getModel();
    List<Compartment> compartments = model.getListOfCompartments();
    if (compartments.size() > 0) {
      return compartments.get(0).getId();
    }
    else {
      return "";
    }
  }


  /**
   * Cheks, if there is at least one SpeciesGlyph for the given Species in the model.
   * @param speciesId
   * @return true, if there is a SpeciesGlyph
   */
  public boolean hasAnySpeciesGlyphForSpeciesId(String speciesId) {
    boolean hasAnyGlyph = false;
    for(Layout layout : getListOfLayouts()) {
      for(SpeciesGlyph glyph : layout.getListOfSpeciesGlyphs()) {
        hasAnyGlyph |= glyph.getSpecies().equals(speciesId);
      }
    }
    return hasAnyGlyph;
  }


  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    // TODO Auto-generated method stub
    
  }
    
}
