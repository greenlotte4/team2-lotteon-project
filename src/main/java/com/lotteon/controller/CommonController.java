package com.lotteon.controller;

import com.lotteon.dto.responseDto.GetConfigDTO;
import com.lotteon.dto.responseDto.GetCopyrightDTO;
import com.lotteon.dto.responseDto.GetFCsDTO;
import com.lotteon.dto.responseDto.GetFLotteDTO;
import com.lotteon.service.config.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@RequiredArgsConstructor
@ControllerAdvice
public class CommonController {

    private final BannerService bannerService;
    private final ConfigService configService;
    private final FLotteService flotteService;
    private final FCsService fcsService;
    private final CopyrightService copyrightService;

    @Value(value = "${spring.application.name}")
    private String appName;

    @Value(value = "${spring.application.version}")
    private String appVersion;

    @ModelAttribute
    public void getCommon(Model model) {

        GetConfigDTO ConfigDTO = configService.getUsedConfig();
        GetFLotteDTO fLotteDTO = flotteService.getRecentFLotte();
        fLotteDTO.splitAddress();
        GetFCsDTO fCsDTO = fcsService.getRecentFCs();
        GetCopyrightDTO copyrightDTO = copyrightService.getRecentCopyright();


        model.addAttribute("appName", appName);
        model.addAttribute("appVersion", appVersion);

        model.addAttribute("site", ConfigDTO);
        model.addAttribute("fLotte", fLotteDTO);
        model.addAttribute("fCs", fCsDTO);
        model.addAttribute("copy", copyrightDTO);
    }

    @ModelAttribute
    public void getAuthentication(Authentication auth, Model model) {
        if(auth != null && auth.getPrincipal() != null) {
            model.addAttribute("user", auth.getPrincipal());
        }else{
            model.addAttribute("user",null);
        }
    }
}
