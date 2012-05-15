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
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class SBMLEditor {

  public static final String PROGRAM_NAME = "SBMLeditor";
  public static final int sbmlLevel = 3;
  public static final int sbmlVersion = 1;
  private JFrame frame;
  private CommandController commandController;
  private TabManager tabManager;
  private ArrayList<OpenedDocument> openedDocuments = new ArrayList<OpenedDocument>();
  //private static Logger logger = Logger.getLogger(SBMLEditor.class.toString());

  public SBMLEditor() {
    commandController = new CommandController(this);
    tabManager = new TabManager(this);   

    try {
      setUpGUI();
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }
  
  /**
   * @return the frame
   */
  public JFrame getFrame() {
    return frame;
  }

  /**
   * @return the tabManager
   */
  public TabManager getTabManager() {
    return tabManager;
  }

  /**
   * @param doc
   */
  public void addDocument(OpenedDocument doc) {
    openedDocuments.add(doc);
  }
  
  /**
   * 
   * @return
   * @throws Throwable
   */
  private void setUpGUI() throws Throwable {
    if (onMac()) {
      System.setProperty("com.apple.mrj.application.apple.menu.about.name",
          PROGRAM_NAME);
    }
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    
    frame = new JFrame(PROGRAM_NAME);
    frame.setJMenuBar(new EditorMenu(commandController));
    frame.add(new EditorToolbar(commandController), BorderLayout.NORTH);
    frame.add(tabManager);
    
    
    frame.setMinimumSize(new Dimension(640, 480));
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }

  /**
   * Detect if system is a Mac OS
   * @return
   */
  private static boolean onMac() {
    return (System.getProperty("os.name").toLowerCase().contains("mac")
        || System.getProperty("mrj.version") != null);
  }

  /**
   * @param args
   * @throws Throwable
   */
  public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new SBMLEditor();
      }
    });
  }

}
