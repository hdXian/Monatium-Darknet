package hdxian.monatium_darknet.controller;

import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.service.CharacterService;
import hdxian.monatium_darknet.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final NoticeService noticeService;
    private final CharacterService characterService;

    // 메인 화면에 캐릭터 정보도 뿌려야 함
    
    @GetMapping("/")
    public String home(Model model) {

        List<Notice> noticeList = noticeService.findAll();
        List<Character> characterList = characterService.findCharacters();

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("characterList", characterList);

        return "main/mainPage";
    }

}
