package com.lotteon.service.article;

import com.lotteon.dto.ArticleDto;
import com.lotteon.entity.article.Qna;
import com.lotteon.entity.article.Qna;
import com.lotteon.entity.category.CategoryArticle;
import com.lotteon.entity.member.Member;
import com.lotteon.repository.article.QnaRepository;
import com.lotteon.repository.category.CategoryArticleRepository;
import com.lotteon.repository.member.MemberRepository;
import com.lotteon.service.category.CategoryArticleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class QnaService {

    private final QnaRepository qnaRepository;
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final CategoryArticleService categoryArticleService;
    private final CategoryArticleRepository categoryArticleRepository;





    /* 관리자 cs 기능 */
    // QNA 목록 조회 (카테고리별)
    public Page<ArticleDto> getQnas(CategoryArticle cate1, CategoryArticle cate2, int limit, Pageable pageable) {
        // Page<Qna>를 사용하여 페이징 처리된 결과를 받아옴
        Page<Qna> QnaPage = qnaRepository.findByCate1AndCate2(cate1, cate2, pageable);
        // Page<ArticleDto>로 변환하여 반환
        return QnaPage.map(Qna -> modelMapper.map(Qna, ArticleDto.class));
    }
    public ArticleDto getById(Long id) {
        Qna qna = qnaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 Qna가 없습니다."));
        return ArticleDto.fromEntity(qna);
    }

    // QNA 목록 조회 (전부)
    public Page<ArticleDto> getAllQnas(Pageable pageable) {
        // 1. QnaRepository에서 페이징 처리된 결과가 반환되게
        Page<Qna> qnaPage = qnaRepository.findAll(pageable);
        // 2. Qna타입을 갖고있는 page를 ArticleDto타입을 갖는 page로 변환
        Page<ArticleDto> result = qnaPage.map(qna-> ArticleDto.fromEntity(qna));
        return result;
    }


    // Qna 삭제
    public void deleteQna(Long id) {
        Qna qna = qnaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 자주묻는질문을 찾을 수 없습니다. ID: " + id));
        qnaRepository.delete(qna);
    }

    public void deleteSelectedqnas(List<Long> ids) {
        List<Qna> qnasToDelete = qnaRepository.findAllById(ids);

        if (qnasToDelete.isEmpty()) {
            throw new IllegalArgumentException("삭제할 문의가 없습니다.");
        }

        qnaRepository.deleteAllById(ids);
    }

    public Page<ArticleDto> getAllqnas(Pageable pageable) {
        // 1. qnaRepository에서 페이징 처리된 결과가 반환되게
        Page<Qna> qnaPage = qnaRepository.findAll(pageable);
        System.out.println("qnaPage.getContent() = " + qnaPage.getContent());
        // 2. qna타입을 갖고있는 page를 ArticleDto타입을 갖는 page로 변환
        Page<ArticleDto> result = qnaPage.map(qna-> ArticleDto.fromEntity(qna));
        return result;
    }

    // qna 상세 조회 (ID로 조회)
    public ArticleDto getQnaById(Long id) {
        return qnaRepository.findById(id)
                .map(ArticleDto::fromEntity) // DTO로 변환
                .orElseThrow(() -> new IllegalArgumentException("Qna를 찾을 수 없습니다."));
    }



    // QnA 글 작성
    public Long insertQna(ArticleDto articleDto, HttpServletRequest req) {
        // 작성일 및 기본 정보 설정
        articleDto.setRdate(LocalDateTime.now());
        articleDto.setViews(0); // 초기 조회수 0
        articleDto.setState(0); // 초기 답변 상태: 대기

        // 세션에서 사용자 ID 가져오기 (비회원 작성 테스트할 때 주석처리 ...)
        Long memberId = (Long) req.getSession().getAttribute("memberId");
        if (memberId == null) {
            throw new IllegalStateException("로그인된 사용자가 아닙니다.");
        }

        // 작성자 설정
        articleDto.setMemId(memberId);

        // 카테고리 설정 (1차, 2차 카테고리)
        CategoryArticle cate1 = categoryArticleService.getCategoryById(articleDto.getCate1Id());
        CategoryArticle cate2 = categoryArticleService.getCategoryById(articleDto.getCate2Id());

        // 회원 정보 설정
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));

        // DTO -> Entity 변환 (필드를 수동 매핑)
        Qna qna = Qna.builder()
                .qnaTitle(articleDto.getTitle())
                .qnaContent(articleDto.getContent())
                .qnaRdate(Timestamp.valueOf(articleDto.getRdate()).toLocalDateTime())
                .qnaState(articleDto.getState())
                .qnaType(articleDto.getType())
                .qnaAnswer(articleDto.getAnswer())
                .qnaViews(articleDto.getViews())
                .cate1(cate1)
                .cate2(cate2)
                .member(member)
                .build();

        // DB에 저장
        Qna savedQna = qnaRepository.save(qna);
        return savedQna.getId(); // 저장된 QnA ID 반환
    }




}
