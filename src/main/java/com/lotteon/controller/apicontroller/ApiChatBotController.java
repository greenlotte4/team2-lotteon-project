package com.lotteon.controller.apicontroller;

import com.lotteon.dto.requestDto.ChatReqDto;
import com.lotteon.dto.responseDto.ChatRespDto;
//import com.lotteon.service.ChatBotService;
import com.lotteon.entity.product.Product;
import com.lotteon.service.ChatBotService;
import com.lotteon.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ApiChatBotController {

    private final RestTemplate template;
    private final ProductService productService;
    private final ChatBotService chatBotService;

    @GetMapping("/chat")
    public String chat(@RequestParam(name = "prompt")String prompt){
        String regex = "(화장품|가전|의류|식품|도서|여성의류|남성의류|브랜드의류|악기|취미용품|스포츠브랜드|로션|스마트폰|상의|캐쥬얼브랜드|유아의류|선크림|라면|자켓|코트|스킨|원피스|삼성|아이폰)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(prompt);
        if (prompt.contains("상품")) {
            String result = chatBotService.findBestProduct(prompt);
            return result;
        }
        if(matcher.find()){
            String result = chatBotService.findBestCategory(prompt);
            return result;
        }

        if(prompt.contains("주문취소")){
            if(prompt.contains("쳇봇주문취소")){
                String result = chatBotService.cancleOrder(prompt);
                return result;
            }
            if(prompt.contains("주문취소조회")){
                String result = chatBotService.possibleOrder(prompt);
                return result;
            }

            return "주문 취소 방법을 알려드리겠습니다. 주문 취소는 판매자가 배송을 보내기 전 상태에 가능합니다. \n 마이페이지의 주문내역이나 메인페이지에서 확인 가능합니다. \n 취소가능한 주문번호를 조회하시려면 \n 주문취소조회 를 입력해주세요. \n 주문취소를 진행하시려면 \n 쳇봇주문취소 : 주문번호를 입력해주세요.";
        }
        if(prompt.contains("배송조회")){
            if(prompt.contains("쳇봇배송조회")){
                String result = chatBotService.selectDeliDate(prompt);
                return result;
            }
            return "배송예정일 조회 방법을 알려드리겠습니다. 배송조회는 판매자가 배송을 보낸 후 상태에 가능합니다. \n 마이페이지의 주문내역이나 메인페이지에서 확인 가능합니다. \n 배송조회가 가능한 주문번호를 조회하시려면 \n 배송예정일조회 를 입력해주세요. \n 배송조회를 진행하시려면 \n 쳇봇배송조회 : 주문번호를 입력해주세요.";
        }

        if(prompt.contains("배송예정일조회")){
            String result = chatBotService.possibleDeli(prompt);
            return result;
        }

        if(prompt.contains("반품")){
            if(prompt.contains("반품주문조회")){
                String result = chatBotService.possibleReturn(prompt);
                return result;
            }

            if(prompt.contains("쳇봇반품하기")){
                String result = chatBotService.selectReturn(prompt);
                return result;
            }

            return "반품방법에 대해 알려드리겠습니다. 반품 서비스는 수취확인을 하지 않은 상태에서 7일이내에 이용가능합니다. \n 마이페이지의 주문내역이나 메인페이지에서 확인 가능합니다. \n 반품 가능한 주문번호를 조회하시려면 \n 반품주문조회 를 입력해주세요. \n 반품하기를 진행하시려면 \n 쳇봇반품하기 : 주문아이템번호를 입력해주세요.";
        }
        if(prompt.contains("교환")){
            if(prompt.contains("교환주문조회")){
                String result = chatBotService.possibleChange(prompt);
                return result;
            }

            if(prompt.contains("쳇봇교환하기")){
                String result = chatBotService.selectChange(prompt);
                return result;
            }

            return "교환방법에 대해 알려드리겠습니다. 교환 서비스는 수취확인을 하지 않은 상태에서 7일이내에 이용가능합니다. \n 마이페이지의 주문내역이나 메인페이지에서 확인 가능합니다. \n 교환 가능한 주문번호를 조회하시려면 \n 교환주문조회 를 입력해주세요. \n 교환하기를 진행하시려면 \n 쳇봇교환하기 : 주문아이템번호를 입력해주세요.";
        }

        if(prompt.contains("이벤트")){
            return "현재 진행중인 이벤트 목록입니다.\n 1. 출석이벤트 : 하루에 한번 출석시 100p 5일 연속 출석시 2000p 7일 연속 출석시 10000원 할인 쿠폰을 받을 수 있습니다. 5일 , 7일차 이벤트 상품은 매달 1회만 수령하실 수 있습니다. \n 2. 생일 이벤트 : 생일날 로그인시 10000원 할인 쿠폰을 수령하실 수 있습니다. \n 3. 데일리 쿠폰 수령 이벤트 : 매일매일 판매자별 다양한 쿠폰을 발급받고 사용하실 수 있습니다.";
        }

        if(prompt.contains("쿠폰")){
            if(prompt.contains("쳇봇쿠폰조회")){
                String result = chatBotService.possibleCustomerCoupon();
                return result;
            }

            return "쿠폰은 전체상품 적용 쿠폰, 판매자별 적용 쿠폰 2가지가 있습니다. \n 사용가능한 쿠폰을 조회하시고 싶으시면 쳇봇쿠폰조회를 입력해주세요.";
        }

        if(prompt.contains("포인트")){
            if(prompt.contains("내포인트조회")){
                String result = chatBotService.findAllCustomerPoint();
                return result;
            }

            return "포인트는 5000p 이상부터 자유롭게 현금처럽 사용할 수 있습니다. \n 포인트 적립은 상품 구매금액의 1%만 적용됩니다. 현재 보유하고있는 포인트를 조회하시려면 \n 내포인트조회를 입력해주세요.";
        }

        ChatReqDto request = new ChatReqDto("gpt-3.5-turbo", prompt);
        ChatRespDto chatGPTResponse =  template.postForObject("https://api.openai.com/v1/chat/completions", request, ChatRespDto.class);
        return chatGPTResponse.getChoices().get(0).getMessage().getContent();
    }
}