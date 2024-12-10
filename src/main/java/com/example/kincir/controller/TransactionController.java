package com.example.kincir.controller;

import com.example.kincir.model.enums.TransactionStatus;
import com.example.kincir.model.meta.Transaction;
import com.example.kincir.model.meta.User;
import com.example.kincir.service.AuthenticationService;
import com.example.kincir.service.MidtransService;
import com.example.kincir.service.TransactionService;
import com.example.kincir.utils.dto.request.MidtransRequestDTO;
import com.example.kincir.utils.dto.request.MidtransSnapRequestDTO;
import com.example.kincir.utils.dto.response.MidtransResponseDTO;
import com.example.kincir.utils.responseWrapper.Response;
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
    private final AuthenticationService authenticationService;
    private final MidtransService midtransService;

    @Operation(summary = "Create a new transaction", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @PostMapping("/transactions")
    public ResponseEntity<?> create(@Valid @RequestBody MidtransRequestDTO req) {
        return Response.success(
                HttpStatus.CREATED,
                "Transaction berhasil dibuat!",
                transactionService.create(req)
        );
    }

    @Operation(summary = "Create a new Snap transaction", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @PostMapping("/transactions-snap")
    public ResponseEntity<?> createSnap(@Valid @RequestBody MidtransSnapRequestDTO req) {
        return Response.success(
                HttpStatus.CREATED,
                "Transaction berhasil dibuat!",
                transactionService.createSnap(req)
        );
    }

    @Operation(summary = "Refresh and Update Transaction", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @PostMapping("/transaction-refresh/{machine_id}")
    public void refreshAndUpdateTransaction(@RequestBody Transaction req, @PathVariable String machine_id) {
        transactionService.refreshAndUpdateTransactionStatus(req, machine_id);
    }

    @Operation(summary = "Cancel Transaction", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @PostMapping("/transaction-cancel/{order_id}")
    public ResponseEntity<?> cancelTransaction(@PathVariable String order_id) {
        MidtransResponseDTO res = midtransService.changeStatus(order_id, TransactionStatus.cancel.name());
        return Response.success(
                HttpStatus.CREATED,
                "Transaction berhasil dibuat!",
                res
        );
    }

    @Operation(summary = "Get all transactions", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/transactions")
    public ResponseEntity<?> findAll(@RequestParam(required = false) Integer userId) {
        return Response.success(transactionService.getAll(userId));
    }

    @Operation(summary = "Get transaction by id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/transactions/{id}")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        return Response.success(transactionService.getById(id));
    }

    @Operation(summary = "Get transaction by user logged in", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/transactions-user")
    public ResponseEntity<?> findByUserLoggedIn() {
        User user = authenticationService.getUserAuthenticated();
        return Response.success(transactionService.getAll(user.getId()));
    }
}
