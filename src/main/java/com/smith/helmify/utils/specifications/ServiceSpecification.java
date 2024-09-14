package com.smith.helmify.utils.specifications;

import com.smith.helmify.model.meta.Service;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ServiceSpecification {

    public static Specification<Service> getSpecification(Integer machineId){
//        List<>

        return (root, query, criteriaBuilder) -> {
            if(machineId != null){
                return criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("machine_id")),
                        "%" + machineId + "%"
                );
            } else {
                return criteriaBuilder.conjunction();
            }
        };
    }
}
