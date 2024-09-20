package com.smith.helmify.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smith.helmify.model.enums.MachineStatus;
import com.smith.helmify.model.meta.Machine;
import com.smith.helmify.service.IotService;
import com.smith.helmify.service.MachineService;
import com.smith.helmify.utils.dto.MachineRequestDTO;
import com.smith.helmify.utils.dto.restClientDto.IotRequestDTO;
import com.smith.helmify.utils.dto.restClientDto.IotResponseDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IotServiceImpl implements IotService {

    private final RestTemplate restTemplate;
    private final HttpHeaders headers;
    private final MachineService machineService;

    public IotServiceImpl(RestTemplate restTemplate, HttpHeaders headers, MachineService machineService) {
        this.restTemplate = restTemplate;
        this.headers = headers;
        this.machineService = machineService;
    }

    @Override
    public void IotAction(IotRequestDTO req) {
        try {
            Machine machine = machineService.getById(req.getMachine_id());
            String uriMachine = "http://" + machine.getIpAddress();

            // Set content type
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Convert request to JSON string
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(req);

            // Create HttpEntity containing headers and the body
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

            // Send POST request
            ResponseEntity<IotResponseDTO> responseEntity = restTemplate.postForEntity(
                    uriMachine,
                    requestEntity,
                    IotResponseDTO.class
            );

            IotResponseDTO iotResponseDTO = responseEntity.getBody();

            if (iotResponseDTO != null && "success".equals(iotResponseDTO.getStatus())) {
                machineService.updateById(machine.getId(), MachineRequestDTO.builder().ipAddress("").location("").status(String.valueOf(MachineStatus.WORKING)).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error in IotAction(): " + e.getMessage(), e);
        }
    }
}
