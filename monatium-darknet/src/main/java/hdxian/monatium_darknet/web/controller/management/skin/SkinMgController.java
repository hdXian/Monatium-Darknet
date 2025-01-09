package hdxian.monatium_darknet.web.controller.management.skin;

import hdxian.monatium_darknet.domain.skin.Skin;
import hdxian.monatium_darknet.service.ImageUrlService;
import hdxian.monatium_darknet.service.SkinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/management/skins")
public class SkinMgController {

    private final SkinService skinService;

    private final ImageUrlService imageUrlService;

    // 스킨 목록
    @GetMapping
    public String skinList(Model model) {
        List<Skin> skinList = skinService.findAllSkin();

        model.addAttribute("skinList", skinList);
        return "management/skins/skinList";
    }

    @ModelAttribute("skinBaseUrl")
    public String skinBaseUrl() {
        return imageUrlService.getSkinBaseUrl();
    }



}
