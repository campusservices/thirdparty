package uwi.thirdparty.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import uwi.thirdparty.dao.OracleDao;
import uwi.thirdparty.entity.Oracletrigger;
import uwi.thirdparty.entity.RequestObject;
import uwi.thirdparty.entity.Student;
import uwi.thirdparty.repository.TestRepository;
import uwi.thirdparty.repository.TriggerListRepository;
import uwi.thirdparty.service.contract.ThirdPartyService;
import uwi.thirdparty.util.UtilityLogger;

@Service
public class ThirdpartyServiceImpl implements ThirdPartyService {

	private final  OracleDao dao;
	
	private final FileStorageService fileStorageService;
	
	private final TriggerListRepository repository;
	private final TestRepository repo;
	private final UtilityLogger utilLogger;
	private boolean altered = false;
	
	Logger logger = LoggerFactory.getLogger(ThirdpartyServiceImpl.class);
	 
	@Autowired
	public ThirdpartyServiceImpl(OracleDao dao, 
			          FileStorageService fileStorageService, 
			                       TriggerListRepository repository, 
			                          TestRepository repo,
			                          UtilityLogger utilLogger) throws NamingException, SQLException {	        
	        this.dao = dao;
	        this.fileStorageService = fileStorageService;
			this.repository = repository;
			this.repo = repo;
			this.utilLogger = utilLogger;
			
	}
	
	
	@Override
	public Student thirdPartyUpdate(Student student) throws SQLException, NamingException {
		dao.openConnection();
		int pidm =dao.findPidm(student.getStudentId());
		String externalUser = dao.getExternalUser(pidm); 
		dao.deleteFromGorpaud(externalUser);
		dao.deleteFromGorpaud(student.getThirdPartyId());
		dao.updateThirdPartyId(pidm, student.getThirdPartyId());
		student.setNewThirdPartyId(student.getThirdPartyId());
		student.setThirdPartyId(externalUser);
		dao.closeConnection();
		return student;
	}

	@Override
	public Student getStudent(String stuId) throws SQLException, NamingException {
		long startTime =  System.currentTimeMillis();
		logger.info("Start Time - {} ", startTime);
		dao.openConnection();
		long endTime =  System.currentTimeMillis();
		logger.info("End Time - {} ", endTime);
		long diffTime = ((endTime - startTime)/1000)/60;
		logger.info("duration  - {} ", diffTime);
		Student student = dao.getStudent(dao.findPidm(stuId));
		dao.closeConnection();
		return student; 
	}

	@Override
	public boolean massUpdate() throws Exception {
		ArrayList<Student> students = fileStorageService.readBulkFile();
		dao.openConnection();
		massUpdates(students);
		dao.closeConnection();
		return true;
	}

	@Override
	public List<Student> showFileFormat() throws Exception {
		fileStorageService.setupFileStorage();
		ArrayList<Student> students = fileStorageService.readBulkFile();
		return students;
	}
	
	private void massUpdates(ArrayList<Student> students) throws NumberFormatException, IOException {
		  
		  fileStorageService.setupFileStorage();
		  fileStorageService.setFormattedDate();
		  
	      students.stream().forEach(student-> {
	    	  
	    	  try {
		    	  String thirdParty = student.getNewThirdPartyId();
		    	  if (!dao.studentExistInGobtPac(student.getPidm())) {
		    		  fileStorageService.writeToFile(student, "record does not exist in gobtpac", utilLogger);
		    	  } else if (!dao.externalUserExist(thirdParty,student.getPidm())) {
		    		  utilLogger.WriteToFile("Updating ---> " +  "," + student.getStudentId() + "," + student.getPidm()+  "," + student.getFirstName()+  "," + student.getLastName()+  "," +student.getThirdPartyId());
		    		  dao.deleteFromGorpaud(student.getThirdPartyId());
		    		  dao.deleteFromGorpaud(student.getNewThirdPartyId());
		    		  utilLogger.WriteToFile("Removed from 'GORPAUD' third party id - {} {} {} "+ "," + student.getThirdPartyId()+ "," + student.getFirstName()+ "," +student.getLastName());  
					  dao.updateThirdPartyId(student.getPidm(), student.getNewThirdPartyId());
					  dao.updateCampusEmailAddresses(student.getPidm(), student.getNewThirdPartyId(), student);
					  utilLogger.WriteToFile("Updated third party ID for {} {} from old {} to new third pary id {} " + "," + student.getFirstName()+ "," +student.getLastName()+ "," + student.getThirdPartyId()+ "," + student.getNewThirdPartyId());
		    	  }  else {
		    		  fileStorageService.writeToFile(student, "thirdpartyid already exist in gobtpac",utilLogger);
		    	  }
		    	 
	    	  } catch (SQLException | SecurityException | IOException ex) {
					// TODO Auto-generated catch block
	    		    
	    		     logger.info(ex.getMessage());
				}	
				   
	      });
		
	}
	@Override
	public List<Student> swap(Object obj) throws SQLException, JsonProcessingException, NamingException {
		int cnt = 0; 
		String tpId="";
		dao.openConnection();
		List<Student> swappedStudents = new ArrayList<Student>();
		List<Student> students = getTransactions(obj);
		List<String> tpIDs = students.stream().map(student->{
			return student.getThirdPartyId();
			}).collect(Collectors.toList());
		for (Student student :students) {
			dao.deleteFromGorpaud(student.getThirdPartyId());
			tpId = cnt == 0 ? tpIDs.get(1): tpIDs.get(0); 
			student.setThirdPartyId(tpId);
			int OriginalPidm = dao. getPidmFromGobTPac(student.getThirdPartyId());
			if (cnt == 0) {
				dao.deleteFromGorpaud(student.getThirdPartyId());
				dao.updateThirdPartyId(OriginalPidm, student.getThirdPartyId()+"_hold");
			}
			dao.updateThirdPartyId(dao.findPidm(student.getStudentId()), tpId);
			logger.info("{} - {} - {}", student.getStudentId(), tpId, student.getEmail());
			Student updateStudent = dao.updateCampusEmailAddresses(dao.findPidm(student.getStudentId()), student.getThirdPartyId(), student);
			logger.info("{}", updateStudent.getEmail());
			
            swappedStudents.add(updateStudent);
            
            cnt++;
		}
		removeAllHolds();
		dao.closeConnection();
		return swappedStudents;
	}
	private void removeAllHolds() {
		dao.deleteAllHoldsFromGorpaud();
	}
	private List<Student> getTransactions(Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String JSONString = mapper.writeValueAsString(obj);
		GsonBuilder builder = new GsonBuilder(); ;
		Gson gson = builder.create();
		
		RequestObject requestObject  = gson.fromJson(JSONString, RequestObject.class);  
   	    List<Student> transactionArrayList = requestObject.getData();
   	  
		return transactionArrayList;
		
	}

	@Override
	public List<Student> getStudents(String tpid) throws SQLException, NamingException {
		dao.openConnection();
		List<Student> students = dao.getStudents(tpid);
		dao.closeConnection();
		return students;
	}

	@Override
	public boolean alterTrigger(String status) throws Exception {
		// TODO Auto-generated method stub
		
		List<Oracletrigger> triggers = repository.findAllTriggers();
		dao.openConnection();
		triggers.stream().forEach(trigger->{
			try {
				dao.AlterTrigger(trigger.getUser(), trigger.getName(), status);
				altered = true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		dao.closeConnection();
		return altered;
	}

	@Override
	public boolean triggersEnabled() throws Exception {
		boolean enabled = false;
		// TODO Auto-generated method stub
		dao.openConnection();
		enabled = dao.triggersEnabled();
		dao.closeConnection();
		return enabled;
	}

	@Override
	public boolean deleteThirdParty(String tpId) throws NamingException, SQLException {
		// TODO Auto-generated method stub
		dao.openConnection();
		boolean success = dao.deleteFromGorpaud(tpId);
		dao.closeConnection();
		return success;
	}

	
}
