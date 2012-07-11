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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.tree.TreeNode;

import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
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
import org.sbml.jsbml.ext.layout.TextGlyph;
import org.sbml.jsbml.util.TreeNodeChangeListener;
import org.sbml.jsbml.util.TreeNodeRemovedEvent;

import de.zbit.editor.SBMLEditorConstants;

/**
 * @author Jan Rudolph
 * @version $Rev$
 */
public class OpenedSBMLDocument extends OpenedDocument<SBMLDocument> implements TreeNodeChangeListener {

  private static Logger logger = Logger.getLogger(OpenedSBMLDocument.class.toString());
	List<String> listOfUsedIds = new ArrayList<String>();

	
	/**
	 * @param document
	 * @param associatedFilepath
	 */
	public OpenedSBMLDocument(SBMLDocument document, String associatedFilepath) {
		super(document, associatedFilepath);
		document.putUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument, this);
		this.initialize();
	}
	
	
	/**
	 * @param document
	 */
	public OpenedSBMLDocument(SBMLDocument document) {
		super(document);
    document.putUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument, this);
		this.initialize();
	}
	
	/**
	 * do all necessary initialisations
	 */
	private void initialize() {
	  Model model = this.document.getModel();
	  if (model != null) {
	    model.addTreeNodeChangeListener(this);
	  }
		initializeIds();
	}

	/**
	 * keep track of ids to enable automatic naming
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
	 * get next generic species id
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
	 * check if given id is available
	 */
	public boolean isIdAvailable(String id) {
	  return !this.listOfUsedIds.contains(id);
	}


	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	}

	/**
	 * add id of added species to usedIds
	 */
	@Override
	public void nodeAdded(TreeNode node) {
		if (node instanceof Species) {
			Species s = (Species) node;
			this.listOfUsedIds.add(s.getId());
		}
	}

	/**
	 * remove id of removed species from usedIds
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
	 * @return
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
   * @return
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
   * 
   * @return true, if Document has a Layout
   */
  public boolean hasLayout() {
    ExtendedLayoutModel extendedLayoutModel =
      (ExtendedLayoutModel) this.document.getModel().getExtension(LayoutConstants.namespaceURI);
    
    return ((extendedLayoutModel != null) &&
        (extendedLayoutModel.getListOfLayouts() != null) &&
        (!extendedLayoutModel.getListOfLayouts().isEmpty()));    
  }
  /**
   * @param name
   * @return
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
   * @param layout
   * @return
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
    for (ReactionGlyph rg : layout.getListOfReactionGlyphs()) {
      ReactionGlyph rgClone = rg.clone();
      rgClone.setId(nextGenericId(SBMLEditorConstants.genericReactionGlyphIdPrefix));
      rgClone.setReaction(rg.getReaction());
      newLayout.addReactionGlyph(rgClone);
    }
    for (SpeciesGlyph sg : layout.getListOfSpeciesGlyphs()) {
      SpeciesGlyph sgClone = sg.clone();
      sgClone.setId(nextGenericId(SBMLEditorConstants.genericSpeciesGlyphIdPrefix));
      sgClone.setSpecies(sg.getSpecies());
      newLayout.addSpeciesGlyph(sgClone);
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
   * create layout information for every element of the model
   * @return
   */
  public boolean createLayoutInformation() {
    Model model = this.document.getModel();
    ExtendedLayoutModel extendedLayoutModel =
        (ExtendedLayoutModel) model.getExtension(LayoutConstants.namespaceURI);
    Layout layout = extendedLayoutModel.getLayout(0);
    
    List<Species> species = model.getListOfSpecies();
    for (Species s : species) {
      SpeciesGlyph sGlyph = SBMLFactory.createSpeciesGlyph(this.nextGenericId(SBMLEditorConstants.genericGlyphIdPrefix),
        s.getLevel(), s.getVersion(), s.getId());
      layout.add(sGlyph);
    }
    
    List<Reaction> reactions = model.getListOfReactions();
    for (Reaction r : reactions) {
      //FIXME Always the first Reactant and Product are used
      SpeciesGlyph source = null;
      SpeciesGlyph target = null;
      List<SpeciesGlyph> sGlyphs = layout.getListOfSpeciesGlyphs();
      for (SpeciesGlyph sGlyph : sGlyphs) {
        if (sGlyph.getSpecies() == r.getListOfReactants().get(0).getSpecies()) {
          source = sGlyph;
        } else if (sGlyph.getSpecies() == r.getListOfProducts().get(0).getSpecies()) {
          target = sGlyph;
        }
      }
      if ((source != null) && (target != null)) {
        ReactionGlyph rGlyph = SBMLFactory.createReactionGlyph(this, r, source, target, r.getLevel(), r.getVersion());
        layout.add(rGlyph);
      }
    }
    
    List<Compartment> compartments = model.getListOfCompartments();
    for (Compartment c : compartments) {
      layout.createCompartmentGlyph(this.nextGenericId(SBMLEditorConstants.genericGlyphIdPrefix), c.getId());
    }
    
    return true;
  }
  
  /**
   * @return
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
   * @return
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
    
}
