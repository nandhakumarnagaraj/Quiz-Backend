package com.nirmaan.entity;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "tbl_quiz")
public class Quiz {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int quizId;

	private String quizText;

	// A Quiz has many Questions.
	// CascadeType.ALL: Operations (like save, delete) cascade down.
	// orphanRemoval=true: If a Question is removed from this list, it's deleted
	// from DB.
	@OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference // The "forward" part of the bi-directional relationship (serialized)
	private List<Question> questions = new ArrayList<>();
}