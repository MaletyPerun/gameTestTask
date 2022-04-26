package com.game.repository;

import com.game.entity.Player;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface PlayerRepository extends PagingAndSortingRepository<Player, Long>, JpaSpecificationExecutor {

}
