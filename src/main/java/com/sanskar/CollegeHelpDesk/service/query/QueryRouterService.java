package com.sanskar.CollegeHelpDesk.service.query;

import com.sanskar.CollegeHelpDesk.model.ResourceType;
import dev.langchain4j.model.chat.ChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueryRouterService {

    // private final ChatModel chatModel;
    private final ChatClient chatClient;

    public Set<ResourceType> detectTabs(String query){
        log.info("QueryRouterService called");
        // end quote treated as base indentation level
        // if text on left of end quote → no extra indentation
        // if text on right of end quote → means space included
        String prompt = """
        You are a strict classification engine.
        
        Your task is to classify the user's question into one or more categories.
        
        Categories:
        faculty
        notice
        syllabus
        
        Rules:
        - Return ONLY category names
        - If multiple apply, return comma separated
        - No explanation
        - No extra text
        - No sentences
        - No reasoning
        - Output must contain only category names
        
        Examples:
        Question: give professor contact
        Output: faculty
        
        Question: exam date announcement
        Output: notice
        
        Question: syllabus of dbms and notice for upcoming events
        Output: syllabus, notice
        
        Question: number of sonal chandal
        Output: faculty
        
        Now classify:
        
        Question: %s
        Output:
        """.formatted(query);

        Instant start = Instant.now();
        String response = chatClient.prompt(prompt).call().content();
        Instant end = Instant.now();
        log.info("QueryRouterService llm call latency: {} ms", end.toEpochMilli() - start.toEpochMilli());
        return parse(response);
    }

    private Set<ResourceType> parse(String res){

        Set<ResourceType> set = new HashSet<>();

        for(String s : res.toUpperCase().split(",")){
            try{
                set.add(ResourceType.valueOf(s.trim()));
            }catch(Exception ignored){

            }
        }

        if(set.isEmpty()){
            // fallback search everywhere
            return Set.of(
                    ResourceType.FACULTY,
                    ResourceType.NOTICE
            );
        }

        return set;
    }
}
