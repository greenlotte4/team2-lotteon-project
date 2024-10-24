package com.lotteon.service.term;

import com.lotteon.dto.requestDto.PostTermsDTO;
import com.lotteon.dto.responseDto.GetBannerDTO;
import com.lotteon.dto.responseDto.GetTermsResponseDto;
import com.lotteon.entity.term.Terms;
import com.lotteon.repository.term.TermsRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        List<Terms> terms = termsRepository.findAllByTermsType(termsType);
        System.out.println("Terms list: " + terms);

        // List<Terms> -> List<GetTermsResponseDto>
        List<GetTermsResponseDto> getTermsResponseDtoList = terms.stream()
                .map(term -> modelMapper.map(term, GetTermsResponseDto.class))
                .collect(Collectors.toList());

        System.out.println("DTO List: " + getTermsResponseDtoList);
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

    public Terms modifyTerms(PostTermsDTO postTermsDTO) {
        Terms terms = termsRepository.findById(postTermsDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("terms not found"));

        terms.setTermsContent(postTermsDTO.getTermsContent());

        return termsRepository.save(terms);
    }

    public List<GetTermsResponseDto> selectAllTerms() {
        List<Terms> terms = termsRepository.findAll();
        System.out.println("Terms list: " + terms);
        return terms.stream()
                .map(Entity -> modelMapper.map(Entity, GetTermsResponseDto.class))
                .toList();
    }
}

