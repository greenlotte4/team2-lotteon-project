package com.lotteon.service.article;

import com.lotteon.dto.requestDto.NoticeRequestDto;
import com.lotteon.dto.responseDto.NoticeResponseDto;
import com.lotteon.entity.article.Notice;
import com.lotteon.repository.article.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final ModelMapper modelMapper;

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

    // 공지사항 단일 조회
    public NoticeResponseDto getNotice(Long id) {
        Optional<Notice> notice = noticeRepository.findById(id);
        return notice.map(value -> modelMapper.map(value, NoticeResponseDto.class))
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + id));
    }

    // 공지사항 저장 (작성 및 수정)
    public NoticeResponseDto saveNotice(NoticeRequestDto noticeRequestDto) {
        Notice notice = modelMapper.map(noticeRequestDto, Notice.class);
        Notice savedNotice = noticeRepository.save(notice);
        return modelMapper.map(savedNotice, NoticeResponseDto.class);
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
}
