package com.orchestrator.TaskOrchestrator.Controller;

import com.orchestrator.TaskOrchestrator.DTO.TaskRequest;
import com.orchestrator.TaskOrchestrator.Service.TaskPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TaskController {
    private final TaskPersistenceService service;

    @PostMapping("/add")
    public ResponseEntity<TaskRequest> addTask(@RequestBody TaskRequest tr){
        service.createTask(tr);
        return new ResponseEntity<>(tr, HttpStatus.CREATED);
    }
}
