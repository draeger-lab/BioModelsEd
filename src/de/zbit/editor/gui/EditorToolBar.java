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

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JToolBar;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.ext.layout.Layout;

import de.zbit.editor.BioModelsEd;
import de.zbit.editor.BioModelsEdConstants;
import de.zbit.editor.control.SBMLView;
import de.zbit.gui.GUITools;


/**
 * Represents the toolbar.
 * 
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Eugen Netz
 * @author Jan Rudolph
 * @version $Rev$
 */
public class EditorToolBar extends JToolBar {

  private static final long serialVersionUID = 4238837776010510727L;
  private JComboBox layoutComboBox = new JComboBox();
  private ListOf<Layout> listOfLayouts = new ListOf<Layout>();
  private static Logger logger = Logger.getLogger(BioModelsEd.class.toString());

  /**
   * Constructor.
   * @param parent
   */
  public EditorToolBar(SBMLView parent) {
    BioModelsEdGUIFactory.addButton(this,
        Resources.getString(BioModelsEdConstants.UNKNOWN_MOLECULE),
        Resources.iconUnknown,
        0,
        0,
        EventHandler.create(ActionListener.class,
            parent,
            BioModelsEdConstants.addUnknownMolecule));
    
    BioModelsEdGUIFactory.addButton(this,
        Resources.getString(BioModelsEdConstants.SIMPLE_MOLECULE),
        Resources.iconSimpleMolecule,
        0,
        0,
        EventHandler.create(ActionListener.class,
            parent,
            BioModelsEdConstants.addSimpleMolecule));
    
    BioModelsEdGUIFactory.addButton(this,
        Resources.getString(BioModelsEdConstants.MACROMOLECULE),
        Resources.iconMacromolecule,
        0,
        0,
        EventHandler.create(ActionListener.class,
            parent,
            BioModelsEdConstants.addMacromolecule));
    
    BioModelsEdGUIFactory.addButton(this,
        Resources.getString(BioModelsEdConstants.EMPTY_SET),
        Resources.iconEmptySet,
        0,
        0,
        EventHandler.create(ActionListener.class, parent, BioModelsEdConstants.addEmptySet));
    
    BioModelsEdGUIFactory.addButton(this,
        Resources.getString(BioModelsEdConstants.REACTION),
        Resources.iconTransition,
        0,
        0,
        EventHandler.create(ActionListener.class, parent, BioModelsEdConstants.addReaction));
    
    BioModelsEdGUIFactory
        .addButton(this,
            Resources.getString(BioModelsEdConstants.CATALYSIS),
            Resources.iconCatalysis,
            0,
            0,
            EventHandler.create(ActionListener.class,
                parent,
                BioModelsEdConstants.addCatalysis));
    
    BioModelsEdGUIFactory.addButton(this,
        Resources.getString(BioModelsEdConstants.INHIBITION),
        Resources.iconInhibition,
        0,
        0,
        EventHandler
            .create(ActionListener.class, parent, BioModelsEdConstants.addInhibition));
    
    
    layoutComboBox.setMaximumSize(new Dimension(150, 24));
    add(layoutComboBox);
    BioModelsEdGUIFactory.addButton(this,
        Resources.getString(BioModelsEdConstants.MENU_TAB_OPEN),
        Resources.iconTab,
        0,
        0,
        EventHandler.create(ActionListener.class,
            parent,
            BioModelsEdConstants.openLayoutInTab));
 
    BioModelsEdGUIFactory.addButton(this,
        Resources.getString(BioModelsEdConstants.MENU_TAB_OPEN_NEW),
        Resources.iconTabNew,
        0,
        0,
        EventHandler.create(ActionListener.class,
            parent,
            BioModelsEdConstants.openLayoutInNewTab));
    
    BioModelsEdGUIFactory.addCheckbox(this, 
      Resources.getString(BioModelsEdConstants.REVERSIBLE),
      EventHandler.create(ActionListener.class,
        parent,
        BioModelsEdConstants.reversible));
    
    BioModelsEdGUITools.setEnabled(this, false);
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
}
