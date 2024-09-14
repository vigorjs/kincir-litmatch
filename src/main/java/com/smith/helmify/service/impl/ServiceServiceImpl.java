package com.smith.helmify.service.impl;

import com.cloudinary.Cloudinary;
import com.smith.helmify.config.advisers.exception.NotFoundException;
import com.smith.helmify.model.enums.ServiceCategories;
import com.smith.helmify.model.meta.Machine;
import com.smith.helmify.model.meta.Service;

import com.smith.helmify.model.meta.ServiceStock;
import com.smith.helmify.model.meta.User;
import com.smith.helmify.repo.ServiceRepository;
import com.smith.helmify.repo.ServiceStockRepository;
import com.smith.helmify.service.AuthenticationService;
import com.smith.helmify.service.MachineService;
import com.smith.helmify.service.ServiceService;
import com.smith.helmify.utils.dto.ServiceRequestDTO;
import com.smith.helmify.utils.dto.ServiceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {
    private final ServiceRepository serviceRepository;
    private final AuthenticationService authenticationService;
    private final MachineService machineService;
    private final ServiceStockRepository serviceStockRepository;
    private final Cloudinary cloudinary;

    @Override
    @CacheEvict(value = "services", allEntries = true)
    public ServiceDTO create(ServiceRequestDTO req) throws IOException {
        User user = authenticationService.getUserAuthenticated();
        Machine machine = machineService.getById(req.getMachine_id());
        Integer reqStock = req.getStock() != null ? req.getStock() : 0;

        File convFile = new File(req.getMultipartFile().getOriginalFilename());
        FileOutputStream fos = new FileOutputStream( convFile );
        fos.write(req.getMultipartFile().getBytes());
        fos.close();

        String photo = cloudinary.uploader()
                .upload(convFile, Map.of("public_id", "profile" + req.getService_name() + "-" + UUID.randomUUID()
                ))
                .get("url")
                .toString();

        convFile.delete();

        // Create service
        Service service = Service.builder()
                .user(user)
                .service_name(req.getService_name())
                .service_description(req.getService_description())
                .price((Long) req.getPrice())
                .category(String.valueOf(ServiceCategories.valueOf(req.getCategory())))
                .imageUrl(photo)
                .machine(machine)
                .build();

        ServiceDTO serviceDTO = serviceRepository.save(service);
        Service savedService = serviceDTOToService(serviceDTO);

        ServiceDTO res = ServiceDTO.builder()
                .id(savedService.getId())
                .service_name(savedService.getService_name())
                .image_url(savedService.getImageUrl())
                .machine_id(savedService.getMachine().getId())
                .service_description(savedService.getService_description())
                .category(savedService.getCategory())
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
    public List<ServiceDTO> getAll(String machineId) {
        return serviceRepository.findAll(machineId);
    }

    @Override
    @Cacheable(value = "services", key = "#id")
    public ServiceDTO getById(Integer id) {
        return serviceRepository.findById(id).orElseThrow(() -> new NotFoundException("Service not found"));
    }

    @Override
    @Cacheable(value = "services", key = "#machineId")
    public List<ServiceDTO> getByMachineId(String machineId) {
        return serviceRepository.findByMachineId(machineId);
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
    public ServiceDTO updateById(Integer id, ServiceRequestDTO req, MultipartFile multipartFile) throws IOException {
        User user = authenticationService.getUserAuthenticated();
        Machine machine;

        ServiceDTO serviceDTO = getById(id);

        if(req.getMachine_id() == null){
            machine = machineService.getById(serviceDTO.getMachine_id());
            serviceDTO.setMachine_id(req.getMachine_id());
        } else {
            machine = machineService.getById(req.getMachine_id());
            serviceDTO.setMachine_id(req.getMachine_id());
        }

        if(serviceDTO.getImage_url() != null && multipartFile != null && !multipartFile.isEmpty()){
            String oldPhoto = serviceDTO.getImage_url();
            String oldCloudId = oldPhoto.substring(oldPhoto.lastIndexOf('/') + 1, oldPhoto.lastIndexOf('.'));

            cloudinary.uploader().destroy(oldCloudId, Map.of());

            File convfile = new File(multipartFile.getOriginalFilename());
            FileOutputStream fos = new FileOutputStream(convfile);
            fos.write(multipartFile.getBytes());
            fos.close();

            String photo = cloudinary.uploader()
                    .upload(convfile, Map.of("public_id", "profile" + serviceDTO.getService_name() + "-" + UUID.randomUUID()
                    ))
                    .get("url")
                    .toString();

            convfile.delete();
            serviceDTO.setImage_url(photo);
        } else {
            File convFile = new File(multipartFile.getOriginalFilename());
            FileOutputStream fos = new FileOutputStream( convFile );
            fos.write(multipartFile.getBytes());
            fos.close();

            String photo = cloudinary.uploader()
                    .upload(convFile, Map.of("public_id", "profile" + serviceDTO.getService_name() + "-" + UUID.randomUUID()
                    ))
                    .get("url")
                    .toString();

            convFile.delete();
            serviceDTO.setImage_url(photo);
        }

//        serviceDTO.setService_stock_id(serviceDTO.getService_stock_id());
        serviceDTO.setService_name(req.getService_name() != null ? req.getService_name() : serviceDTO.getService_name());
        serviceDTO.setService_description(req.getService_description() != null ? req.getService_description() : serviceDTO.getService_description());
        serviceDTO.setPrice(req.getPrice() != null ? req.getPrice() : serviceDTO.getPrice());
        serviceDTO.setCategory(req.getCategory() != null ? req.getCategory() : serviceDTO.getCategory());
        Service service = serviceDTOToService(serviceDTO);
        service.setUser(user);
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
                .machine(machineService.getById(service.getMachine_id()))
                .service_description(service.getService_description())
                .price(service.getPrice())
                .imageUrl(service.getImage_url())
                .category(service.getCategory())
                .build();
    }
}