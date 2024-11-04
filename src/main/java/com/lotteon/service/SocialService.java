package com.lotteon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lotteon.config.MyUserDetails;
import com.lotteon.entity.member.Customer;
import com.lotteon.entity.member.Member;
import com.lotteon.repository.member.CustomerRepository;
import com.lotteon.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class SocialService {

    private final MemberRepository memberRepository;
    private final CustomerRepository customerRepository;

    @Value("${kakao.client_id}")
    private String KAKAO_CLIENT_ID;

    @Value("${kakao.redirect_uri}")
    private String KAKAO_REDIRECT_URL;

    private final static String KAKAO_AUTH_URI = "https://kauth.kakao.com";
    private final static String KAKAO_API_URI = "https://kapi.kakao.com";

    public String getKakaoLogin() {
        return KAKAO_AUTH_URI + "/oauth/authorize?response_type=code"
                + "&client_id=" + KAKAO_CLIENT_ID
                + "&redirect_uri=" + KAKAO_REDIRECT_URL;
    }

    public String getAccessToken(String code, String type) {

        String reqUrl = "", clientId = null, redirectUri = null;

        if(type.equals("kakao")) {
            clientId = KAKAO_CLIENT_ID;
            redirectUri = KAKAO_REDIRECT_URL;
            reqUrl = KAKAO_AUTH_URI+"/oauth/token";
        }

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            // HTTP Body 생성
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "authorization_code");
            body.add("client_id", clientId);
            body.add("redirect_uri", redirectUri);
            body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                reqUrl,
                HttpMethod.POST,
                tokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return jsonNode.get("access_token").asText();
    }

    public MyUserDetails getUserInfo(String accessToken, String type) {
        String apiUrl = "";
        if(type.equals("kakao")) {
            apiUrl = KAKAO_API_URI;
        }
        try {
            //HttpHeader 생성
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            //HttpHeader 담기
            RestTemplate rt = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
            ResponseEntity<String> response = rt.exchange(
                    apiUrl + "/v2/user/me",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            //Response 데이터 파싱
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(response.getBody());
                Member member = socialLogin(rootNode,type);
                log.info("member : {}",member);

                return MyUserDetails.builder()
                        .user(member)
                        .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Member socialLogin(JsonNode rootNode, String type){
        Member member = new Member();
        if(type.equals("kakao")) {
            JsonNode account = rootNode.get("kakao_account");
            String custEmail = account.get("email").asText();
            Customer customer = customerRepository.findByCustEmail(custEmail);

            if(customer != null) {
                Optional<Member> optMember = memberRepository.findByCustomer_id(customer.getId());
                if(optMember.isPresent()) {
                    return optMember.get();
                }
            }

            String memUid = "KAKAO_SOCIAL_"+rootNode.get("id").asLong();
            String connectedAtString = rootNode.get("connected_at").asText();
            Instant instant = Instant.parse(connectedAtString);
            Timestamp memRdate = Timestamp.from(instant);

            String custName = account.get("name").asText();
            String custHp = "0"+String.valueOf(account.get("phone_number")).split(" ")[1];
            Boolean custGender= account.get("gender").asText().equals("male");
            String birthday = account.get("birthday").asText();
            String birthyear = account.get("birthyear").asText();
            String custBirth = birthyear + "-" + birthday.substring(0, 2) + "-" + birthday.substring(2, 4);

            customer = Customer.builder()
                    .custName(custName)
                    .custHp(custHp)
                    .custEmail(custEmail)
                    .custBirth(custBirth)
                    .custGender(custGender)
                    .build();

            member = Member.builder()
                    .memRole("customer")
                    .customer(customer)
                    .memUid(memUid)
                    .memRdate(memRdate)
                    .build();

            memberRepository.save(member);
            customerRepository.save(customer);
        }

        return member;
    }
}
