package com.example.kincir.controller;

import com.example.kincir.service.LitatomService;
import com.example.kincir.utils.dto.response.RoundResultResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/litatom")
@RequiredArgsConstructor
@RestControllerAdvice
@Tag(name = "Litatom", description = "Litatom management APIs")
public class LitatomController {
    private final LitatomService litatomService;

    @Operation(summary = "Get Round Result")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = RoundResultResponseDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/getRoundResult/{roundTime}")
    public RoundResultResponseDTO findAll(@PathVariable Integer roundTime) {
        return litatomService.getRoundResultByRoundTime(roundTime);
    }
}
