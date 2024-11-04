package com.lotteon.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.requestDto.PostCustSignupDTO;
import com.lotteon.entity.member.Customer;
import com.lotteon.entity.member.Member;
import com.lotteon.repository.member.CustomerRepository;
import com.lotteon.repository.member.MemberRepository;
import com.lotteon.service.member.CustomerService;
import com.lotteon.service.member.MemberService;
import groovy.lang.Lazy;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
public class MyOauth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final CustomerRepository customerRepository;

    @Lazy
    private CustomerService customerService;


    public MyOauth2UserService(MemberRepository memberRepository, CustomerRepository customerRepository) {
        this.memberRepository = memberRepository;
        this.customerRepository = customerRepository;
    }

    @Autowired
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Access Token을 통해 사용자 정보를 가져옵니다.
        String accessToken = userRequest.getAccessToken().getTokenValue();
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // 소셜 로그인 유저 정보 로딩
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info(oAuth2User);

        // OAuth2User의 속성에서 필요한 정보를 추출합니다.
        Map<String, Object> attributes = oAuth2User.getAttributes();
        JsonNode rootNode = new ObjectMapper().convertValue(attributes, JsonNode.class);

        Member member = null;

        log.info(provider);
        log.info(rootNode);

        String custEmail = null,
                memUid = null,
                custHp = "Unknown Phone",
                birthyear = null,
                birthday = null,
                custBirth = "0000-00-00",
                custName = "Unknown User";

        Boolean custGender = null;

        JsonNode account = null;

        if (provider.equals("kakao")) {

            account = rootNode.path("kakao_account");
            custEmail = account.path("email").asText(null);
            memUid = "KAKAO_SOCIAL_" + rootNode.path("id").asLong();

        }else if(provider.equals("google")){

            custEmail = rootNode.path("email").asText(null);
            memUid = "GOOGLE_SOCIAL_" + rootNode.path("sub").asText();

        }else if(provider.equals("naver")){

            account = rootNode.path("response");
            custEmail = account.path("email").asText(null);
            memUid = "NAVER_SOCIAL_" + UUID.randomUUID();

        }
        log.info(custEmail);
        log.info(memUid);

        if (custEmail != null) {
            Customer customer = customerRepository.findByCustEmail(custEmail);
            if (customer != null) {
                Optional<Member> optMember = memberRepository.findByCustomer_id(customer.getId());
                member = optMember.orElse(null);
            }else {

            if (provider.equals("kakao")){

                custName = account.path("profile").path("nickname").asText("Unknown User");
                String hp = Optional.ofNullable(account.path("phone_number").asText(null)).orElse("Unknown Phone");
                if (!hp.equals("Unknown Phone")) {custHp = "0" + hp.split(" ")[1];}
                custGender = "male".equals(account.path("gender").asText(null));
                birthyear = account.path("birthyear").asText("0000");
                birthday = account.path("birthday").asText("0101");
                custBirth = birthyear + "-" + birthday.substring(0, 2) + "-" + birthday.substring(2, 4);

            }else if(provider.equals("google")){

                custName = rootNode.path("given_name").asText("Unknown User");

            }else if(provider.equals("naver")){

                custName = account.path("name").asText("Unknown User");
                custHp = account.path("mobile").asText("Unknown Phone");
                custGender = "M".equals(account.path("gender").asText(null));
                birthyear = account.path("birthyear").asText("0000");
                birthday = account.path("birthday").asText("01-01");
                custBirth = birthyear + "-" + birthday;

            }

            PostCustSignupDTO dto = PostCustSignupDTO.builder()
                    .memId(memUid)
                    .memPwd("SOCIAL")
                    .custHp(custHp)
                    .custName(custName)
                    .custGender(custGender)
                    .custBirth(custBirth)
                    .custOptional(false)
                    .custEmail(custEmail)
                    .basicAddr(false)
                    .build();
            log.info(dto);
            member = customerService.insertCustomer(dto);
        }
        }

        return MyUserDetails.builder()
                .user(member)
                .accessToken(accessToken)
                .attributes(attributes)
                .build();
    }
}
