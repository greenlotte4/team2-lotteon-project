package com.lotteon.repository.article;

import com.lotteon.entity.article.Notice;
import com.lotteon.entity.category.CategoryArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findAll();

    Page<Notice> findByCate1_CategoryIdAndNoticeTitleContainingOrNoticeContentContaining(
            Long cate1Id, String titleKeyword, String contentKeyword, Pageable pageable);

    Page<Notice> findByCate1_CategoryId(Long cate1Id, Pageable pageable);

    Page<Notice> findByCate2_CategoryId(Long cate2Id, Pageable pageable);

    Page<Notice> findByNoticeTitleContainingOrNoticeContentContaining(
            String keyword, String keyword1, Pageable pageable);

    List<Notice> findAll(Sort sort);

    Page<Notice> findAllByOrderByIdDesc(Pageable pageable);

    Page<Notice> findByCate1_CategoryIdAndCate2_CategoryId(Long categoryId, Long categoryId2, Pageable pageable);

    // 최신 공지사항 10개 조회
    List<Notice> findTop10ByOrderByNoticeRdateDesc();
    List<Notice> findTop5ByOrderByNoticeRdateDesc();
    // cate1의 CategoryName으로 공지사항을 검색하는 메서드 (중복 제거 후 하나만 남김)
    Page<Notice> findByCate1_CategoryName(String cate1Name, Pageable pageable);

    // cate2 이름을 기반으로 검색 (필요한 경우)
    Page<Notice> findByCate2_CategoryName(String cate2Name, Pageable pageable);

    // 모든 공지사항을 불러오는 메서드 예시
    Page<Notice> findAll(Pageable pageable);

    Page<Notice> findByCate1(CategoryArticle cate1, Pageable pageable);

    Page<Notice> findByCate2(CategoryArticle cate2, Pageable pageable);

    Page<Notice> findByCate1AndNoticeTitleContaining(CategoryArticle cate1, String title, Pageable pageable);

    Page<Notice> findByNoticeTitleContaining(String title, Pageable pageable);
}
