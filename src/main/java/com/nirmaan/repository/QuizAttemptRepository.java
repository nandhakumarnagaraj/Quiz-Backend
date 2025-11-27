package com.nirmaan.repository;

import com.nirmaan.entity.QuizAttempt;
import com.nirmaan.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
	
	List<QuizAttempt> findByUserOrderByAttemptDateDesc(User user);

	List<QuizAttempt> findByQuizQuizIdOrderByAttemptDateDesc(Integer quizId);

	Long countByUser(User user);
}