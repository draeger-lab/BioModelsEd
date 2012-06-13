/*
 * $Id:  ControllerViewSynchronizer.java 14:34:32 Eugen Netz$
 * $URL: ControllerViewSynchronizer.java$
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

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.tree.TreeNode;

import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.layout.BoundingBox;
import org.sbml.jsbml.ext.layout.ExtendedLayoutModel;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.LayoutConstants;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.util.TreeNodeChangeListener;
import org.sbml.jsbml.util.TreeNodeRemovedEvent;

import de.zbit.editor.SBMLEditorConstants;
import de.zbit.graph.gui.TranslatorSBMLgraphPanel;


/**
 * @author Eugen Netz
 * @since 1.0
 * @version $Rev$
 */
public class ControllerViewSynchronizer implements TreeNodeChangeListener {

  private TranslatorSBMLgraphPanel panel;
  private Layout layout;
  private Logger logger = Logger.getLogger(ControllerViewSynchronizer.class.getName());
  
  public ControllerViewSynchronizer(TranslatorSBMLgraphPanel panel, Layout layout) {
    this.panel = panel;
    this.layout = layout;
  }
  
  /* (non-Javadoc)
   * @see org.sbml.jsbml.util.TreeNodeChangeListener#nodeAdded(javax.swing.tree.TreeNode)
   */
  @Override
  public void nodeAdded(TreeNode node) {
    // React only if *glyphs are added
    if (node instanceof Species) {
      Species s = (Species) node;
      // TODO get correct species glyph
      Map<String, List<String>> layoutGlyphMap = 
          (Map<String, List<String>>) s.getUserObject(SBMLEditorConstants.GLYPH_LINK_KEY);
      // TODO check != null, has 0
      String sgId = layoutGlyphMap.get(this.layout.getId()).get(0);
      SpeciesGlyph sg = (SpeciesGlyph) this.layout.getSpeciesGlyph(sgId);
      
      ExtendedLayoutModel extendedLayoutModel = (ExtendedLayoutModel) s.getModel().getExtension(LayoutConstants.namespaceURI);
      
//      BoundingBox boundingBox = extendedLayoutModel.getListOfLayouts()
//          .get(this.layout.getId()).getSpeciesGlyph(sg.getId())
//          .getBoundingBox();
      BoundingBox boundingBox = sg.getBoundingBox();
      
      panel.getConverter().createNode(s.getId(),
          s.getName(),
          s.getSBOTerm(),
          boundingBox.getPosition().getX(),
          boundingBox.getPosition().getY(),
          boundingBox.getDimensions().getWidth(),
          boundingBox.getDimensions().getHeight());
      panel.getGraph2DView().updateView();
    }
  }

  /* (non-Javadoc)
   * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    //TreeNodeChangeEvent event = (TreeNodeChangeEvent) evt;
    //TODO Paint anew with new Doc?
    //panel.getConverter().createGraph(panel.getDocument());
    
  }

  @Override
  public void nodeRemoved(TreeNodeRemovedEvent evt) {
	  // TODO Auto-generated method stub

  }


}
