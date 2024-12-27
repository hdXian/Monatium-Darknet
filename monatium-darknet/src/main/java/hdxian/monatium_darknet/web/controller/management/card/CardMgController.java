package hdxian.monatium_darknet.web.controller.management.card;

import hdxian.monatium_darknet.domain.card.ArtifactCard;
import hdxian.monatium_darknet.domain.card.SpellCard;
import hdxian.monatium_darknet.service.CardService;
import hdxian.monatium_darknet.service.ImageUrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/management/cards")
public class CardMgController {

    private final CardService cardService;

    private final ImageUrlService imageUrlService;

    @GetMapping
    public String selectCardType(Model model) {
        String spellIconUrl = imageUrlService.getSpellIconUrl();
        String artifactIconUrl = imageUrlService.getArtifactIconUrl();

        model.addAttribute("spellIconUrl", spellIconUrl);
        model.addAttribute("artifactIconUrl", artifactIconUrl);

        return "management/cards/selectCardType";
    }

    @GetMapping("/spell")
    public String spellList(Model model) {
        List<SpellCard> cardList = cardService.findAllSpellCards();
        String baseUrl = imageUrlService.getCardBaseUrl() + "spell/";

        model.addAttribute("cardList", cardList);
        model.addAttribute("baseUrl", baseUrl);
        return "management/cards/spellCardList";
    }

    @GetMapping("/artifact")
    public String artifactList(Model model) {
        List<ArtifactCard> cardList = cardService.findAllArtifactCards();
        String baseUrl = imageUrlService.getCardBaseUrl() + "artifact/";

        model.addAttribute("cardList", cardList);
        model.addAttribute("baseUrl", baseUrl);
        return "management/cards/artifactCardList";
    }


}
