package com.lms.system.controller;

import com.lms.system.dto.DashboardResponseDTO;
import com.lms.system.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponseDTO> getDashboard() {
        DashboardResponseDTO response = dashboardService.getDashboard();
        return new ResponseEntity<>(response, HttpStatus.OK); // 200
    }
}