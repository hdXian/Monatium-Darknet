package hdxian.monatium_darknet.controller.management;

import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.service.MemberService;
import hdxian.monatium_darknet.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 공지사항 관리 기능관련 요청 처리

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/management/notices")
public class NoticeMgController {

    private final MemberService memberService;
    private final NoticeService noticeService;

    private final HtmlContentParser htmlContentParser;

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
    public String createNotice(@ModelAttribute("noticeForm") NoticeForm form) {

        System.out.println("form.getTitle() = " + form.getTitle());
        System.out.println("form.getCategory() = " + form.getCategory());
        System.out.println("form.getContent() = " + form.getContent());

        // 1. html인 content를 파싱해서 img 태그들의 src 속성들을 가져온다.
        List<String> srcList = htmlContentParser.getImgSrc(form.getContent());
        for (String src : srcList) {
            System.out.println("src = " + src);
        }
        // 2. 가져온 src들을 바탕으로 temp 폴더의 이미지 파일들을 특정한다.
        // 3. 찾아온 이미지 파일들의 파일명을 noticeId 기반으로 변경하고, 위치도 옮긴다.
        // 4. 변경한 파일명으로 content의 img 태그들의 src 속성을 수정한다.

        return "redirect:/management/notices";
    }


}
