package uwi.thirdparty.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import uwi.thirdparty.exceptions.InvalidExtensionException;
import uwi.thirdparty.service.FileStorageService;
import uwi.thirdparty.service.UploadFileResponse;
import uwi.thirdparty.util.ResponseDetails;
import uwi.thirdparty.util.ResponseStatus;


@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1")
public class FileController {

	@Autowired
	private FileStorageService fileStorageService;
    
    private String fileName = "";
    
	/*uploads csv files to api*/
	@PostMapping(value = "uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public  ResponseEntity<ResponseDetails<UploadFileResponse>> uploadFile(@RequestParam("file") MultipartFile file, 
		 	                                                                                                     HttpServletRequest request) throws IllegalStateException, IOException {
	    
		if (file.isEmpty()) {
			ResponseDetails<UploadFileResponse> responseDetails = new ResponseDetails<UploadFileResponse>(ResponseStatus.BAD_REQUEST, null, "File Empty");
			return new ResponseEntity<>(responseDetails, HttpStatus.BAD_REQUEST);
		}
		
		try {
			fileStorageService.setupFileStorage();
			fileName = fileStorageService.storeFile(file);
		} catch (InvalidExtensionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ResponseDetails<UploadFileResponse> responseDetails = new ResponseDetails<UploadFileResponse>(ResponseStatus.BAD_REQUEST, null, "Invalid Extension");
			return new ResponseEntity<>(responseDetails, HttpStatus.BAD_REQUEST);
		}
		
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("uploadFile/")
				.path(file.getName()).toUriString();
		UploadFileResponse response = new UploadFileResponse(file.getName(), fileDownloadUri, file.getContentType(), file.getSize());
		ResponseDetails<UploadFileResponse> responseDetails = new ResponseDetails<UploadFileResponse>(ResponseStatus.SUCCESS, response, "File Successfully Uploaded");
		
    	return new ResponseEntity<ResponseDetails<UploadFileResponse>>(responseDetails, HttpStatus.OK);
	}
}
