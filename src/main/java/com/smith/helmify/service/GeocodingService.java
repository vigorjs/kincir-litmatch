package com.smith.helmify.service;

import com.smith.helmify.utils.dto.MachineResponseDTO;

public interface GeocodingService {
    MachineResponseDTO.GeometryDTO getGeometryFromLocation(String location);
}
