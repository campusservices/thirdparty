package uwi.thirdparty.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class UtilityLogger {
	
	FileHandler fh;  
	
	@Value("${error}")
	private String ERROR_FILE_DIRECTORY;
	 
	@Value("${filename}")
    private String fileName;
	 
	public void WriteToFile(String msg) throws SecurityException, IOException {
		 
		Logger logger = Logger.getLogger("BulkErrorLog"); 
		fh = new FileHandler(ERROR_FILE_DIRECTORY+ "/"+ fileName);  
		
		logger.addHandler(fh);
	    SimpleFormatter formatter = new SimpleFormatter();  
	    fh.setFormatter(formatter);  

	    // the following statement is used to log any messages  
	    logger.info(msg);  
	        
	}

}
