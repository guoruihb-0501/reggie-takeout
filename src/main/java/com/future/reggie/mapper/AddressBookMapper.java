package com.future.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.future.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author guorui
 * @create 2022-12-10-9:25
*/
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
