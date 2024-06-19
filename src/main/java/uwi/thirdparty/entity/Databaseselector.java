package uwi.thirdparty.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="Databaseselector")
@JsonDeserialize
@Getter @Setter
public class Databaseselector {
  
	@Id
	 @GeneratedValue(
	         strategy = GenerationType.IDENTITY
	 )
	 int id;
	
	private String name;
	
}
