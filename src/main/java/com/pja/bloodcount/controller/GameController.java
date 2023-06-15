package com.pja.bloodcount.controller;

import com.pja.bloodcount.dto.request.AnswerRequest;
import com.pja.bloodcount.dto.response.CaseResponse;
import com.pja.bloodcount.dto.response.GameResponse;
import com.pja.bloodcount.exceptions.UserNotAllowedException;
import com.pja.bloodcount.model.BCAssessmentQuestion;
import com.pja.bloodcount.model.Game;
import com.pja.bloodcount.model.User;
import com.pja.bloodcount.service.GameService;
import com.pja.bloodcount.service.QnAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameService service;
    private final QnAService qnAService;
    @GetMapping(value = "/case/{caseId}", params = "userId")
    public ResponseEntity<GameResponse> start(@PathVariable Long caseId,
                                                   @RequestParam UUID userId,
                                                   Authentication authentication){
        User userDetails = (User) authentication.getPrincipal();
        log.info("request coming from user with email-> {}", userDetails.getEmail());
        if(!userDetails.getId().equals(userId)){
            throw new UserNotAllowedException(
                    "Access restricted, user with email: " +
                            userDetails.getEmail() +
                            " not allowed to this url");
        }
        return ResponseEntity.ok(service.createGame(caseId, userId));
    }

//    @PostMapping("/{id}")
//    public ResponseEntity<GameResponse> completeGame(@PathVariable Long id){
//        return ResponseEntity.ok(service.completeGame(id));
//    }

    @PostMapping(value = "/{gameId}/score", params = "userId")
    public ResponseEntity<Void> score(@PathVariable Long gameId,
                                      @RequestBody List<AnswerRequest> request,
                                      @RequestParam UUID userId,
                                      Authentication authentication){
        User userDetails = (User) authentication.getPrincipal();
        log.info("request coming from user with email-> {}", userDetails.getEmail());
        if(!userDetails.getId().equals(userId)){
            throw new UserNotAllowedException(
                    "Access restricted, user with email: " +
                            userDetails.getEmail() +
                            " not allowed to this url");
        }
        qnAService.score(request, gameId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}