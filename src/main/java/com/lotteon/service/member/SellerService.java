package com.lotteon.service.member;

import com.lotteon.dto.requestDto.PostAdminSellerDto;
import com.lotteon.dto.requestDto.PostCustSignupDTO;
import com.lotteon.dto.requestDto.PostFindIdDto;
import com.lotteon.dto.requestDto.PostSellerSignupDTO;
import com.lotteon.entity.member.Member;
import com.lotteon.entity.member.Seller;
import com.lotteon.repository.member.MemberRepository;
import com.lotteon.repository.member.SellerRepository;
import com.lotteon.repository.term.TermsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class SellerService {

    private final PasswordEncoder passwordEncoder;
    private final TermsRepository termsRepository;
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final SellerRepository sellerRepository;

    // 0. 관리자 판매자 등록
    public void postSeller(PostAdminSellerDto postAdminSellerDto) {

        Member member = Member.builder()
                .memPwd(passwordEncoder.encode(postAdminSellerDto.getMemPwd()))
                .memUid(postAdminSellerDto.getMemId())
                .memRole("seller")
                .memState("basic")
                .build();

        memberRepository.save(member);

        Seller seller = Seller.builder()
                .member(member)
                .sellAddr(postAdminSellerDto.getSellAddr())
                .sellCompany(postAdminSellerDto.getSellCompany())
                .sellFax(postAdminSellerDto.getSellFax())
                .sellBusinessCode(postAdminSellerDto.getSellCode())
                .sellOrderCode(postAdminSellerDto.getSellOrderCode())
                .sellRepresentative(postAdminSellerDto.getSellRep())
                .sellHp(postAdminSellerDto.getSellHp())
                .build();

        sellerRepository.save(seller);

    }

    @Transactional
    public void insertSeller(PostSellerSignupDTO postSellerSignupDTO){
        try {
            // Member 객체 생성 및 저장 (멤버 DB에 아이디, 비번 저장)
            Member member = Member.builder()
                    .memUid(postSellerSignupDTO.getMemId())
                    .memPwd(passwordEncoder.encode(postSellerSignupDTO.getMemPwd()))
                    .memRole("customer") // 기본 사용자 유형 "customer"
                    .memState("basic")   // 기본 계정 상태 "basic"
                    .build();

            memberRepository.save(member);

            // Seller 객체 생성 및 저장 (판매자 DB에 회사명,대표,사업자등록번호,통신판매업번호,전화번호,팩스,주소(7)저장)
            Seller seller = Seller.builder()
                    .member(member)
                    .sellCompany(postSellerSignupDTO.getSellCompany())               // 회사명
                    .sellRepresentative(postSellerSignupDTO.getSellRepresentative()) // 대표
                    .sellGrade(postSellerSignupDTO.getSellGrade())                   // 판매자 회원 등급
                    .sellBusinessCode(postSellerSignupDTO.getSellBusinessCode())     // 사업자등록번호
                    .sellOrderCode(postSellerSignupDTO.getSellOrderCode())           // 통신판매업번호
                    .sellHp(postSellerSignupDTO.getSellHp())                         // 전화번호
                    .sellFax(postSellerSignupDTO.getSellFax())                       // 팩스번호
                    .sellAddr(postSellerSignupDTO.getSellAddr())                     // 회사주소
                    .build();

            sellerRepository.save(seller);

        } catch (Exception e) {
            // 예외 발생 시 로그 출력 및 에러 처리
            log.error("사용자 등록 중 오류가 발생했습니다.: ", e);

            // 필요한 경우 사용자에게 에러 정보를 리턴하거나 예외를 다시 던질 수 있습니다.
            throw new RuntimeException("다시 시도해 주세요.");
        }
    }


    public String findByCodeAndEmail(PostFindIdDto dto) {
        Optional<Seller> seller = sellerRepository.findBySellBusinessCodeAndSellEmail(dto.getName(),dto.getEmail());
        if(seller.isEmpty()){
            return "NF";
        }

        return seller.get().getMember().getMemUid();
    }
}
