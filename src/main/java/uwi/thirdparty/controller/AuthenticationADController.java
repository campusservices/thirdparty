package uwi.thirdparty.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import uwi.thirdparty.entity.Credentials;
import uwi.thirdparty.service.LDAPService;
import uwi.thirdparty.util.ResponseDetails;
import uwi.thirdparty.util.ResponseStatus;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1")
public class AuthenticationADController {
   
private final LDAPService ldapService;
	
	@Autowired
	public AuthenticationADController( LDAPService ldapService) {
	    this.ldapService = ldapService;
	}
	
	
	@RequestMapping(
	        value = "authenticate",
	        		 method = RequestMethod.POST,
	        produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<ResponseDetails<Boolean>> authenticateUser(@RequestBody Credentials credentials) 
    {
		boolean authenticated = true;
		HttpStatus httpStatus = null;
		try {
		   authenticated = ldapService.authenticate(credentials.getUsername(), credentials.getPassword());
		   if (!authenticated) {
			   httpStatus = HttpStatus.BAD_REQUEST;
		   } else {
			httpStatus = HttpStatus.OK;
		   }
		} catch (Exception e) {
			httpStatus = HttpStatus.BAD_REQUEST;
		}

		ResponseDetails<Boolean> responseDetails = authenticated == true ? new ResponseDetails<Boolean>(ResponseStatus.SUCCESS, authenticated, "Succesful Login")
		                                                                                               :new ResponseDetails<Boolean>(ResponseStatus.BAD_REQUEST, authenticated, "Unsuccessful Succesful Transaction");  
		return new ResponseEntity<ResponseDetails<Boolean>>(responseDetails, httpStatus);
	}
	
}
