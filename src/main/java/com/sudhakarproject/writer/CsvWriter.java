package com.sudhakarproject.writer;

import com.sudhakarproject.pojo.Transactions;
import jakarta.annotation.PostConstruct;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

@Component
@StepScope
public class CsvWriter implements ItemWriter<Transactions> {


    @Value("#{jobParameters['jobId']}")
    private Long jobId;

    private BufferedWriter writer;


    @PostConstruct
    public void init() throws Exception {
        File file = new File("output/job_" + jobId + ".csv");
        file.getParentFile().mkdirs();

        writer = new BufferedWriter(new FileWriter(file));
        writer.write("ID,ACCOUNT_ID,CLIENT_ID,AMOUNT,STATUS,RETRY\n");
    }
    @Override
    public void write(Chunk<? extends Transactions> items) throws Exception {
        for (Transactions t : items) {
            writer.write(
                    t.getId() + "," +
                            t.getAccountId() + "," +
                            t.getClientId() + "," +
                            t.getAmount() + "," +
                            t.getStatus() + "," +
                            t.getRetryCount() + "\n"
            );
        }
    }
}
