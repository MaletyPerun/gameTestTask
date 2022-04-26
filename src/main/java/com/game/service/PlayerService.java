package com.game.service;

import com.game.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PlayerService {
    Optional<Player> getPlayer (Long id);
    void deleteShip(Long id);
    Player createShip(Player player);
    Player updateShip(Long id, Player player);
    List<Player> getShips(Map<String, String> paramList);
    Integer getCount(Map<String, String> paramsList);
}
