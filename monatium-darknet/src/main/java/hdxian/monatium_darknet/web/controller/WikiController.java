package hdxian.monatium_darknet.web.controller;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.card.*;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.domain.character.CharacterEn;
import hdxian.monatium_darknet.domain.character.CharacterKo;
import hdxian.monatium_darknet.domain.character.CharacterStatus;
import hdxian.monatium_darknet.domain.skin.Skin;
import hdxian.monatium_darknet.domain.skin.SkinStatus;
import hdxian.monatium_darknet.repository.dto.CardSearchCond;
import hdxian.monatium_darknet.repository.dto.CharacterSearchCond;
import hdxian.monatium_darknet.repository.dto.SkinSearchCond;
import hdxian.monatium_darknet.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    private final ImagePathService imagePathService;
    private final ImageUrlService imageUrlService;

    @GetMapping("/characters")
    public String characterList(Model model, LangCode langCode) {

        CharacterSearchCond searchCond = new CharacterSearchCond();
        searchCond.setStatus(CharacterStatus.ACTIVE); // 활성화 캐릭터만 조회
//        searchCond.setLangCode(langCode);
        if (langCode == LangCode.KO) {
            List<CharacterKo> characterList = characterService.findAllKo(searchCond);
            model.addAttribute("characterList", characterList);
        }
        else if (langCode == LangCode.EN) {
            List<CharacterEn> characterList = characterService.findAllEn(searchCond);
            model.addAttribute("characterList", characterList);
        }
        else {
            List<Character> characterList = characterService.findAll(searchCond);
            model.addAttribute("characterList", characterList);
        }

        return "wiki/characterList";
    }

    @GetMapping("/characters/{id}")
    public String characterInfo(@PathVariable("id") Long characterId, LangCode langCode, Model model) {

        if (langCode == LangCode.KO) {
            CharacterKo character = characterService.findOneActiveKo(characterId);
            model.addAttribute("character", character);
        }
        else if (langCode == LangCode.EN) {
            CharacterEn character = characterService.findOneEn(characterId);
            model.addAttribute("character", character);
        }
        else {
            Character character = characterService.findOneActive(characterId);
            model.addAttribute("character", character);
        }

        SkinSearchCond searchCond = new SkinSearchCond();
        searchCond.setCharacterId(characterId);
        searchCond.setStatus(SkinStatus.ACTIVE); // 활성화된 스킨만 노출
        List<Skin> skinList = skinService.findAllSkin(searchCond);

        model.addAttribute("skinList", skinList);

        return "wiki/characterDetail";
    }

    @GetMapping("/cards/artifact")
    public String artifactList(Model model) {
        CardSearchCond searchCond = new CardSearchCond();
        searchCond.setCardType(CardType.ARTIFACT);
        searchCond.setStatus(CardStatus.ACTIVE);
        List<Card> cardList = cardService.findAll(searchCond);

        String cardBaseUrl = imageUrlService.getArtifactCardBaseUrl();
        String portraitBaseUrl = imageUrlService.getChBaseUrl() + "portrait/";

        model.addAttribute("cardList", cardList);
        model.addAttribute("cardBaseUrl", cardBaseUrl);
        model.addAttribute("portraitBaseUrl", portraitBaseUrl);

        return "wiki/artifactCardList";
    }

    @GetMapping("/cards/spell")
    public String spellList(Model model) {
        CardSearchCond searchCond = new CardSearchCond();
        searchCond.setCardType(CardType.SPELL);
        searchCond.setStatus(CardStatus.ACTIVE);
        List<Card> cardList = cardService.findAll(searchCond);

        String baseUrl = imageUrlService.getSpellCardBaseUrl();

        model.addAttribute("cardList", cardList);
        model.addAttribute("baseUrl", baseUrl);

        return "wiki/spellCardList";
    }


    // === ModelAttribute ===
    @ModelAttribute("faviconUrl")
    public String faviconUrl() {
        return imageUrlService.getErpinFaviconUrl();
    }

    @ModelAttribute("iconBaseUrl")
    public String iconBaseUrl() {
//        return imageUrlService.getIconBaseUrl();
        return imageUrlService.getIconBaseUrl();
    }

    @ModelAttribute("chBaseUrl")
    public String chBaseUrl() {
        return imageUrlService.getChBaseUrl();
    }

    @ModelAttribute("asideBaseUrl")
    public String asideBaseUrl() {
        return imageUrlService.getAsideBaseUrl();
    }

    @ModelAttribute("skinBaseUrl")
    public String skinBaseUrl() {
        return imageUrlService.getSkinBaseUrl();
    }


}
