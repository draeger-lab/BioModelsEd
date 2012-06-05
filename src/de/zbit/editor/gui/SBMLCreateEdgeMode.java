/*
 * $Id:  SBMLCreateEdgeMode.java 21:02:39 Eugen Netz$
 * $URL: SBMLCreateEdgeMode.java$
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

import y.base.Edge;
import y.base.Node;
import y.view.CreateEdgeMode;
import y.view.EdgeRealizer;
import y.view.Graph2D;


/**
 * @author Eugen Netz
 * @since 1.0
 * @version $Rev$
 */
public class SBMLCreateEdgeMode extends CreateEdgeMode {
  
  public void test(){
   
  }
  
  @Override
  public Edge createEdge(Graph2D graph, Node start, Node target, EdgeRealizer realizer) {
    firePropertyChange("EdgeCreated", start, target);
    return graph.createEdge(start, target, realizer);
  }
}
