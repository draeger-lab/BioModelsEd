/*
 * $Id$
 * $URL$
 * ---------------------------------------------------------------------
 * This file is part of BioModelsEd.
 *
 * Copyright (C) 20012-2013 by the University of Tuebingen, Germany.
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation. A copy of the license
 * agreement is provided in the file named "LICENSE.txt" included with
 * this software distribution and also available online as
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 * ---------------------------------------------------------------------
 */
package de.zbit.editor.gui;

import java.util.ResourceBundle;

import de.zbit.gui.actioncommand.ActionCommand;
import de.zbit.util.ResourceManager;

/**
 * @author Jan Rudolph
 * @version $Rev$
 */
public enum Command implements ActionCommand{
	
	/**
	 * Edit menu actions
	 */
	UNDO,
	REDO,
	COPY,
	CUT,
	PASTE,
	DELETE,
	SELECT_ALL, 
	/**
	 * File menu actions
	 */
	NEW,
	//CLOSE, 
	/**
	 * layout menu actions
	 */
	NEW_LAYOUT, 
	RENAME_LAYOUT, 
	DELETE_LAYOUT, 
	CLONE_LAYOUT, 
	AUTOMATIC_LAYOUT,
	OPEN_LAYOUT,

	DELETE_NODE,
	COPY_NODE,
	RENAME_NODE,
	
	MACROMOLECULE,
	INHIBITION,
	CATALYSIS,
	REACTION,
	UNKNOWN_MOLECULE,
	SIMPLE_MOLECULE,
	EMPTY_SET,
	REVERSIBLE,
	
	;

	/* (non-Javadoc)
	 * @see de.zbit.gui.ActionCommand#getName()
	 */
	private final ResourceBundle MESSAGES = ResourceManager.getBundle("de.zbit.editor.locales.Messages");
	public String getName() {
		return MESSAGES.getString(name());
	}

	/* (non-Javadoc)
	 * @see de.zbit.gui.ActionCommand#getToolTip()
	 */
	public String getToolTip() {
		return MESSAGES.getString(name() + "_TOOLTIP");
	}
}
