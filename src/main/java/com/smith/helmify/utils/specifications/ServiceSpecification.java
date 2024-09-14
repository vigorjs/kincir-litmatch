package com.smith.helmify.utils.specifications;

import com.smith.helmify.model.meta.Service;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ServiceSpecification {

    public static Specification<Service> getSpecification(String machineId){
        List<Predicate> predicates = new ArrayList<>();

        return (root, query, criteriaBuilder) -> {
            if(machineId != null && !machineId.isEmpty()){
                predicates.add(criteriaBuilder.like(root.get("machine_id"), "%" + machineId + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
