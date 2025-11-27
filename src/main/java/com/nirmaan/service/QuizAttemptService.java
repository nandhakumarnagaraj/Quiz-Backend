package com.nirmaan.service;

import com.nirmaan.dto.AttemptHistoryResponse;
import com.nirmaan.dto.QuizResultResponse;
import com.nirmaan.dto.QuizSubmissionRequest;
import com.nirmaan.entity.*;
import com.nirmaan.exception.ResourceNotFoundException;
import com.nirmaan.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizAttemptService {

	@Autowired
	private QuizAttemptRepository quizAttemptRepository;

	@Autowired
	private UserAnswerRepository userAnswerRepository;

	@Autowired
	private QuizRepository quizRepository;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private OptionRepository optionRepository;

	@Autowired
	private UserRepository userRepository;

	@Transactional
	public QuizResultResponse submitQuiz(QuizSubmissionRequest request) {
		// Get current authenticated user
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		// Get quiz
		Quiz quiz = quizRepository.findById(request.getQuizId())
				.orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + request.getQuizId()));

		// Create quiz attempt
		QuizAttempt attempt = new QuizAttempt();
		attempt.setUser(user);
		attempt.setQuiz(quiz);
		attempt.setTotalQuestions(quiz.getQuestions().size());

		int correctAnswers = 0;
		List<UserAnswer> userAnswers = new ArrayList<>();
		List<QuizResultResponse.QuestionResult> questionResults = new ArrayList<>();

		// Process each answer
		for (QuizSubmissionRequest.AnswerSubmission answerSubmission : request.getAnswers()) {
			Question question = questionRepository.findById(answerSubmission.getQuestionId())
					.orElseThrow(() -> new ResourceNotFoundException("Question not found"));

			Option selectedOption = optionRepository.findById(answerSubmission.getSelectedOptionId())
					.orElseThrow(() -> new ResourceNotFoundException("Option not found"));

			boolean isCorrect = selectedOption.isCorrect();
			if (isCorrect) {
				correctAnswers++;
			}

			// Create user answer record
			UserAnswer userAnswer = new UserAnswer();
			userAnswer.setQuizAttempt(attempt);
			userAnswer.setQuestion(question);
			userAnswer.setSelectedOption(selectedOption);
			userAnswer.setIsCorrect(isCorrect);
			userAnswers.add(userAnswer);

			// Get correct answer for result
			String correctAnswer = question.getOptions().stream().filter(Option::isCorrect).map(Option::getOptionText)
					.findFirst().orElse("N/A");

			// Add to question results
			QuizResultResponse.QuestionResult questionResult = new QuizResultResponse.QuestionResult();
			questionResult.setQuestionId(question.getQuestionId());
			questionResult.setQuestionText(question.getQuestionText());
			questionResult.setSelectedAnswer(selectedOption.getOptionText());
			questionResult.setCorrectAnswer(correctAnswer);
			questionResult.setIsCorrect(isCorrect);
			questionResults.add(questionResult);
		}

		// Calculate score and percentage
		int wrongAnswers = attempt.getTotalQuestions() - correctAnswers;
		double percentage = ((double) correctAnswers / attempt.getTotalQuestions()) * 100;

		attempt.setCorrectAnswers(correctAnswers);
		attempt.setWrongAnswers(wrongAnswers);
		attempt.setScore(correctAnswers);
		attempt.setPercentage(percentage);
		attempt.setUserAnswers(userAnswers);

		// Save attempt
		QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);

		// Create response
		QuizResultResponse response = new QuizResultResponse();
		response.setAttemptId(savedAttempt.getAttemptId());
		response.setUsername(user.getUsername());
		response.setQuizText(quiz.getQuizText());
		response.setScore(correctAnswers);
		response.setTotalQuestions(attempt.getTotalQuestions());
		response.setCorrectAnswers(correctAnswers);
		response.setWrongAnswers(wrongAnswers);
		response.setPercentage(percentage);
		response.setGrade(calculateGrade(percentage));
		response.setAttemptDate(savedAttempt.getAttemptDate());
		response.setQuestionResults(questionResults);

		return response;
	}

	public List<AttemptHistoryResponse> getUserAttemptHistory() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		List<QuizAttempt> attempts = quizAttemptRepository.findByUserOrderByAttemptDateDesc(user);

		return attempts.stream().map(attempt -> {
			AttemptHistoryResponse response = new AttemptHistoryResponse();
			response.setAttemptId(attempt.getAttemptId());
			response.setUsername(attempt.getUser().getUsername());
			response.setQuizText(attempt.getQuiz().getQuizText());
			response.setScore(attempt.getScore());
			response.setTotalQuestions(attempt.getTotalQuestions());
			response.setPercentage(attempt.getPercentage());
			response.setGrade(calculateGrade(attempt.getPercentage()));
			response.setAttemptDate(attempt.getAttemptDate());
			return response;
		}).collect(Collectors.toList());
	}

	public QuizResultResponse getAttemptDetails(Long attemptId) {
		QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
				.orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));

		// Verify user owns this attempt
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		if (!attempt.getUser().getUsername().equals(username)) {
			throw new SecurityException("You can only view your own attempts");
		}

		List<QuizResultResponse.QuestionResult> questionResults = attempt.getUserAnswers().stream().map(userAnswer -> {
			Question question = userAnswer.getQuestion();
			String correctAnswer = question.getOptions().stream().filter(Option::isCorrect).map(Option::getOptionText)
					.findFirst().orElse("N/A");

			QuizResultResponse.QuestionResult result = new QuizResultResponse.QuestionResult();
			result.setQuestionId(question.getQuestionId());
			result.setQuestionText(question.getQuestionText());
			result.setSelectedAnswer(userAnswer.getSelectedOption().getOptionText());
			result.setCorrectAnswer(correctAnswer);
			result.setIsCorrect(userAnswer.getIsCorrect());
			return result;
		}).collect(Collectors.toList());

		QuizResultResponse response = new QuizResultResponse();
		response.setAttemptId(attempt.getAttemptId());
		response.setUsername(attempt.getUser().getUsername());
		response.setQuizText(attempt.getQuiz().getQuizText());
		response.setScore(attempt.getScore());
		response.setTotalQuestions(attempt.getTotalQuestions());
		response.setCorrectAnswers(attempt.getCorrectAnswers());
		response.setWrongAnswers(attempt.getWrongAnswers());
		response.setPercentage(attempt.getPercentage());
		response.setGrade(calculateGrade(attempt.getPercentage()));
		response.setAttemptDate(attempt.getAttemptDate());
		response.setQuestionResults(questionResults);

		return response;
	}

	public List<AttemptHistoryResponse> getQuizLeaderboard(Integer quizId) {
		List<QuizAttempt> attempts = quizAttemptRepository.findByQuizQuizIdOrderByAttemptDateDesc(quizId);

		return attempts.stream().map(attempt -> {
			AttemptHistoryResponse response = new AttemptHistoryResponse();
			response.setAttemptId(attempt.getAttemptId());
			response.setUsername(attempt.getUser().getUsername());
			response.setQuizText(attempt.getQuiz().getQuizText()); 
			response.setScore(attempt.getScore());
			response.setTotalQuestions(attempt.getTotalQuestions());
			response.setPercentage(attempt.getPercentage());
			response.setGrade(calculateGrade(attempt.getPercentage()));
			response.setAttemptDate(attempt.getAttemptDate());
			return response;
		}).collect(Collectors.toList());
	}

	private String calculateGrade(Double percentage) {
		if (percentage >= 90)
			return "A+";
		else if (percentage >= 80)
			return "A";
		else if (percentage >= 70)
			return "B";
		else if (percentage >= 60)
			return "C";
		else if (percentage >= 50)
			return "D";
		else
			return "F";
	}
}