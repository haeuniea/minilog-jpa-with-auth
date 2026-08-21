package com.asdf.minilog.controller;

import com.asdf.minilog.dto.ArticleRequestDto;
import com.asdf.minilog.dto.ArticleResponseDto;
import com.asdf.minilog.security.MinilogUserDetails;
import com.asdf.minilog.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/article")
public class ArticleController {

    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping
    @Operation(summary = "포스트 생성")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "404", description = "사용자 없음")
    })
    public ResponseEntity<ArticleResponseDto> createArticle(
            @AuthenticationPrincipal MinilogUserDetails userDetails,
            @RequestBody ArticleRequestDto article) {
        ArticleResponseDto createdArticle =
                articleService.createArticle(article.getContent(), userDetails.getId());
        return ResponseEntity.ok(createdArticle);
    }

    @GetMapping("/{articleId}")
    @Operation(summary = "포스트 조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "404", description = "포스트 없음")
    })
    public ResponseEntity<ArticleResponseDto> getArticle(@PathVariable Long articleId) {
        var article = articleService.getArticleById(articleId);
        return ResponseEntity.ok(article);
    }

    @PutMapping("/{articleId}")
    @Operation(summary = "포스트 수정")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "404", description = "포스트 없음")
    })
    public ResponseEntity<ArticleResponseDto> updateArticle(
            @AuthenticationPrincipal MinilogUserDetails userDetails,
            @PathVariable Long articleId, @RequestBody ArticleRequestDto article) {
        ArticleResponseDto updatedArticle = articleService.updateArticle(userDetails.getId(), articleId, article.getContent());
        return ResponseEntity.ok(updatedArticle);
    }

    @DeleteMapping("/{articleId}")
    @Operation(summary = "포스트 삭제")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "삭제됨"),
        @ApiResponse(responseCode = "404", description = "포스트 없음")
    })
    public ResponseEntity<Void> deleteArticle(
            @AuthenticationPrincipal MinilogUserDetails userDetails,
            @PathVariable Long articleId) {
        articleService.deleteArticle(userDetails.getId(), articleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "유저의 게시글 조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    public ResponseEntity<List<ArticleResponseDto>> getArticleByUserId(
            @RequestParam Long authorId) {
        var articleList = articleService.getArticleListByUserId(authorId);
        return ResponseEntity.ok(articleList);
    }
}
