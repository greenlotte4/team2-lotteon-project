package com.lotteon.repository.member;

import com.lotteon.entity.member.UserLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLogRepository extends MongoRepository<UserLog,Long> {
}
