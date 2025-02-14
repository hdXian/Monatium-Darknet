package hdxian.monatium_darknet.web.controller.management.guide;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.guide.UserGuide;
import hdxian.monatium_darknet.domain.guide.UserGuideCategory;
import hdxian.monatium_darknet.repository.dto.UserGuideSearchCond;
import hdxian.monatium_darknet.security.CustomUserDetails;
import hdxian.monatium_darknet.service.ImageUrlService;
import hdxian.monatium_darknet.service.UserGuideService;
import hdxian.monatium_darknet.service.dto.UserGuideDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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

    private final ImageUrlService imageUrlService;

    @ModelAttribute(CURRENT_LANG_CODE)
    public LangCode crntLangCode(HttpSession session) {
        return Optional.ofNullable((LangCode)session.getAttribute(CURRENT_LANG_CODE)).orElse(LangCode.KO);
    }

    // 가이드 목록
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

    // 가이드 작성 페이지
    @GetMapping("/new")
    public String guideForm(HttpSession session, Model model) {
        UserGuideForm guideForm = Optional.ofNullable((UserGuideForm) session.getAttribute(GUIDE_FORM)).orElse(new UserGuideForm());

        model.addAttribute(GUIDE_FORM, guideForm);
        return "management/guide/guideAddForm";
    }

    @PostMapping("/new")
    public String createGuide(@ModelAttribute(CURRENT_LANG_CODE) LangCode langCode,
                              HttpSession session, @RequestParam("action") String action,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              @ModelAttribute(GUIDE_FORM) UserGuideForm guideForm, Model model) {

        // 취소 버튼을 누른 경우
        if (action.equals("cancel")) {
            clearSessionAttributes(session);
            return "redirect:/management/guides";
        }

        // 완료 버튼을 누른 경우
        if (action.equals("complete")) {

//            if (bindingResult.hasErrors()) {
//                return "management/notice/noticeAddForm";
//            }

            UserGuideDto guideDto = generateGuideDto(langCode, guideForm);
            Long savedId = userGuideService.createNewUserGuide(guideDto);
            clearSessionAttributes(session);
            return "redirect:/management/guides";
        }
        // 임시 저장 버튼을 누른 경우
        else {
            session.setAttribute(GUIDE_FORM, guideForm);
            return "redirect:/management/guides/new";
        }

    }

    // 가이드 수정 페이지
    @GetMapping("/{guideId}/edit")
    public String editForm(HttpSession session, Model model, @PathVariable("guideId") Long guideId) {
        UserGuide userGuide = userGuideService.findOne(guideId);

        UserGuideForm guideForm = Optional.ofNullable((UserGuideForm) session.getAttribute(GUIDE_FORM)).orElse(generateGuideForm(userGuide));

        model.addAttribute("guideId", guideId);
        model.addAttribute(GUIDE_FORM, guideForm);
        return "management/guide/guideEditForm";
    }

    // 가이드 수정
    @PostMapping("/{guideId}/edit")
    public String edit(@ModelAttribute(CURRENT_LANG_CODE) LangCode langCode,
                       HttpSession session, @RequestParam("action") String action,
                       @PathVariable("guideId") Long guideId,
                       @ModelAttribute(GUIDE_FORM) UserGuideForm guideForm, Model model) {

        // 취소 버튼을 누른 경우
        if (action.equals("cancel")) {
            clearSessionAttributes(session);
            return "redirect:/management/guides";
        }

        // 완료 버튼을 누른 경우
        if (action.equals("complete")) {

//            if (bindingResult.hasErrors()) {
//                model.addAttribute("noticeId", noticeId);
//                return "management/notice/noticeEditForm";
//            }

            UserGuideDto updateParam = generateGuideDto(langCode, guideForm);
            Long updatedId = userGuideService.updateUserGuide(guideId, updateParam);
            clearSessionAttributes(session);
            return "redirect:/management/guides";
        }
        // 임시저장 버튼을 누른 경우
        else {
            session.setAttribute(GUIDE_FORM, guideForm);
            return "redirect:/management/guides/" + guideId + "/edit";
        }

    }

    @GetMapping("/categories/new")
    public String categoryForm(HttpSession session, Model model) {
        UserGuideCategoryForm categoryForm = Optional.ofNullable((UserGuideCategoryForm) session.getAttribute(GUIDE_CATEGORY_FORM))
                .orElse(new UserGuideCategoryForm());

        model.addAttribute(GUIDE_CATEGORY_FORM, categoryForm);
        return "management/guide/categoryAddForm";
    }

    // 공지사항 카테고리 추가
    @PostMapping("/categories/new")
    public String addCategory(@ModelAttribute(CURRENT_LANG_CODE) LangCode langCode,
                              HttpSession session, @RequestParam("action") String action,
                              @ModelAttribute(GUIDE_CATEGORY_FORM) UserGuideCategoryForm categoryForm,
                              Model model) {

        // 취소 버튼을 누른 경우
        if (action.equals("cancel")) {
            clearSessionAttributes(session);
            return "redirect:/management/guides";
        }

        // 완료 버튼을 누른 경우
        if (action.equals("complete")) {

//            if(bindingResult.hasErrors()) {
//                return "management/notice/categoryAddForm";
//            }

            String name = categoryForm.getName();
            Long savedId = userGuideService.createNewUserGuideCategory(langCode, name);

            clearSessionAttributes(session);
            return "redirect:/management/guides";
        }
        // 임시 저장 버튼을 누른 경우
        else {
            session.setAttribute(GUIDE_CATEGORY_FORM, categoryForm);
            return "redirect:/management/guides/categories/new";
        }

    }

    // 가이드 카테고리 수정 폼
    @GetMapping("/categories/{categoryId}/edit")
    public String categoryEditForm(HttpSession session, Model model, @PathVariable("categoryId") Long categoryId) {
        UserGuideCategory category = userGuideService.findOneCategory(categoryId);

        UserGuideCategoryForm categoryForm = Optional.ofNullable((UserGuideCategoryForm) session.getAttribute(GUIDE_CATEGORY_FORM))
                .orElse(generateCategoryForm(category));

        model.addAttribute("categoryId", categoryId);
        model.addAttribute(GUIDE_CATEGORY_FORM, categoryForm);
        return "management/guide/categoryEditForm";
    }

    // 가이드 카테고리 수정
    @PostMapping("/categories/{categoryId}/edit")
    public String editCategory(HttpSession session, @RequestParam("action") String action,
                               @PathVariable("categoryId") Long categoryId,
                               @ModelAttribute(GUIDE_CATEGORY_FORM) UserGuideCategoryForm categoryForm,
                               Model model) {

        // 취소 버튼을 누른 경우
        if (action.equals("cancel")) {
            clearSessionAttributes(session);
            return "redirect:/management/guides";
        }

        // 완료 버튼을 누른 경우
        if (action.equals("complete")) {

//            if (bindingResult.hasErrors()) {
//                model.addAttribute("categoryId", categoryId);
//                return "management/guide/categoryEditForm";
//            }

            // 카테고리 업데이트 로직
            Long updatedId = userGuideService.updateUserGuideCategory(categoryId, categoryForm.getName());

            clearSessionAttributes(session);
            return "redirect:/management/guides";
        }
        // 임시저장 버튼을 누른 경우
        else {
            session.setAttribute(GUIDE_CATEGORY_FORM, categoryForm);
            return "redirect:/management/guides/categories/" + categoryId + "/edit";
        }

    }

    @ModelAttribute("categoryList")
    public List<UserGuideCategory> categoryList(@ModelAttribute(CURRENT_LANG_CODE) LangCode langCode) {
        return userGuideService.findCategoriesByLangCode(langCode);
    }

    @ModelAttribute("faviconUrl")
    public String faviconUrl() {
        return imageUrlService.getElleafFaviconUrl();
    }

    // === private ===
    private UserGuideCategoryForm generateCategoryForm(UserGuideCategory category) {
        return new UserGuideCategoryForm(category.getName());
    }

    private UserGuideForm generateGuideForm(UserGuide guide) {
        UserGuideForm form = new UserGuideForm();
        form.setCategoryId(guide.getCategory().getId());
        form.setTitle(guide.getTitle());
        form.setContent(guide.getContent());

        return form;
    }

    private UserGuideDto generateGuideDto(LangCode langCode, UserGuideForm guideForm) {
        Long categoryId = guideForm.getCategoryId();
        String title = guideForm.getTitle();
        String htmlContent = guideForm.getContent();

        return new UserGuideDto(langCode, categoryId, title, htmlContent);
    }

    private void clearSessionAttributes(HttpSession session) {
        session.removeAttribute(GUIDE_FORM);
        session.removeAttribute(GUIDE_CATEGORY_FORM);
    }

}
