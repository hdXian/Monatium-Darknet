package hdxian.monatium_darknet.web.controller.management.character;

import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/management/characters")
@RequiredArgsConstructor
public class CharacterMgController {

    private final CharacterService characterService;

    @GetMapping
    public String characterList(Model model) {
        List<Character> characterList = characterService.findCharacters();

        model.addAttribute("characterList", characterList);
        return "management/character/characterList";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        return "management/character/characterAddForm";
    }

}
