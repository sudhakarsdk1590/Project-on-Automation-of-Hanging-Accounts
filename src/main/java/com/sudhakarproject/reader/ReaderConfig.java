package com.sudhakarproject.reader;

import com.sudhakarproject.pojo.Transactions;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.database.JdbcPagingItemReader;
import org.springframework.batch.infrastructure.item.database.Order;
import org.springframework.batch.infrastructure.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.infrastructure.item.database.support.OraclePagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class ReaderConfig {
    @Bean
    @StepScope
    public JdbcPagingItemReader<Transactions> reader(
            DataSource dataSource,
            @Value("#{jobParameters['clientIds']}") String clientIds,
            @Value("#{jobParameters['accountType']}") String accountType,
            @Value("#{jobParameters['lastUpdatedBefore']}") String lastUpdatedBefore)
            throws Exception {

        if (clientIds == null || clientIds.isEmpty()) {
            throw new IllegalArgumentException("clientIds is required");
        }
        String inClause = Arrays.stream(clientIds.split(","))
                .map(id -> "'" + id + "'")
                .collect(Collectors.joining(","));

        OraclePagingQueryProvider queryProvider = new OraclePagingQueryProvider();
        queryProvider.setSelectClause("SELECT ID, ACCOUNT_ID, CLIENT_ID, ACCOUNT_TYPE, AMOUNT, STATUS, RETRY_COUNT, LAST_UPDATED_DATE, DPD");
        queryProvider.setFromClause("FROM GDS_TRANSACTIONS");
        queryProvider.setWhereClause( "WHERE CLIENT_ID IN (" + inClause + ") " +
                "AND ACCOUNT_TYPE IN (:accountTypes) " +
                "AND LAST_UPDATED_DATE < :lastUpdatedBefore");

        Map<String, Order> sort = new HashMap<>();
        sort.put("ID", Order.ASCENDING);
        queryProvider.setSortKeys(sort);

        Map<String, Object> params = new HashMap<>();
        params.put("lastUpdatedBefore", java.sql.Date.valueOf(lastUpdatedBefore));
        params.put("accountTypes", List.of(accountType));


        return new JdbcPagingItemReaderBuilder<Transactions>()
                .name("oracle-reader")
                .dataSource(dataSource)
                .pageSize(1000)
                .queryProvider(queryProvider)
                .parameterValues(params)
                .rowMapper((rs, rowNum) -> {
                    Transactions t = new Transactions();
                    t.setId(rs.getLong("ID"));
                    t.setAccountId(rs.getString("ACCOUNT_ID"));
                    t.setClientId(rs.getString("CLIENT_ID"));
                    t.setAccountType(rs.getString("ACCOUNT_TYPE"));
                    t.setAmount(rs.getDouble("AMOUNT"));
                    t.setStatus(rs.getString("STATUS"));
                    t.setRetryCount(rs.getInt("RETRY_COUNT"));
                    t.setLastUpdatedDate(rs.getDate("LAST_UPDATED_DATE"));
                    return t;
                })
                .build();
    }
}
