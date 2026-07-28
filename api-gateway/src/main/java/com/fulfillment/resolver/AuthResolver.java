package com.fulfillment.resolver;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClient;

import com.fulfillment.dto.LoginRequest;
import com.fulfillment.dto.LoginResponse;
import com.fulfillment.service.JwtService;

@Controller
public class AuthResolver {

    private final JwtService jwtService;

    private final RestClient restClient;

    public AuthResolver(
            JwtService jwtService,
            @Value("${user.service.url:http://localhost:8083/graphql}") String userServiceUrl) {

        this.jwtService = jwtService;
        this.restClient = RestClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }

    @MutationMapping
    @SuppressWarnings("unchecked")
    public LoginResponse login(@Argument LoginRequest request) {

        String mutation = """
            mutation($request: LoginInput!) {
              authenticateUser(request: $request) {
                userId
                userName
                userEmail
                role
              }
            }
            """;

        Map<String, Object> body = Map.of(
                "query", mutation,
                "variables", Map.of("request", request));

        Map<String, Object> response = restClient.post()
                .body(body)
                .retrieve()
                .body(Map.class);

        if (response.containsKey("errors")) {
            throw new IllegalArgumentException("Invalid user credentials");
        }

        Map<String, Object> data = (Map<String, Object>) response.get("data");
        Map<String, Object> user = (Map<String, Object>) data.get("authenticateUser");

        Long userId = Long.valueOf(user.get("userId").toString());
        String userEmail = user.get("userEmail").toString();
        String role = user.get("role").toString();

        return new LoginResponse(
                jwtService.generateToken(userId, userEmail, role),
                userId,
                user.get("userName").toString(),
                userEmail,
                role);
    }
}
