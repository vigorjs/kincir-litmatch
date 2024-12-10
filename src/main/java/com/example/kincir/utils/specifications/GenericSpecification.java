package com.example.kincir.utils.specifications;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class GenericSpecification<T> {
    public Specification<T> buildSpecification(SearchCriteria searchCriteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> andPredicates = new ArrayList<>();
            List<Predicate> orPredicates = new ArrayList<>();

            if (Objects.nonNull(searchCriteria) && !CollectionUtils.isEmpty(searchCriteria.getFilters())) {
                for (SearchCriteria.Filter filter : searchCriteria.getFilters()) {
                    Predicate predicate = createPredicate(root, criteriaBuilder, filter);
                    if (predicate != null) {
                        if (filter.getField().endsWith(".verificationStatus")) {
                            orPredicates.add(predicate);
                        } else {
                            andPredicates.add(predicate);
                        }
                    }
                }
            }

            Objects.requireNonNull(query).distinct(true);

            Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[0]));
            Predicate orPredicate = orPredicates.isEmpty() ? criteriaBuilder.conjunction()
                    : criteriaBuilder.or(orPredicates.toArray(new Predicate[0]));

            return criteriaBuilder.and(andPredicate, orPredicate);
        };
    }

    private Predicate createPredicate(Root<T> root, CriteriaBuilder criteriaBuilder, SearchCriteria.Filter filter) {
        String[] fieldParts = filter.getField().split("\\.");

        Path<?> path = root;
        for (int i = 0; i < fieldParts.length - 1; i++) {
            if (fieldParts[i].equals("talentSkills")) { // ini khusus karna butuh join query dari getAllTalent filter by
                                                        // skill karna datanya nested dari talentSkills -> Skill ->
                                                        // skillName
                path = root.join(fieldParts[i], JoinType.LEFT);
            } else if (fieldParts[i].equals("clients") | fieldParts[i].equals("talents")
                    | fieldParts[i].equals("staffs")) {
                path = root.join(fieldParts[i], JoinType.LEFT);
            } else {
                path = path.get(fieldParts[i]);
            }
        }

        String finalField = fieldParts[fieldParts.length - 1];

        if (filter.getValue() == null) {
            return null;
        }

        switch (filter.getOperator()) {
        case EQUALS:
            return criteriaBuilder.equal(path.get(finalField), filter.getValue());

        case NOT_EQUALS:
            return criteriaBuilder.notEqual(path.get(finalField), filter.getValue());

        case LIKE:
            return criteriaBuilder.like(criteriaBuilder.lower(path.get(finalField).as(String.class)),
                    "%" + filter.getValue().toString().toLowerCase() + "%");

        case GREATER_THAN:
            return criteriaBuilder.greaterThanOrEqualTo(path.get(finalField).as(Comparable.class),
                    (Comparable) filter.getValue());

        case LESS_THAN:
            return criteriaBuilder.lessThanOrEqualTo(path.get(finalField).as(Comparable.class),
                    (Comparable) filter.getValue());

        default:
            return null;
        }
    }
}
