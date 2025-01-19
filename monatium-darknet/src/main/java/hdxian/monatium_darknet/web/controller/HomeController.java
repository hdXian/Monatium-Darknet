package hdxian.monatium_darknet.web.controller;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.service.CharacterService;
import hdxian.monatium_darknet.service.ImageUrlService;
import hdxian.monatium_darknet.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final NoticeService noticeService;
    private final CharacterService characterService;
    private final ImageUrlService imageUrlService;

    @GetMapping("/")
    public String home(Locale locale, RedirectAttributes redirectAttributes) {
        // 여기서 바인딩되는 Locale은 Accept-language 헤더 기반의 LocaleResolver로부터 받아온 Lcoale (기본 LocaleResolver)
        redirectAttributes.addAttribute("lang", locale.getLanguage());
        return "redirect:/{lang}";
    }

    @GetMapping("/{lang}")
    public String homeLang(@PathVariable("lang") LangCode langCode, Model model) {
        log.info("langCode = {}", langCode);

        List<Notice> noticeList = noticeService.findAll();
        List<Character> characterList = characterService.findAll();

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("characterList", characterList);
        model.addAttribute("language", langCode.name().toLowerCase());
        return "main/mainPage";
    }

    @PostMapping("/lang-change")
    public String langChange(@RequestParam("lang") String lang,
                             @RequestHeader(value = "Referer", required = false, defaultValue = "/") String referer) {
        try {
            URI uri = new URI(referer);
            String path = uri.getPath();
            return "redirect:" + (path != null ? generateRedirectPath(path, lang): "/");
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

    // === private ===
    // 지정한 lang으로 path의 언어 코드 부분을 변경해서 리턴
    // ex) ko/wiki/characters -> en/wiki/characters
    private String generateRedirectPath(String path, String lang) {
        StringBuilder sb = new StringBuilder();
        sb.append("/").append(lang);

        StringTokenizer tkn = new StringTokenizer(path, "/");
        tkn.nextToken(); // 첫 토큰이 지역명. 해당 토큰은 버림.
        while (tkn.hasMoreTokens()) {
            sb.append("/").append(tkn.nextToken());
        }

        return sb.toString();
    }

}
