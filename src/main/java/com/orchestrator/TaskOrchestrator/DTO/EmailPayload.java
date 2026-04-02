package com.orchestrator.TaskOrchestrator.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailPayload {
    private String recipient;
    private String subject;
    private String body;
}
