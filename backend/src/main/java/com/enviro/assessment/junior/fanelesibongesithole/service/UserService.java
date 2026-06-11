package com.enviro.assessment.junior.fanelesibongesithole.service;

import com.enviro.assessment.junior.fanelesibongesithole.domain.BusinessRules;
import com.enviro.assessment.junior.fanelesibongesithole.dto.ProfileDetailDto;
import com.enviro.assessment.junior.fanelesibongesithole.dto.ProfileUpdateRequest;
import com.enviro.assessment.junior.fanelesibongesithole.dto.UserProfileDto;
import com.enviro.assessment.junior.fanelesibongesithole.entity.UserEntity;
import com.enviro.assessment.junior.fanelesibongesithole.exception.ApiException;
import com.enviro.assessment.junior.fanelesibongesithole.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    public UserService(CurrentUserService currentUserService,
                       UserRepository userRepository) {
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
    }

    public UserProfileDto getProfile() {
        return UserProfileMapper.toSummary(currentUserService.requireCurrentUser());
    }

    public ProfileDetailDto getProfileDetail() {
        return toDetail(currentUserService.requireCurrentUser());
    }

    @Transactional
    public ProfileDetailDto updateProfile(ProfileUpdateRequest request) {
        UserEntity user = userRepository.findById(currentUserService.requireCurrentUser().getId())
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        if (request.firstName() != null && !request.firstName().isBlank()) {
            user.setFirstName(request.firstName().trim());
        }
        if (request.lastName() != null && !request.lastName().isBlank()) {
            user.setLastName(request.lastName().trim());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone().trim());
        }
        if (request.firmName() != null) {
            user.setFirmName(request.firmName().trim());
        }
        if (request.bio() != null) {
            user.setBio(request.bio().trim());
        }
        if (request.twoFactorEnabled() != null) {
            user.setTwoFactorEnabled(request.twoFactorEnabled());
            user.setSecurityProgress(request.twoFactorEnabled() ? 100 : 75);
        }
        if (request.notifyPortfolio() != null) user.setNotifyPortfolio(request.notifyPortfolio());
        if (request.notifyWithdrawals() != null) user.setNotifyWithdrawals(request.notifyWithdrawals());
        if (request.notifyCompliance() != null) user.setNotifyCompliance(request.notifyCompliance());
        if (request.notifyReports() != null) user.setNotifyReports(request.notifyReports());
        if (request.notifyMarketing() != null) user.setNotifyMarketing(request.notifyMarketing());

        return toDetail(userRepository.save(user));
    }

    private ProfileDetailDto toDetail(UserEntity user) {
        int age = user.getAge();
        return new ProfileDetailDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone() != null ? user.getPhone() : "",
                user.getFirmName() != null ? user.getFirmName() : "",
                user.getRole(),
                user.getBio() != null ? user.getBio() : "",
                age,
                age > BusinessRules.RETIREMENT_MIN_AGE,
                user.isVerified(),
                user.getJoinedYear(),
                user.getSecurityProgress(),
                user.isTwoFactorEnabled(),
                user.isNotifyPortfolio(),
                user.isNotifyWithdrawals(),
                user.isNotifyCompliance(),
                user.isNotifyReports(),
                user.isNotifyMarketing()
        );
    }
}
