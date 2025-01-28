package com.interview.test.tuumaccountservice.mybatis;

import com.interview.test.tuumaccountservice.entities.AccountEntity;
import com.interview.test.tuumaccountservice.typehandlers.UuidTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.UUID;

@Mapper
public interface AccountMapper {

    @Select("SELECT * FROM account " +
            "WHERE accountId = #{accountId};")
    @Results({
            @Result(property = "accountId", column = "accountId", javaType = UUID.class, typeHandler = UuidTypeHandler.class),
            @Result(property = "customerId", column = "customerId", javaType = UUID.class, typeHandler = UuidTypeHandler.class)
    })
    AccountEntity findAccountByAccountId(@Param("accountId") UUID accountId);

    @Select("SELECT * FROM account " +
            "WHERE customerId = #{customerId};")
    @Results({
            @Result(property = "accountId", column = "accountId", javaType = UUID.class, typeHandler = UuidTypeHandler.class),
            @Result(property = "customerId", column = "customerId", javaType = UUID.class, typeHandler = UuidTypeHandler.class)
    })
    AccountEntity findAccountByCustomerId(@Param("customerId") UUID customerId);

    @Insert("INSERT INTO account (accountId, country, customerId) VALUES (" +
            "#{accountId, typeHandler=com.interview.test.tuumaccountservice.typehandlers.UuidTypeHandler}, " +
            "#{country}, " +
            "#{customerId, typeHandler=com.interview.test.tuumaccountservice.typehandlers.UuidTypeHandler});")
    void insertAccount(AccountEntity account);
}
