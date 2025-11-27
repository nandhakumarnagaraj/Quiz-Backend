package com.nirmaan.entity;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "tbl_question")
public class Question {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int questionId;

	private String questionText;

	// A Question has many Options.
	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = jakarta.persistence.FetchType.EAGER)
	@JsonManagedReference
	private List<Option> options = new ArrayList<>();

	// A Question belongs to one Quiz.
	@ManyToOne
	@JoinColumn(name = "quiz_id") // Foreign key column
	@JsonBackReference // The "back" part of the bi-directional relationship (ignored during
						// serialization)
	private Quiz quiz;
}