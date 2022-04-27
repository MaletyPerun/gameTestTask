package com.game.controller;

import com.game.entity.Player;
import com.game.exception.DBConstraintException;
import com.game.exception.DBDataException;
import com.game.exception.NotFoundException;
import com.game.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/rest/players")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping(produces = "application/json")
    public List<Player> getPlayers(@RequestParam Map<String, String> customQuery) {
        customQuery.putIfAbsent("pageSize", "3");
        customQuery.putIfAbsent("pageNumber", "0");
        return playerService.getPlayers(customQuery);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Player> getById(@PathVariable("id") Long id) {
        Optional<Player> player = playerService.getPlayer(id);
        return player.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        if (player.getBanned() == null)
            player.setBanned(false);
        return new ResponseEntity<>(playerService.createPlayer(player), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") Long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable("id") Long id, @RequestBody Player player) {
        return new ResponseEntity<>(playerService.updatePlayer(id, player), HttpStatus.OK);
    }

    @GetMapping("/count")
    public int getCount(@RequestParam Map<String, String> customQuery) {
        return playerService.getCount(customQuery);
    }
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NullPointerException.class,
            DBConstraintException.class,
            DBDataException.class})
    public void badRequestHandler() {

    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public void notFoundHandler() {

    }
}
