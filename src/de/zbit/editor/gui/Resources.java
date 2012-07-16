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
package de.zbit.editor.gui;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

/**
 * @author Alexander Diamantikos
 * @since 1.0
 * @version $Rev$
 */
public class Resources {
	
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
  public static final ImageIcon iconUnknown = Resources.createImageIcon("images/ButtonUnknown.png", null);
  public static final ImageIcon iconSimpleMolecule = Resources.createImageIcon("images/ButtonSimpleMolecule.png", null);
  public static final ImageIcon iconMacromolecule = Resources.createImageIcon("images/ButtonMacromolecule.png", null);
  public static final ImageIcon iconEmptySet = Resources.createImageIcon("images/ButtonEmptySet.png", null);
  public static final ImageIcon iconTransition = Resources.createImageIcon("images/ButtonTransition.png", null);
  public static final ImageIcon iconCatalysis = Resources.createImageIcon("images/ButtonCatalysis.png", null);
  public static final ImageIcon iconInhibition = Resources.createImageIcon("images/ButtonInhibition.png", null);
  public static final ImageIcon iconPositive = Resources.createImageIcon("images/Positive.png", null);
  public static final ImageIcon iconTab = Resources.createImageIcon("images/ButtonTab.png", null);
  public static final ImageIcon iconTabNew = Resources.createImageIcon("images/ButtonTabNew.png", null);
  public static final ImageIcon iconButtonAuto = Resources.createImageIcon("images/ButtonAuto.png", null);
  public static final ImageIcon iconButtonRename = Resources.createImageIcon("images/ButtonRename.png", null);
  public static final ImageIcon iconButtonExport = Resources.createImageIcon("images/ButtonExport.png", null);
  
	// Get localized String in default language
	public static String getString(String key){
		return bundleDefault.getString(key);
	}
	
	/**
	 * Returns an ImageIcon, or null if the path was invalid.
	 * @param path
	 * @param description
	 * @return
	 */
	protected static ImageIcon createImageIcon(String path, String description) {
	  java.net.URL imgURL = Resources.class.getResource(path);
	  if (imgURL != null) {
	    return new ImageIcon(imgURL, description);
	  } else {
	    System.err.println("Couldn't find file: " + path);
	    return null;
	  }
	}
		 
}