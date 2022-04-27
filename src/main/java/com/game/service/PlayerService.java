package com.game.service;

import com.game.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PlayerService {
    Optional<Player> getPlayer (Long id);
    void deletePlayer (Long id);
    Player createPlayer(Player player);
    Player updatePlayer(Long id, Player player);
    List<Player> getPlayers (Map<String, String> paramList);
    Integer getCount(Map<String, String> paramsList);
}
