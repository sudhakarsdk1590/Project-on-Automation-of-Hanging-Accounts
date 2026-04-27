package com.sudhakarproject.controller;

import com.sudhakarproject.dto.JobRequest;
import com.sudhakarproject.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping("/run")
    public ResponseEntity<String> run(@RequestBody JobRequest request) {

       String response = jobService.runJob(request);

        return ResponseEntity.ok(response);
    }
}
