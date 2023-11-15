package uwi.thirdparty.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import uwi.thirdparty.entity.Student;

@Service
public class FileManager {
	
	@Value("${error}")
	private String ERROR_FILE_DIRECTORY;
	
	private BufferedWriter writer;
	
	private FileWriter fileWriter;
	
	
	public BufferedReader getBufferedReader(Path fileLocation) throws FileNotFoundException {
		File file = new File(fileLocation.toString());
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		return br;
	}
	
	public Resource getResource (Path filePath) throws MalformedURLException  {
	
		Resource resource = new UrlResource(filePath.toUri());
		return resource;
	}
	
	private boolean headerExist(String filepath) {
		boolean headerFound = false;
		String header = null;
        try {
        	FileReader fr = new FileReader(filepath);
        	BufferedReader br = new BufferedReader(fr);  
        	header = br.readLine();
    		if (header != null) {
    			if (header.indexOf("FIRST_NAME") >=0) {
    		    	   headerFound = true;
    		     }
    		}
    		 br.close();
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        return headerFound;
	}
	
	
	
	public void writeToFile(Student student, String msg, String formattedDate, UtilityLogger utilLogger) {
		    
		try {
			
			utilLogger.WriteToFile(msg);    
		    utilLogger.WriteToFile(student.getStudentId()+","+student.getFirstName()+","+student.getLastName()+","+student.getLastName()+","+student.getNewThirdPartyId());
		   
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
//
}
