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

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;


//TODO: Insert JavaDoc
//TODO: Insert License Header
//TODO: Set SVN properties
/**
 * @author Alexander Diamantikos
 * @since 1.0
 * @version $Rev$
 */
public class Resources {
	
  private static ArrayList<String> usedIDs = new ArrayList<String>();
	private final static ResourceBundle bundleDefault = ResourceBundle.getBundle("de.zbit.editor.gui.SBMLEditor", Locale.getDefault());
	
	//Icons
	public static final ImageIcon iconButtonAbout = Resources.createImageIcon("images/ButtonAbout.png", null);
	public static final ImageIcon iconButtonAdd = Resources.createImageIcon("images/ButtonAdd.png", null);
  public static final ImageIcon iconButtonClose = Resources.createImageIcon("images/ButtonClose.png", null);
  public static final ImageIcon iconButtonCopy = Resources.createImageIcon("images/ButtonCopy.png", null);
  public static final ImageIcon iconButtonCut = Resources.createImageIcon("images/ButtonCut.png", null);
  public static final ImageIcon iconButtonDelete = Resources.createImageIcon("images/ButtonDelete.png", null);
  public static final ImageIcon iconButtonNew = Resources.createImageIcon("images/ButtonNew.png", null);
  public static final ImageIcon iconButtonOpen = Resources.createImageIcon("images/ButtonOpen.png", null);
  public static final ImageIcon iconButtonPaste = Resources.createImageIcon("images/ButtonPaste.png", null);
  public static final ImageIcon iconButtonQuit = Resources.createImageIcon("images/ButtonQuit.png", null);
  public static final ImageIcon iconButtonRedo = Resources.createImageIcon("images/ButtonRedo.png", null);
  public static final ImageIcon iconButtonSave = Resources.createImageIcon("images/ButtonSave.png", null);
  public static final ImageIcon iconButtonUndo = Resources.createImageIcon("images/ButtonUndo.png", null);
  public static final ImageIcon iconPositive = Resources.createImageIcon("images/Positive.png", null);
  
	//Get localized String in default language
	public static String getString(String key){
		return bundleDefault.getString(key);
	}
	

	/** Returns an ImageIcon, or null if the path was invalid. */

	protected static ImageIcon createImageIcon(String path, String description) {

	  java.net.URL imgURL = Resources.class.getResource(path);

	  if (imgURL != null) {

	    return new ImageIcon(imgURL, description);

	  } else {

	    System.err.println("Couldn't find file: " + path);

	    return null;

	  }

	}
	
	public static String createValidID(String prefix) {
    int i = 1;
    String s = prefix+i;
    while(usedIDs.contains(s)) {
      i+=1;
      s = prefix+i;
    }
    usedIDs.add(s);
    return s;
  }
		 
}