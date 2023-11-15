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
@Table(name="oracletrigger")
@JsonDeserialize
@Getter @Setter
public class Oracletrigger {
	
	 @Id
	 @GeneratedValue(
	         strategy = GenerationType.IDENTITY
	 )
	 int id;
	 
	 private String user;
	 private String name;
	 
}
