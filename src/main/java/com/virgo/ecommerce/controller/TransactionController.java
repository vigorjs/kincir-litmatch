package com.virgo.ecommerce.controller;

import com.virgo.ecommerce.service.MidtransService;
import com.virgo.ecommerce.service.TransactionService;
import com.virgo.ecommerce.utils.dto.restClientDto.MidtransRequestDTO;
import com.virgo.ecommerce.utils.responseWrapper.Response;
import com.virgo.ecommerce.utils.responseWrapper.WebResponse;
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
@Tag(name = "Transaction", description = "Transaction management APIs")
public class TransactionController {
    private final TransactionService transactionService;
    private final MidtransService midtransService;

    @Operation(summary = "Create a new transaction", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = WebResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @PostMapping("/transactions")
    public ResponseEntity<?> create(@Valid @RequestBody MidtransRequestDTO req) {
        return Response.renderJSON(
                transactionService.create(req),
                "Transaction berhasil dibuat!",
                HttpStatus.CREATED
        );
    }
}
