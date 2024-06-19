package uwi.thirdparty.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uwi.thirdparty.config.OracleConfig;
import uwi.thirdparty.entity.DatabaseConfig;
import uwi.thirdparty.entity.Databaseselector;
import uwi.thirdparty.entity.DatabaseTypes;
import uwi.thirdparty.entity.Student;
import uwi.thirdparty.exceptions.RecordNotFoundException;
import uwi.thirdparty.repository.DatabaseSelectorRepository;
import uwi.thirdparty.util.NewDateFormatterImpl;



@Service
public class OracleDao {

	private static final int  SPRIDEN_ID = 0;
	private static final int  GOBTPAC_PIDM = 1;
	private static final int  SPRIDEN_FIRST_NAME = 2;
	private static final int  SPRIDEN_LAST_NAME = 3;
	private static final int  THIRD_PARTY_ID = 4;
	private static final int  NEW_THIRD_PARTY_ID =5;
	private static final int  CA_EMAIL_ADDRESS =6;
	private static final int  STATUS =7;
	
	private boolean massUpdatesSuccessful;
	private final int errorCntr = 0;
    Logger logger = LoggerFactory.getLogger(OracleDao.class);
	
    @SuppressWarnings("unused")
    
	@Autowired
    private final OracleConfig service;
    
    @Autowired
    private DatabaseConfig config;
    
    @Autowired 
    private final DatabaseSelectorRepository selectorRepository;
    
    @Autowired
    private NewDateFormatterImpl df;
    
    private Connection conn;
	
    private String strDate;
    
    @Value("${error}")
	private String ERROR_FILE_DIRECTORY;
	
	@Value("${bulk}")
	private String BULK_FILE_DIRECTORY;
	
	
    
	public OracleDao(OracleConfig service, DatabaseSelectorRepository selectorRepository) {
		this.service = service;	
		this.selectorRepository = selectorRepository; 
	}
	
	public void openConnection() throws NamingException, SQLException  {
		
		String dataSourceString = "";
		
		List<Databaseselector> list = selectorRepository.findAll();
		Databaseselector selector = list.get(0);
        
        if (selector.getName().trim().equals(DatabaseTypes.PROD.toString())) {
           dataSourceString = config.getProd();
        } else if (selector.getName().trim().equals(DatabaseTypes.TEST.toString())) {
        	dataSourceString = config.getTest();
        } else if (selector.getName().trim().equals(DatabaseTypes.TRNG.toString())) {
        	dataSourceString = config.getTrng();
        }

		Context initContext = new InitialContext();
		Context envContext  = (Context)initContext.lookup("java:/comp/env");
		DataSource ds = (DataSource)envContext.lookup(dataSourceString); 
		conn = ds.getConnection();
	}
//	public void openConnection() {
//		try {
//
//			logger.info("Starting Oracle Db");
//			Class.forName( service.getDriver());
//			conn = DriverManager.getConnection(service.getConnect(), 
//															service.getUsername(), 
//															service.getPassword());
//			
//			conn.setAutoCommit(true);
//			logger.info("Oracle Db Started");
//		} catch (ClassNotFoundException e) {
//			logger.info("Driver class not found - {}", e.getMessage());
//		} catch (SQLException ex) {
//			logger.info("Connection error - {}", ex.getMessage());
//		}
//		
//	}
	
	public boolean studentExistInGobtPac(int pidm) {
		boolean found = false;
		String selectStatement = "select gobtpac_pidm from gobtpac where gobtpac_pidm = ?";
		
		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.setInt(1, pidm);
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {
				found = true;
			}
			prepStmt.close();
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return found;

	}
	
	public int getPidmFromGorpaud(String external_user) {
		
		logger.info(new Date().toString() + " - external_user {} {}", external_user, "GORPAUD");
		String selectStatement = "select gorpaud_pidm from gorpaud where gorpaud_external_user = ?";
		int pidm = 0;
		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			prepStmt.setString(1, external_user);
			
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {
				pidm = rs.getInt("gorpaud_pidm");
			}
			prepStmt.close();
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		logger.info(new Date().toString() + " - pidm {} {}", pidm, "GORPAUD");
		return pidm;
	}
	
	public int getPidmFromGobTPac(String external_user) {
		String selectStatement = "select gobtpac_pidm from gobtpac where gobtpac_external_user = ?";
		int pidm = 0;
		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			prepStmt.setString(1, external_user);
			
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {
				pidm = rs.getInt("gobtpac_pidm");
			}
			prepStmt.close();
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return pidm;
	}
	public String getExternalUser(long pidm) {
		 
		String externalUser = "";
		String selectStatement = "select gobtpac_external_user from gobtpac where gobtpac_pidm = ?";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			prepStmt.setLong(1, pidm);
			
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {
				externalUser = rs.getString(1);
				}
			prepStmt.close();
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
     return externalUser;
	}
	public boolean externalUserExist(String external_user, int pidm){
		boolean found=false;
		String selectStatement = "select * from gobtpac where gobtpac_external_user = ?";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			prepStmt.setString(1, external_user);
			
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {
				found=true;
				}
			prepStmt.close();
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return found;
		
	}
	
	public boolean triggersEnabled () {
		
		boolean found=false;
		String selectStatement = "SELECT status FROM dba_TRIGGERS WHERE TRIGGER_NAME = ?";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);

			prepStmt.setString(1, "GT_ICAUR_ADD_NEW_FAC_USER");
			
			ResultSet rs = prepStmt.executeQuery();
			if (rs.next()) {
				found = rs.getString(1).equals("ENABLED")? true: false;	
			}
			prepStmt.close();
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return found;
	}
	
	public void AlterTrigger(String tableOwner, String triggerName, String flag) throws Exception {
		
		String triggerSql = "ALTER TRIGGER " + tableOwner + "." + triggerName + " " + flag;
		

		PreparedStatement trgStmt = conn.prepareStatement(triggerSql);
			
		System.out.println(triggerSql);
//		trgStmt.addBatch(triggerSql);
		
		trgStmt.execute();
		
	}
		
	public void deleteFromGoremal(int pidm) {
		
		String selectStatement = "delete from goremal where goremal_pidm = ? and goremal_emal_code = 'CA'";
		try {
			
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.setInt(1, pidm);
			logger.info(new Date().toString() + " - Delete {} from {} records", pidm, "GOREMAL CA");
			prepStmt.executeUpdate();
			prepStmt.close();
		} catch (SQLException e) {
			logger.info(new Date().toString() + "Exception deleting {} from GOREMAL - {}", pidm, e.getMessage());
		}
		
	}
	
	public boolean deleteFromGorpaud(String external_user) {
		
		boolean success = true; 

		String selectStatement = "delete from gorpaud where gorpaud_external_user = ? and gorpaud_chg_ind in ('I','P')";
		try {
			
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.setString(1, external_user);
			logger.info(new Date().toString() + " - Delete {} from {} records", external_user, "'GORPAUD'");
			prepStmt.executeUpdate();
			prepStmt.close();
		} catch (SQLException e) {
			logger.info(new Date().toString() + "Exception deleting {} from GORPAUD - {}", external_user, e.getMessage());
			success = false;
		}
		return success;
	}
	public void deleteAllHoldsFromGorpaud () {
		
		String selectStatement = "delete from gorpaud where gorpaud_external_user like ? and gorpaud_chg_ind in ('I','P')";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.setString(1, "%"+"_hold"+"%");
			logger.info(new Date().toString() + "- Delete all {} from {} records", "_hold", "'GORPAUD'");
			prepStmt.executeUpdate();
			prepStmt.close();
		} catch (SQLException e) {
			logger.info(new Date().toString() +" - Exception deleting {} from GORPAUD - {}",  "_hold", e.getMessage());
		}
	}
	public Student updateCampusEmailAddresses(int pidm, String external_user, Student student) {
		String selectStatement = "UPDATE goremal SET goremal_email_address = ? WHERE goremal_emal_code = 'CA' AND goremal_pidm = ?";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.setString(1, external_user+"@cavehill.uwi.edu");
			prepStmt.setInt(2, pidm);
			student.setEmail(external_user+"@cavehill.uwi.edu");
			logger.info("email updated - {}", student.getEmail());
			prepStmt.executeUpdate();
			prepStmt.close();
		} catch (SQLException e) {
			logger.info("Exception updating {} ",  "goremal", e.getMessage());
		}
		return student;
	}
	public void  deleteFromGobtPac(String external_user) {
		String selectStatement = "delete from gobtpac where gobtpac_external_user = ? ";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.setString(1, external_user);
			logger.info("Delete {} from {} records", external_user, "'GOBTPAC'");
			prepStmt.executeUpdate();
			prepStmt.close();
		} catch (SQLException e) {
			logger.info("Exception deleting {} from GOBTPAC - {}", external_user, e.getMessage());
		}
	}
	
	public void tempUpdateThirdPartyId(String tpId) {
		
		String selectStatement = "update gobtpac set gobtpac_external_user = ?  where gobtpac_external_user = ? ";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(selectStatement);
			prepStmt.setString(1, "temp_"+tpId);
			prepStmt.setString(2, tpId);
			logger.info("Prepare {} for swap in table {}", tpId, "'GOBTPAC'");
			prepStmt.executeUpdate();
			prepStmt.close();
		} catch (SQLException e) {
			logger.info("Exception updating {} for swap in table GOBTPAC - {}", tpId, e.getMessage());
		}
	}
	public void updateThirdPartyId(long pidm, String third_party_id) throws SQLException {

		CallableStatement callStmt = conn.prepareCall("{CALL gb_third_party_access.p_update(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
		callStmt.setLong(1, pidm);
		callStmt.setString(2, "N");
		callStmt.setString(3, "N");
		callStmt.setString(4, "SVC_UPDATE");
		callStmt.setString(5, null);
		callStmt.setString(6, null);
		callStmt.setString(7, third_party_id);
		callStmt.setString(8, null);
		callStmt.setString(9, null);
		callStmt.setString(10, null);
		callStmt.setString(11, null);
		callStmt.setString(12, null);
		callStmt.setString(13, null);
		callStmt.setString(14, "Y");
		callStmt.setString(15, "N");
		callStmt.setString(16, "N");
		callStmt.setString(17, "Y");
		callStmt.registerOutParameter(18, Types.VARCHAR);
		
		callStmt.executeQuery();
	    callStmt.close();

	    Student student = getStudent(pidm);
	    logger.info(new Date().toString() +" - Third Party ID {} updated for student {} {} with spriden ID {}", third_party_id, student.getFirstName(), student.getLastName(), student.getStudentId());
	}
	
	public Student getStudent(long pidm) {
		if(pidm == 0) {
			throw new RecordNotFoundException("Record Not Found");
		}
	   
		Student student = new Student();
		String sqlstmt = "select distinct a.spriden_id, a.spriden_first_name, a.spriden_last_name, b.gobtpac_external_user, c.goremal_email_address from spriden a, gobtpac b, goremal c  "
				                + "where a.spriden_pidm = b.gobtpac_pidm " 
				                + "and a.spriden_pidm = c.goremal_pidm " 
				                + "and c.goremal_emal_code =  'CA' "
				                + "and a.SPRIDEN_CHANGE_IND is null "
				                + "and a.spriden_pidm = ?";
		
		try {
            PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
            prepStmt.setInt(1, Integer.parseInt(Long.toString(pidm)));
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
				student.setStudentId(rs.getString(1));
				student.setFirstName(rs.getString(2));
				student.setLastName(rs.getString(3));
				student.setThirdPartyId(rs.getString(4));
				student.setEmail(rs.getString(5));
			}

			rs.close();
			prepStmt.close();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} catch (RecordNotFoundException e) {
			e.printStackTrace();
		}
		return student;
	}
	
	public List<Student> getStudents(String tpid) {
		if(tpid == null || tpid.trim().equals("")) {
			throw new RecordNotFoundException("Record Not Found");
		}
	   ArrayList<Student> list =  new ArrayList<Student>();
		
		String sqlstmt = "select distinct a.spriden_id, a.spriden_first_name, a.spriden_last_name, b.gobtpac_external_user, c.goremal_email_address from spriden a, gobtpac b, goremal c  "
				                + "where a.spriden_pidm = b.gobtpac_pidm " 
				                + "and a.spriden_pidm = c.goremal_pidm " 
				                + "and c.goremal_emal_code =  'CA' "
				                + "and a.SPRIDEN_CHANGE_IND is null "
				                + "and gobtpac_external_user like ?";
		
		try {
            PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
            prepStmt.setString(1, "%"+tpid+"%");
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
				Student student = new Student();
				student.setStudentId(rs.getString(1));
				student.setFirstName(rs.getString(2));
				student.setLastName(rs.getString(3));
				student.setThirdPartyId(rs.getString(4));
				student.setEmail(rs.getString(5));
				list.add(student);
			}

			rs.close();
			prepStmt.close();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} catch (RecordNotFoundException e) {
			e.printStackTrace();
		}
		return list;
	}
	
    public int findPidm(String studentId) {
		
		int pidm = 0;

		String sqlstmt = "select spriden_pidm, spriden_id from spriden where spriden_id = ?";

		try {

			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setString(1, studentId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
				pidm = rs.getInt(1);
			}

			rs.close();
			prepStmt.close();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}

		return pidm;
	}

    public boolean receiptExist(long pidm, String receiptNo){
		
		boolean found = false;
		
		String sql = "SELECT tbraccd_pidm, tbraccd_payment_id "
				+ "FROM tbraccd where tbraccd_pidm = ? and tbraccd_payment_id = ?";

		try {
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			prepStmt.setLong(1, pidm);
            prepStmt.setString(2, receiptNo);
			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {
				found = true;
			}
			
			

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return found;
	}
	public Connection getConnection() {
    	return conn;
    }
	public void closeConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}	
public void insertAdhoc(String Id, String detailCode, String amount) {
	float balance = 0;
	if (detailCode.equals("CSBW")) {
		balance = -1 * Float.parseFloat(amount);
	} else if (detailCode.equals("NSBW")) {
		balance = Float.parseFloat(amount);
	}
	
	String insertStatement = "INSERT INTO tbraccd (tbraccd_pidm,TBRACCD_TRAN_NUMBER,tbraccd_term_code, "
			+ "tbraccd_detail_code,"
			+ " tbraccd_user,tbraccd_entry_date,tbraccd_amount,tbraccd_balance,tbraccd_effective_date,tbraccd_desc,tbraccd_srce_code, "
			+ "tbraccd_acct_feed_ind,tbraccd_activity_Date, tbraccd_session_number,tbraccd_document_number,tbraccd_trans_date, "
			+ "tbraccd_invoice_number,tbraccd_atyp_code,tbraccd_atyp_seqno,tbraccd_curr_code)"
			+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

	try {
		
		long pidm = getPidm(Id);
		
		PreparedStatement prepStmt = conn.prepareStatement(insertStatement);
		prepStmt.setLong(1, pidm);
		prepStmt.setInt(2, calcStudentNewTransNumber(pidm));
		prepStmt.setString(3, getCurrentTerm());
		prepStmt.setString(4, detailCode);
		prepStmt.setString(5, "CCCASHIER");
		prepStmt.setTimestamp(6, java.sql.Timestamp.valueOf(df.printDate()));
		prepStmt.setFloat(7, Float.parseFloat(amount));
		prepStmt.setFloat(8, balance);
		prepStmt.setTimestamp(9,
				java.sql.Timestamp.valueOf(df.printDate()));
		prepStmt.setString(10, detailCode.equals("NSBW")? "Student Balances in Credit": "Student Balance for Collection");
		prepStmt.setString(11, "T");
		prepStmt.setString(12, "Y");
		prepStmt.setTimestamp(13,
				java.sql.Timestamp.valueOf(df.printDate()));
		prepStmt.setDouble(14, 0);
		prepStmt.setString(15, null);
		prepStmt.setDate(16, java.sql.Date.valueOf(df.getSimpleDate()));
		prepStmt.setString(17, null);
		prepStmt.setString(18, "TM");
		prepStmt.setString(19, "");
		prepStmt.setString(20, "");
		

		prepStmt.executeUpdate();
		prepStmt.close();
		
	} catch (SQLException e) {
		e.printStackTrace();
	}
}
public void updateTransactionWithPidm(String id, String paymentId, int transNo) {
	long pidm = getPidm(id);
	//%s %2d
	String updateStatement = "UPDATE TBRACCD SET tbraccd_pidm = ? " +
			"WHERE tbraccd_payment_id = ?  AND tbraccd_detail_code = 'SPAY' " +
			"AND tbraccd_tran_number = ? AND tbraccd_pidm = 0";
//where tbraccd_detail_code = 'SPAY' and tbraccd_pidm = ? and tbraccd_tran_number = ?
	try {
		PreparedStatement prepStmt = conn.prepareStatement(updateStatement);
		prepStmt.setLong(1, pidm);
		prepStmt.setString(2, paymentId);
		prepStmt.setInt(3, transNo);
		prepStmt.executeUpdate();
		
		prepStmt.close();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
	private long getPidm(String id) {

		int pidm = 0;

		String sqlstmt = "select spriden_pidm, spriden_id from spriden where spriden_id = ?";

		try {

			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setString(1, id);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
				pidm = rs.getInt(1);
			}

			rs.close();
			prepStmt.close();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}

		return pidm;

	}
	private boolean isChsbStudent(long pidm) {
		boolean found = false;
		String sqlstmt = "select SGRSATT_ATTS_CODE from SGRSATT where SGRSATT_PIDM = ?";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
			prepStmt.setLong(1, pidm);
			ResultSet rs = prepStmt.executeQuery();
		    if (rs.next()) {
			  if (rs.getString(1).equals("CHSB")) {
				  found = true;
			  }
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return found;
	}
	public String getCurrentTerm() {
		String term = null;

		String sqlstmt = "select min(stvterm_code) as maxtermcode from stvterm where stvterm_start_date <= ? and stvterm_end_date >= ? and stvterm_code not in ('201905') and stvterm_desc not like  '%Year%Long%'";
		
		try {

			PreparedStatement prepStmt = conn.prepareStatement(sqlstmt);
            prepStmt.setDate(1, java.sql.Date.valueOf((df.getSimpleDate())));
			prepStmt.setDate(2, java.sql.Date.valueOf(df.getSimpleDate()));

			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {
				term = rs.getString(1);
			}

			if ((term != null) && (term.indexOf("40") >= 0)) {

				term = null;

			}
			if (term == null) {

				sqlstmt = "select min(stvterm_code) as maxtermcode from stvterm where stvterm_start_date >= ? and stvterm_code <> '201905'";
				
				PreparedStatement prepStmt1 = conn.prepareStatement(sqlstmt);
				prepStmt1.setDate(1,
						java.sql.Date.valueOf((df.getSimpleDate())));

				ResultSet rs1 = prepStmt1.executeQuery();
				while (rs1.next()) {
					term = rs1.getString(1);
				}

				rs1.close();
				prepStmt1.close();
			}

			/* Only use for XRUN */
			rs.close();

			prepStmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return term;
	}
	public String getSemesterPaid(String receiptno) {
		String sql = "SELECT tbraccd_term_code "
				+ "FROM taismgr.tbraccd " + "WHERE tbraccd_payment_id = ?";
        String semester = "";
		try {
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			prepStmt.setString(1, receiptno);

			ResultSet rs = prepStmt.executeQuery();

			if (rs.next()) {
				semester = rs.getString(1);
				logger.info("receipt no -{} semester - {}", receiptno, semester);
			}
            return semester;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	private int calcStudentNewTransNumber(long pidm) {
        int trans_number = 0;
		String sql = "SELECT Max(tbraccd_tran_number) as max_trans_number "
				+ "FROM taismgr.tbraccd " + "WHERE tbraccd_pidm = ?";

		try {
			PreparedStatement prepStmt = conn.prepareStatement(sql);
			prepStmt.setLong(1, pidm);

			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {
				trans_number = rs.getInt(1);
			}
           rs.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return trans_number + 1;
	}
//  public void manualTransactionInsert(String id, String transId, String amt){
//    	
//	  Transactions student = new Transactions();
//    	student.setstudentid(id);
//    	student.settransactionamt(amt);
//    	student.setreceiptno(transId);
//    	
//    	insertTransaction(student);
//    	
//    }
}
