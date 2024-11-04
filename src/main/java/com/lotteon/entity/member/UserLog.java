package com.lotteon.entity.member;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "user_logs")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class UserLog {
    @Id

    private String id;
    private Long custId;
    private Long prodId;
    private String action;
    private LocalDateTime timestamp;

    public UserLog(Long custId, Long prodId, String action, LocalDateTime timestamp) {
        this.custId = custId;
        this.prodId = prodId;
        this.action = action;
        this.timestamp = timestamp;
    }
}