package com.nirmaan.service;

import java.util.List;
import java.util.ArrayList;
import com.nirmaan.entity.Quiz;
import com.nirmaan.entity.Question;
import com.nirmaan.entity.Option;
import com.nirmaan.repository.QuizRepository;
import com.nirmaan.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizService implements IQuizService {
	
	@Autowired
	private QuizRepository quizRepository;

	@Override
	public Quiz createQuiz(Quiz quiz) {
		if (quiz == null || quiz.getQuizText() == null || quiz.getQuizText().isEmpty()) {
			throw new IllegalArgumentException("Quiz text cannot be null or empty");
		}
		
		// Manually set the bidirectional relationship
		for (Question question : quiz.getQuestions()) {
			question.setQuiz(quiz);
			for (Option option : question.getOptions()) {
				option.setQuestion(question);
			}
		}
		
		return quizRepository.save(quiz);
	}

	@Override
	public Quiz getQuizById(int quizId) {
		return quizRepository.findById(quizId)
				.orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));
	}

	@Override
	public List<Quiz> getAllQuizzes() {
		List<Quiz> quizzes = quizRepository.findAll();
		if (quizzes.isEmpty()) {
			throw new ResourceNotFoundException("No quizzes found");
		}
		return quizzes;
	}

	@Override
	public void deleteQuiz(int quizId) {
		Quiz quizToDelete = quizRepository.findById(quizId)
				.orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));
		quizRepository.delete(quizToDelete);
	}

	@Override
	public Quiz getQuizByText(String quizText) {
		Quiz quiz = quizRepository.findByQuizText(quizText);
		if (quiz == null) {
			throw new ResourceNotFoundException("Quiz not found with text: " + quizText);
		}
		return quiz;
	}

	// --- COMPLEX NESTED UPDATE LOGIC ---

	@Override
	public Quiz updateQuiz(int quizId, Quiz quizDetails) {
		Quiz existingQuiz = quizRepository.findById(quizId).orElseThrow();

		existingQuiz.setQuizText(quizDetails.getQuizText());

		updateQuestions(existingQuiz, quizDetails.getQuestions());

		return quizRepository.save(existingQuiz);
	}

	private void updateQuestions(Quiz existingQuiz, List<Question> incomingQuestions) {

		List<Question> updatedQuestions = new ArrayList<>();

		for (Question incomingQuestion : incomingQuestions) {

			if (incomingQuestion.getQuestionId() != 0) {
				// UPDATE: Find existing question by ID
				existingQuiz.getQuestions().stream()
						.filter(eq -> eq.getQuestionId() == incomingQuestion.getQuestionId()).findFirst()
						.ifPresent(eq -> {
							eq.setQuestionText(incomingQuestion.getQuestionText());
							updateOptions(eq, incomingQuestion.getOptions()); // Recursive call for options
							updatedQuestions.add(eq);
						});
			} else {
				// ADDITION: New Question (ID is 0)
				incomingQuestion.setQuiz(existingQuiz);
				incomingQuestion.getOptions().forEach(opt -> opt.setQuestion(incomingQuestion));
				updatedQuestions.add(incomingQuestion);
			}
		}

		// Sync collections: Deletions are handled by orphanRemoval=true
		existingQuiz.getQuestions().clear();
		existingQuiz.getQuestions().addAll(updatedQuestions);
	}

	private void updateOptions(Question existingQuestion, List<Option> incomingOptions) {

		List<Option> updatedOptions = new ArrayList<>();

		for (Option incomingOption : incomingOptions) {

			if (incomingOption.getOptionId() != 0) {
				// UPDATE: Find existing option by ID
				existingQuestion.getOptions().stream().filter(eo -> eo.getOptionId() == incomingOption.getOptionId())
						.findFirst().ifPresent(eo -> {
							eo.setOptionText(incomingOption.getOptionText());
							eo.setCorrect(incomingOption.isCorrect());
							updatedOptions.add(eo);
						});
			} else {
				// ADDITION: New Option (ID is 0)
				incomingOption.setQuestion(existingQuestion);
				updatedOptions.add(incomingOption);
			}
		}

		// Sync collections: Deletions are handled by orphanRemoval=true
		existingQuestion.getOptions().clear();
		existingQuestion.getOptions().addAll(updatedOptions);
	}

}