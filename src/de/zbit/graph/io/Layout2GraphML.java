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
package de.zbit.graph.io;

import org.sbml.jsbml.ext.layout.Layout;

/**
 * @author Andreas Dr&aum;ger
 * @since 1.0
 * @version $Rev$
 */
public class Layout2GraphML extends SB_2GraphML<Layout> {

	/**
	 * 
	 */
	public Layout2GraphML() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see de.zbit.graph.io.SB_2GraphML#createNodesAndEdges(java.lang.Object)
	 */
	@Override
	protected void createNodesAndEdges(Layout layout) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see de.zbit.graph.io.SB_2GraphML#isAnyLayoutInformationAvailable()
	 */
	@Override
	protected boolean isAnyLayoutInformationAvailable() {
		// TODO Auto-generated method stub
		return false;
	}

}
