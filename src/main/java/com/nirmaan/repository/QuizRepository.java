package com.nirmaan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nirmaan.entity.Quiz;

// JpaRepository<Entity Type, Primary Key Type>
public interface QuizRepository extends JpaRepository<Quiz, Integer> {

	// Custom finder method
	Quiz findByQuizText(String quizText);

	@Query("SELECT DISTINCT q FROM Quiz q LEFT JOIN FETCH q.questions qu LEFT JOIN FETCH qu.options")
	List<Quiz> findAllWithQuestionsAndOptions();

	@Query("SELECT DISTINCT q FROM Quiz q LEFT JOIN FETCH q.questions qu LEFT JOIN FETCH qu.options WHERE q.id = :id")
	Optional<Quiz> findByIdWithQuestionsAndOptions(@Param("id") Long id);

}