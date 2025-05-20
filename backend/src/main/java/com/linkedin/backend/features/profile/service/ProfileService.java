package com.linkedin.backend.features.profile.service;

import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.authentication.service.AuthenticationService;
import com.linkedin.backend.features.profile.dto.*;
import com.linkedin.backend.features.profile.model.Education;
import com.linkedin.backend.features.profile.model.Experience;
import com.linkedin.backend.features.profile.model.Skill;
import com.linkedin.backend.features.profile.repository.EducationRepository;
import com.linkedin.backend.features.profile.repository.ExperienceRepository;
import com.linkedin.backend.features.profile.repository.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProfileService {
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;
    private final SkillRepository skillRepository;
    private final AuthenticationService authenticationService;

    public ProfileService(ExperienceRepository experienceRepository,
                          EducationRepository educationRepository,
                          SkillRepository skillRepository,
                          AuthenticationService authenticationService) {
        this.experienceRepository = experienceRepository;
        this.educationRepository = educationRepository;
        this.skillRepository = skillRepository;
        this.authenticationService = authenticationService;
    }

    // --- EXPERIENCE METHODS ---

    public List<Experience> getUserExperiences(Long userId) {
        return experienceRepository.findByUserIdOrderByStartDateDesc(userId);
    }

    @Transactional
    public Experience addExperience(Long userId, ExperienceDto experienceDto) {
        User user = authenticationService.getUserById(userId);

        Experience experience = new Experience();
        experience.setUser(user);
        experience.setTitle(experienceDto.title());
        experience.setCompany(experienceDto.company());
        experience.setLocation(experienceDto.location());
        experience.setCurrentPosition(experienceDto.currentPosition());
        experience.setStartDate(experienceDto.startDate());
        experience.setEndDate(experienceDto.endDate());
        experience.setDescription(experienceDto.description());

        return experienceRepository.save(experience);
    }

    @Transactional
    public Experience updateExperience(Long experienceId, Long userId, ExperienceDto experienceDto) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found"));

        if (!experience.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to update this experience");
        }

        experience.setTitle(experienceDto.title());
        experience.setCompany(experienceDto.company());
        experience.setLocation(experienceDto.location());
        experience.setCurrentPosition(experienceDto.currentPosition());
        experience.setStartDate(experienceDto.startDate());
        experience.setEndDate(experienceDto.endDate());
        experience.setDescription(experienceDto.description());

        return experienceRepository.save(experience);
    }

    @Transactional
    public void deleteExperience(Long experienceId, Long userId) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new IllegalArgumentException("Experience not found"));

        if (!experience.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to delete this experience");
        }

        experienceRepository.delete(experience);
    }

    // --- EDUCATION METHODS ---

    public List<Education> getUserEducations(Long userId) {
        return educationRepository.findByUserIdOrderByStartDateDesc(userId);
    }

    @Transactional
    public Education addEducation(Long userId, EducationDto educationDto) {
        User user = authenticationService.getUserById(userId);

        Education education = new Education();
        education.setUser(user);
        education.setSchool(educationDto.school());
        education.setDegree(educationDto.degree());
        education.setFieldOfStudy(educationDto.fieldOfStudy());
        education.setStartDate(educationDto.startDate());
        education.setEndDate(educationDto.endDate());
        education.setDescription(educationDto.description());

        return educationRepository.save(education);
    }

    @Transactional
    public Education updateEducation(Long educationId, Long userId, EducationDto educationDto) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new IllegalArgumentException("Education not found"));

        if (!education.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to update this education");
        }

        education.setSchool(educationDto.school());
        education.setDegree(educationDto.degree());
        education.setFieldOfStudy(educationDto.fieldOfStudy());
        education.setStartDate(educationDto.startDate());
        education.setEndDate(educationDto.endDate());
        education.setDescription(educationDto.description());

        return educationRepository.save(education);
    }

    @Transactional
    public void deleteEducation(Long educationId, Long userId) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new IllegalArgumentException("Education not found"));

        if (!education.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to delete this education");
        }

        educationRepository.delete(education);
    }

    // --- SKILL METHODS ---

    public List<Skill> getUserSkills(Long userId) {
        return skillRepository.findByUserIdOrderByEndorsementsDesc(userId);
    }

    @Transactional
    public Skill addSkill(Long userId, SkillDto skillDto) {
        User user = authenticationService.getUserById(userId);

        Skill skill = new Skill();
        skill.setUser(user);
        skill.setName(skillDto.name());
        skill.setEndorsements(0); // New skills start with 0 endorsements

        return skillRepository.save(skill);
    }

    @Transactional
    public Skill updateSkill(Long skillId, Long userId, SkillDto skillDto) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));

        if (!skill.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to update this skill");
        }

        skill.setName(skillDto.name());
        // We don't update endorsements through this method

        return skillRepository.save(skill);
    }

    @Transactional
    public void deleteSkill(Long skillId, Long userId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));

        if (!skill.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to delete this skill");
        }

        skillRepository.delete(skill);
    }

    @Transactional
    public Skill endorseSkill(Long skillId, Long endorserId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));

        // Can't endorse your own skill
        if (skill.getUser().getId().equals(endorserId)) {
            throw new IllegalArgumentException("Cannot endorse your own skill");
        }

        skill.setEndorsements(skill.getEndorsements() + 1);
        return skillRepository.save(skill);
    }
}