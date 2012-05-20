package de.zbit.editor.gui;

import java.util.Locale;
import java.util.ResourceBundle;


//TODO: Insert JavaDoc
//TODO: Insert License Header
//TODO: Set SVN properties
public class Resources {
	
	// TODO: Avoid loading and initializing ResourceBundles multiple times. This will consume too many resources.
	
	private final static ResourceBundle bundleDefault = ResourceBundle.getBundle("de.zbit.editor.gui.SBMLEditor",Locale.getDefault());
	
	//Get localized String in default language
	public static String getString(String key){
		return bundleDefault.getString(key);
	}
		 
}