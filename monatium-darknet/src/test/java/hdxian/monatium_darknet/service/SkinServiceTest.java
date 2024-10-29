package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.domain.skin.Skin;
import hdxian.monatium_darknet.domain.skin.SkinGrade;
import hdxian.monatium_darknet.repository.CharacterRepository;
import hdxian.monatium_darknet.repository.SkinRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SkinServiceTest {

    @Autowired
    CharacterRepository characterRepository;

    @Autowired
    SkinService service;

    // TODO - 스킨 카테고리 추가 및 테스트해봐야 함

    // 스킨 추가
    @Test
    @DisplayName("스킨 추가")
    @Rollback(value = false)
    void addSkin() {
        // given
        Character rim = generateMockChar("림");
        Long rim_id = characterRepository.save(rim);

        // when
        Long saved_skin_id = service.addSkin(rim_id, "라크로스 림크로스", SkinGrade.NORMAL, "라크로스 림크로스 설명");

        // then
        Skin find_skin = service.findOne(saved_skin_id);
        Character findCharacter = find_skin.getCharacter();

        // 림의 스킨이 맞는가?
        assertThat(findCharacter).isEqualTo(rim);
        assertThat(findCharacter.getName()).isEqualTo("림");

        // 스킨 정보는 맞는가?
        assertThat(find_skin.getName()).isEqualTo("라크로스 림크로스");
        assertThat(find_skin.getGrade()).isEqualTo(SkinGrade.NORMAL);
        assertThat(find_skin.getDescription()).isEqualTo("라크로스 림크로스 설명");
    }


    // 스킨 검색

    static Character generateMockChar(String name) {
        return Character.createCharacter(name, null, null, 0, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);
    }


}