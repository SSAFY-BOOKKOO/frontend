package com.ssafy.bookkoo.notificationservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RequestCreateCommunityNotificationDto(
    @NotNull(message = "Member ID can not be null")
    Long memberId,
    @NotNull(message = "Community ID can not be null")
    Long communityId
) {

}