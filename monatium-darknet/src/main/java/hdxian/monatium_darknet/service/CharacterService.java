package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.card.Card;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.domain.character.CharacterStatus;
import hdxian.monatium_darknet.domain.skin.Skin;
import hdxian.monatium_darknet.domain.skin.SkinCategory;
import hdxian.monatium_darknet.domain.skin.SkinStatus;
import hdxian.monatium_darknet.repository.CardRepository;
import hdxian.monatium_darknet.repository.CharacterRepository;
import hdxian.monatium_darknet.repository.SkinCategoryRepository;
import hdxian.monatium_darknet.repository.SkinRepository;
import hdxian.monatium_darknet.repository.dto.CharacterSearchCond;
import hdxian.monatium_darknet.repository.dto.SkinSearchCond;
import hdxian.monatium_darknet.service.dto.AsideImageDto;
import hdxian.monatium_darknet.service.dto.CharacterDto;
import hdxian.monatium_darknet.service.dto.CharacterImageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CharacterService {

    private final CardRepository cardRepository;
    private final CharacterRepository characterRepository;
    private final SkinRepository skinRepository;
    private final SkinCategoryRepository skinCategoryRepository;

    private final ImagePathService imagePathService;

    // 캐릭터 추가 기능
    @Transactional
    public Long createNewCharacter(CharacterDto chDto, CharacterImageDto chImagePaths, AsideImageDto asideImagePaths) {

        // 캐릭터 기본 정보 저장
        Character ch = Character.createCharacter(
                chDto.getName(),
                chDto.getSubtitle(),
                chDto.getCv(),
                chDto.getGrade(),
                chDto.getQuote(),
                chDto.getTmi(),
                chDto.getFavorite(),
                chDto.getRace(),
                chDto.getPersonality(),
                chDto.getRole(),
                chDto.getAttackType(),
                chDto.getPosition(),
                chDto.getStat(),
                chDto.getNormalAttack(),
                chDto.getEnhancedAttack(),
                chDto.getLowSKill(),
                chDto.getHighSkill(),
                chDto.getAside()
        );

        Long savedId = characterRepository.save(ch);

        // 캐릭터 이미지 정보 저장
        imagePathService.saveCharacterImages(savedId, chImagePaths);
        if (asideImagePaths != null) {
            imagePathService.saveAsideImages(savedId, asideImagePaths);
        }

        return savedId;
    }

    // 캐릭터 업데이트
    @Transactional
    public Long updateCharacter(Long characterId, CharacterDto updateParam, CharacterImageDto chImagePaths, AsideImageDto asideImagePaths) {
        Character ch = findOne(characterId);

        Optional.ofNullable(updateParam.getName()).ifPresent(ch::setName);
        Optional.ofNullable(updateParam.getSubtitle()).ifPresent(ch::setSubtitle);
        Optional.ofNullable(updateParam.getCv()).ifPresent(ch::setCv);
        Optional.ofNullable(updateParam.getGrade()).ifPresent(ch::setGrade);
        Optional.ofNullable(updateParam.getQuote()).ifPresent(ch::setQuote);
        Optional.ofNullable(updateParam.getTmi()).ifPresent(ch::setTmi);
        Optional.ofNullable(updateParam.getFavorite()).ifPresent(ch::setFavorite);
        Optional.ofNullable(updateParam.getRace()).ifPresent(ch::setRace);
        Optional.ofNullable(updateParam.getPersonality()).ifPresent(ch::setPersonality);
        Optional.ofNullable(updateParam.getRole()).ifPresent(ch::setRole);
        Optional.ofNullable(updateParam.getAttackType()).ifPresent(ch::setAttackType);
        Optional.ofNullable(updateParam.getPosition()).ifPresent(ch::setPosition);
        Optional.ofNullable(updateParam.getStat()).ifPresent(ch::setStat);

        // 기존 객체를 수정
        Optional.ofNullable(updateParam.getNormalAttack()).ifPresent(ch::setNormalAttack);
        ch.setEnhancedAttack(updateParam.getEnhancedAttack()); // 강화 공격은 있을 수도, 없을 수도 있음 -> 강화 공격은 null 넘어오면 그대로 null로 설정

        Optional.ofNullable(updateParam.getLowSKill()).ifPresent(ch::setLowSkill);
        Optional.ofNullable(updateParam.getHighSkill()).ifPresent(ch::setHighSkill);

        ch.setAside(updateParam.getAside());

        // 캐릭터 이미지 정보 저장
        // 변경하지 않는 이미지 경로는 인자를 null로 전달
        imagePathService.saveCharacterImages(characterId, chImagePaths);
        if (asideImagePaths != null) {
            imagePathService.saveAsideImages(characterId, asideImagePaths);
        }

        return ch.getId();
    }

    @Transactional
    public void activateCharacter(Long characterId) {
        Character ch = findOne(characterId);
        ch.setStatus(CharacterStatus.ACTIVE);
    }

    @Transactional
    public void disableCharacter(Long characterId) {
        Character ch = findOne(characterId);
        ch.setStatus(CharacterStatus.DISABLED);
    }

    @Transactional
    public void deleteCharacter(Long characterId) {
        Character ch = findOne(characterId);
        ch.setStatus(CharacterStatus.DELETED);

        Optional<Card> findCard = cardRepository.findOneArtifactByCharacterId(characterId);

        // 그럼 아티팩트 카드 추가할 때도 이미 애착 사도가 있는지 확인해야 하나? 아티팩트 카드에 @OneToOne이 걸려있기는 함.

        // 애착 아티팩트 카드가 있을 경우에만 제거
        if (findCard.isPresent()) {
            Card artifactCard = findCard.get();
            artifactCard.removeCharacter(); // 애착 사도, 애착 스킬 제거
            cardRepository.save(artifactCard);
        }

        // 스킨 삭제 로직
        SkinSearchCond skinSearchCond = new SkinSearchCond();
        skinSearchCond.setCharacterId(characterId);
        List<Skin> findSkins = skinRepository.findAll(skinSearchCond);
        for (Skin skin : findSkins) {
            // 스킨이 해당하는 카테고리들을 조회
            List<SkinCategory> skinCategories = skinCategoryRepository.findBySkinId(skin.getId());

            // 해당 카테고리들을 제거
            for (SkinCategory category : skinCategories) {
                skin.removeCategory(category);
            }

            // 스킨의 상태를 DELETED로 변경
            skin.setStatus(SkinStatus.DELETED);
        }

    }

    // 캐릭터 검색 기능
    public Character findOne(Long id) {
        Optional<Character> find = characterRepository.findOne(id);
        if(find.isEmpty()) {
            throw new NoSuchElementException("해당 캐릭터가 없습니다. id=" + id);
        }
        return find.get();
    }

    public Character findOneActive(Long id) {
        Character character = findOne(id);
        if (character.getStatus() != CharacterStatus.ACTIVE) {
            throw new NoSuchElementException("해당 캐릭터가 없습니다. id=" + id);
        }
        return character;
    }

    public List<Character> findAll() {
        CharacterSearchCond searchCond = new CharacterSearchCond();
        return characterRepository.findAll(searchCond);
    }

    public List<Character> findAll(CharacterSearchCond searchCond) {
        return characterRepository.findAll(searchCond);
    }

}
