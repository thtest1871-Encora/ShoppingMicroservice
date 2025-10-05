package com.example.MicroService;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	@Value("${user.username}") private String userName;
    @Value("${user.password}") private String userPass;

    @Value("${customer.username}") private String customerName;
    @Value("${customer.password}") private String customerPass;

    @Value("${admin.username}") private String adminName;
    @Value("${admin.password}") private String adminPass;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchanges -> exchanges
                // USER can view products
                .pathMatchers(HttpMethod.GET, "/productmicroservice/products/**").hasRole("USER")

                // CUSTOMER can add customer, place order, view orders by customer_id
                .pathMatchers("/customerservice/customers/**", "/ordersmicroservice/orders/customer/**").hasRole("CUSTOMER")
                .pathMatchers(HttpMethod.POST, "/ordersmicroservice/orders").hasRole("CUSTOMER")
                .pathMatchers(HttpMethod.DELETE, "/ordersmicroservice/orders/**").hasRole("CUSTOMER")
                .pathMatchers(HttpMethod.PUT, "/productmicroservice/products/**").hasRole("CUSTOMER")

                // ADMIN can add/delete products, manage orders, view all orders
                .pathMatchers(HttpMethod.POST, "/productmicroservice/products/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/productmicroservice/products/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.GET, "/ordersmicroservice/orders/**").hasRole("ADMIN")

                // All other routes require authentication
                .anyExchange().authenticated()
            )
            .httpBasic();

        return http.build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        UserDetails user = User.withUsername(userName)
                .password(encoder.encode(userPass))
                .roles("USER")
                .build();

        UserDetails customer = User.withUsername(customerName)
                .password(encoder.encode(customerPass))
                .roles("CUSTOMER", "USER") // inherits USER permissions
                .build();

        UserDetails admin = User.withUsername(adminName)
                .password(encoder.encode(adminPass))
                .roles("ADMIN", "CUSTOMER", "USER") // inherits CUSTOMER and USER
                .build();

        return new MapReactiveUserDetailsService(user, customer, admin);
    }
}
