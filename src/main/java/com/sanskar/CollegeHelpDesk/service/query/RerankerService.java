package com.sanskar.CollegeHelpDesk.service.query;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RerankerService {

    // private final ChatModel chatModel;
    private final ChatClient chatClient;

    public List<TextSegment> rerank(String query, List<TextSegment> segments, int topK){
        log.info("RerankerService called");
        if(segments.size() <= topK)
            return segments;

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i<segments.size(); i++) {
            sb.append("""
            [Chunk %d]
            %s
            
            """.formatted(i+1, segments.get(i).text()));
        }

        String prompt = """
        You are a semantic ranking engine.
        
        Goal:
        Score each chunk from 0 to 100 based on semantic similarity with query.
        You are NOT answering the query.
        You are NOT extracting any information.
        You are ONLY ranking text similarity.
        
        Rules:
        1. Ignore factual answering.
        2. Do NOT extract phone numbers, emails, or data.
        3. Only measure semantic similarity between query and chunk text.
        4. Rank chunks by relevance to query.
        5. Most relevant first.
        6. No explanation.
        
        --------------------------------------------------
        Output format:
        [number]: score
        
        Then return final ranking numbers only.
        Do not output any chunk content.
        
        --------------------------------------------------
        Query:
        ohone number of faculty: Sonal chandel
        
        --------------------------------------------------
        Question:
        %s
        
        --------------------------------------------------
        Chunks:
        %s
        
        --------------------------------------------------
        Output:
        """.formatted(query, sb.toString());
        log.info("RerankerService prompt:\n{}", prompt);
        Instant start = Instant.now();
        String res = chatClient.prompt(prompt).call().content();
        Instant end = Instant.now();
        log.info("RerankerService llm response:\n{}", res);
        log.info("RerankerService llm latency: {} ms", end.toEpochMilli() - start.toEpochMilli());
        return parse(res, segments, topK);
    }

    private List<TextSegment> parse(String res,
                                    List<TextSegment> segs,
                                    int k){

        List<TextSegment> out = new ArrayList<>();

        for(String s : res.split(",")){
            try{
                int idx = Integer.parseInt(s.trim()) - 1;
                if(idx>=0 && idx<segs.size())
                    out.add(segs.get(idx));
                if(out.size()==k) break;
            }catch(Exception ignored){}
        }

        if(out.isEmpty())
            return segs.subList(0, Math.min(k, segs.size()));

        return out;
    }
}
