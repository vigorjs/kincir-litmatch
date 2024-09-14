package com.smith.helmify.repo;

import com.smith.helmify.model.meta.Service;
import com.smith.helmify.utils.dto.ServiceDTO;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository {
    ServiceDTO save(Service service);
    Optional<ServiceDTO> findById(Integer id);
    List<ServiceDTO> findAll(String machineId);
    void update(Service service);
    void deleteById(Integer id);
}