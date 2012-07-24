package de.zbit.graph.parser;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.junit.Before;
import org.junit.Test;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.SBMLWriter;


public class RenderParserTest {

  String parsed = "";
  
  @Before
  public void setUp() throws Exception {
    /**
     * Erstellt ein einfaches Model mit Layout und 
     * Renderplugin und schreibt es in eine Datei
     */
//    int level = 3, version = 1;
//    
//    SBMLDocument doc = new SBMLDocument(level, version);
//    
//    Model model = doc.createModel("m1");
//    Compartment c = model.createCompartment("c1");
//    Species s1 = model.createSpecies("s1", "Species 1", c);
//    s1.setSBOTerm(SBO.getComplex());
//    Species s2 = model.createSpecies("s2", "Species 2", c);
//    
//    ExtendedLayoutModel extLayout = new ExtendedLayoutModel(model);
//    Layout layout = extLayout.createLayout("l1");
//    
//    RenderLayoutPlugin render = new RenderLayoutPlugin(extLayout.getLayout(0));
//    extLayout.getLayout(0).addExtension(RenderConstants.namespaceURI, render);
//    LocalRenderInformation info = new LocalRenderInformation("i1",null,level,version);
//    info.addColorDefinition(new ColorDefinition("color_255", new Color(255)));
//    render.addLocalRenderInformation(info );
//    SpeciesGlyph sGlyph = layout.createSpeciesGlyph("glyph_" + s1.getId(), s1.getId());
//    sGlyph.createBoundingBox(60, 60, 10);
//    model.addExtension(LayoutConstants.namespaceURI, extLayout);
//    
//    System.out.println(layout.getExtension(RenderConstants.namespaceURI));
    
//    SBMLWriter.write(doc, new File("test1.xml"), ' ', (short) 2);
  }


  @Test
  public void test() throws XMLStreamException, IOException {
    /**
     * Einlesen und nochmaliges schreiben zum Test der Parser funktion
     *  - test2.xml ist ein ganz einfaches Beispiel
     *  - die Dateien in l3v1 sind offizielle Beispiele von der Hompage der Spezifikation
     */
    SBMLDocument doc = SBMLReader.read(new File("test/l3v1/CaMK-Activation_l3v1.xml"));
    System.out.println(doc.getModel().getExtensionPackages());
    SBMLWriter.write(doc, new File("test/CaMK-Activation_l3v1-ReadAndWrite.xml"), ' ', (short) 2);
    /**
     * Der Vergleich der beiden Dateien stellt sich doch als etwas schwieriger heraus,
     * da beim Schreiben eine andere Reihenfolge der Attribute gewählt wurde
     */
  }
}
