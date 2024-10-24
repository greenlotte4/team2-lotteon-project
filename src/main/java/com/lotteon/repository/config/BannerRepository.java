package com.lotteon.repository.config;

import com.lotteon.entity.config.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findAllByBannerLocation(int cateId);

    List<Banner> findAllByBannerLocationAndBannerState(int bannerLocation, int i);
}
