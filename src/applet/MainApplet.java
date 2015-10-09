package applet;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JApplet;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class MainApplet extends JApplet{
	
	/** コンテントペイン */
	private Container contentPane;
	
	public MainApplet(){
		contentPane = getContentPane();
		rootPane = getRootPane();
		
		MyPanel samplePanel = new MyPanel();
		
//		contentPane.setLayout(new BorderLayout());
		contentPane.add(samplePanel, BorderLayout.CENTER);
		
	}

}
