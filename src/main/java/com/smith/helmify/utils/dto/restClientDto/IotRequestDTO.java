package com.smith.helmify.utils.dto.restClientDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IotRequestDTO {

    private String sabun;
    private String parfum;
    private String machine_id;

}
