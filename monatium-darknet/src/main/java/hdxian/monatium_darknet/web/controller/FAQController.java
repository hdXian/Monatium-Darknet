package hdxian.monatium_darknet.web.controller;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.domain.notice.NoticeCategoryStatus;
import hdxian.monatium_darknet.domain.notice.NoticeStatus;
import hdxian.monatium_darknet.repository.dto.NoticeSearchCond;
import hdxian.monatium_darknet.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/{lang}/help")
public class FAQController {

    private final NoticeService noticeService;

    @GetMapping
    public String list(@PathVariable("lang")LangCode langCode, Model model) {
        NoticeSearchCond nsc = new NoticeSearchCond();
        nsc.setLangCode(langCode);
        nsc.setStatus(NoticeStatus.PUBLIC);
        nsc.setCategoryStatus(NoticeCategoryStatus.PUBLIC);
        List<Notice> faqList = noticeService.findAll(nsc);

        List<NoticeCategory> faqCategoryList = noticeService.findCategoriesByLangCode(langCode);

        model.addAttribute("faqCategoryList", faqCategoryList);
        model.addAttribute("faqList", faqList);
        return "FAQ/faqList";
    }

    @GetMapping("/{faqId}")
    public String getDetail(@PathVariable("lang")LangCode langCode, @PathVariable("faqId") Long faqId) {
        Notice faq = noticeService.findOnePublic(faqId);
        return "FQA/faqDetail";
    }

}
