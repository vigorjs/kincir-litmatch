package com.smith.helmify.controller;

import com.smith.helmify.service.ReviewService;
import com.smith.helmify.utils.dto.ReviewRequestDTO;
import com.smith.helmify.utils.responseWrapper.Response;
import com.smith.helmify.utils.responseWrapper.WebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@RestControllerAdvice
@Tag(name = "Review", description = "Review management APIs")
public class ReviewController {
    private final ReviewService ReviewService;

    @Operation(summary = "Get all Reviews", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = WebResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/Reviews")
    public ResponseEntity<?> findAll() {
        return Response.renderJSON(ReviewService.getAll());
    }

    @Operation(summary = "Get Review by id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = WebResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/Reviews/{id}")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        return Response.renderJSON(ReviewService.getById(id));
    }

    @Operation(summary = "Create a new Review", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = WebResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @PostMapping("/Reviews")
    public ResponseEntity<?> create(@Valid @RequestBody ReviewRequestDTO req) {
        return Response.renderJSON(
                ReviewService.create(req),
                "Review berhasil dibuat!",
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "Update Review by id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = WebResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @PutMapping("/Reviews/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody ReviewRequestDTO req) {
//        updateById
        return Response.renderJSON(
                ReviewService.updateById(id, req),
                "Service Updated",
                HttpStatus.OK
        );
    }

    @Operation(summary = "Delete Review by id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = WebResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @DeleteMapping("/Reviews/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Integer id) {
        ReviewService.delete(id);
        return Response.renderJSON(null,"Review berhasil dihapus", HttpStatus.OK);
    }
}
