package com.lotteon.repository.article;

import com.lotteon.entity.article.Notice;
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
}
