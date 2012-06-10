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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.sbml.jsbml.ext.layout.Layout;

import de.zbit.editor.SBMLEditorConstants;
import de.zbit.editor.control.CommandController;
import de.zbit.editor.control.SBMLView;

/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class SBMLEditor extends WindowAdapter implements SBMLView {
	
  public static final String PROGRAM_NAME = "SBML Editor";
  private JFrame frame;
  private CommandController commandController;
  private TabManager tabManager;
  private static Logger logger = Logger.getLogger(SBMLEditor.class.toString());
  
  /**
   * 
   */
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

  public CommandController getController(){
    return this.commandController;
  }

  /**
   * @return
   * @throws Throwable
   */
  private void setUpGUI() throws Throwable {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    frame = new JFrame(PROGRAM_NAME);
    frame.setJMenuBar(new EditorMenu(commandController, this));
    frame.add(new EditorToolbar(this), BorderLayout.NORTH);
    frame.add(tabManager);
    frame.setMinimumSize(new Dimension(640, 480));
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.addWindowListener(this);
    frame.setVisible(true);
  }


  /**
   * @param args
   * @throws Throwable
   */
  public static void main(String[] args) {
    if (GUITools.onMac()) {
      System.setProperty("com.apple.mrj.application.apple.menu.about.name",
        PROGRAM_NAME);
      System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
      System.setProperty("apple.laf.useScreenMenuBar", "true");
      System.setProperty("com.apple.macos.smallTabs", "true");
      System.setProperty("com.apple.macos.useScreenMenuBar", "true");
      System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
      System.setProperty("com.apple.mrj.application.live-resize", "true");
    }
    javax.swing.SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        new SBMLEditor();
      }
    });
  }


  @Override
  public boolean fileNew() {
    return commandController.fileNew();
  }
  
  /**
   * create popup to request File Open Input
   */
  @Override
  public File askUserOpenDialog() {
	  JFileChooser fc = GUIFactory.createFileChooser();
	  int returnVal = fc.showOpenDialog(this.frame);
	  File file = null;
	  if (returnVal == JFileChooser.APPROVE_OPTION) {
		  file = fc.getSelectedFile();
	  }
	  return file;
  }
  
  /**
   * create popup to request File Save Input
   */
  @Override
  public File askUserSaveDialog() {
    JFileChooser fc = GUIFactory.createFileChooser();
    int returnVal = fc.showSaveDialog(this.frame);
    File file = null;
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      file = fc.getSelectedFile();
    }
    return file;
  }
  
  public String askUserFileNew() {
    return JOptionPane.showInputDialog(Resources.getString("NEW_FILE"), Resources.getString("GENERIC_FILE_NAME"));
  }
  
  @Override
  public boolean fileOpen() throws FileNotFoundException {
    return commandController.fileOpen();
  }


  @Override
  public boolean fileClose() {
	  return commandController.fileClose();
  }


  @Override
  public boolean fileSave() {
	  return commandController.fileSave();
  }


  @Override
  public boolean fileSaveAs() {
    return commandController.fileSaveAs();
  }


  @Override
  public void fileQuit() {
    commandController.fileQuit();
  }


  /* (non-Javadoc)
   * @see de.zbit.editor.control.SBMLView#addDocument(de.zbit.editor.control.OpenedSBMLDocument)
   */
  @Override
  public boolean addLayout(Layout layout) {
    return getTabManager().addTab(layout);
  }


  /* (non-Javadoc)
   * @see de.zbit.editor.control.SBMLView#getSelectedLayout()
   */
  @Override
  public Layout getCurrentLayout() {
    return tabManager.getCurrentLayout();
  }


  /* (non-Javadoc)
   * @see de.zbit.editor.control.SBMLView#refreshTitle()
   */
  @Override
  public void refreshTitle() {
    tabManager.refreshTitle();
  }


  /* (non-Javadoc)
   * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
   */
  @Override
  public void windowClosing(WindowEvent e) {
    fileQuit();
  }


  public void addUnknownMolecule() {
    //this.getTabManager().addUnspecified();
    this.commandController.stateUnknownMolecule();
  }


  public void addSimpleMolecule() {
    this.commandController.stateSimpleMolecule();
  }


  public void addMacromolecule() {
    this.commandController.stateMacromolecule();
  }


  public void addEmptySet() {
    this.commandController.stateEmptySet();
  }
  
  public void addReaction() {
    this.commandController.stateReaction();
  }
  
  public void addCatalysis() {
    this.commandController.stateCatalysis();
  }
  
  public void addInhibition() {
    this.commandController.stateInhibition();
  }
  
  public String nameDialogue(String id) {
    return JOptionPane.showInputDialog("Enter name:", id);
  }
  
  @Override
  public void refresh(String id, String name, int sboTerm, double x, double y) {
    //TODO How to refresh the view, using the changed model?
    this.tabManager.refresh(id, name, sboTerm, x, y);
  }


  @Override
  public boolean closeTab(Layout layout) {
    return this.tabManager.closeTab(layout);
  }


  @Override
  public void showWarning(String warning) {
    JOptionPane.showMessageDialog(
      frame, 
      Resources.getString(warning), 
      Resources.getString(SBMLEditorConstants.warningTitle), 
      JOptionPane.WARNING_MESSAGE);
  }
  @Override
  public void showError(String error) {
    JOptionPane.showMessageDialog(
      frame, 
      Resources.getString(error), 
      Resources.getString(SBMLEditorConstants.errorTitle), 
      JOptionPane.ERROR_MESSAGE);
  }

}
