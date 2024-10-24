package com.lotteon.entity.member;

import com.lotteon.dto.responseDto.GetAddressDto;
import com.lotteon.dto.responseDto.PostAddressDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "address")
    private String address;

    @Column(name = "addr_nick")
    private String addrNick;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "basic_state")
    private Integer basicState;

    @Column(name = "basic_request")
    private String request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cust_id")
    private Customer customer;


    public GetAddressDto toGetAddressDto() {
        String addr1 = address.substring(0,address.indexOf("/"));
        String addr2 = address.substring(address.indexOf("/")+1,address.lastIndexOf("/"));
        String addr3 = address.substring(address.lastIndexOf("/")+1);
        String payment = Objects.requireNonNullElse(paymentType, "선택없음");

        return GetAddressDto.builder()
                .addr1(addr1)
                .addr2(addr2)
                .addr3(addr3)
                .state(basicState)
                .payment(payment)
                .id(id)
                .addrNick(addrNick)
                .request(request)
                .name(customer.getCustName())
                .hp(customer.getCustHp())
                .build();
    }

    public void updateAddress(PostAddressDto dto){
        int state ;
        if(dto.getBasicState()){
            state = 1;
        } else {
            state = 0;
        }
        this.addrNick = dto.getAddrNick();
        this.request = dto.getRequest();
        this.address = dto.getAddr();
        this.basicState = state;
    }

    public void offBasic() {
        this.basicState = 0;
    }

    public void updateStateTrue() {
        this.basicState = 1;
    }
}
