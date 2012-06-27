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
import java.util.List;
import java.util.logging.Logger;

import javax.swing.tree.TreeNode;

import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.layout.ExtendedLayoutModel;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.LayoutConstants;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.util.TreeNodeChangeListener;
import org.sbml.jsbml.util.TreeNodeRemovedEvent;

import de.zbit.editor.SBMLEditorConstants;
import de.zbit.editor.gui.Resources;

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
		List<Species> listOfSpecies = model.getListOfSpecies();
		for (Species s : listOfSpecies) {
			if (s.isSetId()) {
				listOfUsedIds.add(s.getId());				
			}
		}
	}
	
	/**
	 * get next generic species id
	 */
	public String nextGenericId() {
		int count = 0;
		String s = SBMLEditorConstants.genericId + count;
		// search for first avalible id
		while(listOfUsedIds.contains(s)) {
      count+=1;
      s = SBMLEditorConstants.genericId+count;
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
    Layout layout = extendedLayoutModel.createLayout(Resources.createValidID("l"));
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

    if (extendedLayoutModel != null &&
        extendedLayoutModel.getListOfLayouts() != null &&
        !extendedLayoutModel.getListOfLayouts().isEmpty()) {
      logger.info("opening first layout");
      return extendedLayoutModel.getLayout(0);
    }
    else {
      logger.info("creating new layout");
      return this.createDefaultLayout();
    }
  }
  
  public Layout createNewLayout(String name) {
    Model model = this.document.getModel();
    ExtendedLayoutModel extendedLayoutModel =
        (ExtendedLayoutModel) model.getExtension(LayoutConstants.namespaceURI);
    
    Layout layout = extendedLayoutModel.createLayout(Resources.createValidID("l"));
    layout.setName(name);
    logger.info("Created Layout in Model: " + model.getId() + " Layout ID: " + layout.getId() + " Layout Name: " + layout.getName());
    setFileModified(true);
    return layout;
  }
  
  public Layout addLayout (Layout layout) {
    Model model = this.document.getModel();
    ExtendedLayoutModel extendedLayoutModel =
        (ExtendedLayoutModel) model.getExtension(LayoutConstants.namespaceURI);
    layout = layout.clone();
    layout.setId(Resources.createValidID("l"));
    
    layout.getListOfSpeciesGlyphs().setParentSBML(layout);
    for (SpeciesGlyph glyph : layout.getListOfSpeciesGlyphs()) {
      glyph.setId(nextGenericId());
      glyph.setSpecies(glyph.getName());
    }
    extendedLayoutModel.addLayout(layout);
    
    
    logger.info("Added Layout in Model: " + model.getId() + " Layout ID: " + layout.getId() + " Layout Name: " + layout.getName());
    setFileModified(true);
    return layout;
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
      // TODO
    }
    
    List<Reaction> reactions = model.getListOfReactions();
    for (Reaction r : reactions) {
      // TODO
    }
    
    List<Compartment> compartments = model.getListOfCompartments();
    for (Compartment c : compartments) {
      // TODO
    }
    
    return true;
  }
  
  public ListOf<Layout> getListOfLayouts() {
    ExtendedLayoutModel extendedLayoutModel =
        (ExtendedLayoutModel) this.document.getModel().getExtension(LayoutConstants.namespaceURI);
    return extendedLayoutModel.getListOfLayouts();
  }
    
}
