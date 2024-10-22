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
        Optional<Member> optMember = memberRepository.findById(uid);
        Member member = optMember.get();

        RequestDto.setMember(member);

        log.info("서비스 "+RequestDto);
        long cate1 = RequestDto.getCateId1();
        long cate2 = RequestDto.getCateId2();
        log.info("카테고리확인1"+cate1+cate2);

        Optional<CategoryArticle> optCategoryArticle1 = categoryArticleRepository.findById(cate1);
        Optional<CategoryArticle> optCategoryArticle2 = categoryArticleRepository.findById(cate2);
        log.info("카테고리확인2"+ optCategoryArticle1 + optCategoryArticle2);

        CategoryArticle categoryArticle1 = optCategoryArticle1.get();
        CategoryArticle categoryArticle2 = optCategoryArticle2.get();

        RequestDto.setCate1(categoryArticle1);
        RequestDto.setCate2(categoryArticle2);

        log.info("테스트" + RequestDto);

        Notice entity = modelMapper.map(RequestDto,Notice.class);

        noticeRepository.save(entity);
        /*Notice notice = new Notice();
        // 작성자(Member) 설정
        Member member = memberRepository.findById(noticeRequestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
        notice.setMember(member);

        // 카테고리(CategoryArticle) 설정
        CategoryArticle cate1 = categoryArticleRepository.findById(noticeRequestDto.getCate1Id())
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다."));
        notice.setCate1(cate1);

        if (noticeRequestDto.getCate2Id() != null) {
            CategoryArticle cate2 = categoryArticleRepository.findById(noticeRequestDto.getCate2Id())
                    .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다."));
            notice.setCate2(cate2);
        }

        // 공지사항 정보 설정
        notice.setNoticeTitle(noticeRequestDto.getNoticeTitle());
        notice.setNoticeContent(noticeRequestDto.getNoticeContent());

        Notice savedNotice = noticeRepository.save(notice);
        return modelMapper.map(savedNotice, NoticeResponseDto.class);*/

    }

    // 공지사항 수정
    public NoticeResponseDto updateNotice(Long id, NoticeRequestDto noticeRequestDto) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + id));

        // 필요한 필드만 업데이트 (제목과 내용)
        notice.setNoticeTitle(noticeRequestDto.getNoticeTitle());
        notice.setNoticeContent(noticeRequestDto.getNoticeContent());

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
        return modelMapper.map(notice, NoticeResponseDto.class);
    }

    // 전체 공지사항 목록 조회
    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    // ID로 공지사항 조회
    public Notice getNoticeById(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다: " + id));
    }
}
