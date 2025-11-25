package de.medmanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(getEncoder());

        return authProvider;
    }

    // @Bean
    // PathPatternRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
    //     return new PathPatternRequestMatcher.Builder(introspector);
    // }

    @Bean
    PathPatternRequestMatcher.Builder requestMatcherBuilder(PathPatternParser mvcPatternParser, DispatcherServletPath servletPath) {
        PathPatternRequestMatcher.Builder builder = PathPatternRequestMatcher.withPathPatternParser(mvcPatternParser);
        String path = servletPath.getPath();
        return ("/".equals(path)) ? builder : builder.basePath(path);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, PathPatternRequestMatcher.Builder mvc) throws Exception {
        http.authenticationProvider(authenticationProvider());
        http.authorizeHttpRequests((authz) -> {
                        authz
                            .requestMatchers(mvc.matcher("/showUsers")).hasRole("ADMIN")
                            .anyRequest().authenticated();
                })
                .formLogin(formLogin ->
                        formLogin.loginPage("/login").permitAll())
                //.and()
                .logout(logout ->
                        logout.logoutUrl("/signout")
                                .logoutSuccessHandler(logoutSuccessHandler())
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID")
                                .permitAll());
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(PathPatternRequestMatcher.Builder mvc) {
        return (web) -> web.ignoring().requestMatchers(mvc.matcher("/styles/**"));
    }

    @Bean
    public BCryptPasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new CustomLogoutSuccessHandler();
    }

}
