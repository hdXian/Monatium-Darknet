package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.Character;
import hdxian.monatium_darknet.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        return characterRepository.findOne(id);
    }

    public List<Character> findByName(String name) {
        return characterRepository.findByName(name);
    }

    public List<Character> findCharacters() {
        return characterRepository.findAll();
    }

}
