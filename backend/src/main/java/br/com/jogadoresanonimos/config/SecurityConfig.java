package br.com.jogadoresanonimos.config;

import br.com.jogadoresanonimos.security.JwtAuthenticationFilter;
import br.com.jogadoresanonimos.security.JwtService;
import br.com.jogadoresanonimos.security.repository.JwtDenylistRepository;
import br.com.jogadoresanonimos.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final JwtDenylistRepository jwtDenylistRepository;
    private final List<String> allowedOrigins;

    public SecurityConfig(JwtService jwtService, UsuarioRepository usuarioRepository,
                           JwtDenylistRepository jwtDenylistRepository,
                           @Value("${app.cors.allowed-origins}") List<String> allowedOrigins) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.jwtDenylistRepository = jwtDenylistRepository;
        this.allowedOrigins = allowedOrigins;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").authenticated()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/cms/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/arquivos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/mural-de-apoio").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/relatos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/relatos/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/avisos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/escritorios-regionais").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/unidades-federativas").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/reunioes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/reunioes-online").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/hotlines").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/reflexao-do-dia/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/faq").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/vinte-perguntas").permitAll()
                        .requestMatchers(HttpMethod.GET, "/stub-storage/**").permitAll()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, usuarioRepository, jwtDenylistRepository),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
