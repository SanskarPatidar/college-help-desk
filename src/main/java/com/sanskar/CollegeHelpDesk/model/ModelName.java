package com.sanskar.CollegeHelpDesk.model;

import lombok.Getter;

@Getter
public enum ModelName {
    GEMINI_2_5_FLASH("gemini-2.5-flash"),
//    GEMINI_2_5_PRO("gemini-2.5-pro"),
    GEMINI_2_5_FLASH_LITE("gemini-2.5-flash-lite"),

//    GEMINI_3_PRO_PREVIEW("gemini-3-pro-preview"),
    GEMINI_3_FLASH_PREVIEW("gemini-3-flash-preview"),
//    GEMINI_3_1_PRO_PREVIEW("gemini-3.1-pro-preview"),
    GEMINI_3_1_FLASH_LITE_PREVIEW("gemini-3.1-flash-lite-preview"),

    GEMINI_FLASH_LATEST("gemini-flash-latest"),
    GEMINI_FLASH_LITE_LATEST("gemini-flash-lite-latest");
//    GEMINI_PRO_LATEST("gemini-pro-latest");

    // can't send system prompt to gemma so not using
//    GEMMA_3_1B("gemma-3-1b-it"),
//    GEMMA_3_4B("gemma-3-4b-it"),
//    GEMMA_3_12B("gemma-3-12b-it"),
//    GEMMA_3_27B("gemma-3-27b-it"),
//
//    GEMMA_3N_E4B("gemma-3n-e4b-it"),
//    GEMMA_3N_E2B("gemma-3n-e2b-it"),
//
//    GEMMA_4_26B("gemma-4-26b-a4b-it"),
//    GEMMA_4_31B("gemma-4-31b-it");

    private final String value;
    ModelName(String value) {
        this.value = value;
    }
}
