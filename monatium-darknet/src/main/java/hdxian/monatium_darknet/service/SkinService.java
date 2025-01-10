package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.domain.skin.Skin;
import hdxian.monatium_darknet.domain.skin.SkinCategory;
import hdxian.monatium_darknet.domain.skin.SkinCategoryMapping;
import hdxian.monatium_darknet.domain.skin.SkinStatus;
import hdxian.monatium_darknet.repository.SkinCategoryRepository;
import hdxian.monatium_darknet.repository.SkinRepository;
import hdxian.monatium_darknet.repository.dto.SkinSearchCond;
import hdxian.monatium_darknet.service.dto.SkinDto;
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

    private final ImagePathService imagePathService;

    // 스킨 추가
    @Transactional
    public Long createNewSkin(Long characterId, SkinDto skinDto) {
        Character character = characterService.findOne(characterId);

        Skin skin = Skin.createSkin(
                skinDto.getName(),
                skinDto.getDescription(),
                character
        );

        return skinRepository.save(skin);
    }

    @Transactional
    public Long createNewSkin(Long characterId, SkinDto skinDto, String tempImagePath) {
        Character character = characterService.findOne(characterId);

        Skin skin = Skin.createSkin(
                skinDto.getName(),
                skinDto.getDescription(),
                character
        );

        Long savedId = skinRepository.save(skin);

        for(Long categoryId: skinDto.getCategoryIds()) {
            linkSkinAndCategory(savedId, categoryId);
        }

        // 스킨 이미지 저장
        if (tempImagePath != null) {
            imagePathService.saveSkinImage(savedId, tempImagePath);
        }

        return savedId;
    }

//    @Transactional
//    public void updateImageUrl(Long skinId, String imageUrl) {
//        Skin skin = findOneSkin(skinId);
//        skin.setImageUrl(imageUrl);
//    }

    // 스킨 업데이트
    @Transactional
    public Long updateSkin(Long skinId, SkinDto updateParam) {
        Skin skin = findOneSkin(skinId);

        skin.setName(updateParam.getName());
//        skin.setGrade(updateParam.getGrade());
        skin.setDescription(updateParam.getDescription());

        return skin.getId(); // save() 호출 x. merge가 필요한 로직이 아님.
    }

    @Transactional
    public Long updateSkin(Long skinId, SkinDto updateParam, Long characterId, String tempImagePath) {
        Skin skin = findOneSkin(skinId);

        skin.setName(updateParam.getName());
        skin.setDescription(updateParam.getDescription());

        // 캐릭터 업데이트. 꼭 캐릭터를 바꾸도록 해야 하는지는 모르겠음.
        Character character = characterService.findOne(characterId);
        skin.setCharacter(character);

        // 현재 연결돼있는 카테고리들의 id를 가져옴.
        List<Long> existCategoryIds = new ArrayList<>();
        for(SkinCategoryMapping mapping: skin.getMappings()) {
            existCategoryIds.add(mapping.getSkinCategory().getId());
        }

        // 기존 카테고리 매핑을 제거
        for (Long existCategoryId : existCategoryIds) {
            unLinkSkinAndCategory(skinId, existCategoryId);
        }

        // 업데이트된 카테고리 매핑들을 추가
        for (Long categoryId : updateParam.getCategoryIds()) {
            linkSkinAndCategory(skinId, categoryId);
        }

        // 이미지 경로 업데이트 (null 넘어오면 업데이트 안 함)
        if (tempImagePath != null) {
            imagePathService.saveSkinImage(skinId, tempImagePath);
        }

        return skin.getId();
    }

    // 카테고리 추가
    @Transactional
    public Long createNewSkinCategory(String name) {
        SkinCategory skinCategory = SkinCategory.createSkinCategory(name);
        return categoryRepository.save(skinCategory);
    }

    @Transactional
    public Long createNewSkinCategory(String name, List<Long> skinIds) {
        SkinCategory skinCategory = SkinCategory.createSkinCategory(name);

        Long savedCategoryId = categoryRepository.save(skinCategory);

        for (Long skinId : skinIds) {
            linkSkinAndCategory(skinId, savedCategoryId);
        }

        return savedCategoryId;
    }

    // 카테고리 변경 (이름밖에 없긴함)
    @Transactional
    public Long updateSkinCategory(Long categoryId, String updateName) {
        SkinCategory category = findOneCategory(categoryId);

        category.setName(updateName);

        return category.getId();
    }

    // 스킨 - 카테고리 관계 추가 (스킨에 카테고리든, 카테고리에 스킨이든 추가 가능)
    @Transactional
    public void linkSkinAndCategory(Long skinId, Long categoryId) {
        Skin skin = findOneSkin(skinId);
        SkinCategory category = findOneCategory(categoryId);

        // skin의 addCategory()만 호출 -> 내부에서 양방향 연관관계가 모두 설정되어 한 쪽만 호출해도 됨.
        //** 중요 ** cascade 설정은 skin, category 둘 중 한쪽에만 해줘야 함. 둘다 해주면 양쪽 연관관계를 설정하는 과정에서 mapping이 DB에 두번 insert됨.
        skin.addCategory(category);
//        category.addSkin(skin); // 동시 호출 금지

        skinRepository.save(skin);
//        categoryRepository.save(category);
    }

    // 스킨-카테고리 간 관계 제거 (스킨 입장에서는 카테고리 제거, 카테고리 입장에서는 스킨 제거)
    @Transactional
    public void unLinkSkinAndCategory(Long skinId, Long categoryId) {
        Skin skin = findOneSkin(skinId);
        SkinCategory category = findOneCategory(categoryId);

        skin.removeCategory(category);
//        category.removeSkin(skin); // 동시 호출 금지

        skinRepository.save(skin);
//        categoryRepository.save(category);
    }

    @Transactional
    public void activateSkin(Long skinId) {
        Skin skin = findOneSkin(skinId);
        skin.setStatus(SkinStatus.ACTIVE);
    }

    @Transactional
    public void disableSkin(Long skinId) {
        Skin skin = findOneSkin(skinId);
        skin.setStatus(SkinStatus.DISABLE);
    }

    @Transactional
    public void deleteSkin(Long skinId) {
        Skin skin = findOneSkin(skinId);
        skin.setStatus(SkinStatus.DELETED);
        // 모든 mapping들을 고아 객체로 만들어 JPA가 자동으로 삭제하도록 함. (skin의 mappings의 orphanRemoval = true)
//        List<SkinCategory> categories = findCategoriesBySkin(skinId);
//        for (SkinCategory category : categories) {
//            skin.removeCategory(category);
//        }

        // character와의 연관관계도 제거해야 함
//        Character skinCharacter = skin.getCharacter();
//        skinCharacter.removeSkin(skin);

//        skinRepository.delete(skin);
    }

    @Transactional
    public void deleteSkinCategory(Long categoryId) {
        SkinCategory categoryToRemove = findOneCategory(categoryId);

        SkinSearchCond searchCond = new SkinSearchCond();
        searchCond.getCategoryIds().add(categoryId);
        // 모든 스킨과의 연관관계를 제거 (연관관계, mapping 추가를 skin에서만 하기 때문)
        List<Skin> skins = findAllSkin(searchCond);
//        List<Skin> skins = findSkinsByCategory(categoryId);
        for (Skin skin : skins) {
            skin.removeCategory(categoryToRemove);
        }

        categoryRepository.delete(categoryToRemove);
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

    // 조건별 스킨 검색
    public List<Skin> findAllSkin() {
        SkinSearchCond searchCond = new SkinSearchCond();
        return skinRepository.findAll(searchCond);
    }

    public List<Skin> findAllSkin(SkinSearchCond searchCond) {
        return skinRepository.findAll(searchCond);
    }

    // 카테고리 리스트
    public List<SkinCategory> findCategoriesBySkin(Long skinId) {
        return categoryRepository.findBySkinId(skinId);
    }

    public List<SkinCategory> findAllCategories() {
        return categoryRepository.findAll();
    }

}
