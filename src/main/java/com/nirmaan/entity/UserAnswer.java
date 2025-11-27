package com.nirmaan.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_user_answers")
public class UserAnswer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userAnswerId;

	@ManyToOne
	@JoinColumn(name = "attempt_id", nullable = false)
	@JsonBackReference
	private QuizAttempt quizAttempt;

	@ManyToOne
	@JoinColumn(name = "question_id", nullable = false)
	private Question question;

	@ManyToOne
	@JoinColumn(name = "selected_option_id", nullable = false)
	private Option selectedOption;

	@Column(nullable = false)
	private Boolean isCorrect;
}