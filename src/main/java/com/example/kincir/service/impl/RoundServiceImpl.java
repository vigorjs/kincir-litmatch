package com.example.kincir.service.impl;

import com.example.kincir.config.security.advisers.exception.NotFoundException;
import com.example.kincir.model.enums.UserRole;
import com.example.kincir.model.meta.Round;
import com.example.kincir.model.meta.User;
import com.example.kincir.repository.RoundRepository;
import com.example.kincir.service.AuthenticationService;
import com.example.kincir.service.RoundService;
import com.example.kincir.utils.specifications.GenericSpecification;
import com.example.kincir.utils.specifications.SearchCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoundServiceImpl implements RoundService {

    private final RoundRepository roundRepository;
    private final AuthenticationService authenticationService;
    private final GenericSpecification<Round> genericSpecification;

    @Override
    public Round create(Round req) {
//        Round round = Round.builder()
//                .roundTimes(req.getRoundTimes())
//                .start(req.getStart())
//                .now(req.getNow())
//                .end(req.getEnd())
//                .fileId(req.getFileId())
//                .build();
        return roundRepository.save(req);
    }

    @Override
    public Round getById(Long id) {
        User user = authenticationService.getUserAuthenticated();
        if (!user.getRole().equals(UserRole.ADMIN)) throw new NotFoundException("must be an admin");

        return roundRepository.findById(id).orElseThrow(() -> new NotFoundException("Round Not Found"));
    }

    @Override
    public List<Round> getAll(Long startFrom) {
        User user = authenticationService.getUserAuthenticated();
        List<SearchCriteria.Filter> filters = new ArrayList<>();

        if (startFrom != null) {
            filters.add(SearchCriteria.Filter.builder().field("end_time")
                    .operator(SearchCriteria.Filter.QueryOperator.GREATER_THAN_OR_EQUALS).value(startFrom).build());
        }

        // Membangun SearchCriteria dan Specification
        SearchCriteria searchCriteria = filters.isEmpty() ? null : SearchCriteria.builder().filters(filters).build();
        Specification<Round> specification =
                searchCriteria != null ? genericSpecification.buildSpecification(searchCriteria) : null;

        return roundRepository.findAll(specification);
    }

    @Override
    public Round updateById(Long id, Round req) {
        Round round = getById(id);

        round.setRoundTimes(req.getRoundTimes());
        round.setEnd(req.getEnd());
        round.setNow(req.getNow());
        round.setStart(req.getStart());
        round.setFileId(req.getFileId());

        return roundRepository.save(round);
    }

    @Override
    public void deleteById(Long id) {
        roundRepository.deleteById(id);
    }
}
