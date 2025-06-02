package com.example.greenprojectA.repository;

import com.example.greenprojectA.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SensorRepository extends JpaRepository<Sensor, Integer> {

    @Query(value = """
    SELECT s.*
    FROM sensor s
    INNER JOIN (
        SELECT device_code, MAX(measure_datetime) AS latest_time
        FROM sensor
        GROUP BY device_code
        ORDER BY device_code
    ) latest ON s.device_code = latest.device_code AND s.measure_datetime = latest.latest_time
    """, nativeQuery = true)
    List<Sensor> getLatestSensorData();
}
