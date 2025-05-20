package com.linkedin.backend.features.profile.dto;

import java.time.LocalDate;

public record ExperienceDto(
        String title,
        String company,
        String location,
        LocalDate startDate,
        LocalDate endDate,
        boolean currentPosition,
        String description
) {}