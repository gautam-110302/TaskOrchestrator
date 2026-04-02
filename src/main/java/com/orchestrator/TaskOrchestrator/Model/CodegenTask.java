package com.orchestrator.TaskOrchestrator.Model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.springframework.context.ApplicationContext;

import java.util.Random;

@Entity
@DiscriminatorValue("codegen")
public class CodegenTask extends Task{

    @Override
    public void execute(ApplicationContext applicationContext){
        Random rand = new Random();
        if (rand.nextBoolean()) { // 50% chance of failure
            throw new RuntimeException("Service Unavailable for Task " + this.getId());
        }
        System.out.println("Running Code Generation task with priority " + this.getPriority());
    }
}
