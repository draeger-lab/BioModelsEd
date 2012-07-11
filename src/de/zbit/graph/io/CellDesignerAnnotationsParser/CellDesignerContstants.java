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

package de.zbit.graph.io.CellDesignerAnnotationsParser;


public class CellDesignerContstants {
  
  public static final String speciesAlias = "speciesAlias";
  public static final String species = "species";
  public static final String compartmentAlias = "compartmentAlias";
  public static final String compartment = "compartment";
  public static final String heigth = "h";
  public static final String width = "w";
  public static final String x = "x";
  public static final String y = "y";
  public static final String bounds = "bounds";
  public static final String id = "id";
  public static final int version = 1;
  public static final int level = 3;
  public static final String reaction = "reaction";
  public static final String baseReactant = "baseReactant";
  public static final String baseProduct = "baseProduct";
  public static final String alias = "alias";
  public static final String reactantLink = "reactantLink";
  public static final String productLink = "productLink";
  public static final String modification = "modification";
  public static final String reversible = "reversible";
  public static final String speciesReference = "speciesReference";
  public static final String reactant = "reactant";
  public static final String listOfReactants = "listOfReactants";
  public static final String listOfProducts = "listOfProducts";
  public static final String reactionType = "reactionType";
  public static final String stateTransition = "STATE_TRANSITION";
  public static final String product = "product";
  public static final String metaid = "metaid";
  public static final String classProtein = "PROTEIN";
  public static final String speciesClass = "class";
  public static final String footer = "</annotation>\n";
  public static final String cellDesignerNamespace = "\"http://www.sbml.org/2001/ns/celldesigner\"";
  public static final String header = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>\n" +
          "<annotation xmlns:celldesigner=" + cellDesignerNamespace + ">\n";
  public static final String moleculeClass = "class";
}
