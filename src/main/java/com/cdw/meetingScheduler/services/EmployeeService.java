package com.cdw.meetingScheduler.services;

import com.cdw.meetingScheduler.entities.Employee;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    List<Employee> findAll();
    Optional<Employee> findById(int employeeId);

    Employee save(Employee employee);

    ResponseEntity update(int employeeId, Employee employee);

    ResponseEntity deleteById(int employeeId);
}
