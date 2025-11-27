package com.nirmaan.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Request DTO for submitting quiz answers
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionRequest {
	private Integer quizId;
	private List<AnswerSubmission> answers;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AnswerSubmission {
		private Integer questionId;
		private Integer selectedOptionId;
	}
}