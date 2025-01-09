package com.example.demo.controller;

import com.example.demo.service.HomepageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/homepage")
public class HomepageController {
    @Autowired
    private HomepageService homepageService;

    @GetMapping
    public String homepage(Model model, Principal principal) {
        model.addAttribute("interviewingCandidates", homepageService.getInterviewingCandidates());
        model.addAttribute("offeringCandidates", homepageService.getOfferingCandidates());
        model.addAttribute("bannedCandidates", homepageService.getBannedCandidates());
        model.addAttribute("openCandidates", homepageService.getOpenCandidates());
        model.addAttribute("username", principal.getName());
        model.addAttribute("adminUsers", homepageService.getAdminUsers());
        model.addAttribute("managerUsers", homepageService.getManagerUsers());
        model.addAttribute("recruiterUsers", homepageService.getRecruiterUsers());
        model.addAttribute("interviewerUsers", homepageService.getInterviewerUsers());
        return "homepage";
    }

    @GetMapping("/count-by-position")
    @ResponseBody
    public List<Map<String, Object>> getCountByPosition() {
        List<Object[]> result = homepageService.countCandidatesByPosition();
        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] row : result) {
            Map<String, Object> map = new HashMap<>();
            map.put("position", row[0]);
            map.put("count", row[1]);
            data.add(map);
        }

        return data;
    }

    @GetMapping("/list-interviewer-by-interviews")
    @ResponseBody
    public List<Map<String, Object>> listInterviewerByInterviews() {
        List<Object[]> result = homepageService.getInterviewersByInterviewCount(5);
        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] row : result) {
            Map<String, Object> map = new HashMap<>();
            map.put("username", row[0]);
            map.put("count", row[1]);
            data.add(map);
        }

        return data;
    }
}
