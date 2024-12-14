package hdxian.monatium_darknet.web.controller.management.character;

import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.service.CharacterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@Controller
@RequestMapping("/management/characters")
@RequiredArgsConstructor
public class CharacterMgController {

    private final CharacterService characterService;

    @GetMapping
    public String characterList(Model model) {
        List<Character> characterList = characterService.findCharacters();

        model.addAttribute("characterList", characterList);
        return "management/characters/characterList";
    }

    @GetMapping("/new")
    public String newForm() {
        return "redirect:/management/characters/new/step1";
    }

    // 1단계 폼 페이지
    @GetMapping("/new/step1")
    public String chFormStep1(@ModelAttribute("chForm")ChFormStep1 chForm) {
        return "management/characters/addChStep1";
    }

    @PostMapping("/new/step1")
    public String chAddStep1(@ModelAttribute("chForm")ChFormStep1 chForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "management/characters/addChStep1";
        }

        log.info("chForm1 = {}", chForm);
        for (String favorite : chForm.getFavorites()) {
            log.info("favorite = {}", favorite);
        }

        return "redirect:/management/characters/new/step2";
    }

    // 2단계 폼 페이지
    @GetMapping("/new/step2")
    public String chFormStep2(@ModelAttribute("chForm")ChFormStep2 chForm) {
        return "management/characters/addChStep2";
    }

    @PostMapping("/new/step2")
    public String chAddStep2(@ModelAttribute("chForm")ChFormStep2 chForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "management/characters/addChStep2";
        }

        log.info("chForm2 = {}", chForm);

        return "redirect:/management/characters/new/step3";
    }

    // 3단계 폼 페이지
    @GetMapping("/new/step3")
    public String chFormStep3(@ModelAttribute("chForm")ChFormStep3 chForm) {
        return "management/characters/addChStep3";
    }

    @PostMapping("/new/step3")
    public String chAddStep3(@ModelAttribute("chForm")ChFormStep3 chForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "management/characters/addChStep3";
        }

        return "redirect:/management/characters/new/step4";
    }

    // 4단계 폼 페이지
    @GetMapping("/new/step4")
    public String chFormStep4(@ModelAttribute("chForm")ChFormStep4 chForm) {
        return "management/characters/addChStep4";
    }

    @PostMapping("/new/step4")
    public String chAddStep4(@ModelAttribute("chForm")ChFormStep4 chForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "management/characters/addChStep4";
        }

        return "redirect:/management/characters/preview"; // 4단계 끝나면 캐릭터 리스트 또는 미리보기 페이지로 이동
    }

    @GetMapping("/preview")
    public String chAddPreview() {
        return "management/characters/preview";
    }

}
