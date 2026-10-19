package com.fitness.Activity_Service.service;

import com.fitness.Activity_Service.dto.ActivityRequest;
import com.fitness.Activity_Service.dto.ActivityResponse;
import com.fitness.Activity_Service.model.Activity;
import com.fitness.Activity_Service.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;
    private final KafkaTemplate<String,Activity> kafkaTemplate;
    @Value("${kafka.topic.name}")
    private String topicName;
    public ActivityResponse trackActivity(ActivityRequest request) {
        // Check whether the user is valid or not
        boolean isValidUser=userValidationService.validateUser(request.getUserId());
        if(!isValidUser)
        {
            throw new RuntimeException("Invalid User"+request.getUserId());
        }
        //If user valided then create a activity object and save it to the db
        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .additionalMetrics(request.getAdditionalMetrics())
                .startTime(request.getStartTime())
                .build();

        Activity savedActivity = activityRepository.save(activity);
        // Now this actitvity data will be sent to the ai model through kafka for generate recommendation
        try{
            kafkaTemplate.send(topicName,savedActivity.getUserId(),savedActivity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapToResponse(savedActivity);
    }

    private ActivityResponse mapToResponse(Activity activity) {

        ActivityResponse response = new ActivityResponse();

        response.setId(activity.getId());
        response.setUserId(activity.getUserId());
        response.setType(activity.getType());
        response.setDuration(activity.getDuration());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setStartTime(activity.getStartTime());
        response.setCreatedAt(activity.getCreatedAt());
        response.setAdditionalMetrics(activity.getAdditionalMetrics());
        response.setUpdatedAt(activity.getUpdatedAt());

        return response;
    }
}