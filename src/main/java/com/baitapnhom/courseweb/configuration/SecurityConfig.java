package com.baitapnhom.courseweb.configuration;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.baitapnhom.courseweb.enums.Role;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final String[] PUBLIC_POST_ENDPOINTS = {
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/introspect",
            "/api/v1/progress/update"
    };

    private final String[] PUBLIC_GET_ENDPOINTS = {
            "/api/courses",
            "/api/courses/**",
            "/api/categories",
            "/api/categories/**",
             "/api/lessons/**"
    };

    @Value("${jwt.signerKey}")
    private String signerKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeHttpRequests(request -> request
                // 1. Cho phép POST các endpoint auth, register...
                .requestMatchers(HttpMethod.POST, PUBLIC_POST_ENDPOINTS).permitAll()

                // 2. Cho phép mọi người XEM khóa học và danh mục không cần Token
                .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()

                // 3. Phân quyền User
                // .requestMatchers(HttpMethod.GET, "/api/users").hasRole(Role.ADMIN.name())
                // .requestMatchers(HttpMethod.GET, "/api/users/my-info").authenticated()
                // .requestMatchers(HttpMethod.GET, "/api/users/{userId}").hasRole(Role.ADMIN.name())

                // 4. Tất cả các request còn lại phải có token xác thực
                .anyRequest().authenticated());

        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())
                .jwtAuthenticationConverter(jwtAuthenticationConverter())));

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    JwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");

        return NimbusJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
