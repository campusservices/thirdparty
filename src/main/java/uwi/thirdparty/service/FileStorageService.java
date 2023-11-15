package uwi.thirdparty.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import uwi.thirdparty.entity.Student;
import uwi.thirdparty.exceptions.FileStorageException;
import uwi.thirdparty.exceptions.InvalidExtensionException;
import uwi.thirdparty.exceptions.MyFileNotFoundException;
import uwi.thirdparty.util.DteFormatter;
import uwi.thirdparty.util.FileManager;
import uwi.thirdparty.util.UtilityLogger;

@Service
public class FileStorageService {

    private Path fileStorageLocation;
    
    private final FileStorageProperties fileStorageProperties;
    private Path targetLocation;
    private Path newFileLocation;
    
    private String strDate;
    
    private final FileManager fileReaderUtil;
    
    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties, FileManager fileReaderUtil) {
        this.fileStorageProperties = fileStorageProperties;
		this.fileReaderUtil = fileReaderUtil;
    }

    public void setupFileStorage() {
    	this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
    	try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }
    public String storeFile(MultipartFile file) throws InvalidExtensionException {
        // Normalize file name
    	
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = FilenameUtils.getExtension(fileName); 
        if (!ext.equals("csv"))
        	throw new InvalidExtensionException("Invalid File Extension");
        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            targetLocation = this.fileStorageLocation.resolve(fileName);
            newFileLocation =this.fileStorageLocation.resolve(fileStorageProperties.getDownloadFile());
            
            File fl = new File(targetLocation.toString());
            File newfl = new File(newFileLocation.toString());
            
            if (fl.exists())  
              if (newfl.exists()) {
                  FileUtils.forceDelete(new File(newFileLocation.toString()));
              }
              Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
             fl.renameTo(newfl);
            
            return newfl.getName();
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = fileReaderUtil.getResource(filePath);
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }
    
    public ArrayList<Student> readBulkFile() {
		int lineCntr = 0;
		ArrayList<Student> students = new ArrayList<Student>();
 		String delimiter = ",";
 		
		Path fileLocation = fileStorageLocation.resolve(fileStorageProperties.getDownloadFile());
		try {
	        
	         BufferedReader br = fileReaderUtil.getBufferedReader(fileLocation);
	         
	         String line = "";
	         String[] tempArr;
	         while((line = br.readLine()) != null) {
	        	 if (lineCntr > 0) {
		            tempArr = line.split(delimiter);
		            Student student = new Student();
		            student.setStudentId(tempArr[0]);
		            student.setPidm(Integer.parseInt(tempArr[1]));
		            student.setFirstName(tempArr[2]);
		            student.setLastName(tempArr[3]);
		            student.setThirdPartyId(tempArr[4]);
		            student.setNewThirdPartyId(tempArr[5]);
		            student.setEmail(tempArr[6]);
		            student.setStatus(tempArr[7]);
		            students.add(student);
	        	 }
	        	 lineCntr++;
	         }
	         br.close();
	         } catch(IOException ioe) {
	            ioe.printStackTrace();
	         }
		  return students;
	}
    
    public void writeToFile(Student student, String msg, UtilityLogger utilLogger) {
    	
    	fileReaderUtil.writeToFile(student, msg, getFormattedDate(), utilLogger);
		
	}
        
    public void setFormattedDate () {
		DteFormatter formatter = new DteFormatter();
		strDate = formatter.parseDate(formatter.printDate());
	}
	
	private String getFormattedDate () {
		  return strDate;
	}
}
