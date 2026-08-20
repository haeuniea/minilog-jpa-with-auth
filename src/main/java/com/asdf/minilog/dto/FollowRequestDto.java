package com.asdf.minilog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowRequestDto {
    @Deprecated(since = "2.0", forRemoval = true)
    @Schema(
            description = "팔로워 ID (이 필드는 더 이상 사용되지 않습니다.)",
            example = "0",
            required = true,
            deprecated = true)
    private Long followerId;

    @NonNull private Long followeeId;
}
