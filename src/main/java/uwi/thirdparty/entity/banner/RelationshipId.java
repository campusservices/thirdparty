package uwi.thirdparty.entity.banner;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RelationshipId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long pidm;
	private String spridenId;
	private String lastName;
	private String firstName;
	private String middleInitial;
	private String changeIndicator;
}
