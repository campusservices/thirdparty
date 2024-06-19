package uwi.thirdparty.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import uwi.thirdparty.dto.AuthResponseDto;
import uwi.thirdparty.dto.LoginDto;
import uwi.thirdparty.dto.RegisterDto;
import uwi.thirdparty.entity.AuthConfig;
import uwi.thirdparty.entity.Credentials;
import uwi.thirdparty.entity.Role;
import uwi.thirdparty.entity.UserEntity;
import uwi.thirdparty.filter.CustomUserDetailsService;
import uwi.thirdparty.filter.JWTGenerator;
import uwi.thirdparty.repository.RoleRepository;
import uwi.thirdparty.repository.UserRepository;
import uwi.thirdparty.service.LDAPService;
import uwi.thirdparty.util.ResponseDetails;
import uwi.thirdparty.util.ResponseStatus;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthAPIController {
	
	private AuthenticationManager authenticationManager;
	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private PasswordEncoder passwordEncoder;
	private AuthConfig authConfig;
	private JWTGenerator jwtGenerator;
	private LDAPService ldapService;
	
	@Autowired
	public AuthAPIController(AuthenticationManager authenticationManager, UserRepository userRepository,
			RoleRepository roleRepository, PasswordEncoder passwordEncoder, JWTGenerator jwtGenerator,
			CustomUserDetailsService userDetailsService, AuthConfig authConfig, LDAPService ldapService) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtGenerator = jwtGenerator;
		this.authConfig = authConfig;
		this.ldapService = ldapService;
	}


	@PostMapping("login")
	public ResponseEntity<AuthResponseDto> login() {
		Authentication authentication  = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authConfig.getUsername(), authConfig.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtGenerator.generateToken(authentication);
		return new ResponseEntity<>(new AuthResponseDto(token), HttpStatus.OK);
	}
	
	@PostMapping("register")
	public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
		if (userRepository.existsByUsername(registerDto.getUsername())) {
			return new ResponseEntity<>("Username is taken!!", HttpStatus.BAD_REQUEST);
		}
		
		UserEntity user = new UserEntity();
		user.setUsername(registerDto.getUsername());
		user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
		
		
		Role role = roleRepository.findByName("USER").get();
		user.setRoles(Collections.singletonList(role));
		
		userRepository.save(user);
		
	    return new ResponseEntity<>("User Registered Success!", HttpStatus.OK);	
	}
	
}
