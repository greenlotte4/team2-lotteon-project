package com.lotteon.service.member;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.responseDto.GetAddressDto;
import com.lotteon.dto.responseDto.PostAddressDto;
import com.lotteon.entity.member.Address;
import com.lotteon.entity.member.Customer;
import com.lotteon.repository.member.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

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
                .addrHp(customer.getCustHp())
                .addrName(customer.getCustName())
                .customer(customer)
                .build();

        addressRepository.save(address);
        List<Address> address2 = addressRepository.findAllByCustomer(customer);
        List<GetAddressDto> dtos = address2.stream().map(Address::toGetAddressDto).toList();

        return dtos;
    }

    public void insertNewAddress(PostAddressDto dto) {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Customer customer = auth.getUser().getCustomer();

        List<Address> address = addressRepository.findAllByCustomer(customer);
        if(address.size()>2){
            return;
        }
        int state ;
        if(dto.getBasicState()){
            state = 1;
            Optional<Address> addressState = addressRepository.findByCustomerAndBasicState(customer,1);
            addressState.get().offBasic();
            addressRepository.save(addressState.get());
        } else {
            state = 0;
        }
        System.out.println(dto.getAddrHp());
        String hp1 = dto.getAddrHp().substring(0,3);
        String hp2 = dto.getAddrHp().substring(3,7);
        String hp3 = dto.getAddrHp().substring(7,11);
        String hp4 = hp1+"-"+hp2+"-"+hp3;
        System.out.println(hp4);
//        String hp = dto.getAddrHp();
//        StringBuilder formattedHp = new StringBuilder();
//        System.out.println(hp);
//        if (hp.length() == 10) {
//            formattedHp.append(hp, 0, 3).append("-")
//                    .append(hp, 3, 6).append("-")
//                    .append(hp, 6, 10);
//        } else {
//            // 주소 길이가 10자가 아닐 경우 예외 처리
//            System.out.println("주소의 길이가 올바르지 않습니다.");
//        }


        Address newAddress = Address.builder()
                .address(dto.getAddr())
                .addrHp(hp4)
                .addrName(dto.getAddrName())
                .basicState(state)
                .customer(customer)
                .request(dto.getRequest())
                .addrNick(dto.getAddrNick())
                .build();

        addressRepository.save(newAddress);
    }

    public void updateNewAddress(PostAddressDto dto) {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Customer customer = auth.getUser().getCustomer();
        Optional<Address> address = addressRepository.findById(dto.getId());

        if(address.isEmpty()){
            return;
        }

        if(dto.getBasicState()){
            Optional<Address> addressState = addressRepository.findByCustomerAndBasicState(customer,1);
            addressState.get().offBasic();
            addressRepository.save(addressState.get());
        }

        address.get().updateAddress(dto);
        addressRepository.save(address.get());
    }

    public void deleteAddress(Long id) {
        Optional<Address> address = addressRepository.findById(id);

        if(address.get().getBasicState()==1){
            return;
        }
        addressRepository.delete(address.get());
    }

    public void updateAddressState(Long id) {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Customer customer = auth.getUser().getCustomer();
        Optional<Address> address = addressRepository.findById(id);

        Optional<Address> addressState = addressRepository.findByCustomerAndBasicState(customer,1);
        addressState.get().offBasic();
        addressRepository.save(addressState.get());

        address.get().updateStateTrue();
        addressRepository.save(address.get());

    }

    public List<GetAddressDto> findAllByUid() {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = auth.getUser().getCustomer();
        List<Address> address = addressRepository.findAllByCustomer(customer);
        List<GetAddressDto> dtos = address.stream().map(Address::toGetAddressDto).toList();
        return dtos;
    }

    public GetAddressDto findByAddrId(Long id) {
        Optional<Address> address = addressRepository.findById(id);
        GetAddressDto dto = address.get().toGetAddressDto();
        return dto;
    }
}
