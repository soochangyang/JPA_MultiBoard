package scyang.mutilboard.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import scyang.mutilboard.global.error.CustomAuthenticationEntryPoint;
import scyang.mutilboard.global.jwt.JwtFilter;
import scyang.mutilboard.global.jwt.JwtTokenProvider;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    //Inject JwtTokenProvider
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;


    @Bean
    public PasswordEncoder passwordEncoder(){
        //
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                // Disable CSRF for REST API
                .csrf(csrf -> csrf.disable())
                // Disable Form Login and Basic Auth
                .formLogin(form -> form.disable())
                .httpBasic(basic ->basic.disable())
                // Set session management to stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth -> auth
                                //Permit all auth APIs
                                .requestMatchers("/api/auth/**").permitAll()
                                // Restrict to authenticated users
                                .requestMatchers(
                                        "/api/boards/**",
                                        "/api/comment/**",
                                        "/api/member/**",
                                        "/api/post/**"
                                ).authenticated()
                                .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        // Set custom class to handle 401 Authentication failures
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                // Add custom JwtFilter
                .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

                return http.build();
    }
}
