package com.say5.learnloop.api;

import com.say5.learnloop.api.Dtos.NextQuestionResponse;
import com.say5.learnloop.api.Dtos.SubmitAnswerRequest;
import com.say5.learnloop.api.Dtos.SubmitAnswerResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/practice")
class PracticeController {

  private final PracticeService service;

  PracticeController(PracticeService service) {
    this.service = service;
  }

  @PostMapping("/answers")
  SubmitAnswerResponse submit(@Valid @RequestBody SubmitAnswerRequest req) {
    return service.submit(req.learnerId(), req.skillId(), req.questionId(), req.correct());
  }

  @GetMapping("/next/{learnerId}/{skillId}")
  NextQuestionResponse next(@PathVariable long learnerId, @PathVariable long skillId) {
    return service.next(learnerId, skillId);
  }

  @GetMapping("/reviews/{learnerId}")
  java.util.List<Long> dueForReview(@PathVariable long learnerId) {
    return service.dueForReview(learnerId);
  }
}
