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
package de.zbit.editor;

/**
 * Constants, that are used in the whole program.
 * 
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Eugen Netz
 * @author Jan Rudolph
 * @version $Rev$
 */
public class Constants {

  public static final String associatedOpenedFile = "associatedOpenedFile";
  
  public static final String MouseClicked = "mouseClicked in EditMode";
  public static final String MousePressedLeft = "mousePressedLeft in EditMode";
  public static final String MouseDraggedLeft = "mouseDraggedLeft in EditMode";
  public static final String MouseReleasedLeft = "mouseReleasedLeft in EditMode";
  public static final String MousePressedRight = "mousePressedRight in EditMode";
  public static final String MouseReleasedRight = "mouseReleasedRight in EditMode";
  
  public static final String nodeClicked = "nodeClicked in EditMode";
  public static final String NodeClickedLeft = "nodeClickedLeft in EditMode";
  public static final String NodeReleasedLeft = "nodeReleasedLeft in EditMode";
  public static final String NodeReleasedRight = "nodeReleasedRight in EditMode";
  public static final String NodePressedRight = "nodePressedRight in EditMode";
  public static final String SelectionChanged = "selection changed in EditMode";
  public static final String UpdateNodes = "update node properties in EditMode";
  
  public static final String genericId = "s";
  public static final String genericReactionIdPrefix = "r";
  public static final String genericModifierReferenceIdPrefix = "modifierReference";
  public static final String genericLayoutIdPrefix = "layout";
  
  public static final String genericGlyphIdPrefix = "glyph";
  public static final String genericCompartmentGlyphIdPrefix = "compartmentglyph";
  public static final String genericReactionGlyphIdPrefix = "reactionglyph";
  public static final String genericSpeciesGlyphIdPrefix = "speciesglyph";
  public static final String genericTextGlyphIdPrefix = "textglyph";
  public static final String genericSpeciesReferenceGlyphIdPrefix = "speciesReferenceGlyph";
  public static final String genericModifierReferenceGlyphIdPrefix = "modifierReferenceGlyph";
  
  public static final String modelDefaultName = "defalt_model";
  public static final String layoutDefaultName = "default_layout";
  public static final String genericFileName = "<unnamed>";
  public static final String genricModelId = "model1";
  public static final String compartmentDefaultName = "default";
  public static final String defaultCompartmentId = "default_compartment";
  
  
  public static final int glyphDefaultDepth = 0;
  public static final int glyphDefaultHeight = 100;
  public static final int glyphDefaultWidth = 100;
  public static final int glyphDefaultZ = 0;
  
  public static final String LAYOUT_LINK_KEY = "LAYOUT_LINK_KEY";
  public static final String GLYPH_NODE_KEY = "GLYPH_NODE_KEY";
  public static final String GRAPHOBJECT_TEXTGLYPH_KEY = "GRAPHOBJECT_TEXTGLYPH_KEY";
  public static final String GLYPH_LINK_KEY = "GLYPH_LINK_KEY";
  
  public static final String warningTitle = "WARNING_TITLE";
  public static final String errorTitle = "ERROR_TITLE";
  public static final String openingDone = "SBML_READING_SUCCESSFULLY_DONE";
  public static final String savingDone = "savingDone";
  public static final String fileNotFound = "FILE_NOT_FOUND";
  public static final String SBML_READING_SUCCESSFULLY_DONE = "SBML_READING_SUCCESSFULLY_DONE";
  
  //EditorMenu
   //Menu File
  //Resource Strings
  public static final String MENU_FILE = "MENU_FILE";
  public static final String MENU_FILE_NEW = "MENU_FILE_NEW";
  public static final String MENU_FILE_OPEN = "MENU_FILE_OPEN";
  public static final String MENU_FILE_CLOSE = "MENU_FILE_CLOSE";
  public static final String MENU_FILE_SAVE = "MENU_FILE_SAVE";
  public static final String MENU_FILE_SAVEAS = "MENU_FILE_SAVEAS";
  public static final String MENU_FILE_EXPORT = "MENU_FILE_EXPORT";
  public static final String MENU_FILE_QUIT = "MENU_FILE_QUIT";
  //Actions
  
  //Menu Edit
  //Resource Strings
  public static final String MENU_EDIT = "MENU_EDIT";
  public static final String MENU_EDIT_UNDO = "MENU_EDIT_UNDO";
  public static final String MENU_EDIT_REDO = "MENU_EDIT_REDO";
  public static final String MENU_EDIT_CUT = "MENU_EDIT_CUT";
  public static final String MENU_EDIT_COPY = "MENU_EDIT_COPY";
  public static final String MENU_EDIT_PASTE = "MENU_EDIT_PASTE";
  public static final String MENU_EDIT_DELETE = "MENU_EDIT_DELETE";
  //Actions
  
   //Menu Layout
  //Resource Strings
  public static final String MENU_LAYOUT = "MENU_LAYOUT";
  public static final String MENU_LAYOUT_NEW = "MENU_LAYOUT_NEW";
  public static final String MENU_LAYOUT_RENAME = "MENU_LAYOUT_RENAME";
  public static final String MENU_LAYOUT_CLONE = "MENU_LAYOUT_CLONE";
  public static final String MENU_LAYOUT_DELETE = "MENU_LAYOUT_DELETE";
  public static final String MENU_LAYOUT_AUTO = "MENU_LAYOUT_AUTO";
  
  //Keystrokes
  public static final String MENU_LAYOUT_NEW_KEYSTROKE = "alt N";
  public static final String MENU_LAYOUT_RENAME_KEYSTROKE = "alt R";
  public static final String MENU_LAYOUT_CLONE_KEYSTROKE = "alt C";
  public static final String MENU_LAYOUT_DELETE_KEYSTROKE = "alt D";
  public static final String MENU_LAYOUT_AUTO_KEYSTROKE = "alt A";
  //Menu Help
  //Resource Strings
  public static final String MENU_HELP = "MENU_HELP";
  public static final String MENU_HELP_ABOUT = "MENU_HELP_ABOUT";
  
  //EditorToolbar
  //Resource Strings
  public static final String UNKNOWN_MOLECULE = "UNKNOWN_MOLECULE";
  public static final String SIMPLE_MOLECULE = "SIMPLE_MOLECULE";
  public static final String MACROMOLECULE = "MACROMOLECULE";
  public static final String EMPTY_SET = "EMPTY_SET";
  public static final String CATALYSIS = "CATALYSIS";
  public static final String REACTION = "REACTION";
  public static final String INHIBITION = "INHIBITION";
  public static final String MENU_TAB_OPEN_NEW = "MENU_TAB_OPEN_NEW";
  public static final String MENU_TAB_OPEN = "MENU_TAB_OPEN";
  public static final String REVERSIBLE = "REVERSIBLE";
  
	  
}
