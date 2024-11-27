package hdxian.monatium_darknet.controller;

import hdxian.monatium_darknet.service.CharacterService;
import hdxian.monatium_darknet.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final NoticeService noticeService;
    private final CharacterService characterService;

    @GetMapping("/")
    public String home() {
        return "main/mainPage";
    }

}
