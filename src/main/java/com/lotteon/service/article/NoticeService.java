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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        Sort sort = Sort.by(Sort.Direction.DESC, "noticeRdate"); // 최신 순으로 정렬
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Notice> notices = (keyword == null || keyword.isEmpty())
                ? noticeRepository.findAll(sortedPageable)
                : noticeRepository.findByNoticeTitleContainingOrNoticeContentContaining(keyword, keyword, sortedPageable);

        return notices.map(this::mapNoticeToResponseDto);
    }

    // cate1을 기준으로 공지사항 목록 조회
    public Page<NoticeResponseDto> getNoticesByCate1(String cate1Name, Pageable pageable) {
        CategoryArticle cate1 = categoryArticleRepository.findByCategoryName(cate1Name)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다. 이름: " + cate1Name));

        Page<Notice> notices = noticeRepository.findByCate1(cate1, pageable);
        return notices.map(this::mapNoticeToResponseDto);
    }

    // cate2을 기준으로 공지사항 목록 조회 (필요한 경우)
    public Page<NoticeResponseDto> getNoticesByCate2(String cate2Name, Pageable pageable) {
        CategoryArticle cate2 = categoryArticleRepository.findByCategoryName(cate2Name)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다. 이름: " + cate2Name));

        Page<Notice> notices = noticeRepository.findByCate2(cate2, pageable);
        return notices.map(this::mapNoticeToResponseDto);
    }

    // cate1과 제목을 기준으로 공지사항 목록 조회
    public Page<NoticeResponseDto> getNoticesByCate1AndTitle(String type, String title, Pageable pageable) {
        CategoryArticle cate1 = categoryArticleRepository.findByCategoryName(type)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다. 이름: " + type));

        Page<Notice> notices = noticeRepository.findByCate1AndNoticeTitleContaining(cate1, title, pageable);
        return notices.map(this::mapNoticeToResponseDto);
    }

    // 제목을 기준으로 공지사항 목록 조회
    public Page<NoticeResponseDto> getNoticesByTitle(String title, Pageable pageable) {
        Page<Notice> notices = noticeRepository.findByNoticeTitleContaining(title, pageable);
        return notices.map(this::mapNoticeToResponseDto);
    }

    // 조회수 증가 후 공지사항 단일 조회
    public NoticeResponseDto incrementViewsAndGetNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + id));
        notice.incrementViews();
        noticeRepository.save(notice);

        return mapNoticeToResponseDto(notice);
    }

    // 공지사항 저장 (작성 및 수정)
    public void saveNotice(NoticeRequestDto requestDto) {
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. ID: " + requestDto.getMemberId()));

        CategoryArticle categoryArticle1 = categoryArticleRepository.findById(requestDto.getCateId1())
                .orElseThrow(() -> new IllegalArgumentException("카테고리 1을 찾을 수 없습니다. ID: " + requestDto.getCateId1()));

        CategoryArticle categoryArticle2 = requestDto.getCateId2() != null
                ? categoryArticleRepository.findById(requestDto.getCateId2()).orElse(null)
                : null;

        Notice entity = modelMapper.map(requestDto, Notice.class);
        entity.setMember(member);
        entity.setCate1(categoryArticle1);
        entity.setCate2(categoryArticle2);

        noticeRepository.save(entity);
    }

    // 공지사항 수정
    public NoticeResponseDto updateNotice(Long id, NoticeRequestDto noticeRequestDto) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + id));

        CategoryArticle cate1 = categoryArticleRepository.findById(noticeRequestDto.getCateId1())
                .orElseThrow(() -> new IllegalArgumentException("카테고리 1을 찾을 수 없습니다. ID: " + noticeRequestDto.getCateId1()));

        CategoryArticle cate2 = noticeRequestDto.getCateId2() != null
                ? categoryArticleRepository.findById(noticeRequestDto.getCateId2()).orElse(null)
                : null;

        notice.setNoticeTitle(noticeRequestDto.getNoticeTitle());
        notice.setNoticeContent(noticeRequestDto.getNoticeContent());
        notice.setCate1(cate1);
        notice.setCate2(cate2);

        Notice updatedNotice = noticeRepository.save(notice);
        return mapNoticeToResponseDto(updatedNotice);
    }

    // 공지사항 삭제
    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + id));
        noticeRepository.delete(notice);
    }

    // 선택된 공지사항 삭제
    public void deleteSelectedNotices(List<Long> ids) {
        List<Notice> noticesToDelete = noticeRepository.findAllById(ids);

        if (noticesToDelete.isEmpty()) {
            throw new IllegalArgumentException("삭제할 공지사항이 없습니다.");
        }

        noticeRepository.deleteAllById(ids);
    }

    // 최신 공지사항 10개 조회
    public List<Notice> getTop10Notices() {
        return noticeRepository.findTop10ByOrderByNoticeRdateDesc();
    }
    public List<Notice> getTop5Notices() { return noticeRepository.findTop5ByOrderByNoticeRdateDesc();
    }
    // Notice -> NoticeResponseDto 변환 함수
    private NoticeResponseDto mapNoticeToResponseDto(Notice notice) {
        NoticeResponseDto responseDto = modelMapper.map(notice, NoticeResponseDto.class);
        responseDto.setMemberId(notice.getMember().getId());
        responseDto.setCategory1Name(notice.getCate1().getCategoryName());
        responseDto.setCategory2Name(notice.getCate2() != null ? notice.getCate2().getCategoryName() : null);
        responseDto.setCateId1(notice.getCate1().getCategoryId());
        responseDto.setCateId2(notice.getCate2() != null ? notice.getCate2().getCategoryId() : null);
        return responseDto;
    }

    // 공지사항 단일 조회 메서드
    public NoticeResponseDto getNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + id));

        return mapNoticeToResponseDto(notice);
    }
    public List<CategoryArticle> getChildCategories(Long parentCategoryId) {
        CategoryArticle parentCategory = categoryArticleRepository.findById(parentCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 부모 카테고리를 찾을 수 없습니다: " + parentCategoryId));
        return categoryArticleRepository.findByParent(parentCategory);
    }
}