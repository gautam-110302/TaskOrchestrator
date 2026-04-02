package com.orchestrator.TaskOrchestrator.Service;

import com.orchestrator.TaskOrchestrator.Model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskExecuteService {
    private final TaskPersistenceService taskPersistenceService;
    private final TaskRunner runner;

    @Scheduled(fixedDelay = 5000)
    public void runPending(){
        List<Task> tasks = taskPersistenceService.updatePending();
        LocalDateTime now = LocalDateTime.now();
        log.info("Started running {} tasks at {}",tasks.size(),now);
        tasks.forEach(runner::runTaskAsync);
    }

}
