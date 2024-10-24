package com.lotteon.repository.config;

import com.lotteon.dto.responseDto.GetVersionDTO;
import com.lotteon.entity.config.Version;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionRepository extends JpaRepository<Version, Long> {

    @Query("SELECT new com.lotteon.dto.responseDto.GetVersionDTO(v.id, v.mem_id, v.verName, v.verContent, v.verRdate, m.memUid) " +
            "FROM Version v JOIN Member m ON v.mem_id = m.id")
    Page<GetVersionDTO> findAllVersionsWithMemberUid(Pageable pageable);


}
