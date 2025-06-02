package com.example.greenprojectA.service;

import com.example.greenprojectA.entity.Sensor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SensorExportService {
    ResponseEntity<Resource> exportToExcel(String startDate, String endDate, String deviceCode);
    public List<Sensor> getSensorData(String startDateStr, String endDateStr, String deviceCode);
}
