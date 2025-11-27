package com.nirmaan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nirmaan.entity.Quiz;

// JpaRepository<Entity Type, Primary Key Type>
public interface QuizRepository extends JpaRepository<Quiz, Integer> {

	// Custom finder method
	Quiz findByQuizText(String quizText);
}