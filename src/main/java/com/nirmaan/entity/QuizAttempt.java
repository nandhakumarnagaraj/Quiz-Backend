package com.nirmaan.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_quiz_attempts")
public class QuizAttempt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long attemptId;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "quiz_id", nullable = false)
	private Quiz quiz;

	@Column(nullable = false)
	private Integer score;

	@Column(nullable = false)
	private Integer totalQuestions;

	@Column(nullable = false)
	private Integer correctAnswers;

	@Column(nullable = false)
	private Integer wrongAnswers;

	@Column(nullable = false)
	private Double percentage;

	@Column(nullable = false)
	private LocalDateTime attemptDate;

	@OneToMany(mappedBy = "quizAttempt", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserAnswer> userAnswers = new ArrayList<>();

	@PrePersist
	public void prePersist() {
		attemptDate = LocalDateTime.now();
	}
}