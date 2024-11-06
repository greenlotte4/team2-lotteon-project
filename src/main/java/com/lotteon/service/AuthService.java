package com.lotteon.service;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.responseDto.GetAdminUserDTO;
import com.lotteon.entity.member.Customer;
import com.lotteon.entity.member.Member;
import com.lotteon.entity.member.Seller;
import com.lotteon.entity.product.Order;
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
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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
      - 2024/11/06 (수) 김주경 - 선택수정 기능 메서드 추가
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
            return MyUserDetails.builder()
                    .user(optUser.get())
                    .build();
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    // 1. 관리자 회원목록 출력기능
    public List<GetAdminUserDTO> selectCustAll() {

        List<String> roles = Arrays.asList("customer", "guest");
        List<Member> customersAndGuests = memberRepository.findAllByMemRoleIn(roles);

        log.info("커스터머 서비스 : "+customersAndGuests);

        List<GetAdminUserDTO> cust = new ArrayList<>();
        customersAndGuests.forEach(customer -> {
            // 번호, 아이디, 이름, 성별, 등급, 포인트, 이메일, 휴대폰, 가입일, 상태, 관리
            if (customer.getMemRole().equals("customer")) {
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
                log.info("거짓말 이거 사용 안 한다구??" + dto);
            }else {



            }
        });
        return cust;
    }

    // 2. 관리자 회원수정 정보조회 (+팝업호출 = select) [수정] 버튼 클릭 시
    public GetAdminUserDTO popCust(Long id) {
        Optional<Member> optMember = memberRepository.findByCustomer_id(id);
        log.info("opt 멤버 확인 "+optMember.get().toString());

        // 전역 변수로 dto 사용해주기 !
        GetAdminUserDTO dto;

        Member member = optMember.get();
        log.info("여기까지는 들어오나? ");

        String[] addr = member.getCustomer().getCustAddr().split("/");
        log.info("배열에 들어가는지 확인 (addr 주소값) "+addr);

        String lastLogin = member.getMemLastLoginDate()!=null
                ?
                member.getMemLastLoginDate().toString().replace("T", " ")
                :
                "최근 로그인 기록 없음";

        dto = GetAdminUserDTO.builder()
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
                .memRole(member.getMemRole())
                .memEtc(member.getMemEtc())
                .memLastLoginDate(lastLogin)
                .build();

        log.info("여기까지 들어오나 2 "+dto);

        return dto;
    }

    // guest 일 때,
    public GetAdminUserDTO popGuest(Long id) {

        Optional<Member> optMember = memberRepository.findByCustomer_id(id);
        log.info("opt 게스트 멤버 확인 "+optMember.get().toString());

        // 전역 변수로 dto 사용해주기 !
        GetAdminUserDTO dto;
        Customer customer = optMember.get().getCustomer();
        // 사용자 유형이 "guest" 일 때,
        if(optMember.get().getMemRole().equals("guest")) {
            Member member = optMember.get();
            log.info("어이 게스트 들어오나? ");

            // 게스트 정보로만 필요한 필드 세팅
            return GetAdminUserDTO.builder()
                    .custId(member.getId())
                    .memUid(member.getMemUid())
                    .memRdate(member.getMemRdate())
                    .memState(member.getMemState().toString())
                    .memRole(member.getMemRole())
                    .build();
        }
        throw new IllegalArgumentException("해당 ID를 가진 guest가 없습니다.");
    }

    // customer 일 떄,
    public GetAdminUserDTO updateCust(Long id, GetAdminUserDTO getAdminUserDTO) {
        Optional<Customer> opt = customerRepository.findById(id);
        if (opt.isPresent()) {
            // `guest` 역할인 경우 null 반환 (수정하지 않음)
            if (opt.get().getMember().getMemRole().equals("guest")) {
                log.info("guest 사용자는 수정할 수 없습니다.");
                return null;
            } else {
                // `customer`의 경우에만 수정 로직 진행
                Customer cust = opt.get();
                Member member = memberRepository.findById(cust.getMember().getId())
                        .orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 회원이 없습니다: " + id));

                Customer customer = member.getCustomer();
                customer.updateUser(getAdminUserDTO);
                member.updateUser(customer, getAdminUserDTO.getMemEtc());

                // 수정된 정보 저장
                memberRepository.save(member);
                customerRepository.save(customer);

                return modelMapper.map(customer, GetAdminUserDTO.class);
            }
        }
        throw new IllegalArgumentException("해당 ID를 가진 회원이 없습니다.");
    }




    // 3. 관리자 회원목록 페이지 처리 (<이전 1,2,3 다음>)
    public Page<GetAdminUserDTO> selectCustAll2(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Member> members = memberRepository.findAllByMemRoleOrderByIdDesc("customer",pageable);
        Page<GetAdminUserDTO> dtos = members.map(v->v.toGetAdminUserDTO());
        log.info("서비스 디티오 변환한 거 "+dtos.getContent());
        return dtos;
    }

    public Page<GetAdminUserDTO> selectCustAndGuestAll(int page) {
        Pageable pageable = PageRequest.of(page, 10);

        // "customer"와 "guest" 역할을 함께 조회하기 위해 조건 리스트를 만듭니다.
        List<String> roles = Arrays.asList("customer", "guest");

        // MemRole이 "customer" 또는 "guest"인 회원을 조회
        Page<Member> members = memberRepository.findAllByMemRoleInOrderByIdDesc(roles, pageable);

        // Member 객체를 GetAdminUserDTO로 변환
        Page<GetAdminUserDTO> dtos = members.map(v -> v.toGetAdminUserDTO());

        log.info("서비스 DTO 변환 결과: " + dtos.getContent());
        return dtos;
    }



    // 4. 관리자 회원목록 선택수정 기능
    public boolean modifyCustsGradeById(List<Long> ids, List<String> grades) {
        try{
            int size = ids.size();
            for (int i = 0 ; i<size ; i++) {
                Optional<Customer> optCust = customerRepository.findById(ids.get(i));
                if(optCust.isPresent()){
                    Customer cust = optCust.get();
                    cust.setGrade(grades.get(i));
                }
            }
            return true;
        }catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public boolean modifyCustStateById(Long id, String state) {
        try {
            Optional<Member> optMember = memberRepository.findByCustomer_id(id);
            if(optMember.isPresent()){
                Member member = optMember.get();
                member.updateMemberState(state);
                return true;
            }
        }catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        return false;
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

    // 검색 기능
    public Page<GetAdminUserDTO> findAllSearchType(int page, String searchType, String keyword) {
        log.info("연화를 찾아라1 ");
        Pageable pageable = PageRequest.of(page,10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Member> members;

        if(searchType.equals("memUid")){  // 아이디
            List<String> roles = Arrays.asList("customer", "guest");
            members = memberRepository.findAllByMemUidContainsAndMemRoleInOrderByIdDesc(keyword,roles,pageable);
        } else if (searchType.equals("custName")){ // 이름
            members = memberRepository.findAllByCustomer_CustNameContainsOrderByIdDesc(keyword,pageable);
        } else if (searchType.equals("custEmail")){ // 이메일
            members = memberRepository.findAllByCustomer_CustEmailContainsOrderByIdDesc(keyword,pageable);
        } else { // 휴대폰
            members = memberRepository.findAllByCustomer_CustHpContainsOrderByIdDesc(keyword,pageable);
        }

        log.info("연화를 찾아라 2"+members.getContent());
        Page<GetAdminUserDTO> dtos = members.map(v -> v.toGetAdminUserDTO());
        return dtos;
    }
}























