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
package de.zbit.editor.control;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.ModifierSpeciesReference;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBO;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.NamedSBaseGlyph;
import org.sbml.jsbml.ext.layout.ReactionGlyph;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceRole;
import org.sbml.jsbml.ext.layout.TextGlyph;
import org.sbml.jsbml.util.ValuePair;

import y.base.Node;
import y.io.GIFIOHandler;
import y.io.ImageOutputHandler;
import y.io.JPGIOHandler;
import y.view.Graph2D;
import y.view.Graph2DView;
import de.zbit.editor.BioModelsEdConstants;
import de.zbit.editor.gui.BioModelsEdGUIFactory;
import de.zbit.editor.gui.GraphLayoutPanel;
import de.zbit.editor.gui.Resources;
import de.zbit.editor.gui.SBMLEditMode;
import de.zbit.gui.GUITools;
import de.zbit.io.OpenedFile;

/**
 * Controlls the execution of all commands and conveys them to the View and the Document.
 * 
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Eugen Netz
 * @author Jan Rudolph
 * @version $Rev$
 */
public class CommandController implements PropertyChangeListener {
	
	private FileManager fileManager;
	private Logger logger = Logger.getLogger(CommandController.class.getName());
	private SBMLView view;
	
	
	private List<Node> nodeList = new ArrayList<Node>();
	private List<NamedSBaseGlyph> nodeCopyList = new ArrayList<NamedSBaseGlyph>();
	
	/**
	 * Constructor
	 * @param editorInstance must implement SBMLView
	 */
	public CommandController(SBMLView editorInstance) {
		this();
		this.setView(editorInstance);
	}
	/**
	 * Constructor 
	 * @param editorInstance must implement SBMLView
	 */
	public CommandController() {
		this.fileManager = new FileManager(this);
		this.logger.setLevel(Level.CONFIG);
	}
	
	/**
	 * setting view for command controller
	 * @param view
	 */
	public void setView(SBMLView view) {
		this.view = view;
	}
	
	
	/**
	 * Creates a Species and adds it to the model.
	 * Creates a SpeciesGlyph and adds it to the layout. Its Position is determined by the ValuePair in NewValue of evt.
	 * @param evt
	 * @param sboTerm the SBO-Term of the Species
	 */
	@SuppressWarnings("unchecked")
	public void createSpecies(OpenedFile<SBMLDocument> selectedDoc, Layout layout, 
		String name, PropertyChangeEvent evt, int sboTerm) {
		String speciesId = SBMLTools.getNextGenericId(selectedDoc, BioModelsEdConstants.genericId);
		
		ValuePair<Double, Double> newMousePosition = (ValuePair<Double, Double>) evt.getNewValue();
		Double x = newMousePosition.getL();
		Double y = newMousePosition.getV();
		
		// layout and model references
		Model model = layout.getModel();
		final int level = model.getLevel();
		final int version = model.getVersion();
		
		String glyphId = SBMLTools.getNextGenericId(selectedDoc, BioModelsEdConstants.genericGlyphIdPrefix);
		String textglyphId = SBMLTools.getNextGenericId(selectedDoc, BioModelsEdConstants.genericTextGlyphIdPrefix);
		
		String compartmentId = findCompartmentId(x, y);
		Species s = SBMLFactory.createSpecies(speciesId, name, sboTerm, level, version, compartmentId);
		SpeciesGlyph sGlyph = SBMLFactory.createSpeciesGlyph(glyphId , level, version, speciesId);
		TextGlyph tGlyph = SBMLFactory.createTextGlyph(textglyphId, level, version, sGlyph, speciesId);
		
		model.addSpecies(s);
		SBMLFactory.addSpeciesGlyphToLayout(layout, sGlyph, x, y, s.getName());
		layout.addTextGlyph(tGlyph);
		
		selectedDoc.setChanged(true);
		view.refreshTitle(layout);
		
		// keep a list of all glyphs which are associated with the species
		List<String> glyphList = new ArrayList<String>();
		glyphList.add(sGlyph.getId());
		
		Map<String, List<String>> layoutGlyphMap = new HashMap<String, List<String>>();
		layoutGlyphMap.put(layout.getId(), glyphList);
		
		s.putUserObject(BioModelsEdConstants.GLYPH_LINK_KEY, layoutGlyphMap);
		
	}
	
	
	/**
	 * Creates a ReactionGlyph with the SpeciesGlyph corresponding to the sourceNode as the Substrate
	 * and the SpeciesGlyph corresponding to the targetNode as the Product and adds it to the layout.
	 * @param sourceNode
	 * @param targetNode
	 * @return the ReactionGlyph
	 */
	public ReactionGlyph createReaction(OpenedFile<SBMLDocument> file, Layout layout, 
		SpeciesGlyph source, SpeciesGlyph target, Boolean reversible) {
		logger.info(source.getName() + " -> " + target.getName());
		Model model = layout.getModel();
		
		Reaction reaction = SBMLFactory.createReaction(file, model.getSpecies(source.getSpecies()),
			model.getSpecies(target.getSpecies()), reversible, model.getLevel(), model.getVersion());
		ReactionGlyph reactionGlyph = SBMLFactory.createReactionGlyph(file, reaction, source, target, model.getLevel(), model.getVersion());
		reaction.setName(reaction.getId());
		model.addReaction(reaction);
		layout.add(reactionGlyph);
		return reactionGlyph;
	}
	
	/**
	 * Creates a ModifierSpeciesReference and adds it to the model.
	 * Creates a SpeciesReferenceGlyph and adds it to the Layout.
	 * @param sourceNode corresponding to the SpeciesGlyph
	 * @param targetNode corresponding to the ReactionGlyph
	 */
	public void createModifier(OpenedFile<SBMLDocument>  file, Layout layout, 
		SpeciesGlyph source, ReactionGlyph target, int sbo) {
		
		Model model = layout.getModel();
		Reaction reaction = (Reaction) target.getReactionInstance();
		reaction.addModifier(SBMLFactory.createModifierSpeciesReference(file, model, sbo, source));
		SpeciesReferenceGlyph modifierGlyph = SBMLFactory.createSpeciesReferenceGlyph(file, source, target, sbo);
		target.addSpeciesReferenceGlyph(modifierGlyph);
		logger.info("created Modifier");
	}
	/**
	 * Opens an empty {@link #SBMLDocument}.
	 * @param name of new file
	 * @return true if successful
	 */
	public boolean fileNew() {
		SBMLDocument sbmlDocument = SBMLFactory.createNewDocument();
		OpenedFile<SBMLDocument> doc = new OpenedFile<SBMLDocument>(sbmlDocument);
		return fileManager.addDocument(doc) && view.addTab(doc);
	}
	
	/**
	 * Quits the program.
	 * @return true, if succesful.
	 */
	public boolean fileQuit() {
		//TODO implement fileQuit
		/*if (this.fileManager.anyFileIsModified()) {
      int returnVal = GUIFactory.createQuestionClose(this.view.getFrame());
      if (returnVal == JOptionPane.YES_OPTION && this.view.getTabManager().closeAllTabs()) {
        System.exit(0);
      }
    } else {
      System.exit(0);
    }*/
		return true;
	}
	
	/**
	 * Gets currently selected doc from view and forwards it to filemanager for saving.
	 * wrapper for fileSave(OpenedFile<SBMLDocument> doc)
	 * @return true if successful. 
	 */
	@SuppressWarnings("unchecked")
	public boolean fileSave() {
//		OpenedFile<SBMLDocument> selectedDoc = (OpenedFile<SBMLDocument>) this.view
//				.getCurrentLayout().getSBMLDocument()
//				.getUserObject(BioModelsEdConstants.associatedOpenedFile);
//		return fileSave(selectedDoc);
		return false;
	}
	
	/**
	 * forwards doc to filemanager for saving.
	 * @return true if successful. 
	 */
	public boolean fileSave(OpenedFile<SBMLDocument> selectedDoc) {
		return fileManager.fileSave(selectedDoc);
	}
	
	/**
	 * forwards doc to filemanager for saving.
	 * @return true if successful. 
	 */
	public boolean fileSaveAll() {
		boolean success = true;
		for (OpenedFile<SBMLDocument> file : view.getTabManager().getOpenedFiles()) {
			success |= fileSave(file);
		}
		return success;
	}
	
	/**
	 * Gets currently selected doc from view and forwards it to filemanager for saving.
	 * @return true if successful. 
	 */
	@SuppressWarnings("unchecked")
	public boolean fileSaveAs() {
//		OpenedFile<SBMLDocument> selectedDoc = (OpenedFile<SBMLDocument>) this.view
//				.getCurrentLayout().getSBMLDocument()
//				.getUserObject(BioModelsEdConstants.associatedOpenedFile);
//		return fileManager.fileSaveAs(selectedDoc);
		return false;
	}
	
	/**
	 * Open files and return all successful for history
	 */
	public File[] openFile(File... arg0) {
		logger.info("openFile");
		return arg0 != null ? fileManager.openFile(arg0) : null;
	}
	/**
	 * Forwards fileClose request to file manager.
	 * @return true if successful.
	 */
	public boolean fileClose() {
		// TODO
		return false;
	}
	
	/**
	 * Forwards save dialog request to view.
	 * @return chosen filepath
	 */
	public File askUserSaveDialog() {
		return view.askUserSaveDialog();
	}
	
	
	/**
	 * @return the editorInstance
	 */
	public SBMLView getEditorInstance() {
		return view;
	}
	
	/**
	 * Is called by a fired PropertyChange and determines the followed action.
	 */
	@SuppressWarnings("unchecked")
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(BioModelsEdConstants.openingDone)) {
			OpenedFile<SBMLDocument> doc = (OpenedFile<SBMLDocument>) evt.getNewValue();
			this.view.addTab(doc);
			this.fileManager.addDocument(doc);
		}
	}
	
	
	/**
	 * Calls {@link #updateGlyphFromNode} for every node in {@link #nodeList}.
	 * @param evt
	 */
	public void updateNodes(PropertyChangeEvent evt) {
		Graph2D graph = (Graph2D) evt.getNewValue();
		
		for (Node node : this.nodeList) {
			updateGlyphFromNode(node, graph);
		}
	}
	
	/**
	 * Updates the BoundingBox of the Glyph corresponding to the node with the size and position from the node.
	 * @param node
	 * @param graph
	 * @return true, if the Glyph was updated. false, if the Glyph wasn't found.
	 */
	public boolean updateGlyphFromNode(Node node, Graph2D graph) {
//		NamedSBaseGlyph glyph =  getGlyphFromNode(node);
//		if (glyph == null) {
//			logger.info("Couldn't find glyph for node");
//			return false;
//		}
//		else {
//			double x = graph.getX(node);
//			double y = graph.getY(node);
//			double width = graph.getWidth(node);
//			double height = graph.getHeight(node);
//			glyph.createBoundingBox(width, height, BioModelsEdConstants.glyphDefaultDepth, x, y, BioModelsEdConstants.glyphDefaultZ);
//			logger.info("Updating glyph information: " + 
//					"Id: " + glyph.getId() + 
//					" X: " + x +
//					" Y:" + y +
//					" Width: " + width +
//					" Height: "+ height);
//			this.layoutModified(this.view.getCurrentLayout());
//			return true;
//		} 
		return false;
	}
	
	/**
	 * Finds the SpeciesGlyph or ReactionGlyph corresponding to the node.
	 * @param node
	 * @return the Glyph or null, if it wasn't found within the SpeciesGlyph or ReactionGlyph lists.
	 */
	private NamedSBaseGlyph getGlyphFromNode(Node node, Layout layout) {
		for (SpeciesGlyph glyph : layout.getListOfSpeciesGlyphs()) {
			Node n = (Node) glyph.getUserObject(BioModelsEdConstants.GLYPH_NODE_KEY);
			if (node == n) {
				return glyph;
			}
		}
		for (ReactionGlyph glyph : layout.getListOfReactionGlyphs()) {
			Node n = (Node) glyph.getUserObject(BioModelsEdConstants.GLYPH_NODE_KEY);
			if (node == n) {
				return glyph;
			}
		}
		return null;
	}
	
	
	/**
	 * Closes the tab, that shows the given layout.
	 * @param layout
	 * @return true, if a tab was found and closed. false otherwise.
	 */
	public boolean closeTab(Layout layout) {
		return this.view.closeTab(layout);
	}
	
	/**
	 * Shows a File-Not-Found error.
	 */
	public void fileNotFound() {
		view.showError(BioModelsEdConstants.fileNotFound);
	}
	
	
	/**
	 * Removes the given layout from the model and closes the corresponding tab.
	 * @param currentLayout
	 */
	public void layoutDelete(Layout layout) {
		//TODO implement layoutDelete
		/*OpenedFile<SBMLDocument> doc = (OpenedFile<SBMLDocument>) layout.getSBMLDocument()
        .getUserObject(SBMLEditorConstants.associatedOpenedFile<SBMLDocument>);
    boolean anyopen = view.getTabManager().isAnyOpenFromDocument(layout);
    if(doc.getListOfLayouts().size() == 1) {
      logger.info("Document doesn't have 2 or more layouts");
      this.view.showError("ERROR_LAYOUT_DELETE");
    }
    else if (anyopen || !anyopen && GUIFactory.createQuestionDelete(this.view.getFrame()) == JOptionPane.YES_OPTION){
      logger.info("Try to delete Layout ID: " + layout.getId() + " Layout Name: " + layout.getName());
      doc.getListOfLayouts().remove(layout);
      doc.setFileModified(true);
      view.closeTab(layout);
    }*/
	}
	
	/**
	 * Asks the user, if the file should be saved and carries out the task.
	 * @param doc
	 * @return if user did not cancel saving progress
	 */
	private boolean askUserSave(OpenedFile<SBMLDocument> doc) {
		//TODO impelement askUserSave
		/*if (doc.isFileModified()) {
      int returnVal = GUIFactory.createQuestionSave(this.view.getFrame(), doc.getAssociatedFilename());
      if (returnVal == JOptionPane.YES_OPTION) {
        logger.info("User chose to save file");
        return fileSave();
      }
      else if (returnVal == JOptionPane.NO_OPTION) {
        logger.info("User chose to not save file");
        return true;
      }
      else {
        logger.info("User canceled closing");
        return false;
      }
    } */
		return true;
	}
	
	/**
	 * Deletes all currently selected nodes.
	 */
	public void nodeDelete() {
//		Layout layout = this.view.getCurrentLayout();
//		OpenedFile<SBMLDocument> selectedDoc = getDocumentFromLayout(layout);
//		
//		for (Node node : this.nodeList) {
//			NamedSBaseGlyph glyph = getGlyphFromNode(node);
//			if (glyph == null) {
//				logger.info("Couldn't find glyph for node");
//			}
//			else if (glyph instanceof SpeciesGlyph) {
//				String speciesId = ((SpeciesGlyph) glyph).getSpecies();
//				deleteReactionGlyphs((SpeciesGlyph) glyph, layout);
//				
//				if (this.nodeCopyList.remove(glyph)){
//					logger.info("Removed glyph from copylist");
//				}
//				layout.getListOfSpeciesGlyphs().remove(glyph);
//				layout.firePropertyChange("nodeDelete", null, glyph.getUserObject(BioModelsEdConstants.GLYPH_NODE_KEY));
//				if (!SBMLTools.hasAnySpeciesGlyphForSpeciesId(selectedDoc, speciesId)) {
//					logger.info("No glyph left for species: Deleting species id: " + speciesId);
//					layout.getModel().removeSpecies(speciesId);
//				}
//			}
//			else if (glyph instanceof ReactionGlyph) {
//				this.nodeCopyList.remove(glyph);
//				layout.getListOfReactionGlyphs().remove(glyph);
//				layout.firePropertyChange("nodeDelete", null, glyph.getUserObject(BioModelsEdConstants.GLYPH_NODE_KEY));
//			}
//		}
//		
//		logger.info("nodeDelete in CC");
//		layoutModified(layout);
	}
	
	/**
	 * Finds the ReactionGlyphs, that hold SpeciesReferenceGlyphs of the given SpeciesGlyph.
	 * @param speciesGlyph
	 * @param layout
	 * @return a list of the found ReactionGlyphs.
	 */
	public List<ReactionGlyph> findReactionGlyphs(SpeciesGlyph speciesGlyph, Layout layout) {
		ArrayList<ReactionGlyph> list = new ArrayList<ReactionGlyph>();
		
		for (ReactionGlyph rGlyph : layout.getListOfReactionGlyphs()) {
			List<SpeciesReferenceGlyph> sRefList = rGlyph.getListOfSpeciesReferenceGlyphs();
			
			for (int i = 0; i < sRefList.size(); i++) {
				if((speciesGlyph.getId().equals(sRefList.get(i).getSpeciesGlyph())) && 
						((sRefList.get(i).getSpeciesReferenceRole() == SpeciesReferenceRole.PRODUCT) ||
								(sRefList.get(i).getSpeciesReferenceRole() == SpeciesReferenceRole.SUBSTRATE))){
					logger.info("Reaktionen löschen: Reaction"+ rGlyph.getId() + " zur Liste hinzugefügt.");
					list.add(rGlyph);
				}
			}
		}
		logger.info("Reaktionen löschen: Liste der Reaktionen wurde erstellt");
		return list;
	}
	
	/**
	 * Deletes the ReactionGlyphs, that hold SpeciesReferenceGlyphs of the given SpeciesGlyph.
	 * @param speciesGlyph
	 * @param layout
	 */
	private void deleteReactionGlyphs(SpeciesGlyph speciesGlyph, Layout layout) {
		List<ReactionGlyph> list = findReactionGlyphs(speciesGlyph, layout);
		
		for (ReactionGlyph rGlyph : list) {
			layout.getListOfReactionGlyphs().remove(rGlyph);
			layout.firePropertyChange("nodeDelete", null, rGlyph.getUserObject(BioModelsEdConstants.GLYPH_NODE_KEY));
			
			if (this.nodeCopyList.remove(rGlyph)){
				logger.info("Removed glyph from copylist");
			}
			logger.info("Reaktionen löschen: Reaction gelöscht.");
		}
	}
	
	/**
	 * 
	 */
	public void editDelete() {
		this.nodeDelete();
	}
	
	/**
	 * Memorizes the selected nodes to paste them later with {@link #nodePaste}.
	 */
	public void nodeCopy() {
//		this.nodeCopyList.clear();
//		for(Node n : this.nodeList) {
//			NamedSBaseGlyph glyph = getGlyphFromNode(n);
//			if (glyph != null) {
//				this.nodeCopyList.add(glyph);
//			}
//		}
//		logger.info("Copy list has changed: Size: " + this.nodeCopyList.size());
	}
	
	/**
	 * 
	 */
	public void editCopy() {
		this.nodeCopy();
	}
	
	
	
	/**
	 * Pastes the nodes memorized with {@link #nodeCopy}.
	 */
	public void nodePaste() {
//		logger.info("Pasting...");
//		Layout layout = this.view.getCurrentLayout();
//		OpenedFile<SBMLDocument> selectedDoc = getDocumentFromLayout(layout);
//		
//		for (NamedSBaseGlyph glyph : this.nodeCopyList) {
//			
//			if (glyph instanceof SpeciesGlyph) {
//				SpeciesGlyph copySpeciesGlyph = (SpeciesGlyph) glyph;
//				copySpeciesGlyph(layout, selectedDoc, copySpeciesGlyph);
//			}
//			else if (glyph instanceof ReactionGlyph) {
//				ReactionGlyph copyReactionGlyph = (ReactionGlyph) glyph;
//				copyReactionGlyph(layout, selectedDoc, copyReactionGlyph);
//			}
//		}
//		layoutModified(layout);
	}
	
	/**
	 * Not implemented.
	 * @param layout
	 * @param selectedDoc
	 * @param copyReactionGlyph
	 */
	private void copyReactionGlyph(Layout layout, OpenedFile<SBMLDocument> selectedDoc,
		ReactionGlyph copyReactionGlyph) {
		logger.info("Reactionglyph");    
	}
	
	/**
	 * Copies a SpeciesGlyph
	 * @param selectedDoc
	 * @param layout
	 * @param copySpeciesGlyph
	 */
	private void copySpeciesGlyph(Layout layout, OpenedFile<SBMLDocument> selectedDoc, SpeciesGlyph copySpeciesGlyph) {
		
		String speciesId = copySpeciesGlyph.getSpecies();
		Species species = copySpeciesGlyph.getModel().getSpecies(speciesId);
		
		TextGlyph originalTextGlyph = (TextGlyph) copySpeciesGlyph.getUserObject(BioModelsEdConstants.GRAPHOBJECT_TEXTGLYPH_KEY);
		String glyphId = SBMLTools.getNextGenericId(selectedDoc, BioModelsEdConstants.genericGlyphIdPrefix);
		String textGlyphId = SBMLTools.getNextGenericId(selectedDoc, BioModelsEdConstants.genericTextGlyphIdPrefix);
		
		double x = copySpeciesGlyph.getBoundingBox().getPosition().getX();
		double y = copySpeciesGlyph.getBoundingBox().getPosition().getY();
		double width = copySpeciesGlyph.getBoundingBox().getDimensions().getWidth();
		double height = copySpeciesGlyph.getBoundingBox().getDimensions().getHeight();
		
		if(layout.getModel() == copySpeciesGlyph.getModel()) {
			logger.info("nodePaste: Same Model");  
			
			SpeciesGlyph speciesGlyph = SBMLFactory.createSpeciesGlyph(glyphId, SBMLView.DEFAULT_LEVEL_VERSION.getL(), SBMLView.DEFAULT_LEVEL_VERSION.getV(), x, y, width, height, speciesId);
			layout.add(speciesGlyph);
			
			logger.info("New Glyph: " + 
					"Id: " + speciesGlyph.getId() + 
					" X: " + x +
					" Y:" + y +
					" Width: " + width +
					" Height: "+ height);
			
			if (originalTextGlyph != null) {
				TextGlyph newTextGlyph = SBMLFactory.createTextGlyph(textGlyphId,
					SBMLView.DEFAULT_LEVEL_VERSION.getL(),
					SBMLView.DEFAULT_LEVEL_VERSION.getV(),
					speciesGlyph,
					originalTextGlyph.getNamedSBase());
				layout.addTextGlyph(newTextGlyph);
			}
		}
		else {
			logger.info("nodePaste: Different Model");
			String speciesIdNew = SBMLTools.getNextGenericId(selectedDoc, BioModelsEdConstants.genericId);
			Species s = SBMLFactory.createSpecies(speciesIdNew,
				species.getName(),
				species.getSBOTerm(),
				SBMLView.DEFAULT_LEVEL_VERSION.getL(),
				SBMLView.DEFAULT_LEVEL_VERSION.getV(),
				SBMLTools.getDefaultCompartmentId(selectedDoc));
			layout.getModel().addSpecies(s);
			SpeciesGlyph speciesGlyph = SBMLFactory.createSpeciesGlyph(glyphId,
				SBMLView.DEFAULT_LEVEL_VERSION.getL(),
				SBMLView.DEFAULT_LEVEL_VERSION.getV(),
				x,
				y,
				width,
				height,
				speciesIdNew);
			layout.add(speciesGlyph);
			
			logger.info("New Glyph: " + 
					"Id: " + speciesGlyph.getId() + 
					" X: " + x +
					" Y:" + y +
					" Width: " + width +
					" Height: "+ height);
			
			if (originalTextGlyph != null) {
				TextGlyph newTextGlyph = SBMLFactory.createTextGlyph(textGlyphId,
					SBMLView.DEFAULT_LEVEL_VERSION.getL(),
					SBMLView.DEFAULT_LEVEL_VERSION.getV(),
					speciesGlyph,
					speciesIdNew);
				layout.addTextGlyph(newTextGlyph);
			}
		}    
	}
	
	/**
	 * 
	 */
	public void editPaste() {
		this.nodePaste();
	}
	
	/**
	 * Finds the document, that holds the given layout.
	 * @param layout
	 * @return the found document.
	 */
	@SuppressWarnings("unchecked")
	private OpenedFile<SBMLDocument> getDocumentFromLayout(Layout layout) {
		return (OpenedFile<SBMLDocument>) layout.getSBMLDocument()
				.getUserObject(BioModelsEdConstants.associatedOpenedFile);
	}
	
	private void layoutModified(Layout layout) {
		OpenedFile<SBMLDocument> doc = getDocumentFromLayout(layout);
		doc.setChanged(true);
		this.view.refreshTitle(layout);
	}
	
	/**
	 * Renames the given layout.
	 * @param currentLayout
	 * @param name
	 */
	public boolean layoutRename(Layout layout, String name) {
		layout.setName(name);
		layoutModified(layout);
		//TODO implement combobox updating
		this.view.updateComboBox(
			SBMLTools.getListOfLayouts(layout.getSBMLDocument()));
		return true;
	}
	
	/**
	 * Exports the current view as a JPED or GIF.
	 * @return true, if file was exported succesfully. false otherwise.
	 */
	public boolean fileExport(File file) {
		GraphLayoutPanel panel = (GraphLayoutPanel) view.getTabManager().getSelectedComponent();
		Graph2DView view = panel.getGraph2DView();
		Graph2D graph = view.getGraph2D();
		
		if(BioModelsEdGUIFactory.createFilterGIF().accept(file)) {
			logger.info("Exporting gif file: " + file.getAbsolutePath());
			exportGraphToImageFileFormat(graph, new GIFIOHandler(), file.getAbsolutePath());
		}
		else if (BioModelsEdGUIFactory.createFilterJPEG().accept(file)) {
			logger.info("Exporting jpeg file: " + file.getAbsolutePath());
			exportGraphToImageFileFormat(graph, new JPGIOHandler(), file.getAbsolutePath());      
		}
		return false;
	}
	
	/**
	 * Exports the graph in the format specified by ioh.
	 * @param graph
	 * @param ioh
	 * @param outFile
	 */
	private void exportGraphToImageFileFormat(Graph2D graph, ImageOutputHandler ioh, String outFile) {  
		
		// Save the currently active view.   
		Graph2DView originalView = (Graph2DView)graph.getCurrentView();  
		
		// Create a new Graph2DView instance with the graph. This will be the   
		// dedicated view for image export.   
		Graph2DView exportView = ioh.createDefaultGraph2DView(graph);  
		
		Rectangle box = exportView.getGraph2D().getBoundingBox();  
		Dimension dim = box.getSize();  
		
		// Set the view's width and height, in turn this also sets the image's size.   
		exportView.setSize(dim);  
		// The clipping should show the entire graph. (The graph's bounding is a   
		// little enlarged.)   
		exportView.zoomToArea(box.getX() - 10, box.getY() - 10,   
			box.getWidth() + 20, box.getHeight() + 20);  
		
		// Set the detail threshold so that it is never switched to less detail mode.   
		exportView.setPaintDetailThreshold(0.0); 
		
		// Replace the currently active view containing the graph with the "export" view.   
		graph.setCurrentView(exportView);  
		
		try {
			ioh.write(graph, outFile);
		} catch (IOException e) {
			logger.info(e.getMessage());
		}
		
		// Remove the "export" view from the graph.   
		graph.removeView(graph.getCurrentView());  
		// Reset the current view to the originally active view.   
		graph.setCurrentView(originalView);    
	}  
	
	/**
	 * Finds the innermost Compartment, in which the Position determined by the parameters lies.
	 * @param x
	 * @param y
	 * @return The Id of the Compartment
	 */
	private String findCompartmentId(Double x, Double y) {
		//FIXME findCompartmentId
		return BioModelsEdConstants.compartmentDefaultName;
	}
	
	//  /**
	//   * Returns the innermost compartment glyph of the current layout at the specified position.
	//   * @param x
	//   * @param y
	//   * @return the id of the compartment
	//   */
	//  @Override
	//  public String findCompartmentId(Double x, Double y) {
	//    Layout layout = this.getCurrentLayout();
	//    if (layout == null) {
	//      logger.info("layout null"); 
	//      return SBMLEditorConstants.compartmentDefaultName;
	//    }
	//    
	//    ListOf<CompartmentGlyph> listOfCompartmentGlyphs = layout.getListOfCompartmentGlyphs();
	//    if (listOfCompartmentGlyphs == null) {
	//      logger.info("listOfCompartmentGlyphs null");
	//      return SBMLEditorConstants.compartmentDefaultName;
	//    }
	//    
	//    for(CompartmentGlyph c : listOfCompartmentGlyphs) {
	//      BoundingBox bb = c.getBoundingBox();
	//      if (!inside(x,y,bb)) {
	//        listOfCompartmentGlyphs.remove(c);
	//      }
	//    }
	//    return getInnermostCompartmentId(listOfCompartmentGlyphs);
	//  }
	//
	//
	//  /**
	//   * Returns the innermost compartment glyph of the given list.
	//   * All bounding boxes need to be set.
	//   * @param listOfCompartmentGlyphs
	//   * @return
	//   */
	//  private String getInnermostCompartmentId(ListOf<CompartmentGlyph> listOfCompartmentGlyphs) {
	//    if (listOfCompartmentGlyphs.size() > 0) {
	//      CompartmentGlyph innermost = listOfCompartmentGlyphs.get(0);
	//      for(CompartmentGlyph cg : listOfCompartmentGlyphs) {
	//        BoundingBox bb = cg.getBoundingBox();
	//        Point p = bb.getPosition();
	//        if (p == null) continue;
	//        BoundingBox innermostBb = innermost.getBoundingBox();
	//        if (inside(p.getX(),p.getY(),innermostBb)) {
	//          innermost = cg;
	//        }
	//      }
	//      return innermost.getCompartment();
	//    }
	//    else {
	//      return SBMLEditorConstants.compartmentDefaultName;
	//    }
	//  }
	//
	//
	//  /**
	//   * Checks if given Coordinates x,y are inside of the BoundingBox.
	//   * @param x
	//   * @param y
	//   * @param bb
	//   * @return true if position is inside the BoundingBox
	//   */
	//  private boolean inside(Double x, Double y, BoundingBox bb) {
	//    if (bb == null) return false;
	//    Dimensions dimensions = bb.getDimensions();
	//    if (dimensions == null) return false;
	//    Point point = bb.getPosition();
	//    if (point == null) return false;
	//    double bx = point.getX();
	//    double by = point.getY();
	//    double width = dimensions.getWidth();
	//    double height = dimensions.getHeight();
	//    return (bx <= x && x <= bx+width && by <= y && y <= by+height);
	//  }
}