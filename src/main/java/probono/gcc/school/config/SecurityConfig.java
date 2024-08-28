package probono.gcc.school.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import probono.gcc.school.jwt.JWTFilter;
import probono.gcc.school.jwt.JWTUtil;
import probono.gcc.school.jwt.LoginFilter;
import probono.gcc.school.model.enums.Role;
import probono.gcc.school.model.enums.Status;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
  private final AuthenticationConfiguration authenticationConfiguration;
  private final JWTUtil jwtUtil;


  //AuthenticationManager Bean 등록
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {

    return configuration.getAuthenticationManager();
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {

    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        .cors((cors) -> cors
            .configurationSource(new CorsConfigurationSource() {
              @Override
              public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(
                    Arrays.asList("http://localhost:3000", "http://52.78.104.115"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
                configuration.setAllowCredentials(true);
                configuration.setAllowedHeaders(Collections.singletonList("*"));
                configuration.setMaxAge(3600L);

                configuration.setExposedHeaders(Collections.singletonList("Authorization"));
                return configuration;
              }
            })
        );

    //csrf disable
    http
        .csrf((auth) -> auth.disable());

    //Form 로그인 방식 disable
    http
        .formLogin((auth) -> auth.disable());
    //http basic 인증 방식 disable
    http
        .httpBasic((auth) -> auth.disable());

    //경로별 인가 작업
    http
        .authorizeHttpRequests((auth) -> auth

            .requestMatchers("/login", "/", "/swagger-ui/**", "/v3/api-docs/**", "/test", "/test2",
                "/users/**", "/checkPwAnswer/**", "/resetPassword/**", "/students/migration")
            .permitAll()
//            .requestMatchers("/admin").hasRole("ADMIN")
//            .requestMatchers("/teachers").hasAnyRole("TEACHER", "ADMIN")
//            .requestMatchers("/students").hasRole("STUDENT")
            .anyRequest().authenticated());

    //필터 추가 LoginFilter()는 인자를 받음 (AuthenticationManager() 메소드에 authenticationConfiguration 객체를 넣어야 함) 따라서 등록 필요
    http
        .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil),
            UsernamePasswordAuthenticationFilter.class); // addFilterAt은 기존 필터자리에 새로운필터 등록(새로등록할 필터, 기존 필터)

    http
        .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

    //세션 설정
    http
        .sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    return http.build();
  }
}
