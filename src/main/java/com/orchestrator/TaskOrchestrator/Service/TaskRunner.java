package com.orchestrator.TaskOrchestrator.Service;

import com.orchestrator.TaskOrchestrator.Model.Status;
import com.orchestrator.TaskOrchestrator.Model.Task;
import com.orchestrator.TaskOrchestrator.Repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskRunner {
    private final TaskRepository repo;
    private final ApplicationContext applicationContext;
    @Async
    public void runTaskAsync(Task t){
        log.info("Started executing task with id: {}, priority: {} and type: {}",t.getId(),t.getPriority(),t.getClass().getSimpleName());
        try{
            Thread.sleep(10000);
            t.execute(applicationContext);
            t.setStatus(Status.COMPLETED);
            repo.save(t);
        }
        catch(InterruptedException e){
            log.error("The thread was interrupted hence task with id {} failed.",t.getId(),e);
            t.setStatus(Status.PENDING);
            repo.save(t);
        }
        catch(RuntimeException e){
            log.error("Failed to complete the task with id {}",t.getId(),e);
            t.setStatus(Status.PENDING);
            repo.save(t);
        }

    }
}
