package uwi.thirdparty.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;

@Service
@ConfigurationProperties(prefix = "oracle.datastring")
@Getter @Setter
public class DatabaseConfig {
  private String prod;
  private String test;
  private String trng;
}

