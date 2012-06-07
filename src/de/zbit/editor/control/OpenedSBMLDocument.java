/*
 * $Id
 * $URL
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

import java.util.ArrayList;
import java.util.List;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.layout.ExtendedLayoutModel;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.LayoutConstant;

import de.zbit.editor.SBMLEditorConstants;

/**
 * @author jan
 *
 */
public class OpenedSBMLDocument extends OpenedDocument<SBMLDocument> {

	List<Layout> listOfLayouts = new ArrayList<Layout>();
	List<String> listOfUsedIds = new ArrayList<String>();
	
	public OpenedSBMLDocument(SBMLDocument document, String associatedFilepath) {
		super(document, associatedFilepath);
		this.initialize();
	}
	
	
	public OpenedSBMLDocument(SBMLDocument document) {
		super(document);
		this.initialize();
	}
	
	/**
	 * do all necessary initialisations
	 */
	private void initialize() {
		initializeLayoutList();
		initializeIds();
	}

	/**
	 * read out all layouts
	 */
	private void initializeLayoutList() {
		Model model = this.document.getModel();
		ExtendedLayoutModel layout = (ExtendedLayoutModel) this.document.getModel().getExtension(LayoutConstant.namespaceURI);
		if (layout != null) {
			listOfLayouts.addAll(layout.getListOfLayouts());
		}
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
	public String getGenericId() {
		int count = 0;
		String genericId = SBMLEditorConstants.genericId;
		// search for first avalible id
		while(count < listOfUsedIds.size() && 
				!listOfUsedIds.contains(genericId + count)) {
			count++;
		}
		return genericId + count;
	}
	
	/**
	 * check if given id is available
	 */
	public boolean isIdAvailable(String id) {
	  return !this.listOfUsedIds.contains(id);
	}
	
}
		
		