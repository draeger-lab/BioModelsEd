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

import java.io.File;

/**
 * @author Alexander Diamantikos
 * @author Jakob Matthes
 * @author Eugen Netz
 * @author Jan Rudolph
 * @version $Rev$
 */
public class OpenedDocument<T> {
	
  /**
   * SBML Document of the openedDocument
   */
  protected T document;
  /**
   * associated filepath of the openedDocument, can be unset
   */
  private String associatedFilepath;
  private String associatedFilename;
  private boolean fileModified = false;


  /**
   * Constructor.
   * @param document
   */
  public OpenedDocument(T document) {
    this.document = document;
  }

  /**
   * Constructor.
   * @param document
   * @param associatedFilepath
   */
  public OpenedDocument(T document, String associatedFilepath) {
    this.document = document;
    this.associatedFilepath = associatedFilepath;
    this.associatedFilename = new File(associatedFilepath).getName();
  }


  /**
   * @return the sbmlDocument
   */
  public T getDocument() {
    return document;
  }


  /**
   * @return the associatedFilepath
   */
  public String getAssociatedFilepath() {
    return associatedFilepath;
  }

  /**
   * 
   * @return the associatedFilename
   */
  public String getAssociatedFilename() {
    return associatedFilename;
  }
  
  /**
   * 
   * @param name
   */
  public void setAssociatedFilename(String name) {
    this.associatedFilename = name;
  }

  /**
   * 
   * @return the filename
   */
  public String getFilename(){
	  return new File(getAssociatedFilepath()).getName();
  }
  
  /**
   * @param associatedFilepath
   *        the associatedFilepath to set
   */
  public void setAssociatedFilepath(String associatedFilepath) {
    this.associatedFilepath = associatedFilepath;
    this.associatedFilename = new File(associatedFilepath).getName();
  }


  /**
   * Check if filepath is set
   *
   * @return true, if it is set
   */
  public boolean hasAssociatedFilepath() {
    return (getAssociatedFilepath() != null);
  }


  /**
   * @return true, if the file is modified
   */
  public boolean isFileModified() {
    return fileModified;
  }


  /**
   * @param fileModified the fileModified to set
   */
  public void setFileModified(boolean fileModified) {
    this.fileModified = fileModified;
  }
}
