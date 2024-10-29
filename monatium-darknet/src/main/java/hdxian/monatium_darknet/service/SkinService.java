package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.domain.skin.Skin;
import hdxian.monatium_darknet.domain.skin.SkinGrade;
import hdxian.monatium_darknet.repository.CharacterRepository;
import hdxian.monatium_darknet.repository.SkinCategoryRepository;
import hdxian.monatium_darknet.repository.SkinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SkinService {

    private final SkinRepository skinRepository;
//    private final SkinCategoryRepository categoryRepository;
    private final CharacterRepository characterRepository;

    // 스킨 추가
    // 추후 Dto 등으로 바꿔야 함
    @Transactional
    public Long addSkin(Long characterId, String name, SkinGrade skinGrade, String description) {

        Character ch = characterRepository.findOne(characterId);
        Skin skin = Skin.createSkin(ch, name, skinGrade, description);

        return skinRepository.save(skin);
    }

    // 스킨 검색
    public Skin findOne(Long id) {
        return skinRepository.findOne(id);
    }

    public List<Skin> findByCategory(Long categoryId) {
        return skinRepository.findBySkinCategory(categoryId);
    }

    public List<Skin> findByCharacter(Long characterId) {
        return skinRepository.findByCharacter(characterId);
    }



}
