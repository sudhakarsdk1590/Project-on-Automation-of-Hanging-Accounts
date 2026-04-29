package com.sudhakarproject.summarytracker;

import com.sudhakarproject.pojo.Transactions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;
@Slf4j
@Component
@StepScope
public class SummaryTracker {

    private AtomicLong total = new AtomicLong();
    private AtomicLong hanging = new AtomicLong();

    public void track(Transactions tx) {
        total.incrementAndGet();
        if (tx.getDpd() != null && tx.getDpd() > 30) {
            hanging.incrementAndGet();
        }
        if (total.get() % 1000 == 0) {
            log.info("Processed={}, Hanging={}", total.get(), hanging.get());
        }
    }

    public long getTotal() {
        return total.get();
    }

    public long getHanging() {
        return hanging.get();
    }
}
