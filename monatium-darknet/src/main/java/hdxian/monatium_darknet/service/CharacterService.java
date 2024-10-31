package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CharacterService {

    private final CharacterRepository characterRepository;

    // 단순 위임 로직 (아직까지는)

    // 캐릭터 추가 기능
    @Transactional
    public Long addCharacter(Character character) {
        return characterRepository.save(character);
    }

    // 캐릭터 검색 기능
    public Character findOne(Long id) {
        Optional<Character> find = characterRepository.findOne(id);
        // TODO - null일 때 예외 던지는 로직 추가
        if(find.isEmpty()) {
            throw new RuntimeException("해당 캐릭터가 없습니다.");
        }
        return find.get();
    }

    public List<Character> findByName(String name) {
        return characterRepository.findByName(name);
    }

    public List<Character> findCharacters() {
        return characterRepository.findAll();
    }

    // TODO - 조건별 캐릭터 검색 기능 추가 필요

    // TODO - 캐릭터 수정/삭제기능 추가 필요
    //    // 캐릭터 변경 기능 (인자에 Dto 필요)
//    public Long updateCharacter(Long characterId, CharacterUpdateDto updateParam) {
//        Optional<Character> find = characterRepository.findOne(characterId);
//
//
//    }

}
