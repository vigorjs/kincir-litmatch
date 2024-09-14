package com.smith.helmify.service.impl;

import com.smith.helmify.config.advisers.exception.NotFoundException;
import com.smith.helmify.model.enums.MachineStatus;
import com.smith.helmify.model.meta.Machine;
import com.smith.helmify.repo.MachineRepository;
import com.smith.helmify.service.GeocodingService;
import com.smith.helmify.service.MachineService;
import com.smith.helmify.utils.dto.MachineRequestDTO;
import com.smith.helmify.utils.dto.MachineResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MachineServiceImpl implements MachineService {
    private final MachineRepository machineRepository;
    private final GeocodingService geocodingService;

    @Override
    public Machine create(MachineRequestDTO req) {
        Machine machine = Machine.builder()
                .location(req.getLocation())
                .ipAddress(req.getIpAddress())
                .status(String.valueOf(MachineStatus.valueOf(req.getStatus())))
                .build();

        machineRepository.save(machine);
        return machine;
    }

    @Override
    public List<MachineResponseDTO> getAll() {
        return machineRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Machine getById(Integer id) {
        return machineRepository.findById(id).orElseThrow(() -> new NotFoundException("machine not found"));
    }

    @Override
    public void delete(Integer id) {
        Machine machine = getById(id);
        machineRepository.delete(machine);
    }

    @Override
    public Machine updateById(Integer id, MachineRequestDTO req) {
        Machine machine = getById(id);
        machine.setLocation(req.getLocation());
        machine.setIpAddress(req.getIpAddress());
        machine.setStatus(String.valueOf(MachineStatus.valueOf(req.getStatus())));
        machineRepository.save(machine);
        return machine;
    }

    private MachineResponseDTO mapToResponse(Machine machine) {
        MachineResponseDTO.GeometryDTO geometry = geocodingService.getGeometryFromLocation(machine.getLocation());

        return MachineResponseDTO.builder()
                .id(machine.getId())
                .location(machine.getLocation())
                .status(machine.getStatus())
                .ipAddress(machine.getIpAddress())
                .geometry(geometry)
                .build();
    }
}
