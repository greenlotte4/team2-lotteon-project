package com.lotteon.controller.apicontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lotteon.dto.requestDto.*;
import com.lotteon.dto.responseDto.GetBannerDTO;
import com.lotteon.entity.config.*;
import com.lotteon.entity.term.Terms;
import com.lotteon.service.config.*;
import com.lotteon.service.term.TermsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Log4j2
@RestController
@RequestMapping("/admin/config")
@RequiredArgsConstructor
public class ApiConfigController {
    private final BannerService bannerService;
    private final ConfigService configService;
    private final FLotteService flotteService;
    private final FCsService fcsService;
    private final CopyrightService copyrightService;
    private final VersionService versionService;
    private final TermsService termsService;

    @GetMapping("/banners/{tab}")
    public ResponseEntity<?> selectBanner(@PathVariable("tab") String tab) {
        int num = Integer.parseInt(tab);
        List<GetBannerDTO> bannerList = bannerService.findAllByCate(num);
        return ResponseEntity.ok().body(bannerList);
    }

    @PostMapping(value = {"/banners"}, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> insertBanner(@RequestPart("bannerDTO") String bannerJson,
                                          @RequestPart("file") MultipartFile file) {
        log.info("inserting...");
        ObjectMapper objectMapper = new ObjectMapper();
        PostBannerDTO bannerDTO;
        try {
            bannerDTO = objectMapper.readValue(bannerJson, PostBannerDTO.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Invalid  Json Data: " + e.getMessage()));
        }
        try {
            bannerDTO.setUploadFile(file);
            Banner banner = bannerService.insert(bannerDTO);
            return ResponseEntity.ok().body(banner);
        } catch (Exception e) {
            log.error("Error inserting banner", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Failed to register banner: " + e.getMessage()));
        }
    }

    @PatchMapping("/banner/{id}/{state}")
    public ResponseEntity<?> changeBannerState(@PathVariable("id") Long id, @PathVariable("state") Integer state) {
        Banner banner = bannerService.updateBannerState(id, state);
        return ResponseEntity.ok().body(banner);
    }

    @DeleteMapping("/banners")
    public ResponseEntity<?> deleteBanner(@RequestBody List<Long> ids) {
        Boolean success = bannerService.deleteBannersById(ids);
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = {"/logo"}, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> updateBanner(@RequestPart("logoDTO") String logoJson,
                                          @RequestPart(value = "file1",required = false) MultipartFile file1,
                                          @RequestPart(value = "file2",required = false) MultipartFile file2,
                                          @RequestPart(value = "file3",required = false) MultipartFile file3) {

        log.info("updateConfig...");

        ObjectMapper objectMapper = new ObjectMapper();
        PatchLogoDTO patchLogoDTO;
        try {
            patchLogoDTO = objectMapper.readValue(logoJson, PatchLogoDTO.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Invalid Json Data: " + e.getMessage()));
        }
        if(file1!=null) patchLogoDTO.setFile1(file1);
        if(file2!=null) patchLogoDTO.setFile2(file2);
        if(file3!=null) patchLogoDTO.setFile3(file3);
        log.info("patchData : {}" , patchLogoDTO);

        Config config = configService.updateLogo(patchLogoDTO);

        return ResponseEntity.ok().body(config);
    }
    @PatchMapping("/info")
    public ResponseEntity<?> updateSiteInfo(@ModelAttribute PatchConfigDTO configDTO) {
        Config config = configService.updateInfo(configDTO);
        return ResponseEntity.ok().body(config);
    }

    @PostMapping("/footer")
    public ResponseEntity<?> updateFooter(@ModelAttribute PostFLotteDTO postDTO) {
        FLotte fLotte = flotteService.updateFooter(postDTO);
        return ResponseEntity.ok().body(fLotte);
    }
    @PostMapping("/cs")
    public ResponseEntity<?> updateCs(@ModelAttribute PostFCsDTO postDTO) {
        FCs fCs = fcsService.updateCS(postDTO);
        return ResponseEntity.ok().body(fCs);
    }

    @PostMapping("/copy")
    public ResponseEntity<?> updateCopyright(@RequestParam("id") Long id,
                                             @RequestParam("copy") String copy) {
        Copyright copyright = copyrightService.updateCopyright(id, copy);
        return ResponseEntity.ok().body(copyright);
    }
    @PostMapping("/version")
    public ResponseEntity<?> insertVersion(@ModelAttribute PostVersionDTO postDTO) {
        Version version = versionService.insertVersion(postDTO);
        return ResponseEntity.ok().body(version);
    }
    @DeleteMapping("/versions")
    public ResponseEntity<?> deleteVersion(@RequestBody List<Long> ids) {
        Boolean success = versionService.deleteVersionsById(ids);
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/term")
    public ResponseEntity<?> modifyTerm(@ModelAttribute PostTermsDTO postDTO) {
        Terms terms = termsService.modifyTerms(postDTO);
        return ResponseEntity.ok().body(terms);
    }
}
