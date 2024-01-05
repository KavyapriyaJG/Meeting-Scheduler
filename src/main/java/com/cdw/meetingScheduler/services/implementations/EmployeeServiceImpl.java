package com.cdw.meetingScheduler.services.implementations;

import com.cdw.meetingScheduler.constants.MeetingSchedulerConstants;
import com.cdw.meetingScheduler.entities.Employee;
import com.cdw.meetingScheduler.repositories.EmployeeRepository;
import com.cdw.meetingScheduler.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> findById(int employeeId) {
        return employeeRepository.findById(employeeId);
    }

    //@Transactional not required since the repo has all methods as transactional
    @Override
    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    //@Transactional not required since the repo has all methods as transactional
    @Override
    public ResponseEntity update(int employeeId, Employee employee) {
        Optional<Employee> optionalEmployee = findById(employeeId);
        if (optionalEmployee.isPresent()) {
            employee.setEmployeeId(optionalEmployee.get().getEmployeeId());
            if(employee.getName() == null)  employee.setName(optionalEmployee.get().getName());
            if(employee.getEmail() == null) employee.setEmail(optionalEmployee.get().getEmail());
            employee.setTeams(optionalEmployee.get().getTeams());
            employee.setMeetings(optionalEmployee.get().getMeetings());

            employeeRepository.save(employee);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(employee);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.EMPLOYEE_NOT_FOUND);
    }

    //@Transactional not required since the repo has all methods as transactional
    @Override
    public ResponseEntity deleteById(int employeeId) {
        Optional<Employee> optionalEmployee = findById(employeeId);
        if (optionalEmployee.isPresent()) {
            employeeRepository.deleteById(employeeId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(MeetingSchedulerConstants.EMPLOYEE_DELETED);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.EMPLOYEE_NOT_FOUND);
    }
}
