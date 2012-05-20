package de.zbit.editor.gui;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

public class GUITools {
	
	/**
	   * Detect if system is a Mac OS
	   * @return
	   */
	public static boolean onMac() {
	    return (System.getProperty("os.name").toLowerCase().contains("mac")
	        || System.getProperty("mrj.version") != null);
	  }
	
	public static int getControlKey(){
		if (onMac()){
			return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		}
		else {
			return KeyEvent.CTRL_MASK;
		}
	}
}