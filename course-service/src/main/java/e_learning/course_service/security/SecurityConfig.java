package e_learning.course_service.security;
import e_learning.course_service.service.jwt.JwtRequestFilter;
import e_learning.course_service.service.jwt.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter(JwtUtils jwtUtils) {
        return new JwtRequestFilter(jwtUtils);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // هذا المستخدم هو فقط لـ Basic Auth الداخلي (من AssessmentService و EnrollmentService)
        UserDetails internalAssessmentService = User.builder()
                .username("internal_assessment_service")
                .password(passwordEncoder().encode("assessment_pass"))
                .roles("INTERNAL_SERVICE")
                .build();

        UserDetails internalEnrollmentService = User.builder()
                .username("internal_enrollment_service")
                .password(passwordEncoder().encode("enrollment_pass"))
                .roles("INTERNAL_SERVICE")
                .build();

        return new InMemoryUserDetailsManager(internalAssessmentService, internalEnrollmentService);
    }

    //  إضافة DaoAuthenticationProvider للسماح بالـ Basic Auth
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    //  إضافة AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter, DaoAuthenticationProvider authenticationProvider) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/courses/public", "/courses/approved").permitAll()
                        .requestMatchers("/courses/admin/**").hasRole("ADMIN")
                        .requestMatchers("/courses").hasAnyRole("ADMIN", "INSTRUCTOR", "STUDENT", "INTERNAL_SERVICE")
                        .requestMatchers("/courses/**").hasAnyRole("ADMIN", "INSTRUCTOR", "STUDENT", "INTERNAL_SERVICE")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider) //  إضافة هذا للـ Basic Auth
                .httpBasic(Customizer.withDefaults()); //  السماح بالـ Basic Auth

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}