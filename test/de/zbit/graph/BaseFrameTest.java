package de.zbit.graph;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.net.URL;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JToolBar;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.ext.layout.Layout;

import de.zbit.editor.control.CommandController;
import de.zbit.editor.control.SBMLView;
import de.zbit.editor.gui.GraphLayoutPanel;
import de.zbit.editor.gui.TabManager;
import de.zbit.gui.BaseFrame;
import de.zbit.io.OpenedFile;

public class BaseFrameTest extends BaseFrame implements SBMLView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5905194830302864398L;
	private TabManager tabManager;
	private Layout layout;
	
	@Override
	public boolean closeFile() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected JToolBar createJToolBar() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Component createMainComponent() {
		tabManager = new TabManager(this);
		layout = new Layout();

		GraphLayoutPanel panel = tabManager.createPanelFromLayout(layout, true);
	    tabManager.addTab("panel1", panel);
	    
	    GraphLayoutPanel panel2 = tabManager.createPanelFromLayout(layout, true);
	    tabManager.addTab("panel2", panel2);

		return tabManager;
	}

	@Override
	public URL getURLAboutMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getURLLicense() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getURLOnlineHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected File[] openFile(File... arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File saveFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Layout getCurrentLayout() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String nameDialogue(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TabManager getTabManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File askUserOpenDialog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File askUserSaveDialog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int askUserCreateLayoutInformation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public JFrame getFrame() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String askUserFileNew() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void showWarning(String warning) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showError(String error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean closeTab(Layout layout) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refreshTitle(Layout layout) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void helpAbout() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void layoutClone() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void layoutDelete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean layoutClose(Layout layout) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean layoutNew() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean layoutRename() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean layoutAuto() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void openLayoutInTab() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openLayoutInNewTab() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CommandController getController() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setControlsOn(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public File saveFileAs() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see de.zbit.editor.control.SBMLView#updateComboBox(java.util.List)
	 */
	@Override
	public void updateComboBox(List<Layout> list) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see de.zbit.editor.control.SBMLView#addTab(de.zbit.io.OpenedFile, java.lang.String, boolean)
	 */
	@Override
	public boolean addTab(OpenedFile<SBMLDocument> file, String layoutId,
		boolean autoLayout) {
		// TODO Auto-generated method stub
		return false;
	}

}
