package com.pja.bloodcount.controller;

import com.pja.bloodcount.dto.request.AnswerRequest;
import com.pja.bloodcount.dto.response.CaseResponse;
import com.pja.bloodcount.dto.response.GameCurrentSessionState;
import com.pja.bloodcount.dto.response.GameResponse;
import com.pja.bloodcount.dto.response.SimpleGameResponse;
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

    @GetMapping(value = "/case/{caseId}", params = "userId")
    public ResponseEntity<GameResponse> start(@PathVariable Long caseId,
                                              @RequestParam UUID userId,
                                              Authentication authentication) {
        User userDetails = (User) authentication.getPrincipal();
        log.info("request coming from user with email-> {}", userDetails.getEmail());
        if (!userDetails.getId().equals(userId)) {
            throw new UserNotAllowedException(
                    "Access restricted, user with email: " +
                            userDetails.getEmail() +
                            " not allowed to this url");
        }
        return ResponseEntity.ok(service.createGame(caseId, userId));
    }

    @PostMapping(value = "/{gameId}/complete", params = "userId")
    public ResponseEntity<SimpleGameResponse> complete(@PathVariable Long gameId,
                                                       @RequestBody List<AnswerRequest> request,
                                                       @RequestParam UUID userId,
                                                       Authentication authentication) {
        User userDetails = (User) authentication.getPrincipal();
        log.info("request coming from user with email-> {}", userDetails.getEmail());
        if (!userDetails.getId().equals(userId)) {
            throw new UserNotAllowedException(
                    "Access restricted, user with email: " +
                            userDetails.getEmail() +
                            " not allowed to this url");
        }
        return ResponseEntity.ok(service.completeGame(gameId, request));
    }

    @GetMapping(value = "/{gameId}", params = "userId")
    public ResponseEntity<GameResponse> getStartedGame(@PathVariable Long gameId,
                                                       @RequestParam UUID userId,
                                                       Authentication authentication) {
        User userDetails = (User) authentication.getPrincipal();
        log.info("request coming from user with email-> {}", userDetails.getEmail());
        if (!userDetails.getId().equals(userId)) {
            throw new UserNotAllowedException(
                    "Access restricted, user with email: " +
                            userDetails.getEmail() +
                            " not allowed to this url");
        }
        return ResponseEntity.ok(service.getInProgressGame(gameId, userId));
    }

    @GetMapping(value = "/", params = "userId")
    public ResponseEntity<Boolean> checkGameInProgress(@RequestParam UUID userId) {
        return ResponseEntity.ok(service.hasGameInProgress(userId));
    }

    @PostMapping(value = "/{gameId}/save", params = "userId")
    public ResponseEntity<Void> onSave(@PathVariable Long gameId,
                                       @RequestBody List<AnswerRequest> request,
                                       @RequestParam UUID userId,
                                       Authentication authentication) {
        User userDetails = (User) authentication.getPrincipal();
        log.info("request coming from user with email-> {}", userDetails.getEmail());
        if (!userDetails.getId().equals(userId)) {
            throw new UserNotAllowedException(
                    "Access restricted, user with email: " +
                            userDetails.getEmail() +
                            " not allowed to this url");
        }
        service.saveSelectedAnswers(gameId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/{gameId}/next", params = "userId")
    public ResponseEntity<GameCurrentSessionState> onNext(@PathVariable Long gameId,
                                                          @RequestBody List<AnswerRequest> request,
                                                          @RequestParam UUID userId,
                                                          Authentication authentication) {
        User userDetails = (User) authentication.getPrincipal();
        log.info("request coming from user with email-> {}", userDetails.getEmail());
        if (!userDetails.getId().equals(userId)) {
            throw new UserNotAllowedException(
                    "Access restricted, user with email: " +
                            userDetails.getEmail() +
                            " not allowed to this url");
        }
        return ResponseEntity.ok(service.next(userId, gameId, request));
    }
}