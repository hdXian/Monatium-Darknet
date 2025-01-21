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
import hdxian.monatium_darknet.web.controller.management.SessionConst;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

import static hdxian.monatium_darknet.web.controller.management.SessionConst.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/{lang}/wiki")
public class WikiController {

    private final CharacterService characterService;
    private final SkinService skinService;
    private final CardService cardService;

    private final ImagePathService imagePathService;
    private final ImageUrlService imageUrlService;

    // /{lang}/wiki/characters -> @PathVariable("lang")
    @GetMapping("/characters")
    public String characterList(@PathVariable("lang") LangCode langCode, Model model) {

        CharacterSearchCond searchCond = new CharacterSearchCond();
        searchCond.setStatus(CharacterStatus.ACTIVE); // 활성화 캐릭터만 조회
        searchCond.setLangCode(langCode);
        List<Character> characterList = characterService.findAll(searchCond);

        model.addAttribute("characterList", characterList);
        return "wiki/characterList";
    }

    @GetMapping("/characters/{count}")
    public String characterInfo(@PathVariable("lang") LangCode langCode,
                                @PathVariable("count") Integer count, Model model) {

        int index = (count - 1);
        Character character = characterService.findOneWiki(langCode, index);

        SkinSearchCond searchCond = new SkinSearchCond();
        searchCond.setCharacterId(character.getId());
        searchCond.setStatus(SkinStatus.ACTIVE); // 활성화된 스킨만 노출
        List<Skin> skinList = skinService.findAllSkin(searchCond);

        model.addAttribute("character", character);
        model.addAttribute("skinList", skinList);
        return "wiki/characterDetail";
    }

    @GetMapping("/cards/artifact")
    public String artifactList(@PathVariable("lang") LangCode langCode, Model model) {
        CardSearchCond searchCond = new CardSearchCond();
        searchCond.setLangCode(langCode);
        searchCond.setCardType(CardType.ARTIFACT);
        searchCond.setStatus(CardStatus.ACTIVE);
        List<Card> cardList = cardService.findAll(searchCond);

        String cardBaseUrl = imageUrlService.getCardBaseUrl();
        String portraitBaseUrl = imageUrlService.getChBaseUrl() + "portrait/";

        model.addAttribute("cardList", cardList);
        model.addAttribute("cardBaseUrl", cardBaseUrl);
        model.addAttribute("portraitBaseUrl", portraitBaseUrl);

        return "wiki/artifactCardList";
    }

    @GetMapping("/cards/spell")
    public String spellList(@PathVariable("lang") LangCode langCode, Model model) {
        CardSearchCond searchCond = new CardSearchCond();
        searchCond.setLangCode(langCode);
        searchCond.setCardType(CardType.SPELL);
        searchCond.setStatus(CardStatus.ACTIVE);
        List<Card> cardList = cardService.findAll(searchCond);

        String cardBaseUrl = imageUrlService.getCardBaseUrl();

        model.addAttribute("cardList", cardList);
        model.addAttribute("cardBaseUrl", cardBaseUrl);

        return "wiki/spellCardList";
    }


    // === ModelAttribute ===
    // navbar.html의 언어 선택 창 렌더링에 사용되는 ${language}
    @ModelAttribute("language")
    public String languageModelAttr(@PathVariable("lang") LangCode langCode) {
        return langCode.name().toLowerCase();
    }

    @ModelAttribute("faviconUrl")
    public String faviconUrl() {
        return imageUrlService.getErpinFaviconUrl();
    }

    @ModelAttribute("iconBaseUrl")
    public String iconBaseUrl() {
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
