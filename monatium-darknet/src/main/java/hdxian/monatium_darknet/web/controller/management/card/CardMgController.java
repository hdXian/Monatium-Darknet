package hdxian.monatium_darknet.web.controller.management.card;

import hdxian.monatium_darknet.domain.card.ArtifactCard;
import hdxian.monatium_darknet.domain.card.SpellCard;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.file.FileDto;
import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.service.CardService;
import hdxian.monatium_darknet.service.CharacterService;
import hdxian.monatium_darknet.service.ImagePathService;
import hdxian.monatium_darknet.service.ImageUrlService;
import hdxian.monatium_darknet.service.dto.CardDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static hdxian.monatium_darknet.web.controller.management.SessionConst.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/management/cards")
public class CardMgController {

    private final CardService cardService;
    private final CharacterService characterService;

    private final LocalFileStorageService fileStorageService;
    private final ImageUrlService imageUrlService;
    private final ImagePathService imagePathService;

    // 아티팩트 카드 리스트
    @GetMapping
    public String selectCardType(Model model) {
        String spellIconUrl = imageUrlService.getSpellIconUrl();
        String artifactIconUrl = imageUrlService.getArtifactIconUrl();

        model.addAttribute("spellIconUrl", spellIconUrl);
        model.addAttribute("artifactIconUrl", artifactIconUrl);

        return "management/cards/selectCardType";
    }

    // 스펠 카드 리스트
    @GetMapping("/spell")
    public String spellList(Model model) {
        List<SpellCard> cardList = cardService.findAllSpellCards();
        String baseUrl = imageUrlService.getSpellCardBaseUrl();

        model.addAttribute("cardList", cardList);
        model.addAttribute("baseUrl", baseUrl);
        return "management/cards/spellCardList";
    }

    // 카드 추가 폼 (스펠, 아티팩트 통합)
    @GetMapping("/spell/new")
    public String addForm(HttpSession session, Model model) {
        CardAddForm cardForm = new CardAddForm();
        List<Character> characterList = characterService.findCharacters();

        String cardImageUrl = setCardImageUrl(session);

        model.addAttribute("cardForm", cardForm);
        model.addAttribute("characterList", characterList);
        model.addAttribute(CARD_IMAGE_URL, cardImageUrl);

        return "management/cards/cardAddForm";
    }

    // 카드 추가 요청 (스펠, 아티팩트 통합)
    @PostMapping("/new")
    public String addCard(HttpSession session, @RequestParam("action") String action,
                          @ModelAttribute("cardForm") CardAddForm cardForm, Model model) {

        log.info("cardForm = {}", cardForm);

        // 취소 버튼 클릭 시
        if (action.equals("cancel")) {
            clearSessionAttributes(session);
            return "redirect:/management/cards";
        }

        cardImageProcess(session, cardForm.getImage()); // 1. 이미지를 임시 경로에 저장하고 url을 세션에 추가

        String cardImageUrl = setCardImageUrl(session); // 2. 카드 이미지 url 세팅
        model.addAttribute(CARD_IMAGE_URL, cardImageUrl);

        session.setAttribute(CARD_FORM, cardForm); // 3. 세션의 폼 데이터 업데이트

        // 카드 생성 완료인 경우
        // 1. 카드 데이터 저장
        // 2. 이미지를 임시 경로에서 정식 경로로 이동
        if (action.equals("complete")) {
            CardDto cardDto = cardForm.generateCardDto();
            String defaultImagePath = imagePathService.getDefaultThumbNailFileName();
            Long cardId;

            // 스펠 카드인 경우
            if (cardForm.getCardType() == CardType.SPELL) {
                cardId = cardService.createNewSpellCard(cardDto);
                // TODO - 카드 이미지 저장
                String tmpImageUrl = (String) Optional.ofNullable(session.getAttribute(CARD_IMAGE_URL)).orElse(defaultImagePath);
                String tempFilePath = convertUrlToFilePathTemp(tmpImageUrl);
                imagePathService.saveSpellCardImage(cardId, tempFilePath); // 임시 경로에서 정식 경로로 파일을 저장
                System.out.println("tempFilePath = " + tempFilePath);
            }
            // 아티팩트 카드인 경우
            else {
                if (cardForm.isHasAttachment()) {
                    cardId = cardService.createNewArtifactCard(cardDto, cardForm.getCharacterId(), cardForm.generateAttachmentSkill());
                    // TODO - 카드 이미지 저장
                    String tmpImageUrl = (String) Optional.ofNullable(session.getAttribute(CARD_IMAGE_URL)).orElse(defaultImagePath);
                    String tempFilePath = convertUrlToFilePathTemp(tmpImageUrl);
                    imagePathService.saveSpellCardImage(cardId, tempFilePath); // 임시 경로에서 정식 경로로 파일을 저장
                    System.out.println("tempFilePath = " + tempFilePath);
                }
                else {
                    cardId = cardService.createNewArtifactCard(cardDto);
                    // TODO - 카드 이미지 저장
                    String tmpImageUrl = (String) Optional.ofNullable(session.getAttribute(CARD_IMAGE_URL)).orElse(defaultImagePath);
                    String tempFilePath = convertUrlToFilePathTemp(tmpImageUrl);
                    imagePathService.saveSpellCardImage(cardId, tempFilePath); // 임시 경로에서 정식 경로로 파일을 저장
                    System.out.println("tempFilePath = " + tempFilePath);
                }
            }
        }

        return "redirect:/management/cards";
    }




    @GetMapping("/artifact")
    public String artifactList(Model model) {
        List<ArtifactCard> cardList = cardService.findAllArtifactCards();
        String baseUrl = imageUrlService.getArtifactCardBaseUrl();

        model.addAttribute("cardList", cardList);
        model.addAttribute("baseUrl", baseUrl);
        return "management/cards/artifactCardList";
    }

    // === private ===

    // === 임시 경로 이미지에 대한 url을 파일 경로로 변경 ===
    private String convertUrlToFilePathTemp(String url) {
        // ex) /api/images/tmp/abcd123 -> {baseDir}/temp/abcd123.png
        String tempDir = fileStorageService.getTempDir();
        String fileName = fileStorageService.extractFileName(url);
        return tempDir + fileName;
    }

    private String setCardImageUrl(HttpSession session) {
        return (String) Optional.ofNullable(session.getAttribute(CARD_IMAGE_URL)).orElse(imageUrlService.getDefaultThumbnailUrl());
    }

    private void cardImageProcess(HttpSession session, MultipartFile imageFile) {

        if (imageFile == null || imageFile.isEmpty()) {
            log.info("imageFile is null or empty");
            return;
        }

        try {
            FileDto fileDto = fileStorageService.saveFileToTemp(imageFile); // 이미지를 임시 경로에 저장하고 파일명 리턴 (fileDto)
            String tmpUrl = imageUrlService.getTempBaseUrl() + fileDto.getFileName(); // 임시 경로 + 파일명으로 url 생성
            session.setAttribute(CARD_IMAGE_URL, tmpUrl); // 생성한 경로를 세션에 저장
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void clearSessionAttributes(HttpSession session) {
        session.removeAttribute(CARD_FORM);
        session.removeAttribute(CARD_IMAGE_URL);
    }

}
