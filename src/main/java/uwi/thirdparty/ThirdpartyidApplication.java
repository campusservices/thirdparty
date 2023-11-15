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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
	TriggerListRepository repo;
	
	@Autowired
	UtilityLogger utilLogger;
	
	Logger logger = LoggerFactory.getLogger(ThirdpartyidApplication.class);
	 
	public static void main(String[] args) {
		SpringApplication.run(ThirdpartyidApplication.class, args);
	}

//	@Bean
//	public WebMvcConfigurer corsConfigurer() {
//		return new WebMvcConfigurer() {
//			@Override
//			public void addCorsMappings(CorsRegistry registry) {
//				registry.addMapping("/thirdparty/uploadFile").allowedOrigins("https://thirdparty.cavehill.uwi.edu");
//			}
//		};
//	}
	
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
//			service.alterTrigger("disable");
		};
	}
}
