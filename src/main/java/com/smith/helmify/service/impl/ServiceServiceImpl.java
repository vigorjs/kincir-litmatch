package com.smith.helmify.service.impl;

import com.smith.helmify.config.advisers.exception.NotFoundException;
import com.smith.helmify.model.meta.Service;

import com.smith.helmify.model.meta.ServiceStock;
import com.smith.helmify.model.meta.User;
import com.smith.helmify.repo.ServiceRepository;
import com.smith.helmify.repo.ServiceStockRepository;
import com.smith.helmify.service.AuthenticationService;
import com.smith.helmify.service.ServiceService;
import com.smith.helmify.utils.dto.ServiceRequestDTO;
import com.smith.helmify.utils.dto.ServiceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {
    private final ServiceRepository serviceRepository;
    private final AuthenticationService authenticationService;
    private final ServiceStockRepository serviceStockRepository;

    @Override
    @CachePut(value = "services", key = "#result.id")
    public ServiceDTO create(ServiceRequestDTO req) {
        User user = authenticationService.getUserAuthenticated();
        Integer reqStock = req.getStock() != null ? req.getStock() : 0;

        // Create service
        Service service = Service.builder()
                .user(user)
                .service_name(req.getService_name())
                .service_description(req.getService_description())
                .price(req.getPrice())
                .build();
        ServiceDTO serviceDTO = serviceRepository.save(service);
        Service savedService = serviceDTOToService(serviceDTO);

        ServiceDTO res = ServiceDTO.builder()
                .id(savedService.getId())
                .service_name(savedService.getService_name())
                .service_description(savedService.getService_description())
                .price(savedService.getPrice())
                .build();

        // create service_stocks
        serviceStockRepository.save(
                ServiceStock.builder()
                        .service(savedService)
                        .quantity(reqStock)
                        .build()
        );
        res.setStock(reqStock);

        return res;
    }

    @Override
    @Cacheable(value = "services")
    public List<ServiceDTO> getAll() {
        return serviceRepository.findAll();
    }

    @Override
    @Cacheable(value = "services", key = "#id")
    public ServiceDTO getById(Integer id) {
        return serviceRepository.findById(id).orElseThrow(() -> new NotFoundException("Service not found"));
    }

    @Override
    @Cacheable(value = "services", key = "#userId")
    public List<ServiceDTO> getByUserId(Integer userId) {
        return serviceRepository.findByUserId(userId);
    }

    @Override
    @CacheEvict(value = "services", key = "#id")
    public void delete(Integer id) {
        if (serviceRepository.findById(id).isPresent()) {
            serviceRepository.deleteById(id);
        } else {
            throw new NotFoundException("Service with ID " + id + " not found");
        }
    }

    @Override
    @CachePut(value = "services", key = "#id")
    public ServiceDTO updateById(Integer id, ServiceRequestDTO req) {
        ServiceDTO serviceDTO = getById(id);
//        serviceDTO.setService_stock_id(serviceDTO.getService_stock_id());
        serviceDTO.setService_name(req.getService_name() != null ? req.getService_name() : serviceDTO.getService_name());
        serviceDTO.setService_description(req.getService_description() != null ? req.getService_description() : serviceDTO.getService_description());
        serviceDTO.setPrice(req.getPrice() != null ? req.getPrice() : serviceDTO.getPrice());
        Service service = serviceDTOToService(serviceDTO);
        serviceRepository.update(service);

        //update stock
        if (req.getStock() != null){
            ServiceStock serviceStock= serviceStockRepository.findByServiceId(service.getId()).orElseThrow(() -> new NotFoundException("service stock not found"));
            serviceStock.setQuantity(req.getStock());
            serviceStockRepository.save(serviceStock);
            serviceDTO.setStock(serviceStock.getQuantity());
        }

        return serviceDTO;
    }

    private Service serviceDTOToService(ServiceDTO service){

        return Service.builder()
                .id(service.getId())
//                .serviceStock(serviceStockRepository.findById(service.getService_stock_id()).orElseThrow(() -> new NotFoundException("ServiceStockRepository Not Found")))
                .service_name(service.getService_name())
                .service_description(service.getService_description())
                .price(service.getPrice())
                .build();
    }
}