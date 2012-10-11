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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JToolBar;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.ext.layout.Layout;

import de.zbit.editor.SBMLEditorConstants;
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
public class EditorToolbar extends JToolBar {

  private static final long serialVersionUID = 4238837776010510727L;
  private JComboBox layoutComboBox = new JComboBox();
  private ListOf<Layout> listOfLayouts = new ListOf<Layout>();
  private static Logger logger = Logger.getLogger(SBMLEditor.class.toString());

  /**
   * Constructor.
   * @param parent
   */
  public EditorToolbar(SBMLView parent) {
    GUIFactory.addButton(this,
        Resources.getString(SBMLEditorConstants.UNKNOWN_MOLECULE),
        Resources.iconUnknown,
        0,
        0,
        EventHandler.create(ActionListener.class,
            parent,
            SBMLEditorConstants.addUnknownMolecule));
    
    GUIFactory.addButton(this,
        Resources.getString(SBMLEditorConstants.SIMPLE_MOLECULE),
        Resources.iconSimpleMolecule,
        0,
        0,
        EventHandler.create(ActionListener.class,
            parent,
            SBMLEditorConstants.addSimpleMolecule));
    
    GUIFactory.addButton(this,
        Resources.getString(SBMLEditorConstants.MACROMOLECULE),
        Resources.iconMacromolecule,
        0,
        0,
        EventHandler.create(ActionListener.class,
            parent,
            SBMLEditorConstants.addMacromolecule));
    
    GUIFactory.addButton(this,
        Resources.getString(SBMLEditorConstants.EMPTY_SET),
        Resources.iconEmptySet,
        0,
        0,
        EventHandler.create(ActionListener.class, parent, SBMLEditorConstants.addEmptySet));
    
    GUIFactory.addButton(this,
        Resources.getString(SBMLEditorConstants.REACTION),
        Resources.iconTransition,
        0,
        0,
        EventHandler.create(ActionListener.class, parent, SBMLEditorConstants.addReaction));
    
    GUIFactory
        .addButton(this,
            Resources.getString(SBMLEditorConstants.CATALYSIS),
            Resources.iconCatalysis,
            0,
            0,
            EventHandler.create(ActionListener.class,
                parent,
                SBMLEditorConstants.addCatalysis));
    
    GUIFactory.addButton(this,
        Resources.getString(SBMLEditorConstants.INHIBITION),
        Resources.iconInhibition,
        0,
        0,
        EventHandler
            .create(ActionListener.class, parent, SBMLEditorConstants.addInhibition));
    
    
    layoutComboBox.setMaximumSize(new Dimension(150, 24));
    add(layoutComboBox);
    GUIFactory.addButton(this,
        Resources.getString(SBMLEditorConstants.MENU_TAB_OPEN),
        Resources.iconTab,
        0,
        0,
        EventHandler.create(ActionListener.class,
            parent,
            SBMLEditorConstants.openLayoutInTab));
 
    GUIFactory.addButton(this,
        Resources.getString(SBMLEditorConstants.MENU_TAB_OPEN_NEW),
        Resources.iconTabNew,
        0,
        0,
        EventHandler.create(ActionListener.class,
            parent,
            SBMLEditorConstants.openLayoutInNewTab));
    
    GUIFactory.addCheckbox(this, 
      Resources.getString(SBMLEditorConstants.REVERSIBLE),
      EventHandler.create(ActionListener.class,
        parent,
        SBMLEditorConstants.reversible));
    
    setEnableState(false);
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
    for(Layout l: listOfLayouts){
      layoutComboBox.addItem(new ListItem(l.getName()));
    }
  }
  
  /**
   * Gets the layout selected in the ComboBox from the listOfLayouts.
   * @return the layout
   */
  public Layout getSelectedLayout() {
    String msg = "";
    for(Layout l: listOfLayouts){
      msg += l.getId() +";";
    }
    
    int sel = layoutComboBox.getSelectedIndex();
    logger.info(msg + "selected index: " + sel);
    return listOfLayouts.get(sel);
  }

  /**
   * Enables or disables the toolbar, depending on the input.
   * Enables, if anyDocumentsOpen is true.
   * @param anyDocumentsOpen
   */
  public void setEnableState(boolean anyDocumentsOpen) {
    Component[] array = this.getComponents();
    
    for (int i = 0; i < array.length; i++) {
      array[i].setEnabled(anyDocumentsOpen);
    }    
  }
}
