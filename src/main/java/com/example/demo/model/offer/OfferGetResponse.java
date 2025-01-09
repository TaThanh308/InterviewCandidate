package com.example.demo.model.offer;

import lombok.Data;

import java.util.List;
@Data
public class OfferGetResponse {
    private List<OfferGetResponseHome> offerList;
    private Integer totalPage;
    private Integer totalElements;

    @Data
    public static class OfferGetResponseHome{
        private String candidateName;
        private String email;
        private String approver;
        private String deparment;
        private String notes;
        private String status;
    }
}