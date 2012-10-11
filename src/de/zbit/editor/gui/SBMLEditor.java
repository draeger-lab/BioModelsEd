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
import javax.swing.filechooser.FileNameExtensionFilter;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.ext.layout.BoundingBox;
import org.sbml.jsbml.ext.layout.CompartmentGlyph;
import org.sbml.jsbml.ext.layout.Dimensions;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.Point;

import de.zbit.editor.SBMLEditorConstants;
import de.zbit.editor.control.CommandController;
import de.zbit.editor.control.OpenedSBMLDocument;
import de.zbit.editor.control.SBMLView;

/**
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Eugen Netz
 * @author Jan Rudolph
 * @version $Rev$
 */
public class SBMLEditor extends WindowAdapter implements SBMLView {
	
  public static final String PROGRAM_NAME = "SBML Editor";
  private JFrame frame;
  private CommandController commandController;
  private EditorToolbar editorToolbar;
  private TabManager tabManager;
  private EditorMenu editorMenu;
  private static Logger logger = Logger.getLogger(SBMLEditor.class.toString());
  
  /**
   * Constructor, which creates a CommandController and TabManager and sets up the GUI.
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

  /**
   * @return the commandController
   */
  public CommandController getController(){
    return this.commandController;
  }

  /**
   * Sets up the GUI.
   * @throws Throwable
   */
  private void setUpGUI() throws Throwable {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    frame = new JFrame(PROGRAM_NAME);
    this.editorMenu = new EditorMenu(commandController, this);
    frame.setJMenuBar(editorMenu);
    editorToolbar = new EditorToolbar(this);
    frame.add(editorToolbar, BorderLayout.NORTH);
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


  /**
   * Forwards fileNew request to commandController.
   * @return true if succesful
   */
  @Override
  public boolean fileNew() {
    return commandController.fileNew();
  }
  
  public void setEnableState(boolean anyDocumentsOpen) {
    this.editorMenu.setEnableState(anyDocumentsOpen);
    this.editorToolbar.setEnableState(anyDocumentsOpen);
  }
  
  /**
   * Creates popup to request File Open Input.
   * @return the file to open
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
   * Creates popup to request File Save Input.
   * @return the file to save
   */
  @Override
  public File askUserSaveDialog() {
    JFileChooser fc = GUIFactory.createFileChooser();
    int returnVal = fc.showSaveDialog(this.frame);
    File file = null;
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      file = fc.getSelectedFile();
      // add extension
      if (! fc.getFileFilter().accept(file)) {
        file = new File(file.getAbsolutePath() + ".sbml");
      }
    }
    
    return file;
  }
  
  /**
   * Creates popup to request an Image Export.
   * @return the image file to export
   */
  public File askUserSaveDialogExport() {
    JFileChooser fc = GUIFactory.createFileChooserExport();
    int returnVal = fc.showSaveDialog(this.frame);
    File file = null;
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      file = fc.getSelectedFile();
      // add extension
      FileNameExtensionFilter filter = (FileNameExtensionFilter) fc.getFileFilter();
      if (! filter.accept(file)) {
        file = new File(file.getAbsolutePath() + "." + filter.getExtensions()[0]);
      }
      logger.info("Path to save image: " + file.getAbsolutePath());
    }
    
    return file;
  }
  
  /**
   * Creates popup for the input of a filename.
   * @eturn the filename
   */
  public String askUserFileNew() {
    return JOptionPane.showInputDialog(Resources.getString("NEW_FILE"), Resources.getString("GENERIC_FILE_NAME"));
  }
  
  /**
   * Creates popup for the input of a layoutname.
   * @return the layoutname
   */
  public String askUserLayoutNew() {
    return JOptionPane.showInputDialog(Resources.getString("NEW_LAYOUT"), Resources.getString("GENERIC_LAYOUT_NAME"));
  }
  
  /**
   * Creates popup for the input of a layoutname.
   * @return the layoutname
   */
  public String askUserLayoutRename() {
    return JOptionPane.showInputDialog(Resources.getString("NEW_NAME"), this.getCurrentLayout().getName());
  }

  /**
   * Asks user, if Glyphs should be created for all Structures in the model.
   * @return the Integer representing the JOptionPane Option
   */
  public int askUserCreateLayoutInformation() {
    return JOptionPane.showConfirmDialog(null, Resources.getString("DIALOG_CREATE_LAYOUT_QUESTION"), Resources.getString("DIALOG_CREATE_LAYOUT_TITLE"), 0);
  }
  
  /**
   * Forwards fileOpen request to commandController.
   * @return true if succesful
   */
  @Override
  public boolean fileOpen() throws FileNotFoundException {
    return commandController.fileOpen();
  }

  /**
   * Forwards fileClose request to commandController.
   * @return true if succesful
   */
  @Override
  public boolean fileClose() {
	  return commandController.fileClose();
  }

  /**
   * Forwards fileSave request to commandController.
   * @return true if succesful
   */
  @Override
  public boolean fileSave() {
	  boolean success = commandController.fileSave();
    if(success) {
      this.tabManager.refreshTitle(getCurrentLayout());
      this.editorMenu.setSaveState(false);
    }
    return success;
  }

  /**
   * Forwards fileSaveAs request to commandController.
   * @return true if succesful
   */
  @Override
  public boolean fileSaveAs() {
    boolean success = commandController.fileSaveAs();
    if(success) {
      this.tabManager.refreshTitle(getCurrentLayout());
    }
    return success;
  }

  /**
   * Calls a SaveDialog for the export and forwards the request to the commandController.
   * @return true if succesful
   */
  public boolean fileExport() {
    File file = askUserSaveDialogExport();
    return (file == null) ? false :  this.commandController.fileExport(file);
  }

  /**
   * Forwards a fileQuit request to the commandController
   * @return true if succesful
   */
  @Override
  public boolean fileQuit() {
    return commandController.fileQuit();
  }


  /**
   * Opens the layout in a new tab.
   * @param layout, the layout to open
   * @param autoLayout, if true the autoLayout Algorithm is used on the layout
   * @return true if succesful
   */
  @Override
  public boolean addLayout(Layout layout, boolean autoLayout) {
    return getTabManager().addTab(layout, autoLayout);
  }


  /**
   * Forwards a getCurrentLayout request to the TabManager.
   * @return the current layout from the TabManager
   */
  @Override
  public Layout getCurrentLayout() {
    return tabManager.getCurrentLayout();
  }

  /**
   * Forwards a fileQuit request to the commandController
   */
  @Override
  public void windowClosing(WindowEvent e) {
    fileQuit();
  }

  /**
   * Changes the State of the commandController to unknownMolecule.
   */
  public void addUnknownMolecule() {
    this.commandController.stateUnknownMolecule();
  }

  /**
   * Changes the State of the commandController to simpleMolecule.
   */
  public void addSimpleMolecule() {
    this.commandController.stateSimpleMolecule();
  }

  /**
   * Changes the State of the commandController to macromolecule.
   */
  public void addMacromolecule() {
    this.commandController.stateMacromolecule();
  }

  /**
   * Changes the State of the commandController to unknownMolecule.
   */
  public void addEmptySet() {
    this.commandController.stateEmptySet();
  }
  
  /**
   * Changes the State of the commandController to reaction.
   */
  public void addReaction() {
    this.commandController.stateReaction();
  }
  
  /**
   * Changes the State of the commandController to catalysis.
   */
  public void addCatalysis() {
    this.commandController.stateCatalysis();
  }
  
  /**
   * Changes the State of the commandController to inhibition.
   */
  public void addInhibition() {
    this.commandController.stateInhibition();
  }
  
  /**
   * Toggles the reversible field in the commandController, which determines, whether a created Reaction is reversible.
   */
  public void reversible() {
    this.commandController.changeReversible();
  }
  
  /**
   * Creates popup for the input of a Species name.
   * @param s, the default name
   * @return the name
   */
  public String nameDialogue(String s) {
    return JOptionPane.showInputDialog(Resources.getString("NEW_SPECIES"), s);
  }
  
  /**
   * Forwards a closeTab request to the TabManager, that closes the tab, that shows the layout.
   * @param layout, the layout to be closed
   * @return true if succesful
   */
  @Override
  public boolean closeTab(Layout layout) {
    return this.tabManager.closeTab(layout);
  }

  /**
   * Shows a warning message corresponding to the given String.
   * @param warning
   */
  @Override
  public void showWarning(String warning) {
    JOptionPane.showMessageDialog(
      frame, 
      Resources.getString(warning), 
      Resources.getString(SBMLEditorConstants.warningTitle), 
      JOptionPane.WARNING_MESSAGE);
  }
  
  /**
   * Shows an error message corresponding to the given String.
   * @param error
   */
  @Override
  public void showError(String error) {
    JOptionPane.showMessageDialog(
      frame, 
      Resources.getString(error), 
      Resources.getString(SBMLEditorConstants.errorTitle), 
      JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Refreshes the title of the layout shown in the tab.
   * @param layout 
   */
  @Override
  public void refreshTitle(Layout layout) {
    this.tabManager.refreshTitle(layout);
    this.editorMenu.setSaveState(true);
  }
  
  /**
   * Updates the ComboBox for choice of layout.
   * @param list
   */
  @Override
  public void updateComboBox(ListOf<Layout> list) {
    editorToolbar.updateComboBox(list);    
  }
  
  /**
   * Opens the layout selected in the ComboBox in a new tab.
   */
  @Override
  public void openLayoutInNewTab() {
    Layout layout = editorToolbar.getSelectedLayout();
    logger.info("Try to Open Layout in new Tab ID: " + layout.getId() + " Layout Name: " + layout.getName());
    if(layout != null) {
      if (tabManager.isLayoutOpen(layout)) {
        tabManager.showTab(layout);
      }
      else {
        addLayout(layout, false);
      }
    }
  }
  
  /**
   * Opens the layout selected in the ComboBox in the current tab.
   */
  @Override
  public void openLayoutInTab() {
    Layout layout = editorToolbar.getSelectedLayout();
    logger.info("Try to Open Layout in current Tab ID: " + layout.getId() + " Layout Name: " + layout.getName());
    if(layout != null) {
      if (tabManager.isLayoutOpen(layout)) {
        tabManager.showTab(layout);
      }
      else {
        tabManager.changeTab(getCurrentLayout(), layout);
      }
    }
  }
  
  /**
   * Creates a new empty layout and opens it in a new tab.
   */
  @Override
  public boolean layoutNew() {
    OpenedSBMLDocument doc = (OpenedSBMLDocument) getCurrentLayout().getSBMLDocument()
        .getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);
    String layoutName = askUserLayoutNew();
    if (layoutName == null) {
      return false;
    }
    Layout layout = doc.createNewLayout(layoutName);
    
    return addLayout(layout, false);
  }
  
  /**
   * Clones the layout shown in the current tab and opens it in a new tab.
   */
  @Override
  public void layoutClone() {
    Layout layout = getCurrentLayout();
    OpenedSBMLDocument doc = (OpenedSBMLDocument) layout.getSBMLDocument()
        .getUserObject(SBMLEditorConstants.associatedOpenedSBMLDocument);

    Layout clonedLayout = doc.addLayout(layout); 
    
    logger.info("Cloning Layout ID: "+ layout.getId() + " Name: " +layout.getName() + " to  Layout ID: "+ clonedLayout.getId() + " Name: " +clonedLayout.getName());
    addLayout(clonedLayout, false);
  }
  
  /**
   * Deletes the layout shown in the current tab.
   */
  @Override
  public void layoutDelete() {
    commandController.layoutDelete(getCurrentLayout());
  }
  
  /**
   * Closes the tab, that shows the given layout.
   * @param layout
   */
  @Override
  public boolean layoutClose(Layout layout) {
    logger.info("ID: "+ layout.getId() + " Name: " +layout.getName());
    return commandController.layoutClose(layout);
  }


  /**
   * Shows the "About"-message.
   */
  @Override
  public void helpAbout() {
    JOptionPane.showMessageDialog(frame,
        Resources.getString("DIALOG_ABOUT_MESSAGE"),
        Resources.getString("DIALOG_ABOUT_TITLE"),
        JOptionPane.PLAIN_MESSAGE
        );
  }


  /**
   * Renames the current layout.
   */
  @Override
  public boolean layoutRename() {
    String name = askUserLayoutRename();
    if (name == null) {
      return false;
    }
    else {
      return this.commandController.layoutRename(this.getCurrentLayout(), name);
    }
  }


  /**
   * Applies an algorithm for an automated layout to the current layout.
   */
  @Override
  public boolean layoutAuto() {
    return this.tabManager.layoutAuto(this.getCurrentLayout());
  } 
}