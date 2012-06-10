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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.ext.layout.BoundingBox;
import org.sbml.jsbml.ext.layout.CompartmentGlyph;
import org.sbml.jsbml.ext.layout.ExtendedLayoutModel;
import org.sbml.jsbml.ext.layout.GraphicalObject;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.LayoutConstants;
import org.sbml.jsbml.ext.layout.Point;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;



/**
 * @author Stephanie Tscherneck
 * @version $Rev$
 */
public class CellDesignerAnnotationParser implements Runnable {

	private static final transient Logger logger = Logger.getLogger(CellDesignerAnnotationParser.class.getName());
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws XMLStreamException 
	 */
	public static void main(String[] args) throws XMLStreamException, IOException {
		logger.info("Reading file " + args[0]);
		CellDesignerAnnotationParser parser = new CellDesignerAnnotationParser(new File(args[0]));
		parser.run();
		if (args.length > 1) {
			SBMLWriter.write(parser.getSBMLDocument(), new File(args[1]), ' ', (short) 2);
		} else {
			SBMLWriter.write(parser.getSBMLDocument(), System.out, ' ', (short) 2);
		}
	}

	/**
	 * 
	 */
	private Map<String, Integer> idCounts = new HashMap<String, Integer>();
	
	/**
	 * Direct link to the layout.
	 */
	private Layout layout;
	
	/**
	 * The document for which CellDesigner information should be parsed.
	 */
	private SBMLDocument sbmlDocument;

	/**
	 * 
	 * @param inputFile
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	public CellDesignerAnnotationParser(File xmlFile) throws XMLStreamException, IOException {
		this.sbmlDocument = SBMLReader.read(xmlFile);
	}

	/**
	 * 
	 * @param doc
	 */
	public CellDesignerAnnotationParser(SBMLDocument doc) {
		this.sbmlDocument = doc;
	}

	/**
	 * @return the sbmlDocument
	 */
	public SBMLDocument getSBMLDocument() {
		return sbmlDocument;
	}

	/**
	 * 
	 * @param doc
	 */
	private void initializeLayout(SBMLDocument doc) {
		Model m = doc.getModel();
		if ((m != null) && (m.getExtension(LayoutConstants.namespaceURI) == null)) {
			ExtendedLayoutModel layoutExt = new ExtendedLayoutModel(m);
			m.addExtension(LayoutConstants.namespaceURI, layoutExt);
			layout = layoutExt.createLayout();
		}
	}

	/**
	 * 
	 * @param inputStream
	 * @throws XMLStreamException 
	 */
	private void readCDLayout(BufferedReader inputStream) throws XMLStreamException {
		initializeLayout(sbmlDocument);
		if (!sbmlDocument.isSetModel() || (sbmlDocument.getModel().getExtension(LayoutConstants.namespaceURI) == null)) {
			logger.info("SBMLDocument didn't contain any model.");
			return;
		}
		
		boolean newSpeciesAlias = false;
		boolean newCompartmentAlias = false;
		Double actualX = null;
		Double actualY = null;
		Double actualHeight = null;
		Double actualWidth = null;
		String actualId = null;
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader streamReader = inputFactory.createXMLStreamReader(inputStream);
		
		// xmlns:celldesigner="http://www.sbml.org/2001/ns/celldesigner"
		while (streamReader.hasNext()) {
			
			if (streamReader.getEventType() == XMLStreamConstants.START_ELEMENT) {
				logger.fine(streamReader.getLocalName());
				if (streamReader.getLocalName().equals("speciesAlias")) {
					newSpeciesAlias = true;
					for (int i = 0; i < streamReader.getAttributeCount(); i++) {
						logger.finer(streamReader.getAttributeLocalName(i) + ": " + streamReader.getAttributeValue(i));
						if (streamReader.getAttributeLocalName(i).equals("species")) {
							actualId = streamReader.getAttributeValue(i);
						}
					}
					logger.fine("species alias " + actualId);
				}
				else if (streamReader.getLocalName().equals("compartmentAlias")) {
					newCompartmentAlias = true;
					for (int i = 0; i < streamReader.getAttributeCount(); i++) {
						logger.finer(streamReader.getAttributeLocalName(i) + ": " + streamReader.getAttributeValue(i));
						if (streamReader.getAttributeLocalName(i).equals("compartment")) {
							actualId = streamReader.getAttributeValue(i);
						}
					}
					logger.fine("compartment alias " + actualId);
				}
				else if ((newSpeciesAlias || newCompartmentAlias) && streamReader.getLocalName().equals("bounds")) {
					for (int i = 0; i < streamReader.getAttributeCount(); i++) {
						logger.finer(streamReader.getAttributeLocalName(i) + ": " + streamReader.getAttributeValue(i));
						if (streamReader.getAttributeLocalName(i).equals("h")) {
							actualHeight = Double.valueOf(streamReader.getAttributeValue(i));
						}
						else if (streamReader.getAttributeLocalName(i).equals("w")) {
							actualWidth = Double.valueOf(streamReader.getAttributeValue(i));
						}
						else if (streamReader.getAttributeLocalName(i).equals("x")) {
							actualX = Double.valueOf(streamReader.getAttributeValue(i));
						}
						else if (streamReader.getAttributeLocalName(i).equals("y")) {
							actualY = Double.valueOf(streamReader.getAttributeValue(i));
						}
					}
					logger.fine("writing layout");
					writeLayout(actualId, actualX, actualY, actualWidth, actualHeight, newCompartmentAlias, newSpeciesAlias);
					
					newSpeciesAlias = false;
					newCompartmentAlias = false;
					actualX = null;
					actualY = null;
					actualHeight = null;
					actualWidth = null;
					actualId = null;
				}
			}	
			streamReader.next();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if ((sbmlDocument != null) && (sbmlDocument.isSetModel())) {
			String annotation =
					"<?xml version='1.0' encoding='UTF-8' standalone='no'?>" +
					"<annotation xmlns:celldesigner=\"http://www.sbml.org/2001/ns/celldesigner\">" +
					sbmlDocument.getModel().getAnnotation().getNonRDFannotation() +
					"</annotation>";
			try {
				readCDLayout(new BufferedReader(new StringReader(annotation)));
			} catch (XMLStreamException exc) {
				throw new RuntimeException(exc);
			}
		}
	}

	/**
	 * 
	 * @param id
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param compartment
	 * @param species
	 */
	private void writeLayout(String id, Double x, Double y, Double width, Double height, boolean compartment, boolean species) {
		int level = layout.getLevel(), version = layout.getVersion();
		
		if (idCounts.containsKey(id)) {
			idCounts.put(id, Integer.valueOf(idCounts.get(id).intValue() + 1));
		} else {
			idCounts.put(id, Integer.valueOf(1));
		}
		GraphicalObject go = null;
		String gID = id + '_' + idCounts.get(id);
		if (compartment) {
			CompartmentGlyph cg = new CompartmentGlyph(gID, level, version);
			cg.setCompartment(id);
			layout.addCompartmentGlyph(cg);
			go = cg;
		} else if (species) {
			SpeciesGlyph sg = new SpeciesGlyph(gID, level, version);
			sg.setSpecies(id);
			layout.addSpeciesGlyph(sg);
			go = sg;
		}
		if (go != null) {
			BoundingBox bb = go.createBoundingBox(width, height, 0);
			bb.setPosition(new Point(x, y, 0));
		}
	}
	
}
