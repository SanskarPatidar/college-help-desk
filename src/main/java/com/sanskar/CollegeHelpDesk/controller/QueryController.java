package com.sanskar.CollegeHelpDesk.controller;

import com.sanskar.CollegeHelpDesk.model.ModelName;
import com.sanskar.CollegeHelpDesk.model.QueryResponse;
import com.sanskar.CollegeHelpDesk.service.query.QueryOrchestrator;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QueryController {
    @Autowired
    private QueryOrchestrator queryOrchestrator;

    @PostMapping("/ask/{conversationId}/{modelName}")
    public QueryResponse ask(
            @NonNull @RequestBody String query,
            @PathVariable String conversationId,
            @PathVariable ModelName modelName) {
        return queryOrchestrator.ask(query, conversationId, modelName.getValue());
    }
}
