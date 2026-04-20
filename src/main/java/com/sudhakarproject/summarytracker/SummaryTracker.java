package com.sudhakarproject.summarytracker;

import com.sudhakarproject.pojo.Transactions;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
@StepScope
public class SummaryTracker {

    private AtomicLong total = new AtomicLong();
    private AtomicLong hanging = new AtomicLong();

    public void track(Transactions transactions) {
        total.incrementAndGet();
        if ("HANGING".equals(transactions.getStatus())) {
            hanging.incrementAndGet();
        }
    }

    public long getTotal() {
        return total.get();
    }

    public long getHanging() {
        return hanging.get();
    }
}
