package com.lotteon.service.article;

import com.lotteon.config.MyUserDetails;
import com.lotteon.dto.ArticleDto;
import com.lotteon.entity.article.Qna;
import com.lotteon.entity.article.Qna;
import com.lotteon.entity.category.CategoryArticle;
import com.lotteon.entity.member.Member;
import com.lotteon.entity.member.Seller;
import com.lotteon.repository.article.QnaRepository;
import com.lotteon.repository.category.CategoryArticleRepository;
import com.lotteon.repository.member.MemberRepository;
import com.lotteon.repository.member.SellerRepository;
import com.lotteon.service.category.CategoryArticleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
    private final SellerRepository sellerRepository;
    private final CategoryArticleService categoryArticleService;


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


    public Page<ArticleDto> getAllQnas(Pageable pageable) {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = auth.getUser();

        Page<Qna> qnaPage;
        if ("seller".equals(member.getMemRole())) {
            // 판매자일 경우 자신의 상품에 대한 QnA만 조회
            qnaPage = qnaRepository.findAllBySeller(member.getSeller(), pageable);
            System.out.println(qnaPage);
        } else {
            // 관리자는 모든 QnA를 조회
            qnaPage = qnaRepository.findAll(pageable);
        }

        return qnaPage.map(ArticleDto::fromEntity); // QnA 엔티티를 ArticleDto로 변환하여 반환
    }


    public long getTotalQnaCount() {
        return qnaRepository.count();  // JpaRepository의 count 메서드를 통해 전체 개수를 반환
    }


    // 답변 여부 확인
    public boolean hasAnswer(Long id) {
        Qna qna = qnaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 QnA가 없습니다."));
        return qna.getQnaAnswer() != null && !qna.getQnaAnswer().isEmpty();
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
        // 기존 코드에서는 findByCategoryNameAndCategoryLevelAndCategoryType 메서드를 사용하여 카테고리를 찾았으나, 중복 결과로 인한 오류를 방지하기 위해 findFirstByCategoryNameAndCategoryLevelAndCategoryType 메서드로 수정했습니다.
        CategoryArticle cate1 = categoryArticleRepository
                .findFirstByCategoryNameAndCategoryLevelAndCategoryType(articleDto.getCate1Name(), 1, 2)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 없습니다."));
        CategoryArticle cate2 = categoryArticleRepository
                .findFirstByCategoryNameAndCategoryLevelAndCategoryType(articleDto.getCate2Name(), 2, 2)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 없습니다."));

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

    // 1차 및 2차 카테고리 기반 QnA 조회
    public Page<ArticleDto> getQnasByCate1AndCate2(String cate1Name, String cate2Name, Pageable pageable) {
        CategoryArticle cate1 = null;
        CategoryArticle cate2 = null;

        // cate1이 빈 문자열이 아닌 경우에만 검색
        if (cate1Name != null && !cate1Name.isEmpty()) {
            cate1 = categoryArticleService.getCategoryByName(cate1Name);
        }

        // cate2가 빈 문자열이 아닌 경우에만 검색
        if (cate2Name != null && !cate2Name.isEmpty()) {
            cate2 = categoryArticleService.getCategoryByName(cate2Name);
        }

        // cate1 또는 cate2가 null인 경우 전체 조회를 반환하도록 수정
        if (cate1 == null && cate2 == null) {
            return getAllQnas(pageable);  // 전체 조회
        }

        Page<Qna> qnaPage = qnaRepository.findByCate1AndCate2(cate1, cate2, pageable);
        return qnaPage.map(ArticleDto::fromEntity);
    }

    public Page<ArticleDto> getNotAllQnas(String cate1, String cate2, Pageable pageable) {
        // 1차 및 2차 카테고리로 CategoryArticle 객체 조회
        CategoryArticle categoryArticle1 = categoryArticleRepository.findByCategoryNameAndCategoryLevelAndCategoryType(cate1, 1, 2)
                .orElseThrow(() -> new IllegalArgumentException("해당 1차 카테고리를 찾을 수 없습니다: " + cate1));
        CategoryArticle categoryArticle2 = categoryArticleRepository.findByCategoryNameAndCategoryLevelAndCategoryType(cate2, 2, 2)
                .orElseThrow(() -> new IllegalArgumentException("해당 2차 카테고리를 찾을 수 없습니다: " + cate2));

        // 1차 및 2차 카테고리로 필터링된 QnA 목록을 페이징하여 조회
        Page<Qna> qnas = qnaRepository.findAllByCate1AndCate2(categoryArticle1, categoryArticle2, pageable);

        // QnA 엔티티를 ArticleDto로 변환하여 반환
        return qnas.map(ArticleDto::fromEntity);
    }


    // QnA 글 작성 셀러 문의
    public Long insertQnaToSeller(ArticleDto articleDto) {
        Long memberId = articleDto.getMemId();
        String sellCompany = articleDto.getSellCompany();
        Seller seller = sellerRepository.findBySellCompany(sellCompany).get();

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
        // 기존 코드에서는 findByCategoryNameAndCategoryLevelAndCategoryType 메서드를 사용하여 카테고리를 찾았으나, 중복 결과로 인한 오류를 방지하기 위해 findFirstByCategoryNameAndCategoryLevelAndCategoryType 메서드로 수정했습니다.
        CategoryArticle cate2 = categoryArticleRepository
                .findFirstByCategoryNameAndCategoryLevelAndCategoryType(articleDto.getCate2Name(), 2, 2)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 없습니다."));
        CategoryArticle cate1 = cate2.getParent();

        // 회원 정보 설정
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));

        // DTO -> Entity 변환 (필드를 수동 매핑)
        Qna qna = Qna.builder()
                .qnaTitle(articleDto.getTitle())
                .qnaContent(articleDto.getContent())
                .qnaRdate(articleDto.getRdate())
                .qnaState(articleDto.getState())
                .qnaType(1)
                .qnaAnswer(articleDto.getAnswer())
                .qnaViews(articleDto.getViews())
                .cate1(cate1)
                .cate2(cate2)
                .member(member)
                .seller(seller) // 판매자 ID 설정
                .build();

        // DB에 저장
        Qna savedQna = qnaRepository.save(qna);
        return savedQna.getId(); // 저장된 QnA ID 반환
    }

    // QNA 삭제
    public void deleteQna(Long id) {
        Qna qna = qnaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의를 찾을 수 없습니다. ID: " + id));
        qnaRepository.delete(qna);
    }


    // 선택 삭제
    public void deleteSelectedQnas(List<Long> ids) {
        List<Qna> qnasToDelete = qnaRepository.findAllById(ids);

        if (qnasToDelete.isEmpty()) {
            throw new IllegalArgumentException("삭제할 문의가 없습니다.");
        }

        qnaRepository.deleteAllById(ids);
    }


    /* 일반 CS (목, 보기) */
    // index에서 qna 5개 조회
    public List<Qna> getTop5Qnas() {
        return qnaRepository.findTop5ByOrderByQnaRdateDesc();
    }

    // 1차 카테고리별 QNA 목록 조회
    public Page<ArticleDto> getQnasByCategory(String category, Pageable pageable) {
        CategoryArticle cate1 = categoryArticleRepository.findByCategoryName(category)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다. 이름: " + category));

        Page<Qna> qnaPage = qnaRepository.findByCate1(cate1, pageable);
        return qnaPage.map(ArticleDto::fromEntity); // 기존 fromEntity 메서드 활용
    }

    public List<ArticleDto> getMyQnas(Long memberId) {
        List<Qna> qnas = qnaRepository.findByMemberId(memberId);
        return qnas.stream()
                .map(ArticleDto::fromEntity)
                .collect(Collectors.toList());
    }

    public Page<ArticleDto> getMyQnas(Long memberId, Pageable pageable) {
        Page<Qna> qnas = qnaRepository.findByMemberId(memberId, pageable);
        return qnas
                .map(ArticleDto::fromEntity);
    }

    public List<Qna> getTop5QnasForMy(long memId) {
        return qnaRepository.findTop5ByMember_IdOrderByQnaRdateDesc(memId);
    }

    public Long findCnt(LocalDateTime startOfDay, LocalDateTime endOfDay) {

        return qnaRepository.countByQnaRdateBetween(startOfDay,endOfDay);
    }

    public Long findByCustomer() {
        MyUserDetails auth = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return qnaRepository.countByMemberId(auth.getUser().getId());

    }
}

