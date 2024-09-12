package com.smith.helmify.service.impl;

import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.smith.helmify.service.GeocodingService;
import com.smith.helmify.utils.dto.MachineResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class GeocodingServiceImpl implements GeocodingService {

    private final GeoApiContext geoApiContext;

    @Override
    public MachineResponseDTO.GeometryDTO getGeometryFromLocation(String location) {
        try {
            GeocodingResult[] results = GeocodingApi.geocode(geoApiContext, location).await();

            if (results != null && results.length > 0) {
                GeocodingResult result = results[0];
                double lat = result.geometry.location.lat;
                double lng = result.geometry.location.lng;

                MachineResponseDTO.GeometryDTO geometryDTO = new MachineResponseDTO.GeometryDTO();
                geometryDTO.setLocation(new MachineResponseDTO.LatLng(lat, lng));
                return geometryDTO;
            } else {
                return null;
            }
        } catch (ApiException | InterruptedException | IOException e) {
            System.out.println("Error geocoding: " + e.getMessage());
            return null;
        }
    }
}
