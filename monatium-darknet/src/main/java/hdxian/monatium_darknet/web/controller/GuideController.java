package hdxian.monatium_darknet.web.controller;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.guide.UserGuide;
import hdxian.monatium_darknet.domain.guide.UserGuideCategory;
import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.domain.notice.NoticeCategoryStatus;
import hdxian.monatium_darknet.domain.notice.NoticeStatus;
import hdxian.monatium_darknet.repository.dto.NoticeSearchCond;
import hdxian.monatium_darknet.repository.dto.UserGuideSearchCond;
import hdxian.monatium_darknet.service.ImageUrlService;
import hdxian.monatium_darknet.service.NoticeService;
import hdxian.monatium_darknet.service.UserGuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/{lang}/guides")
public class GuideController {

    private final UserGuideService userGuideService;

    private final ImageUrlService imageUrlService;

    @GetMapping
    public String guideList(@PathVariable("lang")LangCode langCode,
                            @RequestParam(value = "category", required = false) Long categoryId,
                            @RequestParam(value = "query", required = false) String title, Model model) {

        List<UserGuideCategory> categoryList = userGuideService.findCategoriesByLangCode(langCode);
        // 카테고리 넘어온게 없으면 1번 카테고리로 설정
        if (categoryId == null)
            categoryId = categoryList.get(0).getId();

        UserGuideSearchCond searchCond = new UserGuideSearchCond();
        searchCond.setLangCode(langCode);
        searchCond.setCategoryId(categoryId);
        searchCond.setTitle(title);

        List<UserGuide> guideList = userGuideService.findAll(searchCond);
        UserGuideCategory curCategory = userGuideService.findOneCategory(categoryId);

        model.addAttribute("guideList", guideList);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("curCategory", curCategory);
        model.addAttribute("query", title);
        return "guide/guideList";
    }

    @GetMapping("/search")
    public String searchGuide(@PathVariable("lang")LangCode langCode, @RequestParam(value = "query", required = false) String title, Model model) {
        UserGuideSearchCond searchCond = new UserGuideSearchCond();
        searchCond.setLangCode(langCode);
        searchCond.setTitle(title);

        List<UserGuide> guideList = userGuideService.findAll(searchCond);
        List<UserGuideCategory> categoryList = userGuideService.findCategoriesByLangCode(langCode);

        model.addAttribute("guideList", guideList);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("query", title);
        return "guide/guideSearch";
    }

    @GetMapping("/{guideId}")
    public String getDetail(@PathVariable("lang")LangCode langCode, @PathVariable("guideId") Long guideId, Model model) {
        UserGuide userGuide = userGuideService.findOne(guideId);
        List<UserGuideCategory> categoryList = userGuideService.findCategoriesByLangCode(langCode);

        model.addAttribute("guide", userGuide);
        model.addAttribute("curCategory", userGuide.getCategory());
        model.addAttribute("categoryList", categoryList);
        return "guide/guideDetail";
    }

    // === ModelAttributes ===
    @ModelAttribute("language")
    public String lang(@PathVariable("lang") LangCode langCode) {
        return langCode.name().toLowerCase();
    }

    @ModelAttribute("faviconUrl")
    public String faviconUrl() {
        return imageUrlService.getErpinFaviconUrl();
    }

    @ModelAttribute("guideBaseUrl")
    public String guideBaseUrl() {
        return imageUrlService.getUserGuideImageBaseUrl();
    }

}
