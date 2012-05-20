package de.zbit.editor.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import javax.swing.SwingWorker;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLWriter;

public class SBMLWritingTask extends SwingWorker<Void, Void>{
	private OutputStream stream;
	private File file;
	private SBMLDocument doc;
	
	public SBMLWritingTask(File file, SBMLDocument doc) throws FileNotFoundException{
		this.file = file;
		this.doc = doc;
		this.stream = new FileOutputStream(this.file);
	}
	
	protected Void doInBackground() throws Exception {
		new SBMLWriter().write(doc, stream);
		return null;
	}

	@Override
	protected void done() {
		firePropertyChange("donesaveing", null, null);
	}
}
