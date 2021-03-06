package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.exception.DBDataException;
import com.game.exception.NotFoundException;
import com.game.repository.PlayerRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService {

    private static final int PLAYER_NAME_MAXIMUM = 12;
    private static final int PLAYER_TITLE_MAXIMUM = 30;
    private static final int PLAYER_EXPERIENCE_MINIMUM = 0;
    private static final int PLAYER_EXPERIENCE_MAXIMUM = 10_000_000;
    private static final long PLAYER_DATE_MINIMUM = 946_674_000_000L;
    private static final long PLAYER_DATE_MAXIMUM = 32_535_205_199_000L;


    private final PlayerRepository playerRepository;

    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }


    // TODO: 26.04.2022 здесь разобраться с кодами ошибок страниц 400 и 404:
    //  при getPlayer и deletePlayer одинаковое описание ошибок

    @Override
    public Optional<Player> getPlayer(Long id) {
        checkIdPositive(id);
        return playerRepository.findById(id);
    }

    @Override
    public void deletePlayer(Long id) {
        checkIdPositive(id);
        if (!playerRepository.existsById(id))
            throw new NotFoundException();
        playerRepository.deleteById(id);

    }

    @Override
    public Player createPlayer(Player player) {
        checkFields(player);
        checkConstraints(player);
        calculateExperience(player);
        return playerRepository.save(player);
    }

    @Override
    public Player updatePlayer(Long id, Player player) {
        checkIdPositive(id);
        Player oldPlayer = playerRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        updateFields(oldPlayer, player);
        checkFields(player);
        checkConstraints(player);
        calculateExperience(player);
        return playerRepository.save(player);
    }

    @Override
    public List<Player> getPlayers(Map<String, String> paramList) {
        int pageNumber = Integer.parseInt(paramList.get("pageNumber"));
        int pageSize = Integer.parseInt(paramList.get("pageSize"));
        Sort sort = Sort.by("id");
        if (paramList.containsKey("order")) {
            sort = Sort.by(PlayerOrder.valueOf(paramList.get("order")).getFieldName());
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Player> page = playerRepository.findAll(new PlayerSpecification(paramList), pageable);
        return page.getContent();
    }

    @Override
    public Integer getCount(Map<String, String> paramsList) {
        return playerRepository.findAll(new PlayerSpecification(paramsList)).size();
    }

    private void checkIdPositive(Long id) {
        if (id <= 0) throw new DBDataException();
    }

    private void checkFields(Player player) {
        if (player.getName().isEmpty()
                || player.getRace() == null
                || player.getProfession() == null
                || player.getExperience() < PLAYER_EXPERIENCE_MINIMUM
                || player.getExperience() > PLAYER_EXPERIENCE_MAXIMUM
                || player.getBirthday().after(new Date(PLAYER_DATE_MAXIMUM))
                || player.getBirthday().before(new Date(PLAYER_DATE_MINIMUM))
                || player.getBirthday().getTime() < 0)
            throw new DBDataException();
    }

    private void checkConstraints(Player player) {
        if (player.getName().length() > PLAYER_NAME_MAXIMUM || player.getTitle().length() > PLAYER_TITLE_MAXIMUM)
            throw new DBDataException();
    }

    // TODO: 27.04.2022 здели ли учитывать проверку на banned?

    private void calculateExperience(Player player) {
        // проверка на banned
        int exp = player.getExperience();
        int level = (int) ((Math.sqrt(2500 + 200 * exp) - 50) / 100);
        int untilNextLevel = 50 * (level + 1) * (level + 2) - exp;
        player.setLevel(level);
        player.setUntilNextLevel(untilNextLevel);
    }

    // TODO: 27.04.2022 аккуратно проверить на корректность логики замены id игроков и дальнейшая запись в репозиторий player
    //  взял из примера логику: у player присваиваем id oldPlayer
    //  и берем значения полей oldPlayer при null полей player

    // TODO: 27.04.2022 убедиться в корректности использования полей
    private void updateFields(Player oldPlayer, Player player) {
        // обновлять только те поля, которые не null
        player.setId(oldPlayer.getId());
        if (player.getName() == null)
            player.setName(oldPlayer.getName());
        if (player.getTitle() == null)
            player.setTitle(oldPlayer.getTitle());
        if (player.getRace() == null)
            player.setRace(oldPlayer.getRace());
        if (player.getProfession() == null)
            player.setProfession(oldPlayer.getProfession());
        if (player.getExperience() == null)
            player.setExperience(oldPlayer.getExperience());
        if (player.getBirthday() == null)
            player.setBirthday(oldPlayer.getBirthday());
        if (player.getBanned() == null)
            player.setBanned(oldPlayer.getBanned());
    }
}
