package uwi.thirdparty.entity.banner;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(schema = "svc_update",name="spriden")
@IdClass(RelationshipId.class)
public class Spriden implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="SPRIDEN_PIDM")
	private Long pidm;
	
	@Id
	@Column(name="SPRIDEN_ID")
	private String spridenId;
	
	@Id
	@Column(name="SPRIDEN_LAST_NAME")
	private String lastName;
	
	@Id
	@Column(name="SPRIDEN_FIRST_NAME")
	private String firstName;
	
	@Id
	@Column(name="SPRIDEN_MI")
	private String middleInitial;
	
	@Id
	@Column(name="SPRIDEN_CHANGE_IND")
	private String changeIndicator;
	
	@Column(name="SPRIDEN_ENTITY_IND")
	private String entityIndicator;
	
	@Column(name="SPRIDEN_ACTIVITY_DATE")
	private Date activityDate;
	
	@Column(name="SPRIDEN_USER")
	private String user;
	
	@Column(name="SPRIDEN_ORIGIN")
	private String origin;
	
	@Column(name="SPRIDEN_SEARCH_LAST_NAME")
	private String searchLastName;

	@Column(name="SPRIDEN_SEARCH_FIRST_NAME")
	private String searchFirstName;
	
	@Column(name="SPRIDEN_SEARCH_MI")
	private String searchMiddleInitial;
	
	@Column(name="SPRIDEN_SOUNDEX_LAST_NAME")
	private String soundexLastName;
	
	@Column(name="SPRIDEN_SOUNDEX_FIRST_NAME")
	private String soundexFirstName;
	
	@Column(name="SPRIDEN_NTYP_CODE")
	private String nTypCode;
	
	@Column(name="SPRIDEN_CREATE_USER")
	private String createUser;
	
	@Column(name="SPRIDEN_CREATE_DATE")
	private String createDate;
	
	@Column(name="SPRIDEN_DATA_ORIGIN")
	private String dataOrigin;
	
	@Column(name="SPRIDEN_CREATE_FDMN_CODE")
	private String fdmnCode;
	
	@Column(name="SPRIDEN_SURNAME_PREFIX")
	private String surnamePrefix;
	
	@Column(name="SPRIDEN_SURROGATE_ID")
	private int surrogateId;
	
	@Column(name="SPRIDEN_VERSION")
	private int version;
	
	@Column(name="SPRIDEN_USER_ID")
	private String userId;
	
	@Column(name="SPRIDEN_VPDI_CODE")
	private String vpdiCode;
}
