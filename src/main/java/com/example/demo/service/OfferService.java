package com.example.demo.service;

import com.example.demo.model.offer.OfferDto;
import com.example.demo.model.offer.OfferGetEntity;
import com.example.demo.model.offer.OfferGetViewDto;
import com.example.demo.model.offer.UserLogin;

import java.time.LocalDateTime;
import java.util.List;

public interface OfferService {

    public List<OfferGetEntity> findAllOffer();
    public String createOffer(OfferDto offerDto);
    public String updateOfferById(Integer id, OfferDto offerDto);
    public OfferGetViewDto getOfferView(Integer id);
    public List<List<String>> getOfferListDefaultEntity();
    public List<OfferGetEntity> getBySearch(String name, String department, String status);
    public String getOfferToExcel(LocalDateTime from, LocalDateTime to);
    public String getInterviewNote(String candidate,String interviewName);
    public void editStatus(String status,Integer id);
    public UserLogin getUserLogin(Integer id);
}