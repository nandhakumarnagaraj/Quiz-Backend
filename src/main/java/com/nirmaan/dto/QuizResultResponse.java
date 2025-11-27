package com.nirmaan.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultResponse {
	private Long attemptId;
	private String username;
	private String quizText;
	private Integer score;
	private Integer totalQuestions;
	private Integer correctAnswers;
	private Integer wrongAnswers;
	private Double percentage;
	private String grade;
	private LocalDateTime attemptDate;
	private List<QuestionResult> questionResults;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class QuestionResult {
		private Integer questionId;
		private String questionText;
		private String selectedAnswer;
		private String correctAnswer;
		private Boolean isCorrect;
	}
}
