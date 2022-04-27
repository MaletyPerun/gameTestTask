package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PlayerSpecification implements Specification<Player> {
    public static final String NAME = "name";
    public static final String TITLE = "title";
    public static final String RACE = "race";
    public static final String PROFESSION = "profession";
    public static final String AFTER = "after";
    public static final String BIRTHDAY = "birthday";
    public static final String BEFORE = "before";
    public static final String BANNED = "banned";
    public static final String MIN_EXPERIENCE = "minExperience";
    public static final String EXPERIENCE = "experience";
    public static final String MAX_EXPERIENCE = "maxExperience";
    public static final String MIN_LEVEL = "minLevel";
    public static final String LEVEL = "level";
    public static final String MAX_LEVEL = "maxLevel";

    private final Map<String, String> filter;

    public PlayerSpecification(Map<String, String> filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        checkNameFilter(root, criteriaBuilder, predicates, NAME);
        checkNameFilter(root, criteriaBuilder, predicates, TITLE);
        checkPlayerRaceFilter(root, criteriaBuilder, predicates);
        checkPlayerProfessionFilter(root, criteriaBuilder, predicates);
        checkMinValue(root, criteriaBuilder, predicates, MIN_EXPERIENCE, EXPERIENCE);
        checkMaxValue(root, criteriaBuilder, predicates, MAX_EXPERIENCE, EXPERIENCE);
        checkMinValue(root, criteriaBuilder, predicates, MIN_LEVEL, LEVEL);
        checkMaxValue(root, criteriaBuilder, predicates, MAX_LEVEL, LEVEL);
        checkIsUsedFilter(root, criteriaBuilder, predicates);
        checkMaxDate(root, criteriaBuilder, predicates, BEFORE, BIRTHDAY);
        checkMinDate(root, criteriaBuilder, predicates, AFTER, BIRTHDAY);
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void checkNameFilter(Root<Player> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates, String name) {
        if (filter.get(name) != null)
            predicates.add(criteriaBuilder.like(root.get(name), "%"+filter.get(name)+ "%"));
    }



    // TODO: 27.04.2022 объединить два нижних метода: оба делают проверку на enum поле
    private void checkPlayerRaceFilter(Root<Player> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (filter.get(RACE) != null)
            predicates.add(criteriaBuilder.equal(root.get(RACE), Race.valueOf(filter.get(RACE))));
    }

    private void checkPlayerProfessionFilter(Root<Player> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (filter.get(PROFESSION) != null)
            predicates.add(criteriaBuilder.equal(root.get(PROFESSION), Profession.valueOf(filter.get(PROFESSION))));
    }
    private void checkMinValue(Root<Player> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates, String minQuality, String quality) {
        if (filter.get(minQuality) != null)
            predicates.add(criteriaBuilder.ge(root.get(quality), Integer.parseInt(filter.get(minQuality))));
    }

    private void checkMaxValue(Root<Player> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates, String maxQuality, String quality) {
        if (filter.get(maxQuality) != null)
            predicates.add(criteriaBuilder.le(root.get(quality), Integer.parseInt(filter.get(maxQuality))));
    }
    private void checkIsUsedFilter(Root<Player> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (filter.get(BANNED) != null)
            predicates.add(criteriaBuilder.equal(root.get(BANNED), Boolean.parseBoolean(filter.get(BANNED))));
    }

    private void checkMaxDate(Root<Player> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates, String before, String birthday) {
        if (filter.get(before) != null)
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(birthday), new Date(Long.parseLong(filter.get(before)))));
    }

    private void checkMinDate(Root<Player> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates, String after, String birthday) {
        if (filter.get(after) != null)
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(birthday), new Date(Long.parseLong(filter.get(after)))));
    }
}
