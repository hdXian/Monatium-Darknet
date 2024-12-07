package hdxian.monatium_darknet.controller.management;

import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.file.FileStorageService;
import hdxian.monatium_darknet.service.MemberService;
import hdxian.monatium_darknet.service.NoticeService;
import hdxian.monatium_darknet.service.dto.NoticeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    private final HtmlContentParser htmlContentParser;
    private final ImageProcessor imageProcessor;

    @Value("${file.noticeDir}")
    private String noticeBaseDir;

    @Value("${file.tempDir}")
    private String tempDir;

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

        // 1. 전달받은 내용으로 새로운 공지사항을 생성한다.
        NoticeCategory category = form.getCategory();
        String title = form.getTitle();
        String htmlContent = form.getContent();

        NoticeDto noticeDto = new NoticeDto(category, title, htmlContent);
        Long noticeId = noticeService.createNewNotice(1L, noticeDto);

        // 2. html 콘텐츠로부터 img src들을 추출한다.
        List<String> imgSrcs = htmlContentParser.getImgSrc(htmlContent);

        // 3. img src들은 url이고, 파일명을 포함하고 있다. 우선 파일명을 추출한다.
        List<String> fileNames = new ArrayList<>();
        for (String imgSrc : imgSrcs) {
            // 파일명에 임시 파일 경로를 추가해서 저장한다.
            // ex. temp/image01.png
            fileNames.add(tempDir + extractFileName(imgSrc));
        }

        // 4. 공지사항 Id를 바탕으로 공지사항 이미지들을 저장할 중간 경로를 생성한다.
        String targetDir = noticeBaseDir + noticeId + "/";

        // 5. 이미지들의 파일명을 img_01.png 등으로 변경하고, 경로도 변경하여 저장한다.
        int seq = 1;
        for (String fileName : fileNames) {
            String ext = extractExt(fileName);
            String savedFileName = String.format("img_%02d", seq++) + ext;
            System.out.println("img will saved at " + targetDir + savedFileName);
            fileStorageService.moveFile(fileName, targetDir + savedFileName);
        }

        return "redirect:/management/notices";
    }

    public static String extractExt(String fileName) {
        int idx = fileName.lastIndexOf(".");
        return fileName.substring(idx);
    }

    public static String extractFileName(String src) {
        int idx = src.lastIndexOf("/");
        return src.substring(idx+1);
    }


}
