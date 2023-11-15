package uwi.thirdparty.service;

import javax.naming.NamingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class LDAPService {

	Logger logger = LoggerFactory.getLogger(LDAPService.class);
	
    public  Boolean authenticate(String username, String password) throws NamingException {
		return ActiveDirectory.getConnection(username, password, "cavehill.uwi.edu","cavehillsrv1");	
	} 

}
