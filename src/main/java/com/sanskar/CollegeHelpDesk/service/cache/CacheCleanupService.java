package com.sanskar.CollegeHelpDesk.service.cache;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheCleanupService {
    /**
     * Unlike redis vector store, elasticsearch doesn't have a built-in TTL mechanism for documents.
     * So we need to implement our own cleanup mechanism to remove old cache entries.
     */
    private final ElasticsearchClient elasticsearchClient;

    @Scheduled(cron = "0 0 * * * *") // every 1 hour
    public void cleanOldCache(){

        try{
            Instant expiry = Instant.now().minus(Duration.ofHours(24));

            elasticsearchClient.deleteByQuery(d -> d
                    .index("semantic_cache")
                    .query(q -> q
                            .range(r -> r
                                    .date(dr -> dr
                                            .field("createdAt")
                                            .lt(expiry.toString())
                                    )
                            )
                    )
            );



            log.info("Old semantic cache cleaned");

        }catch(Exception e){
            log.error("Cache cleanup failed {}", e.getMessage());
        }
    }
}