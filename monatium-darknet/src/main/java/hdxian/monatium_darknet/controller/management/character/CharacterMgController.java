package hdxian.monatium_darknet.controller.management.character;

import hdxian.monatium_darknet.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/management/characters")
@RequiredArgsConstructor
public class CharacterMgController {

    private final CharacterService characterService;

    @GetMapping
    public String characterList(Model model) {
        return "management/character/characterList";
    }

}
