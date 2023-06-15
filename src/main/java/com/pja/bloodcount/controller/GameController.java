package com.pja.bloodcount.controller;

import com.pja.bloodcount.dto.response.CaseResponse;
import com.pja.bloodcount.dto.response.GameResponse;
import com.pja.bloodcount.model.Game;
import com.pja.bloodcount.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameService service;

    @GetMapping("/case/{caseId}")
    public ResponseEntity<GameResponse> createGame(@PathVariable Long caseId){
        return ResponseEntity.ok(service.createGame(caseId));
    }

    @PostMapping("/{id}")
    public ResponseEntity<GameResponse> completeGame(@PathVariable Long id){
        return ResponseEntity.ok(service.completeGame(id));
    }
}
