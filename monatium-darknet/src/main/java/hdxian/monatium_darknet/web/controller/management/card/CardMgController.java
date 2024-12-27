package hdxian.monatium_darknet.web.controller.management.card;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/management/cards")
public class CardMgController {

    @GetMapping
    public String cardList() {
        return "management/cards/cardList";
    }


}
