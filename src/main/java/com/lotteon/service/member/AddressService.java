package com.lotteon.service.member;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.responseDto.GetAddressDto;
import com.lotteon.entity.member.Address;
import com.lotteon.entity.member.Customer;
import com.lotteon.repository.member.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;

    public List<GetAddressDto> findAllByCustomer() {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Customer customer = auth.getUser().getCustomer();

        List<Address> address = addressRepository.findAllByCustomer(customer);
        if(address.isEmpty()){
            List<GetAddressDto> dtos = this.insertAddress(customer);
            return dtos;
        }
        List<GetAddressDto> dtos = address.stream().map(Address::toGetAddressDto).toList();
        return dtos;
    }

    private List<GetAddressDto> insertAddress(Customer customer) {
        Address address = Address.builder()
                .address(customer.getCustAddr())
                .basicState(1)
                .addrNick("기본배송지")
                .customer(customer)
                .build();

        addressRepository.save(address);
        List<Address> address2 = addressRepository.findAllByCustomer(customer);
        List<GetAddressDto> dtos = address2.stream().map(Address::toGetAddressDto).toList();

        return dtos;
    }
}
