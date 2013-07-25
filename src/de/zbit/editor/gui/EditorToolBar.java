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
 * agreement is provided in the file named LICENSE.txt included with
 * this software distribution and also available online as
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 * ---------------------------------------------------------------------
 */

package de.zbit.editor.gui;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JToolBar;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.ext.layout.Layout;

import de.zbit.editor.BioModelsEd;
import de.zbit.editor.control.SBMLView;


/**
 * Represents the toolbar.
 * 
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Eugen Netz
 * @author Jan Rudolph
 * @version $Rev$
 */
public class EditorToolBar extends JToolBar {
	
	private static final long serialVersionUID = 4238837776010510727L;
	private JComboBox layoutComboBox = new JComboBox();
	private ListOf<Layout> listOfLayouts = new ListOf<Layout>();
	private static Logger logger = Logger.getLogger(BioModelsEd.class.toString());
	
	/**
	 * Constructor.
	 * @param parent
	 */
	public EditorToolBar(SBMLView parent, ActionListener listener) {
		BioModelsEdGUIFactory.addButton(this,
			Command.UNKNOWN_MOLECULE,
			Resources.iconUnknown,
			listener);
		
		BioModelsEdGUIFactory.addButton(this,
			Command.SIMPLE_MOLECULE,
			Resources.iconSimpleMolecule,
			listener);
		
		BioModelsEdGUIFactory.addButton(this,
			Command.MACROMOLECULE,
			Resources.iconMacromolecule,
			listener);
		
		BioModelsEdGUIFactory.addButton(this,
			Command.EMPTY_SET,
			Resources.iconEmptySet,
			listener);
		
		BioModelsEdGUIFactory.addButton(this,
			Command.REACTION,
			Resources.iconTransition,
			listener);
		
		BioModelsEdGUIFactory.addButton(this,
			Command.CATALYSIS,
			Resources.iconCatalysis,
			listener);
		
		BioModelsEdGUIFactory.addButton(this,
			Command.INHIBITION,
			Resources.iconInhibition,
			listener);
		
		BioModelsEdGUIFactory.addButton(this,
			Command.MODULATION,
			Resources.iconModulation,
			listener);
		
		BioModelsEdGUIFactory.addButton(this,
			Command.NECESSARY_STIMULATION,
			Resources.iconNecessaryStimulation,
			listener);
		
		
		layoutComboBox.setMaximumSize(new Dimension(150, 24));
		add(layoutComboBox);
		BioModelsEdGUIFactory.addButton(this,
			Command.OPEN_LAYOUT,
			Resources.iconTab,
			listener);
		
		BioModelsEdGUIFactory.addButton(this,
			Command.NEW_LAYOUT,
			Resources.iconTab,
			listener);
		
		BioModelsEdGUIFactory.addCheckbox(this, 
			Command.REVERSIBLE,
			listener);
		
		BioModelsEdGUITools.setEnabled(this, false);
	}
	
	/**
	 * Represents the items in the ComboBox
	 */
	private static class ListItem {
		private final String text;
		
		private ListItem(String text) {
			this.text = text;
		}
		
		public String toString() {
			return text;
		}
	}
	
	/**
	 * Updates the ComboBox, so that it has all the layouts in the given list.
	 * @param list
	 */
	public void updateComboBox(ListOf<Layout> list) {
		this.listOfLayouts = list;
		layoutComboBox.removeAllItems();
		for(Layout l: listOfLayouts) {
			layoutComboBox.addItem(new ListItem(l.getName()));
		}
	}
	
	/**
	 * Gets the layout selected in the ComboBox from the listOfLayouts.
	 * @return the layout
	 */
	public Layout getSelectedLayout() {
		String msg = "";
		for(Layout l: listOfLayouts) {
			msg += l.getId() +";";
		}
		
		int sel = layoutComboBox.getSelectedIndex();
		logger.info(msg + "selected index: " + sel);
		return listOfLayouts.get(sel);
	}
}
