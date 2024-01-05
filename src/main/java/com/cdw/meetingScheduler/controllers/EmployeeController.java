package com.cdw.meetingScheduler.controllers;

import com.cdw.meetingScheduler.constants.MeetingSchedulerConstants;
import com.cdw.meetingScheduler.entities.Employee;
import com.cdw.meetingScheduler.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/")
    public ResponseEntity<List<Employee>> findAllEmployees() {
        List<Employee> employees = employeeService.findAll();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity findEmployeeById(@PathVariable int employeeId) {
        Optional<Employee> employeeOptional = employeeService.findById(employeeId);

        if (employeeOptional.isPresent()) {
            return ResponseEntity.ok(employeeOptional.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.EMPLOYEE_NOT_FOUND);
    }

    @PostMapping("/")
    public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee) {
        Employee newEmployee = employeeService.save(employee);
        return ResponseEntity.ok(newEmployee);
    }

    @PatchMapping("/{employeeId}")
    public ResponseEntity updateEmployee(@PathVariable int employeeId, @RequestBody Employee employee) {
        return employeeService.update(employeeId, employee);
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<String> deleteEmployee(@PathVariable int employeeId) {
        return employeeService.deleteById(employeeId);
    }

}
