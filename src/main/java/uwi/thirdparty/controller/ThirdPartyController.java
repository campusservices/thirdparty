package uwi.thirdparty.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import uwi.thirdparty.entity.Student;
import uwi.thirdparty.exceptions.RecordNotFoundException;
import uwi.thirdparty.service.contract.ThirdPartyService;
import uwi.thirdparty.util.ResponseDetails;
import uwi.thirdparty.util.ResponseStatus;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")

@RestController
@RequestMapping("/api/v1")
public class ThirdPartyController {

	private final ThirdPartyService thirdPartyService;
	
	@Autowired
	public ThirdPartyController(ThirdPartyService thirdPartyService) {
	    this.thirdPartyService = thirdPartyService;
	}
	
	@RequestMapping(
	        value = "update",
	        method = RequestMethod.POST,
	        produces = MediaType.APPLICATION_JSON_VALUE
	)
	public  ResponseEntity<ResponseDetails<Student>> update(@RequestBody Student student) throws IOException, NamingException {
		
		try {
			Student studentResponse = thirdPartyService.thirdPartyUpdate(student);
			ResponseDetails<Student> responseDetails = new ResponseDetails<Student>(ResponseStatus.SUCCESS, studentResponse,"Successful Transaction");
			return new ResponseEntity<ResponseDetails<Student>>( responseDetails,HttpStatus.OK);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<ResponseDetails<Student>>( new ResponseDetails<Student>(ResponseStatus.BAD_REQUEST, null,"UnSuccessful Request"),HttpStatus.BAD_REQUEST);
	}


	@RequestMapping(
	        value = "mass/update",
	        method = RequestMethod.POST,
	        produces = MediaType.APPLICATION_JSON_VALUE
	)
	
	public  ResponseEntity<ResponseDetails<Boolean>> massUpdate() throws Exception {
		
		try {
			boolean response = thirdPartyService.massUpdate();
			ResponseDetails<Boolean> responseDetails = new ResponseDetails<Boolean>(ResponseStatus.SUCCESS, response,"Successful Transaction");
			return new ResponseEntity<ResponseDetails<Boolean>>( responseDetails,HttpStatus.OK);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<ResponseDetails<Boolean>>( new ResponseDetails<Boolean>(ResponseStatus.BAD_REQUEST, false,"UnSuccessful Request"),HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(
	        value = "mass/show/format",
	        method = RequestMethod.POST,
	        produces = MediaType.APPLICATION_JSON_VALUE
	)
	
	public  ResponseEntity<ResponseDetails<List<Student>>> showFileFormat() throws Exception {
		
		try {
			List<Student> students = thirdPartyService.showFileFormat();
			ResponseDetails<List<Student>> responseDetails = new ResponseDetails<List<Student>>(ResponseStatus.SUCCESS, students,"Successful Transaction");
			return new ResponseEntity<ResponseDetails<List<Student>>>( responseDetails,HttpStatus.OK);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<ResponseDetails<List<Student>>>( new ResponseDetails<List<Student>>(ResponseStatus.BAD_REQUEST, null,"UnSuccessful Request"),HttpStatus.BAD_REQUEST);
	}
	@RequestMapping(
	        value = "student/{studentId}",
	        method = RequestMethod.GET,
	        produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<ResponseDetails<Student>> getStudent(@PathVariable String studentId) throws NamingException  {
		String errorMessage = "";
		try {
			Student student = thirdPartyService.getStudent(studentId);
			ResponseDetails<Student> responseDetails = new ResponseDetails<Student>(ResponseStatus.SUCCESS, student,"Successful Query");
			if (student != null) {
			  return new ResponseEntity<ResponseDetails<Student>>( responseDetails,HttpStatus.OK);
			} else {
				throw new RecordNotFoundException("Student Record Not Found");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (RecordNotFoundException e) {
			errorMessage = e.getMessage();
			e.printStackTrace();
		} 
		return new ResponseEntity<ResponseDetails<Student>>( new ResponseDetails<Student>(ResponseStatus.BAD_REQUEST, null,errorMessage),HttpStatus.NOT_FOUND);
	}
	@RequestMapping(
	        value = "students/{tpid}",
	        method = RequestMethod.GET,
	        produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<ResponseDetails<List<Student>>> getStudents(@PathVariable String tpid) throws NamingException  {
		String errorMessage = "";
		try {
			List<Student> student = thirdPartyService.getStudents(tpid);
			ResponseDetails<List<Student>> responseDetails = new ResponseDetails<List<Student>>(ResponseStatus.SUCCESS, student,"Successful Query");
			if (student != null) {
			  return new ResponseEntity<ResponseDetails<List<Student>>>( responseDetails,HttpStatus.OK);
			} else {
				throw new RecordNotFoundException("Student Record Not Found");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (RecordNotFoundException e) {
			errorMessage = e.getMessage();
			e.printStackTrace();
		} 
		return new ResponseEntity<ResponseDetails<List<Student>>>( new ResponseDetails<List<Student>>(ResponseStatus.BAD_REQUEST, null,errorMessage),HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(
	        value = "deletion/{tpid}",
	        method = RequestMethod.GET,
	        produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<ResponseDetails<Boolean>> deleteThirdParty(@PathVariable String tpid) throws NamingException, SQLException  {
		Boolean success = thirdPartyService.deleteThirdParty(tpid);
		ResponseDetails<Boolean> responseDetails = new ResponseDetails<Boolean>(ResponseStatus.SUCCESS, success,"Successful Altered");
		return new ResponseEntity<ResponseDetails<Boolean>>(responseDetails, HttpStatus.OK);
	}
	
	@RequestMapping(
	        value = "swap",
	        method = RequestMethod.POST,
	        produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<ResponseDetails<List<Student>>> swap(@RequestBody Object obj) throws NamingException  {
		String errorMessage = "";
		try {
			List<Student> students = thirdPartyService.swap(obj);
			ResponseDetails<List<Student>> responseDetails = new ResponseDetails<List<Student>>(ResponseStatus.SUCCESS, students,"Successful Query");
			return new ResponseEntity<ResponseDetails<List<Student>>>(responseDetails, HttpStatus.OK);
		} catch (JsonProcessingException | SQLException e) {
			errorMessage = e.getMessage();
			e.printStackTrace();
		}
		return new ResponseEntity<ResponseDetails<List<Student>>>( new ResponseDetails<List<Student>>(ResponseStatus.BAD_REQUEST, null,errorMessage),HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(
	        value = "triggers/{status}",
	        method = RequestMethod.GET,
	        produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<ResponseDetails<Boolean>> alterTriggers(@PathVariable String status) throws Exception  {
		
		Boolean altered = thirdPartyService.alterTrigger(status);
		ResponseDetails<Boolean> responseDetails = new ResponseDetails<Boolean>(ResponseStatus.SUCCESS, altered,"Successful Altered");
		return new ResponseEntity<ResponseDetails<Boolean>>(responseDetails, HttpStatus.OK);
	}
	
	@RequestMapping(
	        value = "display/triggers/status",
	        method = RequestMethod.GET,
	        produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<ResponseDetails<Boolean>> displayTriggerStatus() throws Exception  {
		
		Boolean enabled = thirdPartyService.triggersEnabled();
		ResponseDetails<Boolean> responseDetails = new ResponseDetails<Boolean>(ResponseStatus.SUCCESS, enabled,"Status is " + Boolean.toString(enabled));
		return new ResponseEntity<ResponseDetails<Boolean>>(responseDetails, HttpStatus.OK);
	}
	
	
	@RequestMapping(
	        value = "update/databaseSelector/{database}",
	        method = RequestMethod.PUT,
	        produces = MediaType.APPLICATION_JSON_VALUE
    )	
    public ResponseEntity<ResponseDetails<Boolean>>  updateDatabaseSelector(@PathVariable String database) throws Exception  {
		
		Boolean enabled = thirdPartyService.updateDatabaseSector(database);
		ResponseDetails<Boolean> responseDetails = new ResponseDetails<Boolean>(ResponseStatus.SUCCESS, enabled,"Status is " + Boolean.toString(enabled));
		return new ResponseEntity<ResponseDetails<Boolean>>(responseDetails, HttpStatus.OK);
	}
	
	@RequestMapping(
	        value = "find/databaseSelector",
	        method = RequestMethod.GET,
	        produces = MediaType.APPLICATION_JSON_VALUE
    )	
    public ResponseEntity<ResponseDetails<String>>  getCurrentDatabaseFromSelector() throws Exception  {
		
		String database = thirdPartyService.getCurrentDatabaseFromSelector();
		ResponseDetails<String> responseDetails = new ResponseDetails<String>(ResponseStatus.SUCCESS, database,database);
		return new ResponseEntity<ResponseDetails<String>>(responseDetails, HttpStatus.OK);
	}
	
}

