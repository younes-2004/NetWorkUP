package com.linkedin.backend.features.profile.controller;

import com.linkedin.backend.dto.Response;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.profile.dto.*;
import com.linkedin.backend.features.profile.model.Education;
import com.linkedin.backend.features.profile.model.Experience;
import com.linkedin.backend.features.profile.model.Skill;
import com.linkedin.backend.features.profile.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // --- EXPERIENCE ENDPOINTS ---

    @GetMapping("/{userId}/experiences")
    public ResponseEntity<List<Experience>> getUserExperiences(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getUserExperiences(userId));
    }

    @PostMapping("/{userId}/experiences")
    public ResponseEntity<Experience> addExperience(
            @RequestAttribute("authenticatedUser") User user,
            @PathVariable Long userId,
            @RequestBody ExperienceDto experienceDto) {

        if (!user.getId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(profileService.addExperience(userId, experienceDto));
    }

    @PutMapping("/experiences/{experienceId}")
    public ResponseEntity<Experience> updateExperience(
            @RequestAttribute("authenticatedUser") User user,
            @PathVariable Long experienceId,
            @RequestBody ExperienceDto experienceDto) {

        try {
            return ResponseEntity.ok(profileService.updateExperience(experienceId, user.getId(), experienceDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @DeleteMapping("/experiences/{experienceId}")
    public ResponseEntity<Response> deleteExperience(
            @RequestAttribute("authenticatedUser") User user,
            @PathVariable Long experienceId) {

        try {
            profileService.deleteExperience(experienceId, user.getId());
            return ResponseEntity.ok(new Response("Experience deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).body(new Response(e.getMessage()));
        }
    }

    // --- EDUCATION ENDPOINTS ---

    @GetMapping("/{userId}/educations")
    public ResponseEntity<List<Education>> getUserEducations(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getUserEducations(userId));
    }

    @PostMapping("/{userId}/educations")
    public ResponseEntity<Education> addEducation(
            @RequestAttribute("authenticatedUser") User user,
            @PathVariable Long userId,
            @RequestBody EducationDto educationDto) {

        if (!user.getId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(profileService.addEducation(userId, educationDto));
    }

    @PutMapping("/educations/{educationId}")
    public ResponseEntity<Education> updateEducation(
            @RequestAttribute("authenticatedUser") User user,
            @PathVariable Long educationId,
            @RequestBody EducationDto educationDto) {

        try {
            return ResponseEntity.ok(profileService.updateEducation(educationId, user.getId(), educationDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @DeleteMapping("/educations/{educationId}")
    public ResponseEntity<Response> deleteEducation(
            @RequestAttribute("authenticatedUser") User user,
            @PathVariable Long educationId) {

        try {
            profileService.deleteEducation(educationId, user.getId());
            return ResponseEntity.ok(new Response("Education deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).body(new Response(e.getMessage()));
        }
    }

    // --- SKILL ENDPOINTS ---

    @GetMapping("/{userId}/skills")
    public ResponseEntity<List<Skill>> getUserSkills(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getUserSkills(userId));
    }

    @PostMapping("/{userId}/skills")
    public ResponseEntity<Skill> addSkill(
            @RequestAttribute("authenticatedUser") User user,
            @PathVariable Long userId,
            @RequestBody SkillDto skillDto) {

        if (!user.getId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(profileService.addSkill(userId, skillDto));
    }

    @PutMapping("/skills/{skillId}")
    public ResponseEntity<Skill> updateSkill(
            @RequestAttribute("authenticatedUser") User user,
            @PathVariable Long skillId,
            @RequestBody SkillDto skillDto) {

        try {
            return ResponseEntity.ok(profileService.updateSkill(skillId, user.getId(), skillDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @DeleteMapping("/skills/{skillId}")
    public ResponseEntity<Response> deleteSkill(
            @RequestAttribute("authenticatedUser") User user,
            @PathVariable Long skillId) {

        try {
            profileService.deleteSkill(skillId, user.getId());
            return ResponseEntity.ok(new Response("Skill deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).body(new Response(e.getMessage()));
        }
    }

    @PostMapping("/skills/{skillId}/endorse")
    public ResponseEntity<Skill> endorseSkill(
            @RequestAttribute("authenticatedUser") User user,
            @PathVariable Long skillId) {

        try {
            return ResponseEntity.ok(profileService.endorseSkill(skillId, user.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).build();
        }
    }
}