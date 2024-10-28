package com.lotteon.service.term;

import com.lotteon.dto.requestDto.PostTermsDTO;
import com.lotteon.dto.responseDto.GetTermsResponseDto;
import com.lotteon.entity.term.Terms;
import com.lotteon.repository.term.TermsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/*
     날짜 : 2024/10/25
     이름 : 이상훈
     내용 : 컨트롤러 생성

     수정이력
      - 2025/10/28 김주경 - selectAllTerms Redis 캐싱처리
*/


@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class TermsService {

    private final TermsRepository termsRepository;
    private final ModelMapper modelMapper;


    // 1. 이용약관
//    public GetTermsResponseDto selectTerms(String termsType) {
//        System.out.println("22222" + termsType);
//       List<Terms> terms = termsRepository.findAllByTermsType(termsType);
//
//        System.out.println("333333" + terms);
//       GetTermsResponseDto userMapDto = modelMapper.map(terms, GetTermsResponseDto.class);
//
//        System.out.println(" 111111 "+userMapDto);
//       return userMapDto;
//    }


    public List<GetTermsResponseDto> selectTerms(String termsType) {
        List<Terms> terms = termsRepository.findAllByTermsTypeContains(termsType);

        // List<Terms> -> List<GetTermsResponseDto>
        List<GetTermsResponseDto> getTermsResponseDtoList = terms.stream()
                .map(term -> modelMapper.map(term, GetTermsResponseDto.class))
                .collect(Collectors.toList());
        return getTermsResponseDtoList;
    }

    // termsType에 따라 약관 조회
//    public List<GetTermsResponseDto> getTermsByType(String termsType) {
//        // termsType에 따라 데이터 조회
//        List<Terms> termsList = termsRepository.findAllByTermsType(termsType);
//
//        // Terms 엔티티를 GetTermsResponseDto로 매핑
//        return termsList.stream()
//                .map(terms -> new GetTermsResponseDto(terms.getTermsName(), terms.getTermsContent()))
//                .collect(Collectors.toList());
//    }

    @CacheEvict(value = "termsCache", key = "'terms'")
    public Terms modifyTerms(PostTermsDTO postTermsDTO) {
        Terms terms = termsRepository.findById(postTermsDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("terms not found"));

        terms.setTermsContent(postTermsDTO.getTermsContent());

        return termsRepository.save(terms);
    }

    @Cacheable(value = "termsCache", key = "'terms'", cacheManager = "cacheManager")
    public List<GetTermsResponseDto> selectAllTerms() {
        List<Terms> terms = termsRepository.findAll();
        return terms.stream()
                .map(Entity -> modelMapper.map(Entity, GetTermsResponseDto.class))
                .collect(Collectors.toList());
    }
}

