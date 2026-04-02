package com.orchestrator.TaskOrchestrator.Service;

import com.orchestrator.TaskOrchestrator.Model.Status;
import com.orchestrator.TaskOrchestrator.Repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrchestratorRecovery {
    private final TaskRepository repo;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void runPending(){
        log.info("System restart detected. Resetting 'RUNNING' tasks to 'PENDING'.");
        repo.updateStatus(Status.RUNNING,Status.PENDING);
    }
}
