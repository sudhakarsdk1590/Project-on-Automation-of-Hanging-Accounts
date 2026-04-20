package com.sudhakarproject.processor;

import com.sudhakarproject.pojo.Transactions;
import com.sudhakarproject.summarytracker.SummaryTracker;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@RequiredArgsConstructor
public class TransactionProcessor implements ItemProcessor<Transactions,Transactions> {

    private final SummaryTracker tracker;
    @Override
    public @Nullable Transactions process(Transactions item) throws Exception {

        if (item.getDpd() != null && item.getDpd() > 30) {
            item.setStatus("HIGH_RISK");
        } else {
            item.setStatus("LOW_RISK");
        }

        item.setRetryCount(item.getRetryCount() + 1);
        tracker.track(item);
        return item;
    }
}
