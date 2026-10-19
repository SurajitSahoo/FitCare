package com.fitness.gateway;

import com.fitness.gateway.user.RegisterRequest;
import com.fitness.gateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.text.ParseException;

@Component
@Slf4j  // Because i will ad some logs
@RequiredArgsConstructor
public class keycloakUserSyncFilter implements WebFilter  {
    private final UserService userService;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID"); //Extracting userId from the header of JWT token
        String token = exchange.getRequest().getHeaders().getFirst("Authorization"); //Taking token from header
        RegisterRequest registerRequest = getUserDetails(token); // Mapping the token to the register request object
        if(userId==null)
        {
            userId = registerRequest.getKeycloakId();
        }
        if(userId != null && token != null)
        {
            String finalUserId = userId;
            return  userService.validateUser(userId).flatMap(exist -> {
                if(!exist){  //exist is comming from flatmap retur type
                    if(registerRequest != null ){
                        return userService.registerUser(registerRequest).then(Mono.empty());
                    }
                    else{
                        return  Mono.empty();
                    }
                }
                else{
                    log.info("User already exist");
                    return  Mono.empty();
                }
            }).then(Mono.defer(()->{
                ServerHttpRequest mutateRequest = exchange.getRequest().mutate().header("X-UserID", finalUserId).build();
                return chain.filter(exchange.mutate().request(( mutateRequest)).build());
            }));
        }
        return chain.filter(exchange);
    }

    private RegisterRequest getUserDetails(String token) {
        try{
            String tokenWithoutBearer = token.replace("Bearer","").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            //Now mappping each claims to register request dto
            RegisterRequest request = new RegisterRequest();
            request.setEmail(claims.getStringClaim("email"));
            request.setKeycloakId(claims.getStringClaim("sub"));
            request.setFirstName(claims.getStringClaim("given_name"));
            request.setLastName(claims.getStringClaim("family_name"));
            request.setPassword("dummy@123456");
            return request;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
