package com.future.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.future.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author guorui
 * @create 2022-11-29-8:42
 */

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
