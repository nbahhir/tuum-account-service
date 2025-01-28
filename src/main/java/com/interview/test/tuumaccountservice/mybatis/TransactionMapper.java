package com.interview.test.tuumaccountservice.mybatis;

import com.interview.test.tuumaccountservice.entities.TransactionEntity;
import com.interview.test.tuumaccountservice.typehandlers.UuidTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface TransactionMapper {
    @Select("SELECT " +
            "transactionId, " +
            "accountId, " +
            "amount, " +
            "balanceAfterTransaction, " +
            "currency, direction, " +
            "description " +
            "FROM Transaction WHERE accountId = #{accountId};")
    @Results({
            @Result(property = "accountId", column = "accountId", javaType = UUID.class, typeHandler = UuidTypeHandler.class),
            @Result(property = "transactionId", column = "transactionId", javaType = UUID.class, typeHandler = UuidTypeHandler.class)
    })
    List<TransactionEntity> findTransactionsByAccountId(@Param("accountId") UUID accountId);

    @Insert("INSERT INTO Transaction (transactionId, accountId, " +
            "amount, balanceAfterTransaction, " +
            "currency, direction, description ) VALUES ( " +
            "#{transactionId, typeHandler=com.interview.test.tuumaccountservice.typehandlers.UuidTypeHandler}, " +
            "#{accountId, typeHandler=com.interview.test.tuumaccountservice.typehandlers.UuidTypeHandler}, " +
            "#{amount}, #{balanceAfterTransaction}, " +
            "#{currency}, #{direction}, #{description} );")
    void insertTransaction(TransactionEntity transactionEntity);
}
