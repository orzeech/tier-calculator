package pl.orzechsoft.tiercalculator.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@EnableWebFluxSecurity
public class SecurityConfig {

  @Bean
  SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    http.authorizeExchange(exchanges -> exchanges
            .pathMatchers(HttpMethod.GET, "/customer/**").hasAuthority("SCOPE_customer:read")
            .pathMatchers(HttpMethod.POST, "/order/**").hasAuthority("SCOPE_order:report")
            .pathMatchers(HttpMethod.POST, "/admin/**").hasAuthority("SCOPE_admin")
            .pathMatchers(HttpMethod.GET, "/v3/**").permitAll()
            .anyExchange().authenticated())
        .csrf().disable()
        .cors().configurationSource(corsConfigurationSource()).and()
        .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(withDefaults()));
    return http.build();
  }
  
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://localhost:3001", "http://localhost:3000"));
    configuration.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
