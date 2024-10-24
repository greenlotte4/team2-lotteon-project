package com.lotteon.service.config;

import com.lotteon.dto.requestDto.PostVersionDTO;
import com.lotteon.dto.responseDto.GetVersionDTO;
import com.lotteon.dto.responseDto.PageResponseDTO;
import com.lotteon.entity.config.Version;
import com.lotteon.repository.config.VersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class VersionService {
    private final VersionRepository versionRepository;
    private final ModelMapper modelMapper;

    public Version insertVersion(PostVersionDTO postVersionDTO) {
        Version version = modelMapper.map(postVersionDTO, Version.class);
        return versionRepository.save(version);
    }

    public PageResponseDTO<GetVersionDTO> getPagedVersionList(int page) {
        int size = 5;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<GetVersionDTO> versionPage = versionRepository.findAllVersionsWithMemberUid(pageable);
        List<GetVersionDTO> versionList = versionPage.getContent();
        int total = (int) versionPage.getTotalElements();

        return PageResponseDTO.<GetVersionDTO>builder()
                .pg(page)
                .total(total)
                .dtoList(versionList)
                .size(size)
                .build();
    }

    public boolean deleteVersionsById(List<Long> VersionIds) {
        try {
            for (Long VersionId : VersionIds) {
                versionRepository.deleteById(VersionId);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }
}
