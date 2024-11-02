package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.domain.skin.Skin;
import hdxian.monatium_darknet.domain.skin.SkinCategory;
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
    public Long createNewSkin(Long characterId, SkinDto skinDto) {
        Character character = characterService.findOne(characterId);

        List<SkinCategory> categories = new ArrayList<>();

        Skin skin = Skin.createSkin(
                skinDto.getName(),
                skinDto.getGrade(),
                skinDto.getDescription(),
                character
        );

        return skinRepository.save(skin);
    }

    // 스킨 업데이트
    @Transactional
    public Long updateSkin(Long skinId, SkinDto updateParam) {
        Optional<Skin> find = skinRepository.findOne(skinId);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 스킨이 존재하지 않습니다. skinId=" + skinId);
        }

        Skin skin = find.get();
        skin.setName(updateParam.getName());
        skin.setGrade(updateParam.getGrade());
        skin.setDescription(updateParam.getDescription());

        return skin.getId(); // save() 호출 x. merge가 필요한 로직이 아님.
    }

    // 카테고리 추가
    @Transactional
    public Long createNewSkinCategory(String name) {
        SkinCategory skinCategory = SkinCategory.createSkinCategory(name);
        return categoryRepository.save(skinCategory);
    }

    // 카테고리 변경 (이름밖에 없긴함)
    @Transactional
    public Long updateSkinCategory(Long categoryId, String updateName) {
        Optional<SkinCategory> find = categoryRepository.findOne(categoryId);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 스킨 카테고리가 존재하지 않습니다. categoryId=" + categoryId);
        }

        SkinCategory category = find.get();
        category.setName(updateName);

        return category.getId();
    }

    // 스킨 - 카테고리 관계 추가 (스킨에 카테고리든, 카테고리에 스킨이든 추가 가능)
    @Transactional
    public void linkSkinAndCategory(Long skinId, Long categoryId) {
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

        // skin의 addCategory()만 호출 -> 내부에서 양방향 연관관계가 모두 설정되어 한 쪽만 호출해도 됨.
        //** 중요 ** cascade 설정은 skin, category 둘 중 한쪽에만 해줘야 함. 둘다 해주면 양쪽 연관관계를 설정하는 과정에서 mapping이 DB에 두번 insert됨.
        skin.addCategory(category);
//        category.addSkin(skin); // 동시 호출 금지

        skinRepository.save(skin);
    }

    // 스킨-카테고리 간 관계 제거 (스킨 입장에서는 카테고리 제거, 카테고리 입장에서는 스킨 제거)
    @Transactional
    public void unLinkSkinAndCategory(Long skinId, Long categoryId) {
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

        skin.removeCategory(category);

        skinRepository.save(skin);
    }

    // 스킨 검색
    public Skin findOneSkin(Long skinId) {
        Optional<Skin> find = skinRepository.findOne(skinId);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 스킨이 존재하지 않습니다. skinId=" + skinId);
        }

        return find.get();
    }

    // 카테고리 검색
    public SkinCategory findOneCategory(Long categoryId) {
        Optional<SkinCategory> find = categoryRepository.findOne(categoryId);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 스킨 카테고리가 존재하지 않습니다. categoryId=" + categoryId);
        }
        return find.get();
    }

    // 스킨 리스트
    public List<Skin> findSkinsByCategory(Long categoryId) {
        return skinRepository.findBySkinCategoryId(categoryId);
    }

    public List<Skin> findSkinsByCharacter(Long characterId) {
        return skinRepository.findByCharacterId(characterId);
    }

    public List<Skin> findAllSkin() {
        return skinRepository.findAll();
    }

    // 카테고리 리스트
    public List<SkinCategory> findCategoriesBySkin(Long skinId) {
        return categoryRepository.findBySkinId(skinId);
    }

    public List<SkinCategory> findAllCategories() {
        return categoryRepository.findAll();
    }

}
