/*
 * $$Id${file_name} ${time} ${user}$$
 * $$URL${file_name}$$
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
package de.zbit.editor.gui;

import java.util.Locale;
import java.util.ResourceBundle;


//TODO: Insert JavaDoc
//TODO: Insert License Header
//TODO: Set SVN properties
/**
 * @author Alexander Diamantikos
 * @since 1.0
 * @version $Rev$
 */
public class Resources {
	
	// TODO: Avoid loading and initializing ResourceBundles multiple times. This will consume too many resources.
	
	private final static ResourceBundle bundleDefault = ResourceBundle.getBundle("de.zbit.editor.gui.SBMLEditor",Locale.getDefault());
	
	//Get localized String in default language
	public static String getString(String key){
		return bundleDefault.getString(key);
	}
		 
}