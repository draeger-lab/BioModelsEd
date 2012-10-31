/*
 * $Id:  BioModelsEdPanel.java 8:17:05 AM jrudolph $
 * $URL: BioModelsEdPanel.java $
 * ---------------------------------------------------------------------
 * This file is part of BioModelsEd.
 *
 * Copyright (C) 20012-2012 by the University of Tuebingen, Germany.
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBO;
import org.sbml.jsbml.SBase;
import org.sbml.jsbml.ext.layout.GraphicalObject;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.NamedSBaseGlyph;
import org.sbml.jsbml.ext.layout.ReactionGlyph;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;

import y.base.Node;
import y.view.NodeRealizer;
import de.zbit.editor.BioModelsEdConstants;
import de.zbit.editor.control.CommandController;
import de.zbit.editor.control.SBMLTools;
import de.zbit.graph.sbgn.ReactionNodeRealizer;
import de.zbit.io.OpenedFile;
import de.zbit.util.ResourceManager;

/**
 * @author Jan Rudolph
 * @version $Rev$
 */
public class BioModelsEdPanel extends GraphLayoutPanel implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -952263680143491399L;
	private static final ResourceBundle MESSAGES = ResourceManager.getBundle("de.zbit.locales.Messages");
	
	/**
   * States, which determine, which action will be taken on the next mouse click.
   * 
   */
  private enum State {
  	idle,
    catalysis,
    emptySet,
    inhibition,
    macromolecule,
    normal,
    reaction,
    simpleMolecule,
    unknownMolecule
  }
  /**
   * Modifiers
   */
  private enum Modifier {
  	none,
  	reversible,
  }
	
  private State state;
  private Modifier modifier;
  private CommandController controller;
  private OpenedFile<SBMLDocument> file;
  private Object additionalInformation;
  private static Logger logger = Logger.getLogger(BioModelsEdPanel.class.getName());
  
	/**
	 * @param layout
	 * @param editMode
	 * @param state
	 * @param modifier
	 * @param controller
	 * @param file
	 * @param listOfSelectedNodes
	 */
	public BioModelsEdPanel(Layout layout, SBMLEditMode editMode, CommandController controller,
		OpenedFile<SBMLDocument> file) {
		super(layout, editMode);
		this.state = State.idle;
		this.modifier = Modifier.none;
		this.controller = controller;
		this.file = file;
	}
	
	/**
	 * handles incoming action events
	 * @param evt redirected from tab manager
	 */
	public void receive(ActionEvent evt) {
		String action = evt.getActionCommand();
		logger.info(action);
		additionalInformation = null;
		if (action.equals(BioModelsEdConstants.addCatalysis)) {
			state = State.catalysis;
		}
		else if (action.equals(BioModelsEdConstants.addReaction)) {
			state = State.reaction;
		}
		else if (action.equals(BioModelsEdConstants.addEmptySet)) {
			state = State.emptySet;
		}
		else if (action.equals(BioModelsEdConstants.addInhibition)) {
			state = State.inhibition;
		}
		else if (action.equals(BioModelsEdConstants.addMacromolecule)) {
			state = State.macromolecule;
		}
		else if (action.equals(BioModelsEdConstants.addSimpleMolecule)) {
			state = State.simpleMolecule;
		}
		else if (action.equals(BioModelsEdConstants.addUnknownMolecule)) {
			state = State.unknownMolecule;
		}
	}
	public void receive(PropertyChangeEvent evt) {
		String action = evt.getPropertyName();
		//logger.info(action);
		if (action.equals(BioModelsEdConstants.EditModeMousePressedLeft)) {
			if (isSimple()) {
				String name = askForName();
				if (name == null) {
					return; //TODO How will this be handled? will state remain set?
				}
				controller.createSpecies(file, this.document, name, evt, getSBOTerm());
				state = State.idle;
			}
		}
    else if (action.equals(BioModelsEdConstants.EditModeMouseDraggedLeft)) {
      
    }
    else if (action.equals(BioModelsEdConstants.nodeClicked)) {
    	Node node = (Node) evt.getNewValue();
    	NodeRealizer shape = getGraph2DView().getGraph2D().getRealizer(node);
			
    	// reaction or modification created?
    	if (state.equals(State.catalysis) || state.equals(State.reaction) || state.equals(State.inhibition)) {
    		SpeciesGlyph source = (SpeciesGlyph) additionalInformation;
    		if (source == null) {
    			if (!(shape instanceof ReactionNodeRealizer)) {
    				GraphicalObject g = (SpeciesGlyph) getGlyphFromNode(node);
    				additionalInformation = g;
    			}
    		}
    		else {
    			GraphicalObject target = getGlyphFromNode(node);
    			if (target == null) return;
    			//TODO enable adding sideproducts via reaction mode
    			if (!(shape instanceof ReactionNodeRealizer) && state.equals(State.reaction)) {
    				controller.createReaction(file, this.document, source, (SpeciesGlyph) target, 
    					modifier.equals(Modifier.reversible));
    				state = State.idle;
    			}
    			else if ((shape instanceof ReactionNodeRealizer) && state.equals(State.catalysis) || state.equals(State.inhibition)) {
    				controller.createModifier(file, this.document, source, (ReactionGlyph) target, getSBOTerm());
    				state = State.idle;
    			}
    		}
    	}
    }
    else if (action.equals(BioModelsEdConstants.EditModeNodeReleasedLeft)) {
    	
    }
    else if (action.equals(BioModelsEdConstants.EditModeNodePressedRight)) {
    	/**
    	 * Creates and shows PopupMenu on right mouse click on a node.
    	 * @param evt
    	 */
//    	TODO private void nodePressedRight(PropertyChangeEvent evt) {    
//    		JPopupMenu popup;
//    		Node node = (Node) evt.getNewValue();
//    		NamedSBaseGlyph glyph = getGlyphFromNode(node);
//    		if (glyph instanceof SpeciesGlyph) {
//    			popup = BioModelsEdGUIFactory.createSpeciesGlyphPopupMenu(this);
//    		}
//    		else {
//    			popup = BioModelsEdGUIFactory.createReactionGlyphPopupMenu(this);
//    		}
//    		SBMLEditMode editmode =  (SBMLEditMode) evt.getSource();
//    		MouseEvent e = editmode.getLastPressEvent();
//    		popup.show(e.getComponent(), e.getX(), e.getY());
//    	}
    }
    else if (action.equals(BioModelsEdConstants.EditModeMousePressedRight)) {
    	/**
    	 * Creates and shows PopupMenu on right mouse click on empty space.
    	 * @param evt
    	 */
//    	TODO private void mousePressedRight(PropertyChangeEvent evt) {
//    		JPopupMenu popup = BioModelsEdGUIFactory.createPastePopupMenu(this, this.copyEnabled);
//    		SBMLEditMode editmode =  (SBMLEditMode) evt.getSource();
//    		MouseEvent e = editmode.getLastPressEvent();
//    		popup.show(e.getComponent(), e.getX(), e.getY());    
//    	}
    }
    else if (action.equals(BioModelsEdConstants.EditModeSelectionChanged)) {
    	
    }
    else if (action.equals(BioModelsEdConstants.EditModeUpdateNodes)) {
    	controller.updateNodes(evt);
    }
	}

	/**
	 * search in model for matching glyph
	 * @param node
	 * @return
	 */
	private GraphicalObject getGlyphFromNode(Node node) {
		ListOf<SpeciesGlyph> list = this.document.getListOfSpeciesGlyphs();
		for (SpeciesGlyph glyph : list) {
      Node glyphNode = (Node) glyph.getUserObject(BioModelsEdConstants.GLYPH_NODE_KEY);
      if (glyphNode == node) {
        return glyph;
      } 
    }
		ListOf<ReactionGlyph> reactions = this.document.getListOfReactionGlyphs();
		for (ReactionGlyph glyph : reactions) {
      Node glyphNode = (Node) glyph.getUserObject(BioModelsEdConstants.GLYPH_NODE_KEY);
      if (glyphNode == node) {
        return glyph;
      } 
    }
		return null;
	}

	/**
	 * @return SBOTerm according to current state
	 */
	private int getSBOTerm() {
	/*
	 * all reaction like states are converted into the resulting
	 * SBO term of the added node e.g. catalysis -> catalyst
	 * TODO rename states?
	 */
		if (state.equals(State.catalysis)) {
			return SBO.getCatalyst();
		}
		else if (state.equals(State.emptySet)) {
			return SBO.getEmptySet();
		}
		else if (state.equals(State.inhibition)) {
			return SBO.getInhibitor();
		}
		else if (state.equals(State.macromolecule)) {
			return SBO.getMacromolecule();
		}
		else if (state.equals(State.reaction)) {
			return SBO.getStateTransition();
		}
		else if (state.equals(State.simpleMolecule)) {
			return SBO.getSimpleMolecule();
		}
		else /*if (state.equals(State.unknownMolecule))*/ {
			return SBO.getUnknownMolecule();
		}
	}

	/**
	 * panel state is simple "one click"
	 * @return
	 */
	private boolean isSimple() {
		return state.equals(State.emptySet) ||
				state.equals(State.macromolecule) ||
				state.equals(State.simpleMolecule) ||
				state.equals(State.unknownMolecule);
	}

	/**
	 * input dialog requesting text input
	 * @return choosen name
	 */
	private String askForName() {
		String input = (String) JOptionPane.showInputDialog(this, 
			MESSAGES.getString("NAME_SPECIES"), 
			MESSAGES.getString("NAME_SPECIES_TITLE"), 
			JOptionPane.QUESTION_MESSAGE, 
			null, 
			null, 
			SBMLTools.getNextGenericId(file, getGenericPrefix()));
		return input;
	}

	/**
	 * @return prefix according to current state
	 */
	private String getGenericPrefix() {
		if (state.equals(State.catalysis)) {
			return "cat";
		}
		else if (state.equals(State.emptySet)) {
			return "sink";
		}
		else if (state.equals(State.inhibition)) {
			return "inh";
		}
		else if (state.equals(State.macromolecule)) {
			return "macro";
		}
		else if (state.equals(State.reaction)) {
			return "r";
		}
		else if (state.equals(State.simpleMolecule)) {
			return "simple";
		}
		else /*if (state.equals(State.unknownMolecule))*/ {
			return "unknown";
		}
	}
	
	/**
	 * Pops up a dialogue and renames the SpeciesGlyph and Species corresponding to the selected node.
	 */
//	TODO public void nodeRename() {
//		logger.info("Renaming Node");
//		Node nodeToRename = this.nodeList.get(0);
//		SpeciesGlyph selectedGlyph = (SpeciesGlyph) getGlyphFromNode(nodeToRename);
//		TextGlyph textGlyph = (TextGlyph) selectedGlyph.getUserObject(BioModelsEdConstants.GRAPHOBJECT_TEXTGLYPH_KEY);
//		Species species = selectedGlyph.getModel().getSpecies(textGlyph.getNamedSBase());
//		String oldName = species != null ? species.getName() : "";
//		String newName = JOptionPane.showInputDialog(Resources.getString("NEW_NODE_NAME"),
//			oldName);
//		if (newName != null) {
//			// set name
//			species.setName(newName);
//			
//			/*
//			 * TODO
//			 * Set File as modified
//			 * get Panel from view
//			 */
//			GraphLayoutPanel panel = null;
//			Graph2D graph2d = panel.getGraph2DView().getGraph2D();
//			graph2d.setLabelText(nodeToRename, newName);
//			graph2d.updateViews();
//		}
//	}
}
