package com.pja.bloodcount.controller;

import com.pja.bloodcount.dto.request.AnswerRequest;
import com.pja.bloodcount.dto.request.CreateMSQQuestionRequest;
import com.pja.bloodcount.dto.response.GameResponse;
import com.pja.bloodcount.exceptions.UserNotAllowedException;
import com.pja.bloodcount.model.ErythrocyteQuestion;
import com.pja.bloodcount.model.LeukocyteQuestion;
import com.pja.bloodcount.model.User;
import com.pja.bloodcount.model.VariousQuestion;
import com.pja.bloodcount.service.QuestionBaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Slf4j
public class QuestionBaseController {

    private final QuestionBaseService service;

    @PostMapping("/erythrocytes")
    public ResponseEntity<List<ErythrocyteQuestion>> createErythrocytesQuestions(@RequestBody List<CreateMSQQuestionRequest> requests){
        return ResponseEntity.ok(service.createErythrocytes(requests));
    }

    @PostMapping("/leukocytes")
    public ResponseEntity<List<LeukocyteQuestion>> createLeukocytesQuestion(@RequestBody List<CreateMSQQuestionRequest> requests){
        return ResponseEntity.ok(service.createLeukocyte(requests));
    }

    @PostMapping("/various")
    public ResponseEntity<List<VariousQuestion>> createVariousTopicQuestions(@RequestBody List<CreateMSQQuestionRequest> requests){
        return ResponseEntity.ok(service.createVariousQ(requests));
    }
}
