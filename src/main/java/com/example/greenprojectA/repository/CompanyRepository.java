package com.example.greenprojectA.repository;

import com.example.greenprojectA.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
