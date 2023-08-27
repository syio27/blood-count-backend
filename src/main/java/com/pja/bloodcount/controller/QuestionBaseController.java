package com.pja.bloodcount.controller;

import com.pja.bloodcount.dto.request.AnswerRequest;
import com.pja.bloodcount.dto.request.CreateMSQQuestionRequest;
import com.pja.bloodcount.dto.response.GameResponse;
import com.pja.bloodcount.exceptions.UserNotAllowedException;
import com.pja.bloodcount.model.*;
import com.pja.bloodcount.model.enums.Language;
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

    @PostMapping("/erythrocytes/{language}")
    public ResponseEntity<List<ErythrocyteQuestionTranslation>> createErythrocytesQuestions(@RequestBody List<CreateMSQQuestionRequest> requests, @PathVariable Language language){
        return ResponseEntity.ok(service.createErythrocytes(requests, language));
    }

    @PostMapping("/leukocytes/{language}")
    public ResponseEntity<List<LeukocyteQuestionTranslation>> createLeukocytesQuestion(@RequestBody List<CreateMSQQuestionRequest> requests, @PathVariable Language language){
        return ResponseEntity.ok(service.createLeukocyte(requests, language));
    }

    @PostMapping("/various/{language}")
    public ResponseEntity<List<VariousQuestionTranslation>> createVariousTopicQuestions(@RequestBody List<CreateMSQQuestionRequest> requests, @PathVariable Language language){
        return ResponseEntity.ok(service.createVariousQ(requests, language));
    }
}
