package com.lotteon.service.member;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.PatchMyInfoDTO;
import com.lotteon.dto.requestDto.PostCustSignupDTO;
import com.lotteon.dto.requestDto.PostFindIdDto;


import com.lotteon.dto.responseDto.GetAdminUserDTO;
import com.lotteon.dto.responseDto.GetMyInfoDTO;
import com.lotteon.dto.responseDto.cartOrder.UserOrderDto;
import com.lotteon.entity.member.*;
import com.lotteon.entity.point.Point;

import com.lotteon.repository.member.*;

import com.lotteon.repository.point.PointRepository;
import com.lotteon.repository.term.TermsRepository;
import com.lotteon.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


 

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final PasswordEncoder passwordEncoder;
    private final TermsRepository termsRepository;
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final CustomerRepository customerRepository;
    private final PointRepository pointRepository;
    private final AttendanceEventRepository attendanceEventRepository;
    private final AddressRepository addressRepository;
    private final AuthService authService;
    private final MemberChangeDitectorRepository memberChangeDitectorRepository;


    @Transactional
    public Member insertCustomer(PostCustSignupDTO postCustSignupDTO) {
        try {
            Member member;
            if(postCustSignupDTO.getMemPwd().equals("SOCIAL")){
                 member = Member.builder()
                        .memUid(postCustSignupDTO.getMemId())
                        .memPwd(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .memRole("guest")
                        .memState("basic")
                        .build();
            }else {
                // Member 객체 생성 및 저장 (멤버 DB에 아이디, 비번 저장)
                 member = Member.builder()
                        .memUid(postCustSignupDTO.getMemId())
                        .memPwd(passwordEncoder.encode(postCustSignupDTO.getMemPwd()))
                        .memRole("customer") // 기본 사용자 유형 "customer"
                        .memState("basic")   // 기본 계정 상태 "basic"
                        .build();
            }
            Member savedMember = memberRepository.save(member);
            // Addr1 + Addr2 + Addr3 = 부산광역시 + 부산진구 + 부전동
            String addr = postCustSignupDTO.getAddr1()+"/"+postCustSignupDTO.getAddr2()+"/"+postCustSignupDTO.getAddr3();

            // Customer 객체 생성 및 저장
            Customer customer = Customer.builder()
                    .member(member)
                    .custName(postCustSignupDTO.getCustName())
                    .custEventChecker(0)
                    .custBirth(postCustSignupDTO.getCustBirth())
                    .custGender(postCustSignupDTO.getCustGender() != null ? postCustSignupDTO.getCustGender() : false)  // null일 때 false로 처리
                    .custEmail(postCustSignupDTO.getCustEmail())
                    .custHp(postCustSignupDTO.getCustHp())
                    .custAddr(postCustSignupDTO.getAddr1() + "/" + postCustSignupDTO.getAddr2() + "/" + postCustSignupDTO.getAddr3())
                    .custGrade("FAMILY")
                    .build();

            customerRepository.save(customer);

            Address address = Address.builder()
                    .addrNick("기본배송지")
                    .address(postCustSignupDTO.getAddr1() + "/" + postCustSignupDTO.getAddr2() + "/" + postCustSignupDTO.getAddr3())
                    .request("없음")
                    .basicState(1)
                    .addrName(postCustSignupDTO.getCustName())
                    .addrHp(postCustSignupDTO.getCustHp())
                    .customer(customer)
                    .build();

            addressRepository.save(address);

            Point point = this.insertPoint(customer);

            pointRepository.save(point);

            int updatePoint = this.updateCustomerPoint(customer);
            customer.updatePoint(updatePoint);

            AttendanceEvent attendanceEvent = this.createAttendanceEvent(customer);
            attendanceEventRepository.save(attendanceEvent);

            //상훈 작업부분 포인트추가 끝

            return savedMember;

        } catch (Exception e) {
            // 예외 발생 시 로그 출력 및 에러 처리
            log.error("사용자 등록 중 오류가 발생했습니다.: ", e);

            // 필요한 경우 사용자에게 에러 정보를 리턴하거나 예외를 다시 던질 수 있습니다.
            throw new RuntimeException("다시 시도해 주세요.");
        }
    }

    private AttendanceEvent createAttendanceEvent(Customer customer) {
        return AttendanceEvent.builder()
                .attendanceDays(0)
                .attendanceSequence(1)
                .customer(customer)
                .build();
    }

    public int updateCustomerPoint(Customer customer) {
        List<Point> points = pointRepository.findAllByCustomerAndPointType(customer,1);
        if(points.size()==0){
            return 0;
        }
        System.out.println(points);
        int point = 0;

        for(Point v : points ){
            if(v.getPointType()==1){
                point = point+v.getPointVar();
            }
        }
        return point;
    }

    private Point insertPoint(Customer customer) {
        LocalDate today = LocalDate.now().plusMonths(1);

        Point point = Point.builder()
                .customer(customer)
                .pointType(1)
                .pointEtc("회원가입 축하 포인트 적립")
                .pointVar(1000)
                .pointExpiration(today)
                .build();

        return point;
    }

    public int findByCustomer() {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        Customer customer = auth.getUser().getCustomer();

        int points2 = this.updateCustomerPoint(customer);
        customer.updatePoint(points2);
        customerRepository.save(customer);
        return points2;
    }
    //상훈 작업부분 포인트추가 끝

    public UserOrderDto selectedOrderCustomer(){

        MyUserDetails auth =(MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = auth.getUser();

        Optional<Address> addressEntity = addressRepository.findByCustomerAndBasicState(member.getCustomer(),1);
        if(addressEntity.isEmpty()){
            String address = member.getCustomer().getCustAddr();
            String[] addr = address.split("/");
            UserOrderDto user = UserOrderDto.builder()
                    .memUid(member.getMemUid())
                    .custName(member.getCustomer().getCustName())
                    .custHp(member.getCustomer().getCustHp())
                    .custZip(addr[0])
                    .custAddr1(addr[1])
                    .custAddr2(addr[2])
                    .points(member.getCustomer().getCustPoint())
                    .build();
        }
        String address = addressEntity.get().getAddress();
        String[] addr = address.split("/");

        UserOrderDto user = UserOrderDto.builder()
                                        .memUid(member.getMemUid())
                                        .custName(member.getCustomer().getCustName())
                                        .custHp(member.getCustomer().getCustHp())
                                        .custZip(addr[0])
                                        .custAddr1(addr[1])
                                        .custAddr2(addr[2])
                                        .points(member.getCustomer().getCustPoint())
                                        .build();


        return user;
    }

    public String findByNameAndEmail(PostFindIdDto dto) {
        Optional<Customer> customer = customerRepository.findByCustEmailAndCustName(dto.getEmail(),dto.getName());
        if(customer.isEmpty()){
            return "NF";
        }
        return customer.get().getMember().getMemUid();
    }

    // 나의 설정 (사용자 ID, 비밀번호, 이름, 생년월일, 이메일, 휴대폰, 주소) 출력
    public GetMyInfoDTO myInfo() {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer = auth.getUser().getCustomer();

        //Customer customer = customerRepository.findById(customer2.getId()).get();

        log.info("사용자 확인: " + customer.toString());

        String[] email = customer.getCustEmail().split("@");
        log.info("이메일 확인: " + Arrays.toString(email));

        String hp = customer.getCustHp();
        System.out.println(hp);
        String[] phone = hp.split("-");
        String[] addr = customer.getCustAddr().split("/");
        System.out.println(addr);
        log.info("주소 확인: " + Arrays.toString(addr));

        GetMyInfoDTO dto = GetMyInfoDTO.builder()
                .custId(customer.getId()) // 번호
                .memUid(customer.getMember().getMemUid()) // 아이디
                .custName(customer.getCustName()) // 이름
                .custEmail1(email[0]) // 이메일1 @ 앞부분
                .custEmail2(email[1]) // 이메일2 @ 뒷부분
                .custHp1(phone[0]) // 휴대폰1 010
                .custHp2(phone[1]) // 휴대폰2 -1234
                .custHp3(phone[2]) // 휴대폰3 -5678
                .custBirth(customer.getCustBirth()) // 생일
                .custAddr1(addr[0]) // 우편
                .custAddr2(addr[1]) // 기본
                .custAddr3(addr[2]) // 상세
                .build();

        log.info("DTO 정보: " + dto);
        return dto;
    }

    public Boolean updateCustomerEmail(Long custId, String email) {
        log.info("서비스 접속 "+custId+email);
        Optional<Customer> optCustomer = customerRepository.findById(custId);
        if(optCustomer.isPresent()){
            Customer customer = optCustomer.get();
            customer.updateEmail(email);
            updateUser(customer.getMember());
            LocalDate today = LocalDate.now();
            memberChangeDitectorRepository.deleteAllByMemIdAndAction(optCustomer.get().getMember().getId(),"email");

            MemberChangeDitector memberChangeDitector = MemberChangeDitector.builder()
                    .action("email")
                    .memId(optCustomer.get().getMember().getId())
                    .changeDate(today)
                    .build();
            memberChangeDitectorRepository.save(memberChangeDitector);
            return true;
        }else{
            return false;
        }
    }

    public Boolean updateCustomerHp(Long custId, String hp) {
        log.info("서비스 접속 "+custId+hp);
        Optional<Customer> optCustomer = customerRepository.findById(custId);
        if(optCustomer.isPresent()){
            Customer customer = optCustomer.get();
            customer.updateHp(hp);
            updateUser(customer.getMember());

            memberChangeDitectorRepository.deleteAllByMemIdAndAction(optCustomer.get().getMember().getId(),"hp");
            LocalDate today = LocalDate.now();
            MemberChangeDitector memberChangeDitector = MemberChangeDitector.builder()
                    .action("hp")
                    .memId(optCustomer.get().getMember().getId())
                    .changeDate(today)
                    .build();
            memberChangeDitectorRepository.save(memberChangeDitector);
            return true;
        }else{
            return false;
        }
    }
    public void updateUser(Member member) {
        // 1. 사용자 정보를 업데이트합니다.
        memberRepository.save(member);

        // 2. UserDetailsService를 통해 업데이트된 사용자 정보를 가져옵니다.
        MyUserDetails updatedUserDetails = (MyUserDetails) authService.loadUserByUsername(member.getMemUid());

        // 3. 인증 정보를 업데이트합니다.
        updateAuthentication(updatedUserDetails);
    }
    public void updateAuthentication(MyUserDetails updatedUserDetails) {
        // 새로운 인증 객체를 생성합니다.
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                updatedUserDetails,    // 업데이트된 사용자 정보
                updatedUserDetails.getPassword(),  // 비밀번호
                updatedUserDetails.getAuthorities() // 권한 정보
        );

        // SecurityContextHolder에 새로운 인증 객체를 설정합니다.
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    public GetMyInfoDTO myInfoModify(Customer customer) {
        Optional<Customer> optCustomer = customerRepository.findById(customer.getId());
        log.info("서비스 오피티 커스터머 "+optCustomer.get());
        if(optCustomer.isPresent()){
            Customer cust = optCustomer.get();
            String[] custHps = cust.getCustHp().split("-");
            String[] custAddr = cust.getCustAddr().split("/");
            String[] custEmail = cust.getCustEmail().split("@");
            return GetMyInfoDTO.builder()
                    .custId(cust.getId())
                    .memUid(cust.getMember().getMemUid())
                    .custBirth(cust.getCustBirth())
                    .custName(cust.getCustName())
                    .custHp1(custHps[0])
                    .custHp2(custHps[1])
                    .custHp3(custHps[2])
                    .custAddr1(custAddr[0])
                    .custAddr2(custAddr[1])
                    .custAddr3(custAddr[2])
                    .custEmail1(custEmail[0])
                    .custEmail2(custEmail[1])
                    .build();
        }else{
            return null;
        }
    }

    public Boolean updateCustomer(GetMyInfoDTO getMyInfoDTO) {
        String addr = getMyInfoDTO.getCustAddr1()+"/"+getMyInfoDTO.getCustAddr2()+"/"+getMyInfoDTO.getCustAddr3();
        String email = getMyInfoDTO.getCustEmail1()+"@"+getMyInfoDTO.getCustEmail2();
        String hp = getMyInfoDTO.getCustHp1()+"-"+getMyInfoDTO.getCustHp2()+"-"+getMyInfoDTO.getCustHp3();
        Long custId = getMyInfoDTO.getCustId();

        Optional<Customer> optCustomer = customerRepository.findById(custId);

        if(optCustomer.isPresent()){
            Customer customer = optCustomer.get();
            customer.updateEmail(email);
            customer.updateHp(hp);
            customer.updateAddr(addr);
            updateUser(customer.getMember());
            return true;
        }else{
            return false;
        }
    }
}


