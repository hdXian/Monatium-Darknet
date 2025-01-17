package hdxian.monatium_darknet.web.controller;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.service.CharacterService;
import hdxian.monatium_darknet.service.ImageUrlService;
import hdxian.monatium_darknet.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final NoticeService noticeService;
    private final CharacterService characterService;
    private final ImageUrlService imageUrlService;

    // 메인 화면에 캐릭터 정보도 뿌려야 함
    
    @GetMapping("/")
    public String home(Model model, LangCode langCode) {

        List<Notice> noticeList = noticeService.findAll();
        List<Character> characterList = characterService.findAll();

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("characterList", characterList);
        model.addAttribute("langCode", langCode.name());
        return "main/mainPage";
    }

    @PostMapping("/lang-change")
    public String langChange(@RequestParam("lang") String lang,
                             @RequestHeader(value = "Referer", required = false, defaultValue = "/") String referer) {

        log.info("lang-change called: lang = {}", lang);
        try {
            URI uri = new URI(referer);
            String path = uri.getPath();
            return "redirect:" + (path != null ? path: "/");
        } catch (URISyntaxException e) {
            log.error("URI Syntax Error occurs on url '/lang-change'");
            return "redirect:/";
        }

    }


    @ModelAttribute("faviconUrl")
    public String faviconUrl() {
        return imageUrlService.getErpinFaviconUrl();
    }

    @ModelAttribute("iconBaseUrl")
    public String iconBasesUrl() {
//        return imageUrlService.getIconBaseUrl();
        return imageUrlService.getIconBaseUrl();
    }

    @ModelAttribute("chBaseUrl")
    public String chBasesUrl() {
        return imageUrlService.getChBaseUrl();
    }

    @ModelAttribute("asideBaseUrl")
    public String asideBasesUrl() {
        return imageUrlService.getAsideBaseUrl();
    }

    @ModelAttribute("staticMainUrl")
    public String mainBaseUrl() {
        return imageUrlService.getStaticImageBaseUrl() + "main/";
    }

}
