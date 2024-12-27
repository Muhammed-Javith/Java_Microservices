package com.mj.employee.config;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.mj.employee.Payload.EmployeeDto;
import com.mj.employee.entity.Employee;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

	EmployeeMapper MAPPER = Mappers.getMapper(EmployeeMapper.class);

	// Employee employeDto to employeentity conversion
	Employee mapToEntity(EmployeeDto employeeDto);

	// employeeentity to employedto conversion
	EmployeeDto mapToDto(Employee employee);

}
