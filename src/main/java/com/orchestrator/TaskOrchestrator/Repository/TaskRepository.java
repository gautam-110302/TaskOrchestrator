package com.orchestrator.TaskOrchestrator.Repository;


import com.orchestrator.TaskOrchestrator.Model.Status;
import com.orchestrator.TaskOrchestrator.Model.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,String> {

    @Modifying
    @Query("UPDATE Task t SET t.status = :newStatus WHERE t.status = :oldStatus")
    public void updateStatus(@Param("oldStatus") Status oldStatus, @Param("newStatus") Status newStatus);

    @Modifying
    @Query("UPDATE Task t SET t.status = :newStatus WHERE t.status = :oldStatus AND updatedAt <= :threshold")
    public int updateStuckTasks(@Param("oldStatus") Status oldStatus,
                                 @Param("newStatus") Status newStatus,
                                 @Param("threshold") LocalDateTime threshold);

    @Modifying
    @Query("UPDATE Task t SET t.status = :newStatus WHERE t.status = :oldStatus AND retryCount > :threshold")
    public int failTasks(@Param("oldStatus") Status oldStatus,
                                 @Param("newStatus") Status newStatus,
                                 @Param("threshold") int threshold);


    public List<Task> findByStatusAndRetryCountLessThanOrderByPriorityDesc(Status status,int count, Pageable pageable);
}
