package br.com.jogadoresanonimos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

// Autenticação é 100% custom via JWT (ver security/JwtAuthenticationFilter e auth/service/AuthService);
// nunca usamos UserDetailsService/AuthenticationManager, então excluímos o autoconfig que gera
// um InMemoryUserDetailsManager com senha aleatória a cada boot (WARN de segurança e bean morto/não intencional).
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@EnableScheduling
@EnableAsync
public class JogadoresAnonimosApplication {

	public static void main(String[] args) {
		SpringApplication.run(JogadoresAnonimosApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
