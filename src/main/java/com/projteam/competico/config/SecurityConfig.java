package com.projteam.competico.config;

import static com.projteam.competico.domain.Account.ACTUATOR_ADMIN;
import static com.projteam.competico.domain.Account.SWAGGER_ADMIN;
import static com.projteam.competico.domain.Account.TASK_DATA_ADMIN;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.projteam.competico.service.AccountService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
	private DaoAuthenticationProvider authProvider;
	
	public SecurityConfig(@Lazy DaoAuthenticationProvider authProvider)
	{
		this.authProvider = authProvider;
	}
	
	@Override
	public void configure(HttpSecurity sec) throws Exception
	{
		sec
			.authorizeRequests()
			.antMatchers(
		                "/js/**",
		                "/css/**",
		                "/assets/**",
		                "/login",
		                "/register",
		                "/api/v1/login/",
		                "/api/v1/register/",
		                "/api/v1/authenticated/",
		                "/api/v1/forgotpassword",
		                "/api/v1/resetpassword/**",
		                "/api/v1/emailverification",
		                "/forgotpassword",
		                "/resetpassword/**",
		                "/verifyemail/**")
			.permitAll()
			.antMatchers("/actuator/**").hasAuthority(ACTUATOR_ADMIN)
			.antMatchers(
					"/swagger-ui/**",
					"/swagger-resources/**",
					"/webjars/**",
					"/v2/api-docs/**",
					"/v3/api-docs/**")
			.hasAuthority(SWAGGER_ADMIN)
			.antMatchers("/api/v1/tasks/**").hasAuthority(TASK_DATA_ADMIN)
			.anyRequest().authenticated()
			.and()
			.logout()
			.invalidateHttpSession(true)
			.clearAuthentication(true)
			.logoutUrl("/logout")
			.logoutSuccessUrl("/")
			.permitAll()
			.and()
			.exceptionHandling()
			.accessDeniedHandler((request, response, authException) ->
			{
				Optional.ofNullable(request.getSession(false))
					.ifPresent(session -> session.invalidate());
				SecurityContextHolder.clearContext();
				response.sendRedirect("/login");
			})
			.authenticationEntryPoint((request, response, authException) -> response.sendRedirect("/login"))
			.and()
			.csrf().disable(); //TODO remove and implement properly client-side
	}
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception
	{
		auth.authenticationProvider(authProvider);
	}
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception
	{
		return super.authenticationManagerBean();
	}
}
@Configuration
class PasswordEncoderConfig
{
	@Primary
	@Bean
	@Qualifier("bCryptPasswordEncoder")
	public PasswordEncoder encoder()
	{
		return new BCryptPasswordEncoder();
	}
}
@Configuration
class AuthenticationProviderConfig
{
	private AccountService accServ;
	private PasswordEncoder encoder;
	
	@Autowired
	public AuthenticationProviderConfig(AccountService accServ,
			@Qualifier("bCryptPasswordEncoder") PasswordEncoder encoder)
	{
		this.accServ = accServ;
		this.encoder = encoder;
	}
	
	@Bean
	public DaoAuthenticationProvider authProvider()
	{
		DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
		auth.setUserDetailsService(accServ);
		auth.setPasswordEncoder(encoder);
		return auth;
	}
}