package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.domain.skin.Skin;
import hdxian.monatium_darknet.domain.skin.SkinCategory;
import hdxian.monatium_darknet.domain.skin.SkinCategoryMapping;
import hdxian.monatium_darknet.domain.skin.SkinGrade;
import hdxian.monatium_darknet.repository.CharacterRepository;
import hdxian.monatium_darknet.repository.SkinCategoryRepository;
import hdxian.monatium_darknet.repository.SkinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SkinService {

    private final SkinRepository skinRepository;
    private final SkinCategoryRepository categoryRepository;
    private final CharacterRepository characterRepository;

    // 스킨 추가
    // 추후 Dto 등으로 바꿔야 함
    // Skin은 여러 개의 SkinCategory를 가질 수 있음
    @Transactional
    public Long createNewSkin(String name, SkinGrade grade, String description, Long characterId, List<Long> skinCategoryIds) {

        // 필요 엔티티 조회
        Character character = characterRepository.findOne(characterId);
        List<SkinCategory> categories = new ArrayList<>();

        for (Long categoryId : skinCategoryIds) {
            SkinCategory skinCategory = categoryRepository.findOne(categoryId);
            categories.add(skinCategory);
        }

        Skin skin = Skin.createSkin(name, grade, description, character, categories);

        return skinRepository.save(skin);
    }

    // 스킨 검색
    public Skin findOne(Long skinId) {
        return skinRepository.findOne(skinId);
    }

    public List<Skin> findByCategory(Long categoryId) {
        return skinRepository.findBySkinCategory(categoryId);
    }

    public List<Skin> findByCharacter(Long characterId) {
        return skinRepository.findByCharacter(characterId);
    }

}
