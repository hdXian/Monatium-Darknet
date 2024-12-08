package hdxian.monatium_darknet.controller.management.notice;

import hdxian.monatium_darknet.controller.management.HtmlContentUtil;
import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.file.FileStorageService;
import hdxian.monatium_darknet.service.MemberService;
import hdxian.monatium_darknet.service.NoticeService;
import hdxian.monatium_darknet.service.dto.NoticeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

// 공지사항 관리 기능 관련 요청 처리

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/management/notices")
public class NoticeMgController {

    private final MemberService memberService;
    private final NoticeService noticeService;
    private final FileStorageService fileStorageService;

    private final HtmlContentUtil htmlContentUtil;

    // 공지사항 목록 (대시보드 -> 공지사항 관리)
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
        model.addAttribute("category", category);
        return "management/notice/noticeList";
    }

    // 공지사항 작성 페이지
    @GetMapping("/new")
    public String noticeForm(@ModelAttribute("noticeForm") NoticeForm form) {
        return "management/notice/noticeForm";
    }

    // 공지사항 작성 기능
    // 로그인 처리 선처리 필요 (memberId)
    @PostMapping("/new")
    public String createNotice(@ModelAttribute("noticeForm") NoticeForm form) throws IOException {

        NoticeCategory category = form.getCategory();
        String title = form.getTitle();
        String htmlContent = form.getContent();

        // 전달받은 내용으로 공지사항 생성 (공지사항 Id 리턴)
        NoticeDto noticeDto = new NoticeDto(category, title, htmlContent);
        Long noticeId = noticeService.createNewNotice(1L, noticeDto); // TODO - 로그인 처리 후 memberId 설정 필요

        // 공지사항 본문에서 img 태그들의 src 속성들을 추출
        // 1. "/api/images/abcdef.png"
        List<String> imgSrcs = htmlContentUtil.getImgSrc(htmlContent);

        // 추출한 src를 바탕으로 이미지 파일명 수정 및 경로 변경 (서버 내 경로)
        // 2. {basePath}/temp/abcdef.png -> {basePath}}/notice/{noticeId}/img_01.png
        List<String> changedImgSrcs = noticeService.moveImagesFromTemp(noticeId, imgSrcs);

        // 이동한 이미지에 대한 새로운 url 생성 및 html 본문 업데이트 (src 속성 변경, url 경로)
        // 3. th:src="@{/notices/{noticeId}/images/img_01.png}"
        String baseUrl = "/notices/" + noticeId + "/images/";
        String updatedContent = htmlContentUtil.updateImgSrc(htmlContent, baseUrl, changedImgSrcs);

        // 업데이트 한 본문 내용으로 공지사항 내용 업데이트
        NoticeDto updateDto = new NoticeDto(category, title, updatedContent);
        Long updatedId = noticeService.updateNotice(noticeId, updateDto);

        return "redirect:/management/notices";
    }

}
