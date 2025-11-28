package com.nirmaan.controller;

import com.nirmaan.dto.AttemptHistoryResponse;
import com.nirmaan.dto.QuizResultResponse;
import com.nirmaan.dto.QuizSubmissionRequest;
import com.nirmaan.service.QuizAttemptService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz-attempts")
@CrossOrigin(origins = "http://localhost:4200")
public class QuizAttemptController {

	@Autowired
	private QuizAttemptService quizAttemptService;

	// Submit quiz answers and get results
	@PostMapping("/submit")
	public ResponseEntity<QuizResultResponse> submitQuiz(@Valid @RequestBody QuizSubmissionRequest request) {
		QuizResultResponse result = quizAttemptService.submitQuiz(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	// Get user's attempt history
	@GetMapping("/my-attempts")
	public ResponseEntity<List<AttemptHistoryResponse>> getMyAttempts() {
		List<AttemptHistoryResponse> attempts = quizAttemptService.getUserAttemptHistory();
		return ResponseEntity.ok(attempts);
	}

	// Get detailed results of a specific attempt
	@GetMapping("/{attemptId}")
	public ResponseEntity<QuizResultResponse> getAttemptDetails(@PathVariable Long attemptId) {
		QuizResultResponse result = quizAttemptService.getAttemptDetails(attemptId);
		return ResponseEntity.ok(result);
	}

	// Get leaderboard for a specific quiz
	@GetMapping("/leaderboard/{quizId}")
	public ResponseEntity<List<AttemptHistoryResponse>> getQuizLeaderboard(@PathVariable Integer quizId) {
		List<AttemptHistoryResponse> leaderboard = quizAttemptService.getQuizLeaderboard(quizId);
		return ResponseEntity.ok(leaderboard);
	}
}