package uwi.thirdparty.service.contract;

import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import uwi.thirdparty.entity.Student;
@Service
public interface ThirdPartyService {
	public Student thirdPartyUpdate(Student student) throws SQLException, NamingException;
	public Student getStudent(String stuId) throws SQLException, NamingException;
	public List<Student> getStudents(String tpid) throws SQLException, NamingException;
	public boolean massUpdate() throws Exception;
	public boolean deleteThirdParty(String tpId) throws NamingException, SQLException;
	public List<Student> swap(Object obj) throws SQLException, JsonProcessingException, NamingException;  
	public boolean alterTrigger(String status) throws Exception;
	public boolean triggersEnabled() throws Exception;
	public List<Student> showFileFormat() throws Exception;
	public boolean updateDatabaseSector(String fieldname) throws Exception;
	public String getCurrentDatabaseFromSelector() throws Exception;
}
