package com.example.kincir.service;

import com.example.kincir.service.impl.LitatomServiceImpl;
import com.example.kincir.utils.dto.response.RoundInfoResponseDTO;
import com.example.kincir.utils.dto.response.RoundResultResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class LitatomServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Spy
    private HttpHeaders headers = new HttpHeaders();

    @InjectMocks
    private LitatomServiceImpl litatomService;

    private RoundInfoResponseDTO roundInfoResponseDTO;
    private RoundResultResponseDTO roundResultResponseDTO;

    @BeforeEach
    void setUp() {
        // Initialize headers
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Setup RoundInfoResponseDTO
        roundInfoResponseDTO = new RoundInfoResponseDTO();
        roundInfoResponseDTO.setSuccess(true);
        roundInfoResponseDTO.setResult(1);

        RoundInfoResponseDTO.Data infoData = new RoundInfoResponseDTO.Data();
        infoData.setEnd(1000L);
        infoData.setNow(500L);
        infoData.setRoundTimes(5);
        infoData.setStart(0L);
        roundInfoResponseDTO.setData(infoData);

        // Setup RoundResultResponseDTO
        roundResultResponseDTO = new RoundResultResponseDTO();
        roundResultResponseDTO.setSuccess(true);
        roundResultResponseDTO.setResult(1);

        RoundResultResponseDTO.Data resultData = new RoundResultResponseDTO.Data();
        resultData.setFileId("test-file-id");
        roundResultResponseDTO.setData(resultData);
    }

    @Test
    void getRoundInfo_Success() {
        // Arrange
        ResponseEntity<RoundInfoResponseDTO> responseEntity = new ResponseEntity<>(roundInfoResponseDTO, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(RoundInfoResponseDTO.class)
        )).thenReturn(responseEntity);

        // Act
        RoundInfoResponseDTO result = litatomService.getRoundInfo();

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getResult());
        assertEquals(1000L, result.getData().getEnd());
        assertEquals(500L, result.getData().getNow());
        assertEquals(5, result.getData().getRoundTimes());
        assertEquals(0L, result.getData().getStart());

        verify(restTemplate).exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(RoundInfoResponseDTO.class)
        );
    }

    @Test
    void getRoundInfo_ThrowsException() {
        // Arrange
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(RoundInfoResponseDTO.class)
        )).thenThrow(new RuntimeException("API Error"));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> litatomService.getRoundInfo());
        assertEquals("API Error", exception.getMessage());
    }

    @Test
    void getRoundResultByRoundTime_Success() {
        // Arrange
        ResponseEntity<RoundResultResponseDTO> responseEntity = new ResponseEntity<>(roundResultResponseDTO, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(RoundResultResponseDTO.class)
        )).thenReturn(responseEntity);

        // Act
        RoundResultResponseDTO result = litatomService.getRoundResultByRoundTime(5);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getResult());
        assertEquals("test-file-id", result.getData().getFileId());

        verify(restTemplate).exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(RoundResultResponseDTO.class)
        );
    }

    @Test
    void getRoundResultByRoundTime_ThrowsException() {
        // Arrange
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(RoundResultResponseDTO.class)
        )).thenThrow(new RuntimeException("API Error"));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class,
                () -> litatomService.getRoundResultByRoundTime(5));
        assertEquals("API Error", exception.getMessage());
    }
}