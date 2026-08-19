package com.fitness.Activity_Service.controller;

import com.fitness.Activity_Service.dto.ActivityRequest;
import com.fitness.Activity_Service.dto.ActivityResponse;
import com.fitness.Activity_Service.service.ActivityService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor  //used to inject activity service here in this controller
public class ActivityController {
    private ActivityService activityService;
    @PostMapping()
    public ResponseEntity<ActivityResponse> trackActivity (@RequestBody ActivityRequest request)
    {
        return  ResponseEntity.ok(activityService.trackActivity(request));
    }
}
