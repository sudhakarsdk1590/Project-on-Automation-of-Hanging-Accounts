package com.sudhakarproject.stepsummarylistener;

import com.sudhakarproject.summarytracker.SummaryTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
public class StepSummaryListener implements StepExecutionListener {
    private final SummaryTracker tracker;

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        System.out.println("FINAL TOTAL: " + tracker.getTotal());   // 🔥 debug
        System.out.println("FINAL HANGING: " + tracker.getHanging());

        stepExecution.getExecutionContext().put("total", tracker.getTotal());
        stepExecution.getExecutionContext().put("hanging", tracker.getHanging());

        return ExitStatus.COMPLETED;
    }
}
