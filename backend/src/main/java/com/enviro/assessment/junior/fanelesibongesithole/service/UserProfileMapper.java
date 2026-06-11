package com.enviro.assessment.junior.fanelesibongesithole.service;

import com.enviro.assessment.junior.fanelesibongesithole.domain.BusinessRules;
import com.enviro.assessment.junior.fanelesibongesithole.dto.UserProfileDto;
import com.enviro.assessment.junior.fanelesibongesithole.entity.UserEntity;

final class UserProfileMapper {

    private UserProfileMapper() {}

    static UserProfileDto toSummary(UserEntity user) {
        int age = user.getAge();
        return new UserProfileDto(
                user.getFirstName(),
                user.getLastName(),
                user.getFirmName() != null ? user.getFirmName() : "Fanele & Partners",
                user.getRole(),
                age,
                age > BusinessRules.RETIREMENT_MIN_AGE
        );
    }
}
