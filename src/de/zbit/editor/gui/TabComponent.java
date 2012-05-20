package de.zbit.editor.gui;


import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public class TabComponent extends JPanel {
	
	private static final long serialVersionUID = -8179219783406520882L;
	private TabManager tabManager;
	private JLabel label;

	public TabComponent(TabManager tabManager){
		
		this.tabManager = tabManager;
		setOpaque(false);
		
		String title = tabManager.getTitleAt(tabManager.getSelectedIndex());
		this.label = new JLabel(title);
            
        add(this.label);
        ImageIcon iconCloseButton = new ImageIcon("CloseButton.gif");
        JButton button = new JButton(iconCloseButton);
        button.addActionListener(EventHandler.create(ActionListener.class, this, "close"));
        add(button);
        
        MouseListener tabListener = new TabListener(GUIFactory.createTabPopupMenu(this));
        addMouseListener(tabListener);
	}
	
	public void setTitle(String title){
		this.label.setText(title);
	}
	
	public void close(){
		tabManager.closeTab(tabManager.indexOfTabComponent(this));
	}
	
	public void closeAll(){
		tabManager.closeAllTabs();
	}
	
	public void select(){
		tabManager.setSelectedIndex(tabManager.indexOfTabComponent(this));
	}
	
	class TabListener extends MouseAdapter {
        JPopupMenu popup;
 
        TabListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }
        
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }
        
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }
 
        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(),
                           e.getX(), e.getY());
            }
            else{
            	((TabComponent) e.getComponent()).select();
            }
        }
    }
}