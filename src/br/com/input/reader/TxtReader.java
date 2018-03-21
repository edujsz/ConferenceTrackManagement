package br.com.input.reader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TxtReader {		
		
	public List<String> readTxtFile(String path) throws IOException{		
		String file = path;				
				
		List<String> events = Files.readAllLines(Paths.get(file),StandardCharsets.UTF_8);
		
		for(String event:events){
			event =  event != null ? event.trim():"";
		}
		
		return events;
		
	}

}
