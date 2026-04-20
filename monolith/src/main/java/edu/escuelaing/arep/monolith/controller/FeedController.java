package edu.escuelaing.arep.monolith.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.escuelaing.arep.monolith.dto.response.ErrorResponse;
import edu.escuelaing.arep.monolith.dto.response.PostResponse;
import edu.escuelaing.arep.monolith.entity.Post;
import edu.escuelaing.arep.monolith.service.FeedService;
import edu.escuelaing.arep.monolith.util.mapper.PostMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;
    private final PostMapper postMapper;

    @GetMapping("/public")
    @Operation(summary = "Get public feed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Public feed returned", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostResponse.class)))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<PostResponse>> getPublicFeed() {
        List<Post> posts = feedService.getPublicFeed();

        return ResponseEntity.ok(postMapper.toPostResponseList(posts));
    }
}
