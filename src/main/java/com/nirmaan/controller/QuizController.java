package com.nirmaan.controller;

import java.util.List;
import com.nirmaan.entity.Quiz;
import com.nirmaan.service.IQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quizzes") // Base path for all quiz endpoints
@CrossOrigin(origins = "http://localhost:4200")
public class QuizController {
	
	@Autowired
	private IQuizService quizService;

	// 1. CREATE - POST /api/quizzes
	@PostMapping
	public ResponseEntity<Quiz> createQuiz(@RequestBody Quiz quiz) {
		Quiz createdQuiz = quizService.createQuiz(quiz);
		return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
	}

	@GetMapping
    @Transactional(readOnly = true)  // ADD THIS LINE
    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizzes = quizService.getAllQuizzes();
        // Force initialize lazy collections
        quizzes.forEach(quiz -> {
            quiz.getQuestions().size();  // This triggers loading
            quiz.getQuestions().forEach(question -> 
                question.getOptions().size()  // This triggers loading
            );
        });
        return quizzes;
    }

	@GetMapping("/{id}")
    @Transactional(readOnly = true)  // ADD THIS LINE
    public Quiz getQuizById(@PathVariable int id) {
        Quiz quiz = quizService.getQuizById(id);
        // Force initialize lazy collections
        quiz.getQuestions().size();
        quiz.getQuestions().forEach(question -> 
            question.getOptions().size()
        );
        return quiz;
    }

	// 4. UPDATE - PUT /api/quizzes/{id}
	@PutMapping("/{id}")
	public ResponseEntity<Quiz> updateQuiz(@PathVariable int id, @RequestBody Quiz quizDetails) {
		// This calls the robust nested update logic in the service layer
		Quiz updatedQuiz = quizService.updateQuiz(id, quizDetails);
		return ResponseEntity.ok(updatedQuiz);
	}

	// 5. DELETE - DELETE /api/quizzes/{id}
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteQuiz(@PathVariable int id) {
		quizService.deleteQuiz(id);
		return ResponseEntity.noContent().build();
	}

	// 6. READ BY TEXT - GET /api/quizzes/search?text=YourQuizText
	@GetMapping("/search")
	public ResponseEntity<Quiz> getQuizByText(@RequestParam String text) {
		Quiz quiz = quizService.getQuizByText(text);
		return ResponseEntity.ok(quiz);
	}
}