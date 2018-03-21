package br.com.main;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import br.com.scheduler.Scheduler;

public class Main {		

	public static void main(String[] args) {
		
		String path = null;
		
		Scheduler scheduler = new Scheduler();
		
		JFileChooser file = new JFileChooser(); 
		file.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int i= file.showSaveDialog(null);
		if (i==1){
			
		} else {
			File archive = file.getSelectedFile();
			try {
				path = archive.getCanonicalPath().replace("/", "//");
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
		
		if(path != null){
			String result = scheduler.runSchedule(path);
			
			JTextArea textArea = new JTextArea(result);
			JScrollPane scrollPane = new JScrollPane(textArea);  
			textArea.setLineWrap(true);  
			textArea.setWrapStyleWord(true); 
			scrollPane.setPreferredSize( new Dimension( 500, 500 ) );
			JOptionPane.showMessageDialog(null, scrollPane, "Agenda Evento",  
			                                       JOptionPane.DEFAULT_OPTION);
		}
			
	}			
}
