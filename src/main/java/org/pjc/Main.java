package org.pjc;

import org.pjc.windows.MainWindow;

import java.awt.EventQueue;

public class Main {

	public static void main(String[] args) {
		//System.setProperty("sun.java2d.opengl", "True");

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
