package com.nirmaan.dto;

import java.time.LocalDateTime;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptHistoryResponse {
	
	private Long attemptId;
	private String username;
	private String quizText;
	private Integer score;
	private Integer totalQuestions;
	private Double percentage;
	private String grade;
	private LocalDateTime attemptDate;
}
