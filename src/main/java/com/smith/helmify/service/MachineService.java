package com.smith.helmify.service;

import com.smith.helmify.model.meta.Machine;
import com.smith.helmify.utils.dto.MachineRequestDTO;
import com.smith.helmify.utils.dto.MachineResponseDTO;

import java.util.List;


public interface MachineService {
    Machine create(MachineRequestDTO req);
    List<MachineResponseDTO> getAll();
    Machine getById(String id);
    void delete(String id);
    Machine updateById(String id, MachineRequestDTO req);
}
