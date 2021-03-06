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
 * Manages resources, like button icons and localized strings.
 * 
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Eugen Netz
 * @author Jan Rudolph
 * @version $Rev$
 */
public class Resources {
	
	private final static ResourceBundle bundleDefault = ResourceBundle.getBundle("de.zbit.editor.gui.SBMLEditor", Locale.getDefault());
	
	//Icons
	public static final ImageIcon iconButtonAbout = Resources.createImageIcon("img/ButtonAbout.png", null);
	public static final ImageIcon iconButtonAdd = Resources.createImageIcon("img/ButtonAdd.png", null);
  public static final ImageIcon iconButtonClose = Resources.createImageIcon("img/ButtonClose.png", null);
  public static final ImageIcon iconButtonCopy = Resources.createImageIcon("img/ButtonCopy.png", null);
  public static final ImageIcon iconButtonCut = Resources.createImageIcon("img/ButtonCut.png", null);
  public static final ImageIcon iconButtonDelete = Resources.createImageIcon("img/ButtonDelete.png", null);
  public static final ImageIcon iconButtonNew = Resources.createImageIcon("img/ButtonNew.png", null);
  public static final ImageIcon iconButtonOpen = Resources.createImageIcon("img/ButtonOpen.png", null);
  public static final ImageIcon iconButtonPaste = Resources.createImageIcon("img/ButtonPaste.png", null);
  public static final ImageIcon iconButtonQuit = Resources.createImageIcon("img/ButtonQuit.png", null);
  public static final ImageIcon iconButtonRedo = Resources.createImageIcon("img/ButtonRedo.png", null);
  public static final ImageIcon iconButtonSave = Resources.createImageIcon("img/ButtonSave.png", null);
  public static final ImageIcon iconButtonUndo = Resources.createImageIcon("img/ButtonUndo.png", null);
  public static final ImageIcon iconUnknown = Resources.createImageIcon("img/ButtonUnknown.png", null);
  public static final ImageIcon iconSimpleMolecule = Resources.createImageIcon("img/ButtonSimpleMolecule.png", null);
  public static final ImageIcon iconMacromolecule = Resources.createImageIcon("img/ButtonMacromolecule.png", null);
  public static final ImageIcon iconEmptySet = Resources.createImageIcon("img/ButtonEmptySet.png", null);
  public static final ImageIcon iconTransition = Resources.createImageIcon("img/ButtonTransition.png", null);
  public static final ImageIcon iconCatalysis = Resources.createImageIcon("img/ButtonCatalysis.png", null);
  public static final ImageIcon iconInhibition = Resources.createImageIcon("img/ButtonInhibition.png", null);
  // TODO fix icon paths for modulation and necessary stimulation
  public static final ImageIcon iconModulation = Resources.createImageIcon("img/ButtonCatalysis.png", null);
  public static final ImageIcon iconNecessaryStimulation = Resources.createImageIcon("img/ButtonInhibition.png", null);
  public static final ImageIcon iconPositive = Resources.createImageIcon("img/Positive.png", null);
  public static final ImageIcon iconTab = Resources.createImageIcon("img/ButtonTab.png", null);
  public static final ImageIcon iconTabNew = Resources.createImageIcon("img/ButtonTabNew.png", null);
  public static final ImageIcon iconButtonAuto = Resources.createImageIcon("img/ButtonAuto.png", null);
  public static final ImageIcon iconButtonRename = Resources.createImageIcon("img/ButtonRename.png", null);
  public static final ImageIcon iconButtonExport = Resources.createImageIcon("img/ButtonExport.png", null);
  
	/**
	 * @param key
	 * @return a localized string in the default language
	 */
	public static String getString(String key) {
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