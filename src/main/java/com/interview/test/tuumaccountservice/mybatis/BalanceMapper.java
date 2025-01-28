package com.interview.test.tuumaccountservice.mybatis;

import com.interview.test.tuumaccountservice.entities.BalanceEntity;
import com.interview.test.tuumaccountservice.typehandlers.UuidTypeHandler;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Mapper
public interface BalanceMapper {
    @Select("SELECT balanceId, accountId, currency, amount " +
            "FROM Balance WHERE accountId = #{accountId} " +
            "FOR UPDATE;")
    @Results({
            @Result(property = "balanceId", column = "balanceId", javaType = UUID.class, typeHandler = UuidTypeHandler.class),
            @Result(property = "accountId", column = "accountId", javaType = UUID.class, typeHandler = UuidTypeHandler.class)
    })
    List<BalanceEntity> findBalancesByAccountId(@Param("accountId") UUID accountId);

    @Insert("INSERT INTO Balance (balanceId, accountId, currency, amount) VALUES (" +
            "#{balanceId, typeHandler=com.interview.test.tuumaccountservice.typehandlers.UuidTypeHandler}, " +
            "#{accountId, typeHandler=com.interview.test.tuumaccountservice.typehandlers.UuidTypeHandler}, " +
            "#{currency}, " +
            "#{amount});")
    void insertBalance(BalanceEntity balanceEntity);

    @Update("UPDATE Balance SET amount = #{amount} WHERE balanceId = #{balanceId};")
    void updateBalance(@Param("balanceId") UUID balanceId, @Param("amount") BigDecimal amount);
}
