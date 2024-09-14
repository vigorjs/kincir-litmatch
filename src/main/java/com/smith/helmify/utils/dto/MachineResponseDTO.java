package com.smith.helmify.utils.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineResponseDTO {
    private String id;
    private String location;
    private String status;
    private String ipAddress;
    private GeometryDTO geometry;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeometryDTO {
        private LatLng location;
    }

    @Data
    @AllArgsConstructor
    public static class LatLng {
        private double lat;
        private double lng;
    }
}
