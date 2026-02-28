package com.sanskar.CollegeHelpDesk.service.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ConversationMemoryService {

    // sessionId -> Deque of query, answer
    private final Map<String, Deque<String[]>> cache = new ConcurrentHashMap<>();

    @Value("${conversation.memory.max-history:5}")
    private int MAX_HISTORY;

    public void addMessage(String sessionId, String query, String answer){
        log.info("ConversationMemoryService called to add message to history");
        if(!cache.containsKey(sessionId)) {
            cache.put(sessionId, new ArrayDeque<>());
        }
        cache.get(sessionId).addLast(new String[]{query, answer});
        if(cache.get(sessionId).size() > MAX_HISTORY){
            cache.get(sessionId).pollFirst();
        }
    }

    public List<String[]> getHistory(String sessionId){
        log.info("ConversationMemoryService called to fetch history");
        return new ArrayList<>(cache.getOrDefault(sessionId, new ArrayDeque<>()));
    }

}