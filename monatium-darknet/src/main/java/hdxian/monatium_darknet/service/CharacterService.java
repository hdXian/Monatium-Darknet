package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.Attribute;
import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.SkillCategory;
import hdxian.monatium_darknet.domain.card.ArtifactCard;
import hdxian.monatium_darknet.domain.character.Attack;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.repository.CardRepository;
import hdxian.monatium_darknet.repository.CharacterRepository;
import hdxian.monatium_darknet.service.dto.CharacterDto;
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

    // 캐릭터 추가 기능
    @Transactional
    public Long createNewCharacter(CharacterDto chDto) {
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
                chDto.getAside(),
                chDto.getUrls()
        );
        return characterRepository.save(ch);
    }

    // 캐릭터 업데이트
    @Transactional
    public Long updateCharacter(Long characterId, CharacterDto updateParam) {
        Character ch = findOne(characterId);

        ch.setName(updateParam.getName());
        ch.setSubtitle(updateParam.getSubtitle());
        ch.setCv(updateParam.getCv());
        ch.setGrade(updateParam.getGrade());
        ch.setQuote(updateParam.getQuote());
        ch.setTmi(updateParam.getTmi());
        ch.setFavorite(updateParam.getFavorite());
        ch.setRace(updateParam.getRace());
        ch.setPersonality(updateParam.getPersonality());
        ch.setRole(updateParam.getRole());
        ch.setAttackType(updateParam.getAttackType());
        ch.setPosition(updateParam.getPosition());
        ch.setStat(updateParam.getStat());

        // 기존 객체를 수정
        updateAttack(ch.getNormalAttack(), updateParam.getNormalAttack());
        updateAttack(ch.getEnhancedAttack(), updateParam.getEnhancedAttack());

        updateSkill(ch.getLowSkill(), updateParam.getLowSKill());
        updateSkill(ch.getHighSkill(), updateParam.getHighSkill());

        // 어사이드는 구조가 더 복잡해서 그냥 객체를 갈아끼운 다음 orphanRemoval = true로 기존 객체를 삭제하도록 설정함
        // 기존 객체의 데이터들을 변경해서 업데이트 시키는 것 <-> 아예 새로운 객체로 갈아 끼우는 것 사이의 차이점 숙지해야 함
        ch.setAside(updateParam.getAside());
        ch.setUrls(updateParam.getUrls());

//        return characterRepository.save(ch);
        return ch.getId(); // ***중요 -> em.find()를 통해 찾아온 엔티티는 merge로 업데이트하면 안됨. (이해는 안됨. 추가 학습 필요)
    }

    // 스킬 변경
    private static void updateSkill(Skill skill, Skill updateParam) {
        skill.setName(updateParam.getName());
        skill.setDescription(updateParam.getDescription());
        skill.setImageUrl(updateParam.getImageUrl());

        if(skill.getCategory() == SkillCategory.HIGH) {
            skill.setCooldown(updateParam.getCooldown());
        }

        List<Attribute> attributes = skill.getAttributes();
        attributes.clear();
        attributes.addAll(updateParam.getAttributes());
    }

    // 공격 변경
    private static void updateAttack(Attack attack, Attack updateParam) {
        attack.setDescription(updateParam.getDescription());
        List<Attribute> attributes = attack.getAttributes();
        attributes.clear();
        attributes.addAll(updateParam.getAttributes());
    }

    @Transactional
    public void deleteCharacter(Long characterId) {
        Optional<ArtifactCard> findCard = cardRepository.findOneArtifactByCharacterId(characterId);

        // 그럼 아티팩트 카드 추가할 때도 이미 애착 사도가 있는지 확인해야 하나? 아티팩트 카드에 @OneToOne이 걸려있기는 함.

        // 애착 아티팩트 카드가 있을 경우에만 제거
        if (findCard.isPresent()) {
            ArtifactCard artifactCard = findCard.get();
            artifactCard.removeCharacter(); // 애착 사도, 애착 스킬 제거
            cardRepository.save(artifactCard);
        }

        characterRepository.delete(characterId);
    }

    // 캐릭터 검색 기능
    public Character findOne(Long id) {
        Optional<Character> find = characterRepository.findOne(id);
        if(find.isEmpty()) {
            throw new NoSuchElementException("해당 캐릭터가 없습니다. id=" + id);
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
