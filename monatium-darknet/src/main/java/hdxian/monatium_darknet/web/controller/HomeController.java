package hdxian.monatium_darknet.web.controller;

import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.service.CharacterService;
import hdxian.monatium_darknet.service.ImageUrlService;
import hdxian.monatium_darknet.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final NoticeService noticeService;
    private final CharacterService characterService;
    private final ImageUrlService imageUrlService;

    // 메인 화면에 캐릭터 정보도 뿌려야 함
    
    @GetMapping("/")
    public String home(Model model) {

        List<Notice> noticeList = noticeService.findAll();
        List<Character> characterList = characterService.findAll();

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("characterList", characterList);
        return "main/mainPage";
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

}
