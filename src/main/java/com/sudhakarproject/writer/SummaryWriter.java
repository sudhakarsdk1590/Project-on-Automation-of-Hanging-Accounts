package com.sudhakarproject.writer;

import com.sudhakarproject.summarytracker.SummaryTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

@Component
@RequiredArgsConstructor
public class SummaryWriter {

    public void write(JobExecution jobExecution,long total, long hanging) throws Exception {

        Long jobId = jobExecution.getJobParameters().getLong("jobId");

        File dir = new File("output");
        if (!dir.exists()) dir.mkdirs();

        BufferedWriter writer = new BufferedWriter(
                new FileWriter("output/job_" + jobId + "_summary.txt")
        );

        writer.write("Total: " + total + "\n");
        writer.write("Hanging: " + hanging + "\n");

        writer.close();
    }
}
