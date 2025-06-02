package com.example.greenprojectA.repository;

import com.example.greenprojectA.entity.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventLogRepository extends JpaRepository<EventLog, Integer> {

}
