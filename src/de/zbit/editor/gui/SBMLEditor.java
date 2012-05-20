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
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import de.zbit.editor.control.CommandController;
import de.zbit.editor.control.OpenedDocument;
import de.zbit.editor.control.SBMLView;

/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class SBMLEditor extends WindowAdapter implements SBMLView  {

  public static final String PROGRAM_NAME = "SBML Editor";
  
  private JFrame frame;
  private CommandController commandController;
  private TabManager tabManager;
  
  // TODO: Do not declare variables of type ArrayList -> use the List interface or some other abstraction level.
  //private ArrayList<OpenedDocument> openedDocuments = new ArrayList<OpenedDocument>();
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
   * 
   * @return
   * @throws Throwable
   */
  private void setUpGUI() throws Throwable {
	  // TODO: This is already too late, doesn't work anymore.
    if (GUITools.onMac()) {
      System.setProperty("com.apple.mrj.application.apple.menu.about.name",
          PROGRAM_NAME);
    }
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    
    frame = new JFrame(PROGRAM_NAME);
    frame.setJMenuBar(new EditorMenu(commandController, this));
    frame.add(new EditorToolbar(this), BorderLayout.NORTH);
    frame.add(tabManager);
    
    
    frame.setMinimumSize(new Dimension(640, 480));
    frame.pack();
    frame.setLocationRelativeTo(null);
    // TODO: This will cause that program closes immediately. You may want to ask users if they want to save their work before closing the program. 
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.addWindowListener(this);
    frame.setVisible(true);
  }
  
  // TODO: This could be a public method in some new class GUITools.
  
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
  
  @Override
  public void fileNew() {
  	String name = JOptionPane.showInputDialog("Name", "FileNew");
  	if (name != null) {
  		if (!name.isEmpty()) {
  			commandController.fileNew(name);
  		} else {
  			fileNew();
  		}
  	}
  }
  
  @Override
  public boolean fileOpen() {
  	JFileChooser fc = GUIFactory.createFileChooser();
      int returnVal = fc.showOpenDialog(this.frame);
      
      if (returnVal == JFileChooser.APPROVE_OPTION)
  		try {
  			{
  			  File file = fc.getSelectedFile();
  			  try {
  				SBMLReadingTask task = new SBMLReadingTask(file, this.frame);
  				task.addPropertyChangeListener(commandController);
  				task.execute();
  			} catch (FileNotFoundException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
  			  //return commandController.fileOpen(file);      
  			}
  		} catch (Exception e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
      return false;
  }
  
  @Override
  public void fileClose() {
  	tabManager.closeCurrentTab();
  }
  
  @Override
  public void fileSave() {
  	commandController.fileSave();	
  }

  @Override
  public void fileSaveAs() {
  	  TabManager tabmanager = getTabManager(); 
  		if (tabmanager.isAnySelected()){
  			JFileChooser fc = GUIFactory.createFileChooser();
  			int returnVal = fc.showSaveDialog(getFrame());
  			// TODO: respect standard Java code convention
  			if (returnVal == JFileChooser.APPROVE_OPTION) {
  				File file = fc.getSelectedFile();
  				if (!GUIFactory.createFilterXML().accept(file)){
  					file = new File(file.getAbsolutePath()+".xml");
  				}
  	  	    	commandController.fileSaveAs(file);
  			}
  		}	
  }

  @Override
  public void fileQuit(){
  	int returnVal = GUIFactory.createQuestionClose(frame);
  	if(returnVal == JOptionPane.YES_OPTION){
  	    System.exit(0);
  	}
  }

  /**
   * @param doc
   */
  @Override
  public void addDocument(OpenedDocument doc) {
    //openedDocuments.add(doc);
    getTabManager().addTab(doc);
  }
  
  @Override
  public OpenedDocument getSelectedDoc() {
  	return tabManager.getCurrentDocument();
  }

  @Override
  public void refreshTitle() {
  	tabManager.refreshTitle();
  	
  }

  @Override
  public void windowClosing(WindowEvent e){
  	fileQuit();
  }
}