package com.lotteon.repository.config;

import com.lotteon.dto.responseDto.GetConfigDTO;
import com.lotteon.entity.config.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigRepository extends JpaRepository<Config, Long> {
    Config findByConfigIsUsed(boolean b);

    List<Config> findTop10ByOrderByConfigCreatedAtDesc();
}
