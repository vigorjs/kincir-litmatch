package com.smith.helmify.service;

import com.smith.helmify.model.meta.Machine;
import com.smith.helmify.utils.dto.restClientDto.IotRequestDTO;
import com.smith.helmify.utils.dto.restClientDto.IotResponseDTO;

public interface IotService {

    void IotAction(IotRequestDTO req);
}
