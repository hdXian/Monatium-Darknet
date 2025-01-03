package hdxian.monatium_darknet.web.controller.management.card;

import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.card.ArtifactCard;
import hdxian.monatium_darknet.domain.card.Card;
import hdxian.monatium_darknet.domain.card.SpellCard;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.file.FileDto;
import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.service.CardService;
import hdxian.monatium_darknet.service.CharacterService;
import hdxian.monatium_darknet.service.ImagePathService;
import hdxian.monatium_darknet.service.ImageUrlService;
import hdxian.monatium_darknet.service.dto.CardDto;
import hdxian.monatium_darknet.web.validator.CardFormValidator;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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

    private final CardFormValidator formValidator;

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
        model.addAttribute("pageTitle", "스펠 카드 관리");
        return "management/cards/cardList";
    }

    // 아티팩트 카드 리스트
    @GetMapping("/artifact")
    public String artifactList(Model model) {
        List<ArtifactCard> cardList = cardService.findAllArtifactCards();
        String baseUrl = imageUrlService.getArtifactCardBaseUrl();

        model.addAttribute("cardList", cardList);
        model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("pageTitle", "아티팩트 카드 관리");
        return "management/cards/cardList";
    }

    // 카드 추가 폼 (스펠, 아티팩트 통합)
    @GetMapping("/new")
    public String addForm(HttpSession session, Model model) {
        // 세션에 있으면 (임시저장 등으로 리다이렉트) 가져오고, 없으면 (최초 요청) 새로운 객체를 생성.
        CardForm cardForm = (CardForm) Optional.ofNullable(session.getAttribute(CARD_FORM)).orElse(new CardForm());
        List<Character> characterList = characterService.findCharacters();

        // 세션에 있으면 (임시저장 등으로 리다이렉트) 가져오고, 없으면 (최초 요청) 새로운 디폴트 url 지정
        String cardImageUrl = getImageUrl(session, imageUrlService.getDefaultThumbnailUrl());

        model.addAttribute(CARD_FORM, cardForm);
        model.addAttribute("characterList", characterList);
        model.addAttribute(CARD_IMAGE_URL, cardImageUrl);

        return "management/cards/cardAddForm";
    }

    // 카드 추가 요청 처리 (스펠, 아티팩트 통합)
    @PostMapping("/new")
    public String addCard(HttpSession session, @RequestParam("action") String action,
                          @Validated @ModelAttribute("cardForm") CardForm cardForm, BindingResult bindingResult,
                          Model model) {

        log.info("cardForm = {}", cardForm);

        formValidator.validate(cardForm, bindingResult); // 폼 검증 추가

        if (bindingResult.hasErrors()) {
            String tempImageUrl = getImageUrl(session, imageUrlService.getDefaultThumbnailUrl());
            model.addAttribute(CARD_IMAGE_URL, tempImageUrl);

            List<Character> characterList = characterService.findCharacters();
            model.addAttribute("characterList", characterList);
            return "management/cards/cardAddForm";
        }

        // 0. 취소 버튼 클릭 시 세션 데이터를 초기화하고 목록으로 리다이렉트
        if (action.equals("cancel")) {
            clearSessionAttributes(session);
            return "redirect:/management/cards";
        }

        // 1. 이미지를 임시경로에 저장하고, url을 세션에 업데이트 한다.
        saveCardImageToTemp(session, cardForm.getImage());

        // 2. 임시경로 이미지에 대한 url을 생성하고 모델에 추가한다.
        String tempImageUrl = getImageUrl(session, imageUrlService.getDefaultThumbnailUrl());
        model.addAttribute(CARD_IMAGE_URL, tempImageUrl);

        // 3. 세션의 폼 데이터를 업데이트한다.
        session.setAttribute(CARD_FORM, cardForm);

        // 4. 완료 버튼을 누른 경우 카드 정보를 저장하고 목록으로 리다이렉트
        if (action.equals("complete")) {
            Long cardId = saveCard(session, cardForm);

            clearSessionAttributes(session);

            // 스펠 카드 목록 혹은 아티팩트 카드 목록으로 리다이렉트
            if (cardForm.getCardType() == CardType.SPELL) {
                return "redirect:/management/cards/spell";
            }
            else {
                return "redirect:/management/cards/artifact";
            }
        }
        // 5. 중간 저장 버튼을 누른 경우 폼 페이지로 리다이렉트
        else {
            return "redirect:/management/cards/new";
        }

    }

    // 카드 수정 폼 (스펠, 아티팩트 통합)
    @GetMapping("/edit/{cardId}")
    public String editForm(HttpSession session, @PathVariable("cardId") Long cardId, Model model) {

        // 세션에 수정하던 폼이 없으면 수정할 카드 id로 새로운 폼 객체를 생성
        CardForm cardForm = (CardForm) Optional.ofNullable(session.getAttribute(CARD_FORM)).orElse(generateNewCardForm(cardId, model));

        List<Character> characterList = characterService.findCharacters();

        if (cardForm.getCardType() == CardType.SPELL) {
            String imageUrl = getImageUrl(session, imageUrlService.getSpellCardBaseUrl() + cardId);
            model.addAttribute(CARD_IMAGE_URL, imageUrl);
        }
        else {
            String imageUrl = getImageUrl(session, imageUrlService.getArtifactCardBaseUrl() + cardId);
            model.addAttribute(CARD_IMAGE_URL, imageUrl);
        }

        model.addAttribute("cardId", cardId);
        model.addAttribute(CARD_FORM, cardForm);
        model.addAttribute("characterList", characterList);

        return "management/cards/cardEditForm";
    }

    // 카드 수정 요청
    @PostMapping("/edit/{cardId}")
    public String edit(HttpSession session, @PathVariable("cardId") Long cardId, @RequestParam("action") String action,
                       @ModelAttribute("cardForm") CardForm cardForm, Model model) {

        log.info("edit cardForm = {}", cardForm);

        // 0. 취소 버튼 클릭 시 세션 데이터를 초기화하고 목록으로 리다이렉트
        if (action.equals("cancel")) {
            clearSessionAttributes(session);
            if (cardForm.getCardType() == CardType.SPELL)
                return "redirect:/management/cards/spell";
            else
                return "redirect:/management/cards/artifact";
        }

        // 1. 이미지를 임시경로에 저장하고, url을 세션에 업데이트 한다.
        saveCardImageToTemp(session, cardForm.getImage());

        // 2. 임시경로 이미지에 대한 url을 생성하고 모델에 추가한다.
        if (cardForm.getCardType() == CardType.SPELL) {
            String imageUrl = getImageUrl(session, imageUrlService.getSpellCardBaseUrl() + cardId);
            model.addAttribute(CARD_IMAGE_URL, imageUrl);
        }
        else {
            String imageUrl = getImageUrl(session, imageUrlService.getArtifactCardBaseUrl() + cardId);
            model.addAttribute(CARD_IMAGE_URL, imageUrl);
        }

        // 3. 세션의 폼 데이터를 업데이트한다.
        session.setAttribute(CARD_FORM, cardForm);

        // TODO 4. 완료 버튼을 누른 경우 카드 정보를 저장하고 목록으로 리다이렉트
        if (action.equals("complete")) { // 수정 완료 버튼을 누른 경우
            Long updatedCardId = updateCard(session, cardId, cardForm);

            clearSessionAttributes(session);

            if (cardForm.getCardType() == CardType.SPELL) {
                return "redirect:/management/cards/spell";
            }
            else {
                return "redirect:/management/cards/artifact";
            }
        }
        else { // 임시저장 버튼을 누른 경우
            return "redirect:/management/cards/edit/" + cardId;
        }

    }


    @PostMapping("/activate/{cardId}")
    public ResponseEntity<Void> activateCard(@PathVariable("cardId") Long cardId) {
        cardService.activateCard(cardId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/disable/{cardId}")
    public ResponseEntity<Void> disableCard(@PathVariable("cardId") Long cardId) {
        cardService.disableCard(cardId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{cardId}")
    public String deleteCard(@PathVariable("cardId") Long cardId) {
        cardService.deleteCard(cardId);
        Card card = cardService.findOneCard(cardId);
        if (card instanceof SpellCard) {
            return "redirect:/management/cards/spell";
        }
        else {
            return "redirect:/management/cards/artifact";
        }
    }

    // ===== private =====

    private Long updateCard(HttpSession session, Long cardId, CardForm cardForm) {

        // 카드 타입은 바뀌면 안됨 (타입 체크용)
        Card card = cardService.findOneCard(cardId);

        // CardDto (공통 부분) 생성
        CardDto cardDto = cardForm.generateCardDto();

        // 업데이트할 이미지 가져오기 (세션에서 가져옴. 바뀐거 없으면 null)
        String imagePath_Edit = getImagePath_Edit(session);

        if (card instanceof SpellCard) { // 스펠 카드 업데이트

            // DB 업데이트 전 마지막 타입 체크
            if (cardForm.getCardType() != CardType.SPELL) {
                throw new IllegalArgumentException("카드의 타입이 맞지 않습니다. cardId = " + cardId);
            }

            // 스펠 카드 업데이트 (이미지 업데이트 포함)
            return cardService.updateSpellCard(cardId, cardDto, imagePath_Edit);
        }
        else { // 아티팩트 카드 업데이트

            // DB 업데이트 전 마지막 타입 체크
            if (cardForm.getCardType() != CardType.ARTIFACT) {
                throw new IllegalArgumentException("아티팩트 카드의 타입이 맞지 않습니다. cardId = " + cardId);
            }

            // 애착 사도가 있는 경우
            if (cardForm.isHasAttachment()) {
                Long chId = cardForm.getCharacterId();
                Skill attachmentSkill = cardForm.generateAttachmentSkill();

                // 아티팩트 카드 업데이트 (이미지 업데이트 포함)
                return cardService.updateArtifactCard(cardId, cardDto, chId, attachmentSkill, imagePath_Edit);
            }
            // 애착 사도가 없는 경우
            else {
                // 아티팩트 카드 업데이트 (이미지 업데이트 포함)
                return cardService.updateArtifactCard(cardId, cardDto, null, null, imagePath_Edit);
            }

        }

    }

    private Long saveCard(HttpSession session, CardForm cardForm) {
        // 1. 카드 데이터 저장
        // 2. 이미지를 임시 경로에서 정식 경로로 이동
        CardDto cardDto = cardForm.generateCardDto();
        String tempFilePath = getImagePath(session);
        Long cardId;

        // 스펠 카드인 경우
        if (cardForm.getCardType() == CardType.SPELL) {
            cardId = cardService.createNewSpellCard(cardDto);
            // 카드 이미지 저장
            imagePathService.saveSpellCardImage(cardId, tempFilePath); // 임시 경로에서 정식 경로로 파일을 저장
            System.out.println("tempFilePath = " + tempFilePath);
        }
        // 아티팩트 카드인 경우
        else {
            if (cardForm.isHasAttachment()) { // 애착 사도가 있는 경우
                cardId = cardService.createNewArtifactCard(cardDto, cardForm.getCharacterId(), cardForm.generateAttachmentSkill());
                // 카드 이미지 저장
                imagePathService.saveArtifactCardImage(cardId, tempFilePath); // 임시 경로에서 정식 경로로 파일을 저장
                System.out.println("tempFilePath = " + tempFilePath);
            }
            else { // 애착 사도가 없는 경우
                cardId = cardService.createNewArtifactCard(cardDto);
                // 카드 이미지 저장
                imagePathService.saveArtifactCardImage(cardId, tempFilePath); // 임시 경로에서 정식 경로로 파일을 저장
                System.out.println("tempFilePath = " + tempFilePath);
            }
        }

        return cardId;
    }

    private CardForm generateNewCardForm(Long cardId, Model model) {
        Card card = cardService.findOneCard(cardId);

        CardForm cardForm = new CardForm();

        // 공통 부분 처리
        cardForm.setGrade(card.getGrade());
        cardForm.setName(card.getName());
        cardForm.setDescription(card.getDescription());
        cardForm.setStory(card.getStory());
        cardForm.setCost(card.getCost());
        cardForm.setCardAttributes(card.getAttributes());

        // 카드가 스펠 카드인 경우
        if (card instanceof SpellCard) {
            cardForm.setCardType(CardType.SPELL);
//            String imageUrl = imageUrlService.getSpellCardBaseUrl() + cardId;
//            model.addAttribute(CARD_IMAGE_URL, imageUrl);
        }
        // 카드가 아티팩트 카드인 경우
        else {
            // 아티팩트 카드인 경우 애착 사도에 대한 정보 처리 필요
            cardForm.setCardType(CardType.ARTIFACT);
//            String imageUrl = imageUrlService.getArtifactCardBaseUrl() + cardId;
//            model.addAttribute(CARD_IMAGE_URL, imageUrl);

            ArtifactCard artifactCard = (ArtifactCard) card;

            // 애착 사도가 있다면
            if (artifactCard.getCharacter() != null) {
                cardForm.setHasAttachment(true);

                cardForm.setCharacterId(artifactCard.getCharacter().getId());

                Skill attachmentSkill = artifactCard.getAttachmentSkill();
                cardForm.setAttachmentSkillName(attachmentSkill.getName());
                cardForm.setAttachmentSkillDescription(attachmentSkill.getDescription());
                cardForm.setAttachmentLv3Description(attachmentSkill.getAttachmentLv3Description());
                cardForm.setAttachmentAttributes(attachmentSkill.getAttributes());
            }
            else {
                cardForm.setHasAttachment(false); // 기본값이라 필수 동작은 아님
            }

        }
        return cardForm;
    }

    private String getImagePath_Edit(HttpSession session) {
        String imageUrl = (String) session.getAttribute(CARD_IMAGE_URL);

        // 해당 이미지가 있으면 파일명을 뽑아 temp 경로와 합쳐 리턴
        if (imageUrl != null) {
            return convertUrlToTempPath(imageUrl);
        }
        else // 없으면 null 리턴 (업데이트할 이미지 x)
            return null;
    }

    private String getImagePath(HttpSession session) {
        String imageUrl = (String) session.getAttribute(CARD_IMAGE_URL);

        // 해당 이미지가 있으면 (세션에 url이 있으면), url에서 파일명을 뽑아 temp 경로와 합쳐서 파일 경로를 리턴
        if (imageUrl != null) {
            return convertUrlToTempPath(imageUrl);
        }
        // 지정한 이미지가 없으면 (세션에 url이 없으면) 기본 썸네일 경로를 리턴
        else {
            return imagePathService.getDefaultThumbNailFilePath();
        }

    }

    private String convertUrlToTempPath(String imageUrl) {
        String fileName = fileStorageService.extractFileName(imageUrl);
        return fileStorageService.getTempDir() + fileName;
    }

    // 세션에 이미지 url이 있으면 해당 url을 리턴, 없으면 디폴트 썸네일 url을 리턴
    private String getImageUrl(HttpSession session, String defaultUrl) {
        return (String) Optional.ofNullable(session.getAttribute(CARD_IMAGE_URL)).orElse(defaultUrl);
    }

    // MultipartFile을 temp 경로에 저장하고 파일명을 리턴한다.
    private void saveCardImageToTemp(HttpSession session, MultipartFile imageFile) {

        // 파일이 없을 경우 동작 x.
        if (imageFile == null || imageFile.isEmpty()) {
            log.info("[saveCardImageToTemp()] imageFile is null or empty");
            return;
        }

        // 파일을 저장하고 이미지 url을 세션에 저장.
        try {
            FileDto fileDto = fileStorageService.saveFileToTemp(imageFile);
            String tempUrl = imageUrlService.getTempImageBaseUrl() + fileDto.getFileName();
            session.setAttribute(CARD_IMAGE_URL, tempUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void clearSessionAttributes(HttpSession session) {
        session.removeAttribute(CARD_FORM);
        session.removeAttribute(CARD_IMAGE_URL);
    }

}
