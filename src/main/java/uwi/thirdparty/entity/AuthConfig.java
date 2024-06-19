package uwi.thirdparty.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;

@Service
@ConfigurationProperties(prefix = "tp.auth")
@Getter @Setter
public class AuthConfig {
	private String username;
	private String password;
}
