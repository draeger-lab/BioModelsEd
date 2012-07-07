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

import java.io.BufferedReader;

import de.zbit.editor.control.SBMLFactory;
import de.zbit.graph.io.CellDesignerAnnotationsParser.CellDesignerContstants;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
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
import org.sbml.jsbml.ext.layout.Dimensions;
import org.sbml.jsbml.ext.layout.ExtendedLayoutModel;
import org.sbml.jsbml.ext.layout.GraphicalObject;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.LayoutConstants;
import org.sbml.jsbml.ext.layout.Point;
import org.sbml.jsbml.ext.layout.ReactionGlyph;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceRole;



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
		logger.setLevel(Level.FINEST);
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
	private int ReactionCounter = 0;
	
	/**
	 * Direct link to the layout.
	 */
	private Layout layout;
	
	/**
	 * The document for which CellDesigner information should be parsed.
	 */
	private SBMLDocument sbmlDocument;
	private File xmlFile;

	/**
	 * 
	 * @param inputFile
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	public CellDesignerAnnotationParser(File xmlFile) throws XMLStreamException, IOException {
		this.sbmlDocument = SBMLReader.read(xmlFile);
		this.xmlFile = xmlFile;
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
		String reactionRole = "";
		
		Map<String, String> attributeMap = new HashMap<String,String>();
		List<SpeciesReferenceGlyph> listOfReactants = new ArrayList<SpeciesReferenceGlyph>();
		List<SpeciesReferenceGlyph> listOfProducts = new ArrayList<SpeciesReferenceGlyph>();
    
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader streamReader = inputFactory.createXMLStreamReader(inputStream);
		
		// xmlns:celldesigner="http://www.sbml.org/2001/ns/celldesigner"
		// parse all tokens
		while (streamReader.hasNext()) {
			
		  // get basis information
		  int eventType = streamReader.getEventType();
			
      if (eventType == XMLStreamConstants.START_ELEMENT) {
        String name = streamReader.getLocalName();
        parse(attributeMap, streamReader);
        if (name.equals(CellDesignerContstants.listOfReactants)) {
          reactionRole = "reactant";
        }
        else if (name.equals(CellDesignerContstants.listOfProducts)) {
          reactionRole = "product";
        }
        // parsing species alias
        else if (name.equals(CellDesignerContstants.speciesAlias)) {
          newSpeciesAlias = true;
				}
				else if (name.equals(CellDesignerContstants.compartmentAlias)) {
				  newCompartmentAlias = true;
				}
				else if (name.equals(CellDesignerContstants.bounds)) {
          if (newSpeciesAlias) {
            createSpeciesGlyph(attributeMap);
          }
          else if (newCompartmentAlias) {
            createCompartment(attributeMap);
          }
          attributeMap = new HashMap<String, String>();
          newSpeciesAlias = false;
          newCompartmentAlias = false;
				}
				//FIXME Check if reaction is STATE_TRANSITION
				else if (name.equals(CellDesignerContstants.reaction)) {
          //newReaction = true;
        }
        else if (name.equals(CellDesignerContstants.baseReactant)) {
          listOfReactants.add(createSpeciesReferenceGlyph(attributeMap, reactionRole));
        }
        else if (name.equals(CellDesignerContstants.speciesReference)) {
          listOfReactants.add(createSpeciesReferenceGlyph(attributeMap, reactionRole));
        }
			}	
      else if (eventType == XMLStreamConstants.END_ELEMENT) {
        String name = streamReader.getLocalName();
        if (name.equals(CellDesignerContstants.listOfReactants)) {
          reactionRole = "";
        }
        else if (name.equals(CellDesignerContstants.listOfProducts)) {
          reactionRole = "";
        }
        else if (name.equals(CellDesignerContstants.reaction)) {
			    createReaction(attributeMap);
			    attributeMap = new HashMap<String, String>();
			  }
			}
     // else if ()
			streamReader.next();
		}
	}

  private SpeciesReferenceGlyph createSpeciesReferenceGlyph(
    Map<String, String> attributeMap, String basereactant) {
    logger.info("adding " + basereactant);
    return null;
  }

  private void createReaction(Map<String, String> attributeMap) {
    int level = layout.getLevel();
    int version = layout.getVersion();
    String r = CellDesignerContstants.reactionPrefix;
    String br = CellDesignerContstants.baseReactantPrefix;
    String bp = CellDesignerContstants.baseProductPrefix;
    
    String id = attributeMap.get(r + CellDesignerContstants.id + getRandomSuffix());
    
    ReactionGlyph rGlyph = new ReactionGlyph(id, level, version);

    SpeciesReferenceGlyph sRGlyph1 = new SpeciesReferenceGlyph(id, level, version);
    SpeciesReferenceGlyph sRGlyph2 = new SpeciesReferenceGlyph(id, level, version);
  }

  private String getRandomSuffix() {
    Random rand = new Random();
    return NumberFormat.getInstance(Locale.ENGLISH).format(rand.nextDouble());
  }

  private void createCompartment(Map<String, String> attributeMap) {
    String c = CellDesignerContstants.compartmentPrefix;
    String b = CellDesignerContstants.boundsPrefix;
    String id = attributeMap.get(c + CellDesignerContstants.id);
    String compartment = attributeMap.get(c + CellDesignerContstants.compartment);
    String x = attributeMap.get(b + CellDesignerContstants.x);
    String y = attributeMap.get(b + CellDesignerContstants.y);
    String width = attributeMap.get(b + CellDesignerContstants.width);
    String height = attributeMap.get(b + CellDesignerContstants.heigth);
    
    Double actualX = x == null ? 0 : Double.parseDouble(x);
    Double actualY = y == null ? 0 : Double.parseDouble(y);
    Double actualWidth = width == null ? 0 : Double.parseDouble(width);
    Double actualHeigth = height == null ? 0 : Double.parseDouble(height);
    
    addCompartmentGlyphToLayout(id, compartment, actualX, actualY, actualWidth, actualHeigth);
  }

  private void addCompartmentGlyphToLayout(String id, String compartment, Double actualX,
    Double actualY, Double actualWidth, Double actualHeigth) {
    CompartmentGlyph cGlyph = SBMLFactory.createCompartmentGlyph(id, layout.getLevel(), layout.getVersion(), compartment);
    cGlyph.createBoundingBox(actualWidth, actualHeigth, 0, actualX, actualY, 0);
    layout.addCompartmentGlyph(cGlyph);
  }
  
  private void createSpeciesGlyph(Map<String, String> attributeMap) {
    String s = CellDesignerContstants.speciesPrefix;
    String b = CellDesignerContstants.boundsPrefix;
    String id = attributeMap.get(s + CellDesignerContstants.id);
    String speciesId = attributeMap.get(s + CellDesignerContstants.species);
    String x = attributeMap.get(b + CellDesignerContstants.x);
    String y = attributeMap.get(b + CellDesignerContstants.y);
    String width = attributeMap.get(b + CellDesignerContstants.width);
    String height = attributeMap.get(b + CellDesignerContstants.heigth);
    
    Double actualX = x == null ? 0 : Double.parseDouble(x);
    Double actualY = y == null ? 0 : Double.parseDouble(y);
    Double actualWidth = width == null ? 0 : Double.parseDouble(width);
    Double actualHeigth = height == null ? 0 : Double.parseDouble(height);

    addSpeciesGlyphToLayout(id, speciesId, actualX, actualY, actualWidth, actualHeigth);
  }

  private void addSpeciesGlyphToLayout(String id, String speciesId, Double actualX,
    Double actualY, Double actualWidth, Double actualHeigth) {
    SpeciesGlyph sGlyph = SBMLFactory.createSpeciesGlyph(id, layout.getLevel(), layout.getVersion(), speciesId);
    sGlyph.createBoundingBox(actualWidth, actualHeigth, 0, actualX, actualY, 0);
    layout.add(sGlyph);
  }
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if ((sbmlDocument != null) && (sbmlDocument.isSetModel())) {
			String annotation =
					"<?xml version='1.0' encoding='UTF-8' standalone='no'?>\n" +
					"<annotation xmlns:celldesigner=\"http://www.sbml.org/2001/ns/celldesigner\">\n" +
					//sbmlDocument.getModel().getAnnotation().getNonRDFannotation() +
					readCellDesignerAnnotations() +
					"</annotation>\n";
			System.err.print(annotation);
			try {
				readCDLayout(new BufferedReader(new StringReader(annotation)));
			} catch (XMLStreamException exc) {
				throw new RuntimeException(exc);
			}
		}
	}
	
	public String readCellDesignerAnnotations() {
	  StringBuffer annotations = new StringBuffer();
	  
	  try {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(xmlFile));
      String line;
      
      //TODO parse "<reaction " Tag, because it contains the ReactionId (or create Ids for Reactions)
      while((line = bufferedReader.readLine()) != null) {
        while((line != null) && (!line.startsWith("<annotation>"))) {
          line = bufferedReader.readLine();
        }
        line = bufferedReader.readLine();
        
        while((line != null) && (!line.startsWith("</annotation>"))) {
          annotations.append(line + "\n");
          line = bufferedReader.readLine();
        }
        while((line != null) && (!line.startsWith("<listOfReactions>"))) {
          line = bufferedReader.readLine();
        }
        line = bufferedReader.readLine();
        
        while((line != null) && (!line.startsWith("</listOfReactions>"))) {
          annotations.append(line + "\n");
          line = bufferedReader.readLine();
        }
      }      
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return annotations.toString();
	}
	
	/**
	 * Parse context object
	 */
	private void parse(Map<String,String> map, XMLStreamReader reader) {
	  for (int i = 0; i < reader.getAttributeCount(); i++) {
	    String attributeName = reader.getLocalName() + ":" + reader.getAttributeLocalName(i);
	    String attributeValue = reader.getAttributeValue(i);
	    map.put(attributeName, attributeValue);
	    logger.info(attributeName + " : " +  attributeValue);
	  }
	}
	
}
