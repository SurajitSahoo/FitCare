package com.fitness.Activity_Service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

@Service
@RequiredArgsConstructor
public class UserValidationService {
    private final WebClient userServiceWebClient;
    public Boolean validateUser(String userId){
        try{
            return userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate",userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        }catch (WebClientRequestException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
