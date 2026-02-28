package com.sanskar.CollegeHelpDesk.controller;

import com.sanskar.CollegeHelpDesk.service.query.QueryOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class QueryController {
    private final QueryOrchestrator queryOrchestrator;

    @PostMapping("/ask/{sessionId}")
    public String ask(@RequestBody String query, @PathVariable String sessionId) {
        return queryOrchestrator.ask(query, sessionId);
    }
}
