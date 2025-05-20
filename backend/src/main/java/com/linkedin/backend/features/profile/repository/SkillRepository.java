package com.linkedin.backend.features.profile.repository;

import com.linkedin.backend.features.profile.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByUserIdOrderByEndorsementsDesc(Long userId);
}