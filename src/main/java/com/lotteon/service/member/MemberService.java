package com.lotteon.service.member;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.PostCustSignupDTO;
import com.lotteon.entity.member.Customer;
import com.lotteon.entity.member.Member;
import com.lotteon.repository.member.MemberRepository;
import com.lotteon.repository.term.TermsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
}
