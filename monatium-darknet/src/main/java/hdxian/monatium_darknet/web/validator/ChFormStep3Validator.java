package hdxian.monatium_darknet.web.validator;

import hdxian.monatium_darknet.domain.Attribute;
import hdxian.monatium_darknet.web.controller.management.character.ChFormStep3;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class ChFormStep3Validator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ChFormStep3.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ChFormStep3 chForm = (ChFormStep3) target;

        List<Attribute> normalAttributes = chForm.getNormalAttributes();
        List<Attribute> lowSkillAttributes = chForm.getLowSkillAttributes();
        List<Attribute> highSkillAttributes = chForm.getHighSkillAttributes();

        if (normalAttributes == null || normalAttributes.isEmpty()) {
            errors.rejectValue("normalAttributes", "NotEmpty.attributes", new String[] {"일반 공격"}, "일반 공격 특성은 하나 이상 존재해야 합니다.");
        }

        if (lowSkillAttributes == null || lowSkillAttributes.isEmpty()) {
            errors.rejectValue("lowSkillAttributes", "NotEmpty.attributes", new String[] {"저학년 스킬"}, "저학년 스킬 특성은 하나 이상 존재해야 합니다.");
        }

        if (highSkillAttributes == null || highSkillAttributes.isEmpty()) {
            errors.rejectValue("highSkillAttributes", "NotEmpty.attributes", new String[] {"고학년 스킬"}, "고학년 스킬 특성은 하나 이상 존재해야 합니다.");
        }

        if (normalAttributes != null) {
            for(int i=0; i<normalAttributes.size(); i++) {
                Attribute attribute = normalAttributes.get(i);
                String attrName = attribute.getAttrName();
                String attrValue = attribute.getAttrValue();

                if (attrName == null || attrName.isBlank()) {
                    errors.rejectValue("normalAttributes[" + i + "].attrName", "NotBlank.attrName", "속성 이름은 비어있을 수 없습니다.");
                }

                if (attrValue == null || attrValue.isBlank()) {
                    errors.rejectValue("normalAttributes[" + i + "].attrValue", "NotBlank.attrValue", "속성 값은 비어있을 수 없습니다.");
                }

            }
        }

        if (lowSkillAttributes != null) {
            for(int i=0; i<lowSkillAttributes.size(); i++) {
                Attribute attribute = lowSkillAttributes.get(i);
                String attrName = attribute.getAttrName();
                String attrValue = attribute.getAttrValue();

                if (attrName == null || attrName.isBlank()) {
                    errors.rejectValue("lowSkillAttributes[" + i + "].attrName", "NotBlank.attrName", "속성 이름은 비어있을 수 없습니다.");
                }

                if (attrValue == null || attrValue.isBlank()) {
                    errors.rejectValue("lowSkillAttributes[" + i + "].attrValue", "NotBlank.attrValue", "속성 값은 비어있을 수 없습니다.");
                }

            }
        }

        if (highSkillAttributes != null) {
            for(int i=0; i<highSkillAttributes.size(); i++) {
                Attribute attribute = highSkillAttributes.get(i);
                String attrName = attribute.getAttrName();
                String attrValue = attribute.getAttrValue();

                if (attrName == null || attrName.isBlank()) {
                    errors.rejectValue("highSkillAttributes[" + i + "].attrName", "NotBlank.attrName", "속성 이름은 비어있을 수 없습니다.");
                }

                if (attrValue == null || attrValue.isBlank()) {
                    errors.rejectValue("highSkillAttributes[" + i + "].attrValue", "NotBlank.attrValue", "속성 값은 비어있을 수 없습니다.");
                }

            }
        }

        // 강화 공격 여부는 enableEnhancedAttack 여부에 따라 결정
        if (!chForm.isEnableEnhancedAttack()) {
            return;
        }

        List<Attribute> enhancedAttributes = chForm.getEnhancedAttributes();
        if (enhancedAttributes == null || enhancedAttributes.isEmpty()) {
            errors.rejectValue("enhancedAttributes", "NotEmpty.attributes", new String[] {"강화 공격"}, "강화 공격 특성은 하나 이상 존재해야 합니다.");
        }

        if (enhancedAttributes != null) {
            for(int i=0; i<enhancedAttributes.size(); i++) {
                Attribute attribute = enhancedAttributes.get(i);
                String attrName = attribute.getAttrName();
                String attrValue = attribute.getAttrValue();

                if (attrName == null || attrName.isBlank()) {
                    errors.rejectValue("enhancedAttributes[" + i + "].attrName", "NotBlank.attrName", "속성 이름은 비어있을 수 없습니다.");
                }

                if (attrValue == null || attrValue.isBlank()) {
                    errors.rejectValue("enhancedAttributes[" + i + "].attrValue", "NotBlank.attrValue", "속성 값은 비어있을 수 없습니다.");
                }

            }
        }

    }

}
