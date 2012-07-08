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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.SBO;
import org.sbml.jsbml.ext.layout.ExtendedLayoutModel;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.LayoutConstants;
import org.sbml.jsbml.ext.layout.SpeciesReferenceGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceRole;

import de.zbit.editor.control.SBMLFactory;



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
	 * Reads out CellDesigner annotations and converts them into
	 * a layout according to JSBML Layout extension
	 * @param inputStream
	 * @throws XMLStreamException 
	 */
	private void readCDLayout(BufferedReader inputStream) throws XMLStreamException {
		initializeLayout(sbmlDocument);
		if (!sbmlDocument.isSetModel() || (sbmlDocument.getModel().getExtension(LayoutConstants.namespaceURI) == null)) {
			logger.info("SBMLDocument didn't contain any model.");
			return;
		}
	
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader streamReader = inputFactory.createXMLStreamReader(inputStream);
		
		// local variables to hold all parsed infomation before it can be used to create Layout Objects 
		boolean newSpeciesAlias = false;
		boolean newCompartmentAlias = false;
		String reactionRole = "";
		Map<String, String> attributeMap = new HashMap<String,String>();
		List<SpeciesReferenceGlyph> listOfSpeciesReferenceGlyphs = new LinkedList<SpeciesReferenceGlyph>();
    
		
		// parse all tokens
		while (streamReader.hasNext()) {
			
		  // get basis information
		  int eventType = streamReader.getEventType();
			
		  // on XML-opening tab
      if (eventType == XMLStreamConstants.START_ELEMENT) {
        // parses attributes
        parse(attributeMap, streamReader);
        
        // Local XML-Name
        String name = streamReader.getLocalName();
        
        if (name.equals(CellDesignerContstants.listOfReactants)) {
          reactionRole = CellDesignerContstants.reactant;
        }
        else if (name.equals(CellDesignerContstants.listOfProducts)) {
          reactionRole = CellDesignerContstants.product;
        }
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
          newSpeciesAlias = false;
          newCompartmentAlias = false;
          attributeMap = new HashMap<String, String>();
				}
				else if (name.equals(CellDesignerContstants.alias)) {
          attributeMap.put(CellDesignerContstants.alias, streamReader.getElementText());
          listOfSpeciesReferenceGlyphs.add(createSpeciesReferenceGlyph(attributeMap, reactionRole));
        }
				else if (name.equals(CellDesignerContstants.speciesClass)) {
          attributeMap.put(CellDesignerContstants.speciesClass, streamReader.getElementText());
          listOfSpeciesReferenceGlyphs.add(createSpeciesReferenceGlyph(attributeMap, reactionRole));
        }
				else if (name.equals(CellDesignerContstants.reactionType)) {
				  attributeMap.put(CellDesignerContstants.reactionType, streamReader.getElementText());
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
			    createReaction(attributeMap, listOfSpeciesReferenceGlyphs);
			    // after the reaction is created all references are reset
			    listOfSpeciesReferenceGlyphs = new LinkedList<SpeciesReferenceGlyph>();
			    attributeMap = new HashMap<String, String>();
			  }
			}
			streamReader.next();
		}
	}

  /**
   * creates a SpeciesReferenceGlyph
   * BoundingBox is set to (0,0,0,0)
   * @param attributeMap
   * @param type
   * @return SpeciesReferenceGlyph
   */
  private SpeciesReferenceGlyph createSpeciesReferenceGlyph(
    Map<String, String> attributeMap, String type) {
    int level = layout.getLevel();
    int version = layout.getVersion();
    String sR = CellDesignerContstants.speciesReference;
    String id = attributeMap.get(sR + CellDesignerContstants.species) + "_ref" + getRandomSuffix();
    String speciesReference = attributeMap.get(CellDesignerContstants.alias);
    SpeciesReferenceRole role = getRole(type);
    double x = 0;
    double y = 0;
    double width = 0;
    double height = 0;
    return SBMLFactory.createSpeciesReferenceGlyph(id, level, version, x, y, width, height, role, speciesReference);
  }

  /**
   * Transfers reaction terms used in CellDesigner annotations
   * into SpeciesReferenceRoles used in JSBML
   * @param parsed type
   * @return SpeciesRefereneRole
   */
  private SpeciesReferenceRole getRole(String type) {
    if (type.equals(CellDesignerContstants.baseProduct)) {
      return SpeciesReferenceRole.PRODUCT;
    }
    else if (type.equals(CellDesignerContstants.product)) {
      return SpeciesReferenceRole.SIDEPRODUCT;
    }
    else if (type.equals(CellDesignerContstants.baseReactant)) {
      return SpeciesReferenceRole.SUBSTRATE;
    }
    else if (type.equals(CellDesignerContstants.reactant)) {
      return SpeciesReferenceRole.SIDESUBSTRATE;
    }
    else {
      return SpeciesReferenceRole.UNDEFINED;
    }
  }
  
  /**
   * Transfers reaction terms used in CellDesigner annotations
   * into SBO terms used in JSBML
   * @param reactionType
   * @return according SBO term
   */
  private int getSBO(String reactionType) {
    if (reactionType.equals(CellDesignerContstants.stateTransition)) {
      return SBO.getStateTransition();
    }
    else {
      return SBO.getUnknownTransition();
    }
  }
  
  /**
   * creates a ReactionGlyph, includes all SpeciesRefernceGlyphs
   * and adds it to the layout
   * the BoundingBox is set to 0,0,0,0
   * and no Curve is set
   * @param attributeMap
   * @param listOfSpeciesReferenceGlyphs
   */
  private void createReaction(Map<String, String> attributeMap, List<SpeciesReferenceGlyph> listOfSpeciesReferenceGlyphs) {
    int level = layout.getLevel();
    int version = layout.getVersion();
    String r = CellDesignerContstants.reaction;
    
    String reaction = attributeMap.get(r + CellDesignerContstants.id);
    String id = reaction + getRandomSuffix();
    String reactionType = attributeMap.get(CellDesignerContstants.reactionType);
    double x = 0;
    double y = 0;
    double width = 0;
    double height = 0;
    
    layout.add(SBMLFactory.createReactionGlyph(id, level, version, listOfSpeciesReferenceGlyphs, 
      x, y, width, height, reaction, getSBO(reactionType)));
  }

  /**
   * generates random id suffices _randRANDOMINTEGER
   * to avoid collision with other ids
   * this is neccessary in some cases since ids cannot be counter
   * checked against the models ids
   * @return random id suffix
   */
  private String getRandomSuffix() {
    Random rand = new Random();
    return "_rand" + Integer.toString(Math.abs(rand.nextInt()));
  }

  /**
   * Reads out attributes form map, creats a compartment glyph
   * and adds it to the layout
   * @param attributeMap
   */
  private void createCompartment(Map<String, String> attributeMap) {
    String c = CellDesignerContstants.compartment;
    String b = CellDesignerContstants.bounds;
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
    
    layout.addCompartmentGlyph(SBMLFactory.createCompartmentGlyph(id, layout.getLevel(), layout.getVersion(), compartment,
      actualX, actualY, actualWidth, actualHeigth));
  }

  
  /**
   * Reads out attributes form map, creats a species glyph
   * and adds it to the layout
   * @param attributeMap
   */
  private void createSpeciesGlyph(Map<String, String> attributeMap) {
    String s = CellDesignerContstants.species;
    String b = CellDesignerContstants.bounds;
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

    layout.add(SBMLFactory.createSpeciesGlyph(id, layout.getLevel(), layout.getVersion(), 
      actualX, actualY, actualWidth, actualHeigth, speciesId));
  }

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		if ((sbmlDocument != null) && (sbmlDocument.isSetModel())) {
			String annotation =
					CellDesignerContstants.header +
					readCellDesignerAnnotations() +
					CellDesignerContstants.footer;
			System.err.print(annotation);
			try {
				readCDLayout(new BufferedReader(new StringReader(annotation)));
			} catch (XMLStreamException exc) {
				throw new RuntimeException(exc);
			}
		}
	}
	
	/**
	 * Reads all relevant lines from xmlFile for parsing
	 * CellDesigner annotations
	 * @return annotations
	 */
	public String readCellDesignerAnnotations() {
	  StringBuffer annotations = new StringBuffer();
	  
	  try {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(xmlFile));
      String line;
      
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
      e.printStackTrace();
    }
    return annotations.toString();
	}
	
	/**
	 * parses attributes and inserts them into map
	 * all attributes can be accessed by
	 * localName:attributeName
	 * @param map
	 * @param reader
	 */
	private void parse(Map<String,String> map, XMLStreamReader reader) {
	  for (int i = 0; i < reader.getAttributeCount(); i++) {
	    String attributeName = reader.getLocalName() + reader.getAttributeLocalName(i);
	    String attributeValue = reader.getAttributeValue(i);
	    map.put(attributeName, attributeValue);
	    logger.info(reader.getLocalName() + ":" + reader.getAttributeLocalName(i) + " = " +  attributeValue);
	  }
	}
	
	
}
