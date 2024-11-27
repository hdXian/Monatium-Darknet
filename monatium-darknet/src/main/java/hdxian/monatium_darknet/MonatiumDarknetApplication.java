package hdxian.monatium_darknet;

import hdxian.monatium_darknet.service.CharacterService;
import hdxian.monatium_darknet.service.MemberService;
import hdxian.monatium_darknet.service.NoticeService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MonatiumDarknetApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonatiumDarknetApplication.class, args);
	}

	@Bean
	public TestDataInit dataInit(MemberService memberService, NoticeService noticeService, CharacterService characterService) {
		return new TestDataInit(memberService, noticeService, characterService);
	}

}
