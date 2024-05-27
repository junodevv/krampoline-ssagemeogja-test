package project.back.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import project.back.etc.aboutlogin.EntryPointHandler;
import project.back.etc.aboutlogin.JwtFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    private final JwtFilter jwtFilter;
    private final EntryPointHandler entryPointHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(c->c.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize)->{
                    authorize//.requestMatchers("/api1","/code/**","/mypage/**").hasRole("user")
                            //.requestMatchers("/login/**","/","/test/**","/reqlogin/**",tokenuri,userinfouri,"http://krmp-proxy.9rum.cc:3128/**","/logouts").permitAll()
                            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                            .requestMatchers("/test").hasRole("user")
                            .anyRequest().permitAll();

                })
                .sessionManagement((session)->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                /*.oauth2Login((login)->login
                        .loginPage("/login")

                        .userInfoEndpoint(c->c.userService(customOatuh2Service))
                        .successHandler(sucessHandler)
                        .failureHandler(failHandler)
                )*/
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(c->c.authenticationEntryPoint(entryPointHandler)
                        /*.accessDeniedHandler(authenHandler)*/)

                .build();



    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("https://k56733b335962a.user-app.krampoline.com");
        configuration.addAllowedOrigin("https://k9bceeba41403a.user-app.krampoline.com");
        configuration.addAllowedOrigin("wss://k9bceeba41403a.user-app.krampoline.com");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
