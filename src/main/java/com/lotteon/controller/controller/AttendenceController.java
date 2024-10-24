package com.lotteon.controller.controller;

import com.lotteon.entity.member.AttendanceEvent;
import com.lotteon.service.event.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/event")
@Controller
@RequiredArgsConstructor
@Log4j2
public class AttendenceController {

    private final EventService eventService;

    @GetMapping("/attendance")
    public String attendance(Model model) {
        String result = eventService.updateEvent();

        int days = eventService.findEvent(result);
        model.addAttribute("days", days);
        return "pages/event/attendance";
    }
}
