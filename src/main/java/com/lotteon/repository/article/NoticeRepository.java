package com.lotteon.repository.article;

import com.lotteon.entity.article.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 제목 또는 내용에 특정 키워드가 포함된 공지사항을 검색 (페이징 지원)
    Page<Notice> findByNoticeTitleContainingOrNoticeContentContaining(String titleKeyword, String contentKeyword, Pageable pageable);

    // 특정 카테고리1에 속하는 공지사항 조회 (페이징 지원)
    Page<Notice> findByCate1_CategoryId(Long cate1Id, Pageable pageable);  // 수정: cate1_Id -> cate1_CategoryId

    // 특정 카테고리2에 속하는 공지사항 조회 (페이징 지원)
    Page<Notice> findByCate2_CategoryId(Long cate2Id, Pageable pageable);  // 수정: cate2_Id -> cate2_CategoryId

    // 제목 또는 내용에 특정 키워드가 포함되고 카테고리1에 속하는 공지사항을 검색 (페이징 지원)
    Page<Notice> findByCate1_CategoryIdAndNoticeTitleContainingOrNoticeContentContaining(
            Long cate1Id, String titleKeyword, String contentKeyword, Pageable pageable);  // 수정: cate1_Id -> cate1_CategoryId
}
