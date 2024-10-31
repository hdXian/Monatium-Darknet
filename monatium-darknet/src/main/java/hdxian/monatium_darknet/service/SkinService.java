package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.domain.skin.Skin;
import hdxian.monatium_darknet.domain.skin.SkinCategory;
import hdxian.monatium_darknet.domain.skin.SkinGrade;
import hdxian.monatium_darknet.repository.SkinCategoryRepository;
import hdxian.monatium_darknet.repository.SkinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SkinService {
    // Skin과 SkinCategory 관련 로직 통합

    private final SkinRepository skinRepository;
    private final SkinCategoryRepository categoryRepository;

    private final CharacterService characterService;

    // 스킨 추가
    @Transactional
    public Long createNewSkin(Long characterId, String name, SkinGrade grade, String description, Long... skinCategoryIds) {

        // 필요 엔티티 조회
        Character character = characterService.findOne(characterId);

        // 카테고리 리스트
        List<SkinCategory> categories = new ArrayList<>();

        for (Long categoryId : skinCategoryIds) {
            Optional<SkinCategory> findCategory = categoryRepository.findOne(categoryId);
            if (findCategory.isEmpty()) {
                throw new NoSuchElementException("해당 스킨 카테고리가 존재하지 않습니다. categoryId=" + categoryId);
            }
            categories.add(findCategory.get());
        }

        Skin skin = Skin.createSkin(name, grade, description, character, categories);

        return skinRepository.save(skin);
    }

    // 스킨에 카테고리 추가
    @Transactional
    public void addCategoryOnSkin(Long skinId, Long categoryId) {
        Optional<Skin> findSkin = skinRepository.findOne(skinId);
        Optional<SkinCategory> findCategory = categoryRepository.findOne(categoryId);
        if (findSkin.isEmpty()) {
            throw new NoSuchElementException("해당 스킨이 존재하지 않습니다. skinId=" + skinId);
        }
        if (findCategory.isEmpty()) {
            throw new NoSuchElementException("해당 스킨 카테고리가 존재하지 않습니다. categoryId=" + categoryId);
        }

        Skin skin = findSkin.get();
        SkinCategory category = findCategory.get();
        skin.addCategory(category);
        skinRepository.save(skin);
    }

    // 카테고리 추가
    @Transactional
    public Long createNewCategory(String name, Long... skinIds) {

        List<Skin> skins = new ArrayList<>();

        for (Long skinId : skinIds) {
            Optional<Skin> findSkin = skinRepository.findOne(skinId);
            if (findSkin.isEmpty()) {
                throw new NoSuchElementException("해당 스킨이 존재하지 않습니다. skinId=" + skinId);
            }
            skins.add(findSkin.get());
        }

        SkinCategory skinCategory = SkinCategory.createSkinCategory(name, skins);

        return categoryRepository.save(skinCategory);
    }

    // 스킨을 검색
    public Skin findOneSkin(Long skinId) {
        Optional<Skin> find = skinRepository.findOne(skinId);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 스킨이 존재하지 않습니다. skinId=" + skinId);
        }

        return find.get();
    }

    public List<Skin> findSkinByCategoryId(Long categoryId) {
        return skinRepository.findBySkinCategory(categoryId);
    }

    public List<Skin> findSkinByCharacterId(Long characterId) {
        return skinRepository.findByCharacter(characterId);
    }

    public List<Skin> findAllSkin() {
        return skinRepository.findAll();
    }

    // 카테고리를 검색
    public SkinCategory findOneCategory(Long categoryId) {
        Optional<SkinCategory> find = categoryRepository.findOne(categoryId);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 스킨 카테고리가 존재하지 않습니다. categoryId=" + categoryId);
        }
        return find.get();
    }

    // 스킨 카테고리 여러개 검색
    public List<SkinCategory> findCategoryBySkinId(Long skinId) {
        return categoryRepository.findBySkin(skinId);
    }

    public List<SkinCategory> findAllCategories() {
        return categoryRepository.findAll();
    }

}
