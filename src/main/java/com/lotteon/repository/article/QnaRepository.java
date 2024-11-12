package com.lotteon.repository.article;

import com.lotteon.entity.article.Faq;
import com.lotteon.entity.article.Qna;
import com.lotteon.entity.category.CategoryArticle;
import com.lotteon.entity.member.Customer;
import com.lotteon.entity.member.Member;
import com.lotteon.entity.member.Seller;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QnaRepository extends JpaRepository<Qna,Long> {

    // 1차 및 2차 카테고리로 FAQ 목록을 조회 (페이징 처리 포함)
    Page<Qna> findByCate1AndCate2(CategoryArticle cate1Id, CategoryArticle cate2Id, Pageable pageable);
    Page<Qna> findAllByCate1AndCate2(CategoryArticle categoryArticle, CategoryArticle categoryArticle2, Pageable pageable);

    List<Qna> findTop5ByOrderByQnaRdateDesc();

    // 1차 카테고리로 QnA 목록 조회
    Page<Qna> findByCate1(CategoryArticle cate1, Pageable pageable);

    List<Qna> findByCate1AndCate2(CategoryArticle cate1Id, CategoryArticle cate2Id, Limit limit);

    List<Qna> findByMemberId(Long memberId);
    // 사용자 ID로 조회하는 메서드 추가
    Page<Qna> findByMemberId(Long memberId, Pageable pageable); // 사용자 ID로 조회하는 메서드 추가

    Page<Qna> findAllByMember_Seller(Seller seller, Pageable pageable);

    List<Qna> findTop5ByMember_IdOrderByQnaRdateDesc(Long id);

    Page<Qna> findAllByMember(Member member, Pageable pageable);

    Long countByQnaRdateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

    Long countByMemberId(Long id);

    Page<Qna> findAllBySeller(Seller seller, Pageable pageable);

}
