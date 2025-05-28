package com.example.greenprojectA.service;

import com.example.greenprojectA.entity.Company;
import com.example.greenprojectA.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    // 전체 기업 조회
    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    // 기업 추가
    public void addCompany(Company company) {
        companyRepository.save(company);
    }

    // (선택) 이름 중복 체크
    public boolean isNameExists(String name) {
        return companyRepository.existsByName(name);
    }
}