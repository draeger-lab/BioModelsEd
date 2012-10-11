package de.zbit.graph;

import java.awt.Window;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import de.zbit.AppConf;
import de.zbit.Launcher;
import de.zbit.util.prefs.KeyProvider;

public class SysBioTest extends Launcher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new SysBioTest().run();
	}

	@Override
	public void commandLineMode(AppConf appConf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Class<? extends KeyProvider>> getCmdLineOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Class<? extends KeyProvider>> getInteractiveOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getURLlicenseFile() {
		try {
			return new URL("http://www.gnu.org/licenses/gpl-3.0-standalone.html");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public URL getURLOnlineUpdate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVersionNumber() {
		return "0.1";
	}

	@Override
	public short getYearOfProgramRelease() {
		return 2012;
	}

	@Override
	public short getYearWhenProjectWasStarted() {
		return 2012;
	}

	@Override
	public Window initGUI(AppConf appConf) {
		BaseFrameTest gui = new BaseFrameTest();
		gui.setVisible(true);
		return gui;
	}

}
