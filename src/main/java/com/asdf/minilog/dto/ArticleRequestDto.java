package com.asdf.minilog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleRequestDto {
    @NonNull private String content;
    @NonNull private Long authorId;
}
