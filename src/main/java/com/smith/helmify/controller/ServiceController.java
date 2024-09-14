package com.smith.helmify.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smith.helmify.service.ServiceService;
import com.smith.helmify.utils.dto.ServiceRequestDTO;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@RestControllerAdvice
@Tag(name = "Service", description = "Service management APIs")
public class ServiceController {
    private final ServiceService serviceService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "Get all services", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = WebResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/services")
    public ResponseEntity<?> findAll() {
        return Response.renderJSON(serviceService.getAll());
    }

    @Operation(summary = "Get service by id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = WebResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/services/{id}")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        return Response.renderJSON(serviceService.getById(id));
    }

    @Operation(summary = "Get service by machineId", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = WebResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/services?machineId={machineId}")
    public ResponseEntity<?> findByMachineId(@PathVariable String machineId) {
        return Response.renderJSON(serviceService.getByMachineId(machineId));
    }

    @Operation(summary = "Create a new service", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = WebResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PostMapping(path = "/services", consumes = "multipart/form-data")
    public ResponseEntity<?> create(
            @RequestPart("req") String req,  // Handles JSON request part
            @RequestPart("file") MultipartFile multipartFile  // Handles file part
    ) throws IOException {
        ServiceRequestDTO serviceRequestDTO = objectMapper.readValue(req, new TypeReference<>() {
        });
        serviceRequestDTO.setMultipartFile(multipartFile);
        return Response.renderJSON(
                serviceService.create(serviceRequestDTO),
//                serviceRequestDTO,
                "Service berhasil dibuat!",
                HttpStatus.CREATED
        );
    }

    @Operation(summary = "Update service by id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = WebResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @PutMapping(path="/services/{id}",consumes = "multipart/form-data")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @ModelAttribute ServiceRequestDTO req, @RequestPart("file") MultipartFile multipartFile) throws IOException{
//        updateById
        return Response.renderJSON(
                serviceService.updateById(id, req, multipartFile),
                "Service Updated",
                HttpStatus.OK
        );
    }

    @Operation(summary = "Delete service by id", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success!", content = {@Content(schema = @Schema(implementation = WebResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {@Content(schema = @Schema())})
    })
    @DeleteMapping("/services/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Integer id) {
//        delete
        serviceService.delete(id);
        return Response.renderJSON(null,"Service berhasil dihapus", HttpStatus.OK);
    }
}
