package hdxian.monatium_darknet.controller;

import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/wiki")
public class WikiController {

    private final CharacterService characterService;

    @GetMapping("/characters")
    public String characterList(Model model) {

        List<Character> characterList = characterService.findCharacters();

        model.addAttribute("characterList", characterList);

        return "wiki/characterList";
    }

}
