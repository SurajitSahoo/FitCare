package com.fitness.gateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.ChannelPipelineConfigurer;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final WebClient userServiceWebClient;
    public Mono<Boolean> validateUser(String userId){  //MONO aloows us to deal asynchronious way
        log.info("Calling User Service for {}", userId);
            return userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate",userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .onErrorResume(WebClientResponseException.class, e ->{
                        if(e.getStatusCode() == HttpStatus.NOT_FOUND){
                            return Mono.error(new RuntimeException("User Not found"+ userId));}
                        else if (e.getStatusCode()==HttpStatus.BAD_REQUEST) {
                            return Mono.error(new RuntimeException("Invalid Reqest"+ userId));
                        }
                        return Mono.error(new RuntimeException("Unexpected Error"+ userId));
                    });
    }

    public Mono<UserResponse> registerUser(RegisterRequest registerRequest) {
        log.info("Calling user registration api{}",registerRequest.getEmail());
        return userServiceWebClient.post()
                .uri("/api/users/register")
                .bodyValue(registerRequest)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, e ->{
                    if (e.getStatusCode()==HttpStatus.BAD_REQUEST) {
                        return Mono.error(new RuntimeException("Bad Request"+ e.getMessage()));
                    }
                    return Mono.error(new RuntimeException("Unexpected Error"+ e.getMessage()));
                });
    }
}
