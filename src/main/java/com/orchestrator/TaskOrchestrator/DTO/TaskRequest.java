package com.orchestrator.TaskOrchestrator.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskRequest {
    private String type;
    private int priority;
    private String payload;
}
