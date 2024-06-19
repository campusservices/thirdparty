package uwi.thirdparty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import uwi.thirdparty.dao.OracleDao;
import uwi.thirdparty.repository.TriggerListRepository;
import uwi.thirdparty.service.contract.ThirdPartyService;
import uwi.thirdparty.util.UtilityLogger;


@SpringBootApplication
@Configuration
@ComponentScan(basePackages ="uwi.thirdparty")
public class ThirdpartyidApplication {

    @Autowired
    ThirdPartyService service;
	
    @Autowired
    OracleDao dao;
    
	@Autowired
	TriggerListRepository repo;
	
	@Autowired
	UtilityLogger utilLogger;
	
	Logger logger = LoggerFactory.getLogger(ThirdpartyidApplication.class);
	 
	public static void main(String[] args) {
		SpringApplication.run(ThirdpartyidApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
//          dao.openConnection();
		};
	}
}
