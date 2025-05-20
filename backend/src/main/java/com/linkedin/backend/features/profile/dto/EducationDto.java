package com.linkedin.backend.features.profile.dto;

import java.time.LocalDate;

public record EducationDto(
        String school,
        String degree,
        String fieldOfStudy,
        LocalDate startDate,
        LocalDate endDate,
        boolean currentlyStudying,
        String description
) {}