package hdxian.monatium_darknet.controller;

import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.service.NoticeService;
import hdxian.monatium_darknet.service.dto.NoticeDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public String noticeList(@RequestParam(value = "category", required = false) NoticeCategory category, Model model) {

        List<Notice> noticeList;
        if (category == null) {
            noticeList = noticeService.findAll();
        }
        else {
            noticeList = noticeService.findByCategory(category);
        }

        model.addAttribute("noticeList", noticeList);
        
        return "notice/noticeList";
    }

    @GetMapping("/{id}")
    public String content(@PathVariable("id") Long noticeId, Model model) {
        Notice notice = noticeService.findOne(noticeId);

        model.addAttribute("notice", notice);

        return "notice/noticeDetail";
    }

}
