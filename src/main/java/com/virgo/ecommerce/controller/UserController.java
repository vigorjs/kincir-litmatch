package com.virgo.ecommerce.controller;

import com.virgo.ecommerce.service.AuthenticationService;
import com.virgo.ecommerce.service.UserService;
import com.virgo.ecommerce.utils.dto.RegisterRequestDTO;
import com.virgo.ecommerce.utils.responseWrapper.Response;
import com.virgo.ecommerce.utils.responseWrapper.WebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@RestControllerAdvice
@Tag(name = "User", description = "User management APIs")
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Get all users", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = WebResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/users")
    public ResponseEntity<?> findAll() {
        return Response.renderJSON(userService.getAll());
    }

    @Operation(summary = "Get user by id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = WebResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        return Response.renderJSON(userService.getById(id));
    }

    @Operation(summary = "Create a new user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = WebResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @PostMapping("/users")
    public ResponseEntity<?> create(@RequestBody RegisterRequestDTO req) {
        return Response.renderJSON(
                userService.create(req),
                "User berhasil dibuat!",
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "Update user by id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = WebResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @PutMapping("/users/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody RegisterRequestDTO req) {
//        updateById
        return Response.renderJSON(
                userService.updateById(id, req),
                "User Updated",
                HttpStatus.OK
        );
    }

    @Operation(summary = "Delete user by id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = WebResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Integer id) {
//        delete
        userService.delete(id);
        return Response.renderJSON(null,"User berhasil dihapus", HttpStatus.OK);

    }

    @Operation(summary = "Get users by user id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = WebResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/user")
    public ResponseEntity<?> getUser() {
//        getUserAuth
        return Response.renderJSON(
                authenticationService.getUserAuthenticated(),
                "Success get User Authenticated",
                HttpStatus.OK
        );
    }

    @Operation(summary = "Update profile user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = WebResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @PutMapping("/users")
    public ResponseEntity<?> updateUserProfile(@RequestBody RegisterRequestDTO req) {
//        update
        return Response.renderJSON(
                userService.update(req),
                "Profile Updated",
                HttpStatus.OK
        );
    }
}
