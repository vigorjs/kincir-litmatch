package com.smith.helmify.service;

import com.smith.helmify.model.meta.Review;
import com.smith.helmify.utils.dto.ReviewRequestDTO;

import java.util.List;

public interface ReviewService {

    Review create(ReviewRequestDTO req);
    List<Review> getAll();
    Review getById(Integer id);
    void delete(Integer id);
    Review updateById(Integer id, ReviewRequestDTO req);
}
