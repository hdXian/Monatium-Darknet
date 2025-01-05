package hdxian.monatium_darknet;

import hdxian.monatium_darknet.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class MonatiumDarknetApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonatiumDarknetApplication.class, args);
	}

	@Bean
	@Profile("dev")
	public TestDataInit dataInit(MemberService memberService, NoticeService noticeService, CharacterService characterService, SkinService skinService, CardService cardService) {
		return new TestDataInit(memberService, noticeService, characterService, skinService, cardService);
	}

}
