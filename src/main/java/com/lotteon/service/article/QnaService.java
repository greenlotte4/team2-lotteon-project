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
/*
 *  이름 : 박경림
 *  날짜 : 2024-10-30
 *  작업내용 : 일반 CS index, list (공지사항 제외)
 *
 *
 * 수정이력
      - 2024/10/31 박경림 - 일반 CS 1차 유형별 글 목록 조회 기능 메서드 수정

 * */

@Service
@Transactional
@RequiredArgsConstructor
public class QnaService {

    private final QnaRepository qnaRepository;
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
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

    // 답변 여부 확인
    public boolean hasAnswer(Long id) {
        Qna qna = qnaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 QnA가 없습니다."));
        return qna.getQnaAnswer() != null && !qna.getQnaAnswer().isEmpty();
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


    // qna 답변하기 *관리자 작성
   public void reply(Long id, String answer){
       Qna qna = qnaRepository.findById(id)
               .orElseThrow(() -> new IllegalArgumentException("해당하는 QnA가 없습니다."));
       qna.changeAnswer(answer); // 답변 내용 업데이트
    }

    public void save(Qna qna) {
        qnaRepository.save(qna); // JPA save 메서드 호출
    }


    // QnA 글 작성 *일반 CS 고객 작성
    public Long insertQna(ArticleDto articleDto, Long memberId) {
        // 작성일 및 기본 정보 설정
        articleDto.setRdate(LocalDateTime.now());
        articleDto.setViews(0); // 초기 조회수 0
        articleDto.setState(0); // 초기 답변 상태: 대기 (답변 없음)

        if (memberId == null) {
            throw new IllegalStateException("로그인된 사용자가 아닙니다.");
        }

        // 작성자 설정
        articleDto.setMemId(memberId);

        // 카테고리 설정 (1차, 2차 카테고리)
        CategoryArticle cate1 = categoryArticleRepository.findByCategoryNameAndCategoryLevelAndCategoryType(articleDto.getCate1Name(),1,2)
                .orElseThrow(()->new IllegalArgumentException("해당 카테고리가 없습니다."));
        CategoryArticle cate2 = categoryArticleRepository.findByCategoryNameAndCategoryLevelAndCategoryType(articleDto.getCate2Name(),2,2)
                .orElseThrow(()->new IllegalArgumentException("해당 카테고리가 없습니다."));

        // 회원 정보 설정
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));

        // DTO -> Entity 변환 (필드를 수동 매핑)
        Qna qna = Qna.builder()
                .qnaTitle(articleDto.getTitle())
                .qnaContent(articleDto.getContent())
                .qnaRdate(articleDto.getRdate())
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




    /* 일반 CS (목록, 보기) */
    // index에서 qna 5개 조회
    public List<Qna> getTop5Qnas() {
        return qnaRepository.findTop5ByOrderByQnaRdateDesc();
    }

    /* TODO: qna 1차 유형별 조회 기능 */
    // 1차 카테고리별 QNA 목록 조회
    public Page<ArticleDto> getQnasByCategory(String category, Pageable pageable) {
        CategoryArticle cate1 = categoryArticleRepository.findByCategoryName(category)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다. 이름: " + category));

        Page<Qna> qnaPage = qnaRepository.findByCate1(cate1, pageable);
        return qnaPage.map(ArticleDto::fromEntity); // 기존 fromEntity 메서드 활용
    }






}




