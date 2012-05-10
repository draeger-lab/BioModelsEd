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

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.ext.layout.ExtendedLayoutModel;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.LayoutConstant;

import de.zbit.graph.gui.SBGNEditMode;
import de.zbit.graph.gui.TranslatorSBMLgraphPanel;

import y.view.EditMode;
import y.view.Graph2DView;
import y.view.ViewMode;

/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class SBMLEditor {

  /**
   * @param args
   * @throws Throwable
   */
  public static void main(String[] args) throws Throwable {
    int level = 3, version = 1;
    System.setProperty("com.apple.mrj.application.apple.menu.about.name",
        "SBMLeditor");

    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    JFrame f = new JFrame("SBML Editor");

    f.setJMenuBar(new EditorMenu());
    f.add(new EditorToolbar(), BorderLayout.NORTH);

    // Model: SBMLDocument
    SBMLDocument doc = new SBMLDocument(level, version);
    Model model = doc.createModel("m1");

    // Layout
    ExtendedLayoutModel extLayout = new ExtendedLayoutModel(model);
    model.addExtension(LayoutConstant.namespaceURI, extLayout);
    Layout layout = extLayout.createLayout();

    TranslatorSBMLgraphPanel panel = new TranslatorSBMLgraphPanel(doc, false);

    Graph2DView view = panel.getGraph2DView();
    view.removeViewMode((ViewMode) view.getViewModes().next());
    EditMode editMode = new SBGNEditMode<SBMLDocument>(panel.getConverter());
    editMode.showNodeTips(true);
    view.addViewMode(editMode);

    f.getContentPane().add(panel);

    f.setMinimumSize(new Dimension(640, 480));
    f.pack();
    f.setLocationRelativeTo(null);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setVisible(true);
  }

}
