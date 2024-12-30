package com.mj.employer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mj.employer.entity.Employer;

public interface EmployerRepository extends JpaRepository<Employer, Long> {

}
