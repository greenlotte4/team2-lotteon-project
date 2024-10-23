package com.lotteon.service.article;

import com.lotteon.dto.requestDto.NoticeRequestDto;
import com.lotteon.dto.responseDto.NoticeResponseDto;
import com.lotteon.entity.article.Notice;
import com.lotteon.entity.category.CategoryArticle;
import com.lotteon.entity.member.Member;
import com.lotteon.repository.article.NoticeRepository;
import com.lotteon.repository.member.MemberRepository;
import com.lotteon.repository.category.CategoryArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;  // Sort 클래스 import 추가
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final CategoryArticleRepository categoryArticleRepository;

    // 공지사항 목록 조회 (검색 및 페이징)
    public Page<NoticeResponseDto> getNotices(String keyword, Pageable pageable) {
        Page<Notice> notices;
        if (keyword == null || keyword.isEmpty()) {
            notices = noticeRepository.findAll(pageable);
        } else {
            notices = noticeRepository.findByNoticeTitleContainingOrNoticeContentContaining(keyword, keyword, pageable);
        }
        return notices.map(notice -> modelMapper.map(notice, NoticeResponseDto.class));
    }

    // 조회수 증가 후 공지사항 단일 조회
    public NoticeResponseDto incrementViewsAndGetNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + id));
        notice.incrementViews();  // 조회수 증가
        noticeRepository.save(notice);  // 증가된 조회수를 저장
        return modelMapper.map(notice, NoticeResponseDto.class);
    }

    // 공지사항 저장 (작성 및 수정)
    public void saveNotice(NoticeRequestDto RequestDto) {
        long uid = RequestDto.getMemberId();
        Member member = memberRepository.findById(uid)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. ID: " + uid));
        RequestDto.setMember(member);

        long cate1 = RequestDto.getCateId1();
        long cate2 = RequestDto.getCateId2();

        CategoryArticle categoryArticle1 = categoryArticleRepository.findById(cate1)
                .orElseThrow(() -> new IllegalArgumentException("카테고리 1을 찾을 수 없습니다. ID: " + cate1));
        CategoryArticle categoryArticle2 = categoryArticleRepository.findById(cate2)
                .orElseThrow(() -> new IllegalArgumentException("카테고리 2을 찾을 수 없습니다. ID: " + cate2));

        RequestDto.setCate1(categoryArticle1);
        RequestDto.setCate2(categoryArticle2);

        Notice entity = modelMapper.map(RequestDto, Notice.class);
        noticeRepository.save(entity);
    }

    // 공지사항 수정
    public NoticeResponseDto updateNotice(Long id, NoticeRequestDto noticeRequestDto) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + id));

        notice.setNoticeTitle(noticeRequestDto.getNoticeTitle());
        notice.setNoticeContent(noticeRequestDto.getNoticeContent());

        CategoryArticle cate1 = categoryArticleRepository.findById(noticeRequestDto.getCateId1())
                .orElseThrow(() -> new IllegalArgumentException("카테고리 1을 찾을 수 없습니다. ID: " + noticeRequestDto.getCateId1()));
        CategoryArticle cate2 = categoryArticleRepository.findById(noticeRequestDto.getCateId2())
                .orElseThrow(() -> new IllegalArgumentException("카테고리 2을 찾을 수 없습니다. ID: " + noticeRequestDto.getCateId2()));

        notice.setCate1(cate1);
        notice.setCate2(cate2);

        Notice updatedNotice = noticeRepository.save(notice);
        return modelMapper.map(updatedNotice, NoticeResponseDto.class);
    }

    // 공지사항 삭제
    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + id));
        noticeRepository.delete(notice);
    }

    // 공지사항 단일 조회
    public NoticeResponseDto getNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + id));

        NoticeResponseDto responseDto = modelMapper.map(notice, NoticeResponseDto.class);
        responseDto.setMemberId(notice.getMember().getId());
        responseDto.setCateId1(notice.getCate1().getCategoryId());
        responseDto.setCateId2(notice.getCate2() != null ? notice.getCate2().getCategoryId() : null);
        responseDto.setCategory1Name(notice.getCate1().getCategoryName());
        responseDto.setCategory2Name(notice.getCate2() != null ? notice.getCate2().getCategoryName() : null);

        return responseDto;
    }

    // 선택된 공지사항 삭제
    public void deleteSelectedNotices(List<Long> ids) {
        List<Notice> noticesToDelete = noticeRepository.findAllById(ids);

        if (noticesToDelete.isEmpty()) {
            throw new IllegalArgumentException("삭제할 공지사항이 없습니다.");
        }

        noticeRepository.deleteAllById(ids);
    }

    // 전체 공지사항 목록을 시간순으로 조회 (최신순)
    public List<Notice> getAllNoticesSortedByDate() {
        return noticeRepository.findAll(Sort.by(Sort.Direction.DESC, "noticeRdate"));
    }

    public List<Notice> searchNotices(String keyword, Pageable pageable) {
        return noticeRepository.findByNoticeTitleContainingOrNoticeContentContaining(keyword, keyword, pageable).getContent();
    }

    public Page<Notice> getNotices(Pageable pageable) {
        return noticeRepository.findAll(pageable);
    }

    public List<Notice> getNoticesByCategory(Long categoryId, Pageable pageable) {
        return noticeRepository.findByCate1_CategoryId(categoryId, pageable).getContent();
    }
}
