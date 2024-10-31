package com.lotteon.service;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.responseDto.GetAdminUserDTO;
import com.lotteon.entity.member.Customer;
import com.lotteon.entity.member.Member;
import com.lotteon.entity.member.Seller;
import com.lotteon.repository.member.CustomerRepository;
import com.lotteon.repository.member.MemberRepository;
import com.lotteon.repository.member.SellerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
     날짜 : 2024/10/25 (금)
     이름 : 김민희
     내용 : 관리자 회원 Service 생성

     수정이력
      - 2025/10/27 (일) 김민희 - 회원정보조회 팝업호출 기능 메서드(popCust) 추가
      - 2025/10/28 (월) 김민희 - 회원수정 기능 메서드 추가
      - 2025/10/31 (목) 김민희 - 회원수정 기능 메서드 추가
*/

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final CustomerRepository customerRepository;
    private final SellerRepository sellerRepository;

    public String matchingCheckByNameAndEmail(String name, String email) {
        Optional<Customer> customer = customerRepository.findByCustNameAndCustEmail(name,email);
        if(customer.isPresent()){
            return "SU";
        }
        return "NF";
    }

    // 0. 관리자 회원목록
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> optUser = memberRepository.findByMemUid(username);

        if(optUser.isPresent()) {
            // 시큐리티 사용자 인증객체 생성 후 반환
            return MyUserDetails.builder()
                    .user(optUser.get())
                    .build();
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    // 1. 관리자 회원목록 출력기능
    public List<GetAdminUserDTO> selectCustAll() {
        String role = "customer";
        List<Member> customers = memberRepository.findAllByMemRole(role);
        log.info("커스터머 서비스 : "+customers);

        // List<GetAdminUserDTO> cust = customers.stream().map(customer -> modelMapper.map(customer, GetAdminUserDTO.class)).toList();
        List<GetAdminUserDTO> cust = new ArrayList<>();
        customers.forEach(customer -> {
            // 번호, 아이디, 이름, 성별, 등급, 포인트, 이메일, 휴대폰, 가입일, 상태, 관리
            if (customer.getCustomer() != null) {
                GetAdminUserDTO dto = GetAdminUserDTO.builder()
                        .custId(customer.getId()) // 번호
                        .memUid(String.valueOf(customer.getMemUid())) // 아이디
                        .custName(customer.getCustomer().getCustName()) // 이름
                        .custGender(customer.getCustomer().getCustGender()) // 성별
                        .custGrade(customer.getCustomer().getCustGrade()) // 등급
                        .custPoint(customer.getCustomer().getCustPoint()) // 포인트
                        .custEmail(customer.getCustomer().getCustEmail()) // 이메일
                        .custHp(customer.getCustomer().getCustHp()) // 휴대폰
                        .memRdate(customer.getMemRdate()) // 가입일
                        .memState(String.valueOf(customer.getMemState())) // 상태
                        .build();
                cust.add(dto);
            } else {
                // null일 때 처리 방법
                // 1. 기본값으로 처리ㄴ

                // 2. 혹은 생략할 수도 있습니다. (이 경우 아무 작업도 하지 않음)
                // log.warn("Customer 정보가 없는 회원 ID: " + customer.getId());
            }
        });

        return cust;
    }

    // 2. 관리자 회원수정 정보조회 (+팝업호출 = select)
    public GetAdminUserDTO popCust(Long id) {
        Optional<Member> optMember = memberRepository.findByCustomer_id(id);
        log.info("opt 멤버 확인 "+optMember.get().toString());

        if(optMember.isPresent()) {
            Member member = optMember.get();
            log.info("여기까지는 들어오나? ");

            String[] addr = member.getCustomer().getCustAddr().split("/");
            log.info("배열에 들어가는지 확인 "+addr);
            GetAdminUserDTO dto = GetAdminUserDTO.builder()
                                            .custId(member.getCustomer().getId())
                                            .memUid(member.getMemUid())
                                            .custName(member.getCustomer().getCustName())
                                            .custGrade(member.getCustomer().getCustGrade())
                                            .custGender(member.getCustomer().getCustGender())
                                            .custEmail(member.getCustomer().getCustEmail())
                                            .custHp(member.getCustomer().getCustHp())
                                            .memRdate(member.getMemRdate())
                                            .memState(member.getMemState())
                                            .custAddr1(addr[0])
                                            .custAddr2(addr[1])
                                            .custAddr3(addr[2])
                                            .memEtc(member.getMemEtc())
                                            .memLastLoginDate(member.getMemLastLoginDate())
                                            .build();

            log.info("여기까지 들어오나 2 "+dto);
            return dto;
        }
       //Optional<Member> custPop = memberRepository.findByMemUid(selectCustDto.getMemUid());
        return null;
    }

    // 3. 관리자 회원 수정
    public GetAdminUserDTO updateCust(Long id, GetAdminUserDTO getAdminUserDTO) {
        // 1. Member 조회
        Optional<Customer> opt = customerRepository.findById(id);

        Customer cust = null;
        if(opt.isPresent()) {
            cust = opt.get();
        }
        Member member = memberRepository.findById(cust.getMember().getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 회원이 없습니다: " + id));

        // 2. Customer 엔티티의 updateUser 메서드를 통해 정보 업데이트
        Customer customer = member.getCustomer();
        customer.updateUser(getAdminUserDTO);

        member.updateUser(customer,getAdminUserDTO.getMemEtc());
        memberRepository.save(member);
        customerRepository.save(customer);
//        if (customer != null) {
//
//            customer.updateUser(getAdminUserDTO,member);
////            member.setCustomer(customer);
//            log.info("customer save :::::"+customer.toString());
//        } else {
//            throw new IllegalArgumentException("해당 ID의 회원 정보가 존재하지 않습니다.");
//        }

        // 3. 저장 및 반환
//        log.info("member save :::::::"+customer.toString());
//        customerRepository.save(customer);  // 연관된 Customer 객체가 자동으로 저장됨
        return modelMapper.map(customer, GetAdminUserDTO.class);

    }


    // 3. 관리자 회원목록 페이지 처리 (<이전 1,2,3 다음>)
    public Page<GetAdminUserDTO> selectCustAll2(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Member> members = memberRepository.findAllByMemRoleOrderByIdDesc("customer",pageable);
        Page<GetAdminUserDTO> dtos = members.map(v->v.toGetAdminUserDTO());
        log.info("서비스 디티오 변환한 거 "+dtos.getContent());
        return dtos;
    }


    // 4. 관리자 회원목록 선택삭제 기능
    public boolean deleteCustsById(List<Long> deleteCustIds) {
        try{
            for (Long deleteCustId : deleteCustIds) {
                memberRepository.deleteById(deleteCustId);
            }
            return true;
        }catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }


    public String matchingCheckByBusinessAndEmail(String name, String email) {
        Optional<Seller> seller = sellerRepository.findBySellBusinessCodeAndSellEmail(name,email);
        if(seller.isPresent()){
            return seller.get().getSellRepresentative();
        }
        return "NF";
    }

    public String matchingCheckByUidAndEmail(String name, String email) {
        Optional<Customer> customer = customerRepository.findByMember_MemUidAndCustEmail(name,email);
        if(customer.isPresent()){
            return "SU";
        }
        return "NF";
    }

    public String matchingCheckByBusinessAndEmailAndUid(String name, String email, String uid) {
        Optional<Seller> seller = sellerRepository.findBySellBusinessCodeAndSellEmailAndMember_MemUid(name,email,uid);
        if(seller.isPresent()){
            return seller.get().getSellRepresentative();
        }
        return "NF";
    }
}






















