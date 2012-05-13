package de.zbit.editor.gui;

import java.util.Locale;
import java.util.ResourceBundle;

public class Resources {
	
	//Get localized String in default language
	public static String getString(String key){
		ResourceBundle bundle = ResourceBundle.getBundle("de.zbit.editor.gui.SBMLEditor",Locale.getDefault());
    	return bundle.getString(key);
	}
	
	//Get localized String in language locale
	public static String getString(String key,Locale locale){
		ResourceBundle bundle = ResourceBundle.getBundle("de.zbit.editor.gui.SBMLEditor",locale);
    	return bundle.getString(key);
	}
}