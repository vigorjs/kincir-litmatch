package com.example.kincir.service;

import com.example.kincir.model.meta.Round;

import java.util.List;

public interface RoundService {
    Round create(Round req);
    Round getById(Long id);
    List<Round> getAll(Long startFrom);
    Round updateById(Long id, Round req);
    void deleteById(Long id);
}
