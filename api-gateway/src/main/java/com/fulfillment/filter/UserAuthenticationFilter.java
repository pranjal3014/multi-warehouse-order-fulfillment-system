package com.fulfillment.filter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserAuthenticationFilter extends OncePerRequestFilter {

    private final RestClient restClient;

    public UserAuthenticationFilter(
            @Value("${user.service.url:http://localhost:8083/graphql}") String userServiceUrl) {

        this.restClient = RestClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String query = """
                query($token: String!) {
                  validateToken(token: $token) {
                    userEmail
                    role
                  }
                }
                """;

            Map<String, Object> body = Map.of(
                    "query", query,
                    "variables", Map.of("token", authorization.substring(7)));

            Map<String, Object> graphqlResponse = restClient.post()
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            if (graphqlResponse.containsKey("errors")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Map<String, Object> data = (Map<String, Object>) graphqlResponse.get("data");
            Map<String, Object> user = (Map<String, Object>) data.get("validateToken");
            String userEmail = user.get("userEmail").toString();
            String role = user.get("role").toString();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userEmail,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role)));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
