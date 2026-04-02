package com.orchestrator.TaskOrchestrator.Service;

import com.orchestrator.TaskOrchestrator.DTO.TaskRequest;
import com.orchestrator.TaskOrchestrator.Model.*;
import com.orchestrator.TaskOrchestrator.Repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskPersistenceService {
    private final TaskRepository repo;

    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void checkAndUpdateStuckTasks(){
        int stuckCount = repo.updateStuckTasks(Status.RUNNING,Status.PENDING, LocalDateTime.now().minusMinutes(30));
        log.warn("Found {} tasks stuck. Trying to run them again",stuckCount);
    }

    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void updateToFailed(){
        int failedCount = repo.failTasks(Status.PENDING,Status.FAILED,3);
        log.error("Found {} tasks exceed retry limit and marked them as FAILED",failedCount);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Task> updatePending(){
        Pageable firstPageWithTenElements = PageRequest.of(0,10, Sort.by("priority").descending());
        List<Task> tasks =  repo.findByStatusAndRetryCountLessThanOrderByPriorityDesc(Status.PENDING,4,firstPageWithTenElements);
        tasks.forEach(task -> {
            task.setStatus(Status.RUNNING);
            task.setRetryCount(task.getRetryCount() + 1);
            log.info("Sending task with id {} to run",task.getId());
        });
        return tasks;
    }

    public void createTask(TaskRequest tr){
        String type = tr.getType();
        Task t;
        if("EMAIL".equals(type)){
            t = new EmailTask();
        }
        else if("DATABASE".equals(type)){
            t = new DatabaseTask();
        }
        else{
            t = new CodegenTask();
        }
        t.setPriority(tr.getPriority());
        t.setPayload(tr.getPayload());
        addTask(t);
    }

    public void addTask(Task t){
        repo.save(t);
    }
}
