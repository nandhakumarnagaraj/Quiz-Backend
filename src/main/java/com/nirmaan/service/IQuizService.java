package com.nirmaan.service;

import java.util.List;
import com.nirmaan.entity.Quiz;

public interface IQuizService {

	Quiz createQuiz(Quiz quiz);

	Quiz getQuizById(int id);

	List<Quiz> getAllQuizzes();

	Quiz updateQuiz(int quizId, Quiz quizDetails);

	void deleteQuiz(int quizId);

	Quiz getQuizByText(String quizText);
}