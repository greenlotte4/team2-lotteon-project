package com.lotteon.service.member;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.PostCustSignupDTO;
import com.lotteon.dto.requestDto.PostFindIdDto;
import com.lotteon.dto.responseDto.cartOrder.UserOrderDto;
import com.lotteon.entity.member.AttendanceEvent;
import com.lotteon.entity.member.Customer;
import com.lotteon.entity.member.Member;
import com.lotteon.entity.point.Point;

import com.lotteon.repository.member.AttendanceEventRepository;

import com.lotteon.repository.member.CustomerRepository;
import com.lotteon.repository.member.MemberRepository;
import com.lotteon.repository.point.PointRepository;
import com.lotteon.repository.term.TermsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
        System.out.println(points);
        int point = 0;

        for(Point v : points ){
            if(v.getPointType()==1){
                point = point+v.getPointVar();
            } else if(v.getPointType()==2){
                point = point-v.getPointVar();
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
        int point = customer.getCustPoint();
        return point;
    }
    //상훈 작업부분 포인트추가 끝

    public UserOrderDto selectedOrderCustomer(){

        MyUserDetails auth =(MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = auth.getUser();
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


        return user;
    }

    public String findByNameAndEmail(PostFindIdDto dto) {
        Optional<Customer> customer = customerRepository.findByCustEmailAndCustName(dto.getEmail(),dto.getName());
        if(customer.isEmpty()){
            return "NF";
        }
        return customer.get().getMember().getMemUid();
    }
}
