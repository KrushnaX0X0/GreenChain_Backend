package com.krish.AgariBackend.dto.ai;

import lombok.Data;

import java.util.List;

@Data
public class AiDiagnosisResponse {
    private int healthScore;
    private String disease;
    private String confidence;
    private List<String> recommendations;
    private String status;
}
