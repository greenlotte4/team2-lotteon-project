package com.lotteon.service.member;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.PostCustSignupDTO;
import com.lotteon.entity.member.Customer;
import com.lotteon.entity.member.Member;
import com.lotteon.repository.member.CustomerRepository;
import com.lotteon.repository.member.MemberRepository;
import com.lotteon.repository.term.TermsRepository;
import com.lotteon.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final TermsRepository termsRepository;
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;


    public void updateLastLoginDate(MyUserDetails user) {
        Optional<Member> member = memberRepository.findById(user.getUser().getId());
        if(member.isEmpty()){
            return;
        }
        LocalDateTime today = LocalDateTime.now();
        member.get().updateLastLogin(today);
    }

    @Scheduled(cron = "30 0 0 * * ?")
    public void updateMemberStateToSleep(){
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime standardDate = today.minusMonths(6);
        List<Member> members = memberRepository.findAllByMemLastLoginDateBefore(standardDate);
        for(Member member : members){
            member.updateMemberStateToSleep();
        }
        memberRepository.saveAll(members);
    }

    public void updatePwd(String pwd, String uid) {
        Optional<Member> member = memberRepository.findByMemUid(uid);
        member.get().updatePassword(passwordEncoder.encode(pwd));
    }

    public Boolean updateMyPwd(String pwd, Long memId) {
        Optional<Member> member = memberRepository.findById(memId);
        if(member.isPresent()){
            member.get().updatePassword(passwordEncoder.encode(pwd));
            memberRepository.save(member.get());
            return true;
        } else {
            return false;
        }
    }

    public Map<String,String> findByUid(String uid) {
        Map<String,String> map = new HashMap<>();
        Optional<Member> member = memberRepository.findByMemUid(uid);
        if(member.isPresent()){
            map.put("code" ,"DUP");
            map.put("msg","중복된 아이디입니다.");
            return map;
        } else {
            map.put("code","SU");
            map.put("msg","사용할 수 있는 아이디입니다.");
            return map;
        }
    }


    public Map<String, String> findByEmail(String email) {
        Map<String,String> map = new HashMap<>();
        Customer customer = customerRepository.findByCustEmail(email);
        if(customer != null){
            map.put("code" ,"DUP");
            map.put("msg","중복된 이메일입니다.");
            return map;
        } else {
            map.put("code","SU");
            map.put("msg","사용할 수 있는 아이디입니다.");
            String randomCode = String.format("%06d", (int)(Math.random() * 1000000));
            emailService.sendEmail(email,"회원가입 인증코드입니다.",randomCode);
            map.put("validation",randomCode);
            return map;
        }
    }

    public Map<String, String> findByCompany(String company) {
        Map<String,String> map = new HashMap<>();
        Optional<Member> member = memberRepository.findBySeller_SellCompany(company);
        if(member.isPresent()){
            map.put("code" ,"DUP");
            map.put("msg","중복된 회사명입니다.");
            return map;
        } else {
            map.put("code","SU");
            map.put("msg","사용할 수 있는 회사명입니다.");
            return map;
        }
    }

    public Long findCnt(LocalDateTime startOfDay,LocalDateTime endOfDay) {
        Timestamp startTimestamp = Timestamp.valueOf(startOfDay);
        Timestamp endTimestamp = Timestamp.valueOf(endOfDay);
        return memberRepository.countByMemRdateBetween(startTimestamp,endTimestamp);
    }

    public Boolean confirmPass(String pass, Long memId) {
        Optional<Member> optMember = memberRepository.findById(memId);
        log.info("서비스 패스워드 일치 "+optMember.get());
        if(optMember.isPresent() && passwordEncoder.matches(pass,optMember.get().getMemPwd())){
            log.info("여기 안 들어가? 왜? ");
            return true;
        }else{
            return false;
        }
    }

    public Boolean leaveUser(Long memId) {
        Optional<Member> optMember = memberRepository.findById(memId);
        if(optMember.isPresent()){
            Member member = optMember.get();
            member.updateMemberStateToLeave();
            return true;
        }else{
            return false;
        }
    }
}
