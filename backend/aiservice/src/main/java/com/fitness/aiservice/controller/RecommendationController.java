package com.fitness.aiservice.controller;

import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor //provide automatic constructor by lombok
@RequestMapping("/api/recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;
    @GetMapping("/user/{userId}")  //get ai recommendation by user id
    public ResponseEntity<List<Recommendation>> getUserRecommendation(@PathVariable String userId)
    {
        return ResponseEntity.ok(recommendationService.getUserRecommendation(userId));
    }
    @GetMapping("/activity/{activityId}") //get ai recommendation by activities
    public ResponseEntity<Recommendation>getActivityRecommendation(@PathVariable String activityId)
    {
        return ResponseEntity.ok(recommendationService.getActivityRecommendation(activityId));
    }
}
