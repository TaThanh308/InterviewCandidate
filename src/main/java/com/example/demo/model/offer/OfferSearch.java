package com.example.demo.model.offer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OfferSearch {
    @JsonProperty("data")
    private String[] data;

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }
}
