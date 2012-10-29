/*
 * $Id:  Action.java 10:26:35 AM jrudolph $
 * $URL: Action.java $
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

import org.sbml.jsbml.SBMLDocument;
import de.zbit.io.OpenedFile;

/**
 * @author Jan Rudolph
 * @version $Rev$
 */
public abstract class Action {
	/**
	 * @param file
	 */
	public Action(OpenedFile<SBMLDocument> file) {
		this.file = file;
	}

	protected OpenedFile<SBMLDocument> file;
	
	/**
	 * @param file the file to set
	 */
	public void setFile(OpenedFile<SBMLDocument> file) {
		this.file = file;
		execute();
	}

	/**
	 * execute as soon as all neccessary fields are set
	 */
	public abstract void execute();	
	/**
	 * check if all fields are set
	 * @return 
	 */
	public boolean isReady() {
		return file != null;
	}
}
