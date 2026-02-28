package com.sanskar.CollegeHelpDesk.service.ingestion;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SimpleTextSplitter {
    private final int chunkSize;
    private final int overlap;

    // default
    public SimpleTextSplitter() {
        this.chunkSize = 500;
        this.overlap = 50;
    }

    public List<String> split(String text) {
        List<String> chunks = new ArrayList<>();

        if (text == null || text.isBlank()) return chunks;

        int start = 0;
        int length = text.length();

        while (start < length) {
            int end = Math.min(start + chunkSize, length);
            String chunk = text.substring(start, end).trim();
            chunks.add(chunk);

            // Moving forward with overlap
            start = start + chunkSize - overlap;
            if (start < 0) break;
        }

        return chunks;
    }
}
