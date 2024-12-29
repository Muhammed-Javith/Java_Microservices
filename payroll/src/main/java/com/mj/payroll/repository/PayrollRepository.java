package com.mj.payroll.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mj.payroll.entity.Payroll;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

}
