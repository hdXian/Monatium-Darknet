package hdxian.monatium_darknet.web.controller.management.guide;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.guide.UserGuide;
import hdxian.monatium_darknet.domain.guide.UserGuideCategory;
import hdxian.monatium_darknet.repository.dto.UserGuideSearchCond;
import hdxian.monatium_darknet.service.UserGuideService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static hdxian.monatium_darknet.web.controller.management.SessionConst.*;

@Slf4j
@Controller
@RequestMapping("/management/guides")
@SessionAttributes(CURRENT_LANG_CODE)
@RequiredArgsConstructor
public class GuideMgController {

    private final UserGuideService userGuideService;

    @ModelAttribute(CURRENT_LANG_CODE)
    public LangCode crntLangCode(HttpSession session) {
        return Optional.ofNullable((LangCode)session.getAttribute(CURRENT_LANG_CODE)).orElse(LangCode.KO);
    }

    @GetMapping
    public String guideList(@ModelAttribute(CURRENT_LANG_CODE) LangCode langCode,
                            @RequestParam(value = "category", required = false) Long categoryId,
                            @RequestParam(value = "query", required = false) String title, Model model) {
        UserGuideSearchCond searchCond = new UserGuideSearchCond();
        searchCond.setLangCode(langCode);
        searchCond.setCategoryId(categoryId);
        searchCond.setTitle(title);
        List<UserGuide> guideList = userGuideService.findAll(searchCond);
        List<UserGuideCategory> categoryList = userGuideService.findCategoriesByLangCode(langCode);

        model.addAttribute("guideList", guideList);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("curCategoryId", categoryId);
        model.addAttribute("query", title);
        return "management/guide/guideList";
    }

}
