package com.smith.helmify.service.impl;

import com.smith.helmify.config.advisers.exception.NotFoundException;
import com.smith.helmify.model.meta.Review;
import com.smith.helmify.model.meta.User;
import com.smith.helmify.repo.ReviewRepository;
import com.smith.helmify.service.AuthenticationService;
import com.smith.helmify.service.ReviewService;
import com.smith.helmify.utils.dto.ReviewRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final AuthenticationService authenticationService;

    @Override
    public Review create(ReviewRequestDTO req) {
        User user = authenticationService.getUserAuthenticated();

        Review review = Review.builder()
                .user(user)
                .stars(req.getStars())
                .title(req.getTitle())
                .description(req.getDescription())
                .build();

        reviewRepository.save(review);
        return review;
    }

    @Override
    public List<Review> getAll() {
        return reviewRepository.findAll();
    }

    @Override
    public Review getById(Integer id) {
        return reviewRepository.findById(id).orElseThrow(() -> new NotFoundException("Review not found"));
    }

    @Override
    public void delete(Integer id) {
        Review review = getById(id);
        reviewRepository.delete(review);
    }

    @Override
    public Review updateById(Integer id, ReviewRequestDTO req) {
        Review review = getById(id);
        review.setTitle(req.getTitle());
        review.setStars(req.getStars());
        review.setDescription(req.getDescription());
        reviewRepository.save(review);
        return review;
    }


}
