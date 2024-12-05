package hdxian.monatium_darknet.controller;

import hdxian.monatium_darknet.domain.card.ArtifactCard;
import hdxian.monatium_darknet.domain.card.SpellCard;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.domain.skin.Skin;
import hdxian.monatium_darknet.service.CardService;
import hdxian.monatium_darknet.service.CharacterService;
import hdxian.monatium_darknet.service.SkinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/wiki")
public class WikiController {

    private final CharacterService characterService;
    private final SkinService skinService;
    private final CardService cardService;

    @GetMapping("/characters")
    public String characterList(Model model) {

        List<Character> characterList = characterService.findCharacters();

        model.addAttribute("characterList", characterList);

        return "wiki/characterList";
    }

    @GetMapping("/characters/{id}")
    public String characterInfo(@PathVariable("id") Long characterId, Model model) {

        Character character = characterService.findOne(characterId);
        List<Skin> skinList = skinService.findSkinsByCharacter(characterId);

        model.addAttribute("character", character);
        model.addAttribute("skinList", skinList);

        return "wiki/characterDetail";
    }

    @GetMapping("/cards/artifact")
    public String artifactList(Model model) {
        List<ArtifactCard> cardList = cardService.findAllArtifactCards();

        model.addAttribute("cardList", cardList);

        return "wiki/artifactCardList";
    }

    @GetMapping("/cards/spell")
    public String spellList(Model model) {

        List<SpellCard> cardList = cardService.findAllSpellCards();

        model.addAttribute("cardList", cardList);

        return "wiki/spellCardList";
    }


}
