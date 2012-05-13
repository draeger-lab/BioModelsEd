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
import java.util.MissingResourceException;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.ext.layout.ExtendedLayoutModel;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.LayoutConstant;

import y.view.EditMode;
import y.view.Graph2DView;
import y.view.ViewMode;
import de.zbit.graph.gui.SBGNEditMode;
import de.zbit.graph.gui.TranslatorSBMLgraphPanel;

/**
 * @author Jakob Matthes
 * @version $Rev$
 */
public class SBMLEditor {

  private static final String PROGRAM_NAME = "SBMLeditor";
  private static final int sbmlLevel = 3;
  private static final int sbmlVersion = 1;
  private JFrame frame;
  private CommandController commandController;
  private TabManager tabManager;

  public SBMLEditor() {
    // Create controller
    commandController = new CommandController(this);
    
    // Tab Manager
    tabManager = new TabManager(this);   
        
    //GUI
    try {
      setUpGUI();
    } catch (Throwable e) {
      e.printStackTrace();
    }

    // Aus Beispiel:
    // Model: SBMLDocument
    SBMLDocument doc = new SBMLDocument(sbmlLevel, sbmlVersion);
    Model model = doc.createModel("m1");
    //model.createSpecies("s1");

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

    frame.getContentPane().add(panel);
    
  }

  //Create new SBML file
  public void fileNew(){
	  
	  SBMLDocument doc = new SBMLDocument(sbmlLevel, sbmlVersion);
	  Model model = doc.createModel("m1");
	  //TODO Create new Tab for File
	  
  }
  
  //Open SBML file
  public void fileOpen(){
	  
	  JFileChooser fc = new JFileChooser();
	  int returnVal = fc.showOpenDialog(frame);
	  
	  if(returnVal == JFileChooser.APPROVE_OPTION){
		  File file = fc.getSelectedFile();
		  try {
			  //Read file
			  SBMLDocument doc = SBMLReader.read(file);
			  Model model = doc.createModel("m1");
			  //TODO Create new Tab for File
			  
			  
		  } catch (XMLStreamException e) {
			  //e.printStackTrace();
			  System.err.println( e );
		  } catch (IOException e) {
			  //e.printStackTrace();
			  System.err.println( e );
		 }
	  }
  }
  
  //Save SBML file
  public void fileSave(){
	  
	  JFileChooser fc = new JFileChooser();
	  int returnVal = fc.showSaveDialog(frame);
	  
	  if(returnVal == JFileChooser.APPROVE_OPTION){
		  File file = fc.getSelectedFile();
	  /*
		  try {
			  //TODO Get document from currently visible tab
			  SBMLDocument doc = 
			  new SBMLWriter().write(doc,file);
			  
			  
		  } catch (XMLStreamException e) {
			  e.printStackTrace();
		  } catch (IOException e) {
			  e.printStackTrace();
		 }*/
	  }
	  
  }
  
  /**
   * 
   * @return
   * @throws Throwable
   */
  private void setUpGUI() throws Throwable {
    System.setProperty("com.apple.mrj.application.apple.menu.about.name",
        PROGRAM_NAME);
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    
    frame = new JFrame(PROGRAM_NAME);
    frame.setJMenuBar(new EditorMenu(commandController));
    frame.add(new EditorToolbar(commandController), BorderLayout.NORTH);
    
    frame.setMinimumSize(new Dimension(640, 480));
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
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
