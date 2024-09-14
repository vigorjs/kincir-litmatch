package com.smith.helmify.service;

import com.smith.helmify.utils.dto.ServiceRequestDTO;
import com.smith.helmify.utils.dto.ServiceDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ServiceService {
    ServiceDTO create(ServiceRequestDTO req) throws IOException;
//    ServiceDTO create(Serv req) throws IOException;
    List<ServiceDTO> getAll(String machineId);
    ServiceDTO getById(Integer id);
    List<ServiceDTO> getByMachineId(String machineId);
    List<ServiceDTO> getByUserId(Integer userId);
    void delete(Integer id);
    ServiceDTO updateById(Integer id, ServiceRequestDTO req, MultipartFile multipartFile) throws IOException;
}