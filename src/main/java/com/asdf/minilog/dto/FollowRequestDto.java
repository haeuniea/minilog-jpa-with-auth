package com.asdf.minilog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowRequestDto {
    @NonNull private Long followerId;
    @NonNull private Long followeeId;
}
