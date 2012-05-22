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

import org.sbml.jsbml.SBMLDocument;

import y.view.EditMode;
import de.zbit.editor.control.CommandController;
import de.zbit.graph.io.SB_2GraphML;

/**
 * @author Eugen Netz
 * @since 1.0
 * @version $Rev$
 */
public class SBMLEditMode extends EditMode  {

  private int                       counter = 0;
  private SB_2GraphML<SBMLDocument> converter;
 /* private states                    state   = states.normal;
  private TabManager                tabMan;

  private enum states {
    normal,
    unspecified,
    simpleChemical,
    macromolecule,
    sink,
  } */


  public SBMLEditMode(SB_2GraphML<SBMLDocument> converter, CommandController controller) {
    super();
    this.converter = converter;
    this.allowNodeCreation(true);
    this.allowEdgeCreation(true);
    this.addPropertyChangeListener(controller);
  }


  @Override
  public void mousePressedLeft(double x, double y) {
    /*if (this.state != states.normal) {
      String name = JOptionPane.showInputDialog("Enter name:", "s" + ++counter);
      if ((name != null) && (name.length() > 0)
        && !name.equalsIgnoreCase("undefined")) {
        Graph2D graph = getGraph2D();
        createNode(x, y, name);
        graph.updateViews();
        this.tabMan.normalState();
      }
    }*/
    firePropertyChange("EditModeMPLeft", x, y);
  }


  /*private void createNode(double x, double y, String name) {
    Integer current = null;
    if (this.state == states.unspecified) {
      current = SBO.getUnknownMolecule();
    } else if (this.state == states.simpleChemical) {
      current = SBO.getSimpleMolecule();
    } else if (this.state == states.macromolecule) {
      current = SBO.getMacromolecule();
    } else if (this.state == states.sink) {
      current = SBO.getEmptySet();
    }
    // TODO Apply changes to model
    Node n = converter.createNode("s" + counter, name, current, x, y);
  }*/


 /* @Override
  public void propertyChange(PropertyChangeEvent event) {
    if (event.getPropertyName().equals("Unspecified")) {
      this.state = states.unspecified;
    } else if (event.getPropertyName().equals("SimpleChemical")) {
      this.state = states.simpleChemical;
    } else if (event.getPropertyName().equals("Macromolecule")) {
      this.state = states.macromolecule;
    } else if (event.getPropertyName().equals("Sink")) {
      this.state = states.sink;
    } else if (event.getPropertyName().equals("Normal")) {
      this.state = states.normal;
    }
  }*/
}
