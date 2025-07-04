package e_learning.enrollment_service.security;
import e_learning.enrollment_service.jwt.JwtRequestFilter;
import e_learning.enrollment_service.jwt.JwtUtils;
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

    //  إضافة UserDetailsService لـ Basic Auth الداخلي
    @Bean
    public UserDetailsService userDetailsService() {
        // هذا المستخدم هو فقط لـ Basic Auth الداخلي (من AssessmentService)
        UserDetails internalAssessmentService = User.builder()
                .username("internal_assessment_service")
                .password(passwordEncoder().encode("assessment_pass"))
                .roles("INTERNAL_SERVICE")
                .build();

        // يمكن إضافة مستخدم EnrollmentService الداخلي هنا أيضاً لجعله أكثر شمولاً
        // UserDetails internalEnrollmentService = User.builder()
        //         .username("internal_enrollment_service")
        //         .password(passwordEncoder().encode("enrollment_pass"))
        //         .roles("INTERNAL_SERVICE")
        //         .build();

        return new InMemoryUserDetailsManager(internalAssessmentService); // أو كل المستخدمين الداخليين
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
                        .requestMatchers("/enrollments/public").permitAll()
                        .requestMatchers("/enrollments/admin/**").hasRole("ADMIN")
                        .requestMatchers("/enrollments/student/**").hasAnyRole("STUDENT", "INTERNAL_SERVICE") //  السماح لـ INTERNAL_SERVICE
                        .requestMatchers("/enrollments/instructor/**").hasRole("INSTRUCTOR")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider) //  إضافة هذا للـ Basic Auth
                .httpBasic(Customizer.withDefaults()); //  السماح بالـ Basic Auth

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}