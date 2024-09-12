package com.smith.helmify.service;

import com.smith.helmify.utils.dto.ServiceRequestDTO;
import com.smith.helmify.utils.dto.ServiceDTO;

import java.util.List;

public interface ServiceService {
    ServiceDTO create(ServiceRequestDTO req);
    List<ServiceDTO> getAll();
    ServiceDTO getById(Integer id);
    List<ServiceDTO> getByUserId(Integer userId);
    void delete(Integer id);
    ServiceDTO updateById(Integer id, ServiceRequestDTO req);
}