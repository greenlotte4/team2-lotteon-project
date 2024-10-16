package com.lotteon.service.term;

import com.lotteon.entity.term.Terms;
import com.lotteon.repository.term.TermsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TermsService {

    private final TermsRepository termsRepository;

    public List<Terms> selectTerms(String termsType){

       List<Terms> terms = termsRepository.findAllByTermsType(termsType);

       return terms;
    }




}

