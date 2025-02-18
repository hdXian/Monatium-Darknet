package hdxian.monatium_darknet.web.controller.management.notice;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.notice.Member;
import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.exception.notice.NoticeImageProcessException;
import hdxian.monatium_darknet.file.FileDto;
import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.repository.dto.NoticeSearchCond;
import hdxian.monatium_darknet.security.CustomUserDetails;
import hdxian.monatium_darknet.service.ImageUrlService;
import hdxian.monatium_darknet.service.MemberService;
import hdxian.monatium_darknet.service.NoticeService;
import hdxian.monatium_darknet.service.dto.NoticeDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static hdxian.monatium_darknet.web.controller.management.SessionConst.*;

// 공지사항 관리 기능 관련 요청 처리

@Slf4j
@Controller
@RequestMapping("/management/notices")
@SessionAttributes(CURRENT_LANG_CODE)
@RequiredArgsConstructor
public class NoticeMgController {

    private final MemberService memberService;
    private final NoticeService noticeService;
    private final ImageUrlService imageUrlService;
    private final LocalFileStorageService fileStorageService;

    @ModelAttribute(CURRENT_LANG_CODE)
    public LangCode crntLangCode(HttpSession session) {
        return Optional.ofNullable((LangCode)session.getAttribute(CURRENT_LANG_CODE)).orElse(LangCode.KO);
    }

    @ModelAttribute("loginMember")
    public Member loginMember(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userDetails.getMember();
    }

    // 공지사항 목록 (대시보드 -> 공지사항 관리)
    @GetMapping
    public String noticeList(@ModelAttribute(CURRENT_LANG_CODE) LangCode langCode,
                             @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
                             @RequestParam(value = "category", required = false) Long categoryId,
                             @RequestParam(value = "query", required = false) String title, Model model) {
        NoticeSearchCond searchCond = new NoticeSearchCond();
        searchCond.setLangCode(langCode);
        searchCond.setCategoryId(categoryId);
        searchCond.setTitle(title);
        Page<Notice> noticePage = noticeService.findAll_Paging(searchCond, pageNumber);

        setPages(noticePage.getTotalPages(), pageNumber, model);

        List<Notice> noticeList = noticePage.getContent();
        model.addAttribute("noticeList", noticeList);

        model.addAttribute("curCategoryId", categoryId);
        model.addAttribute("page", noticePage);
        model.addAttribute("query", title);
        return "management/notice/noticeList";
    }

    // 공지사항 작성 페이지
    @GetMapping("/new")
    public String noticeForm(HttpSession session, Model model) {
        NoticeForm noticeForm = Optional.ofNullable((NoticeForm) session.getAttribute(NOTICE_FORM)).orElse(new NoticeForm());
        String thumbnailUrl = Optional.ofNullable((String) session.getAttribute(NOTICE_THUMBNAIL_URL)).orElse(imageUrlService.getDefaultThumbnailUrl());

        model.addAttribute(NOTICE_FORM, noticeForm);
        model.addAttribute(NOTICE_THUMBNAIL_URL, thumbnailUrl);
        return "management/notice/noticeAddForm";
    }

    // 공지사항 작성 기능
    @PostMapping("/new")
    public String createNotice(@ModelAttribute(CURRENT_LANG_CODE) LangCode langCode,
                               HttpSession session, @RequestParam("action") String action,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               @Validated @ModelAttribute(NOTICE_FORM) NoticeForm noticeForm, BindingResult bindingResult, Model model) {

        // 취소 버튼을 누른 경우
        if (action.equals("cancel")) {
            clearSessionAttributes(session);
            return "redirect:/management/notices";
        }

        // 임시 경로에 썸네일 이미지를 저장하고 url을 세션에 저장해야 함
        saveThumbnailImageToTemp(session, noticeForm.getThumbnailImg());
        noticeForm.setThumbnailImg(null); // 세션에 url만 저장하고 폼 객체 필드는 비워둠. 폼 객체를 세션에 저장해야 하는데, 이미지 파일까지 같이 저장하기엔 메모리 낭비가 큼.

        // 완료 버튼을 누른 경우
        if (action.equals("complete")) {

            if (bindingResult.hasErrors()) {
                return "management/notice/noticeAddForm";
            }

            Member member = userDetails.getMember();
            NoticeDto noticeDto = generateNoticeDto(langCode, noticeForm);
            String thumbnailFilePath = generateThumbnailFilePath(session);
            Long savedId = noticeService.createNewNotice(member.getId(), noticeDto, thumbnailFilePath);

            clearSessionAttributes(session);
            return "redirect:/management/notices";
        }
        // 임시 저장 버튼을 누른 경우
        else {
            session.setAttribute(NOTICE_FORM, noticeForm);
            return "redirect:/management/notices/new";
        }

    }

    // 공지사항 수정 페이지
    @GetMapping("/{noticeId}/edit")
    public String editForm(HttpSession session, Model model, @PathVariable("noticeId") Long noticeId) {

        Notice notice = noticeService.findOne(noticeId);

        NoticeForm noticeForm = Optional.ofNullable((NoticeForm) session.getAttribute(NOTICE_FORM)).orElse(generateNoticeForm(notice));

        // 세션에 저장된 url을 가져온다.
        // 세션에 없으면 notice에 저장된 썸네일 파일명을 가져온 뒤, noticeImageBaseUrl과 합쳐서 넣는다.
        String thumbnailUrl = Optional.ofNullable((String) session.getAttribute(NOTICE_THUMBNAIL_URL))
                .orElse(imageUrlService.getNoticeImageBaseUrl() + (noticeId + "/") + notice.getThumbnailFileName());

        model.addAttribute("noticeId", noticeId);
        model.addAttribute(NOTICE_FORM, noticeForm);
        model.addAttribute(NOTICE_THUMBNAIL_URL, thumbnailUrl);

        return "management/notice/noticeEditForm";
    }

    // 공지사항 수정
    @PostMapping("/{noticeId}/edit")
    public String edit(@ModelAttribute(CURRENT_LANG_CODE) LangCode langCode,
                       HttpSession session, @RequestParam("action") String action,
                       @PathVariable("noticeId") Long noticeId,
                       @Validated @ModelAttribute(NOTICE_FORM) NoticeForm noticeForm, BindingResult bindingResult,
                       Model model) {

        // 취소 버튼을 누른 경우
        if (action.equals("cancel")) {
            clearSessionAttributes(session);
            return "redirect:/management/notices";
        }

        // 임시 경로에 썸네일 이미지를 저장하고 url을 세션에 저장해야 함
        saveThumbnailImageToTemp(session, noticeForm.getThumbnailImg());
        noticeForm.setThumbnailImg(null); // 세션에 url만 저장하고 폼 객체 필드는 비워둠. 폼 객체를 세션에 저장해야 하는데, 이미지 파일까지 같이 저장하기엔 메모리 낭비가 큼.

        // 완료 버튼을 누른 경우
        if (action.equals("complete")) {

            if (bindingResult.hasErrors()) {
                model.addAttribute("noticeId", noticeId);
                return "management/notice/noticeEditForm";
            }

            NoticeDto updateParam = generateNoticeDto(langCode, noticeForm);
            // 세션에 url이 남아있으면 이미지를 바꾼다는 의미. 찾아와야 함. 없으면 null 리턴.
            String thumbnailFilePath = generateThumbnailFilePath(session);

            Long updatedId = noticeService.updateNotice(noticeId, updateParam, thumbnailFilePath);
            clearSessionAttributes(session);
            return "redirect:/management/notices";
        }
        // 임시저장 버튼을 누른 경우
        else {
            session.setAttribute(NOTICE_FORM, noticeForm);
            return "redirect:/management/notices/" + noticeId + "/edit";
        }

    }

    // 공지사항 공개 설정
    @PostMapping("/publish/{noticeId}")
    public ResponseEntity<Void> publish(@PathVariable("noticeId") Long noticeId) {
        noticeService.publishNotice(noticeId);
        return ResponseEntity.ok().build();
    }

    // 공지사항 공개 설정
    @PostMapping("/unpublish/{noticeId}")
    public ResponseEntity<Void> unPublish(@PathVariable("noticeId") Long noticeId) {
        noticeService.unPublishNotice(noticeId);
        return ResponseEntity.ok().build();
    }

    // 공지사항 삭제 요청 -> 페이지 쪽에서 delete 메서드로 요청 보내도록 구현할 것
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice(@PathVariable("noticeId")Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return ResponseEntity.ok().build();
    }

    // 공지사항 카테고리 추가 폼
    @GetMapping("/categories/new")
    public String categoryForm(HttpSession session, Model model) {
        NoticeCategoryForm categoryForm = Optional.ofNullable((NoticeCategoryForm) session.getAttribute(NOTICE_CATEGORY_FORM)).orElse(new NoticeCategoryForm());

        model.addAttribute(NOTICE_CATEGORY_FORM, categoryForm);
        return "management/notice/categoryAddForm";
    }

    // 공지사항 카테고리 추가
    @PostMapping("/categories/new")
    public String addCategory(@ModelAttribute(CURRENT_LANG_CODE) LangCode langCode,
                              HttpSession session, @RequestParam("action") String action,
                              @Validated @ModelAttribute(NOTICE_CATEGORY_FORM) NoticeCategoryForm categoryForm, BindingResult bindingResult,
                              Model model) {

        // 취소 버튼을 누른 경우
        if (action.equals("cancel")) {
            clearSessionAttributes(session);
            return "redirect:/management/notices";
        }

        // 완료 버튼을 누른 경우
        if (action.equals("complete")) {

            if(bindingResult.hasErrors()) {
                return "management/notice/categoryAddForm";
            }

            String name = categoryForm.getName();
            Long savedId = noticeService.createNewNoticeCategory(langCode, name);

            clearSessionAttributes(session);
            return "redirect:/management/notices";
        }
        // 임시 저장 버튼을 누른 경우
        else {
            session.setAttribute(NOTICE_CATEGORY_FORM, categoryForm);
            return "redirect:/management/notices/categories/new";
        }

    }

    // 공지사항 카테고리 수정 폼
    @GetMapping("/categories/{categoryId}/edit")
    public String categoryEditForm(HttpSession session, Model model, @PathVariable("categoryId") Long categoryId) {
        NoticeCategory category = noticeService.findOneCategory(categoryId);

        NoticeCategoryForm categoryForm = Optional.ofNullable((NoticeCategoryForm) session.getAttribute(NOTICE_CATEGORY_FORM)).orElse(generateCategoryForm(category));

        model.addAttribute("categoryId", categoryId);
        model.addAttribute(NOTICE_CATEGORY_FORM, categoryForm);
        return "management/notice/categoryEditForm";
    }

    // 공지사항 카테고리 수정
    @PostMapping("/categories/{categoryId}/edit")
    public String editCategory(HttpSession session, @RequestParam("action") String action,
                               @PathVariable("categoryId") Long categoryId,
                               @Validated @ModelAttribute(NOTICE_CATEGORY_FORM) NoticeCategoryForm categoryForm, BindingResult bindingResult,
                               Model model) {

        // 취소 버튼을 누른 경우
        if (action.equals("cancel")) {
            clearSessionAttributes(session);
            return "redirect:/management/notices";
        }

        // 완료 버튼을 누른 경우
        if (action.equals("complete")) {

            if (bindingResult.hasErrors()) {
                model.addAttribute("categoryId", categoryId);
                return "management/notice/categoryEditForm";
            }

            // 카테고리 업데이트 로직
            Long updatedId = noticeService.updateNoticeCategory(categoryId, categoryForm.getName(), categoryForm.getStatus());

            clearSessionAttributes(session);
            return "redirect:/management/notices";
        }
        // 임시저장 버튼을 누른 경우
        else {
            session.setAttribute(NOTICE_CATEGORY_FORM, categoryForm);
            return "redirect:/management/notices/categories/" + categoryId + "/edit";
        }


    }

    // 공지사항 카테고리 삭제

    @ModelAttribute("categoryList")
    public List<NoticeCategory> categoryList(@ModelAttribute(CURRENT_LANG_CODE) LangCode langCode) {
        return noticeService.findCategoriesByLangCode(langCode);
    }

    @ModelAttribute("faviconUrl")
    public String faviconUrl() {
        return imageUrlService.getElleafFaviconUrl();
    }


    // === private ===
    private String generateThumbnailFilePath(HttpSession session) {
        String thumbnailFilePath; // 0. 서비스 계층에 넘길 썸네일 이미지 경로 (tmp 경로)
        String thumbnailUrl = (String) session.getAttribute(NOTICE_THUMBNAIL_URL); // 1. 세션에서 이미지 url을 가져옴
        // 2-1. 세션에 뭐가 없으면 -> 서비스 계층에 null 넘김 -> 서비스 계층에서도 기본 이미지로 처리할 것
        if (thumbnailUrl == null) {
            thumbnailFilePath = null;
        }
        // 2-2. 세션에 뭐가 있으면 -> url에서 파일명 추출한 다음 temp 경로 붙여서 서비스 계층에 넘김 -> 서비스 계층에서 해당 경로로 파일 접근해서 옮겨 저장할 것
        else {
            String fileName = fileStorageService.extractFileName(thumbnailUrl);
            thumbnailFilePath = fileStorageService.getTempDir() + fileName;
        }
        return thumbnailFilePath;
    }

    // 임시 경로에 썸네일 이미지를 저장하고 url을 세션에 저장
    private void saveThumbnailImageToTemp(HttpSession session, MultipartFile imgFile) {
        if (imgFile == null || imgFile.isEmpty())
            return;

        try {
            FileDto fileDto = fileStorageService.saveFileToTemp(imgFile); // 저장된 파일의 fullPath를 받아옴
//            System.out.println("fileDto = " + fileDto.getTotalPath());

            String tempBaseUrl = imageUrlService.getTempImageBaseUrl();
            String tempImgUrl = tempBaseUrl + fileDto.getFileName();
//            System.out.println("tempImgUrl = " + tempImgUrl);

            session.setAttribute(NOTICE_THUMBNAIL_URL, tempImgUrl);
        } catch (IOException e) {
            throw new NoticeImageProcessException(e);
        }
    }

    private NoticeCategoryForm generateCategoryForm(NoticeCategory category) {
        return new NoticeCategoryForm(category.getName(), category.getStatus());
    }

    private NoticeForm generateNoticeForm(Notice notice) {
        NoticeForm noticeForm = new NoticeForm();
        noticeForm.setCategoryId(notice.getCategory().getId());
        noticeForm.setTitle(notice.getTitle());
        noticeForm.setContent(notice.getContent());

        return noticeForm;
    }

    private NoticeDto generateNoticeDto(LangCode langCode, NoticeForm noticeForm) {
        Long categoryId = noticeForm.getCategoryId();
        String title = noticeForm.getTitle();
        String htmlContent = noticeForm.getContent();

        return new NoticeDto(langCode, categoryId, title, htmlContent);
    }

    private void setPages(int totalPages, int pageNumber, Model model) {
        // 한번에 페이지가 5개씩만 나오도록 조정
        int maxPages = 5;
        int idx = pageNumber-1;

        int startPage;
        if (idx*maxPages > totalPages-1) {
            startPage = 1;
        }
        else {
            startPage = (maxPages * (idx / 5)) + 1; // 5 * (현재 페이지를 maxPage로 나눈 몫)을 시작 페이지 번호로 지정
        }

        int endPage = Math.min(totalPages, (startPage + maxPages - 1));
        if (endPage == 0)
            endPage = 1;
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
    }

    private void clearSessionAttributes(HttpSession session) {
        session.removeAttribute(NOTICE_FORM);
        session.removeAttribute(NOTICE_CATEGORY_FORM);
        session.removeAttribute(NOTICE_THUMBNAIL_URL);
    }

}
