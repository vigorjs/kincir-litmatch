package com.example.kincir.utils.specifications;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder(toBuilder = true)
public class SearchCriteria {

    private List<Filter> filters;

    @Getter
    @Builder(toBuilder = true)
    public static class Filter {
        public enum QueryOperator {
            EQUALS, NOT_EQUALS, LIKE, LESS_THAN, GREATER_THAN, GREATER_THAN_OR_EQUALS
        }

        private String field;
        private QueryOperator operator;
        private Object value;
    }
}