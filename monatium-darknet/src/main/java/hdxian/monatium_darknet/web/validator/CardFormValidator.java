package hdxian.monatium_darknet.web.validator;

import hdxian.monatium_darknet.domain.Attribute;
import hdxian.monatium_darknet.web.controller.management.card.CardForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class CardFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CardForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        CardForm cardForm = (CardForm) target;

        // 카드 특성 검증
        List<Attribute> cardAttributes = cardForm.getCardAttributes();
        if (cardAttributes == null || cardAttributes.isEmpty()) {
            errors.rejectValue("cardAttributes", "NotEmpty.attributes", "카드 특성은 하나 이상 존재해야 합니다.");
        }

        // 카드 특성 검증 (빈 값 허용 x)
        if (cardAttributes != null) {
            for (int i=0; i< cardAttributes.size(); i++) {
                Attribute cardAttribute = cardAttributes.get(i);
                String attrName = cardAttribute.getAttrName();
                String attrValue = cardAttribute.getAttrValue();

                if (attrName == null || attrName.isBlank()) {
                    errors.rejectValue("cardAttributes[" + i + "].attrName", "NotBlank.attrName", new String[] {"속성 이름"}, "속성 이름은 비어있을 수 없습니다.");
                }

                if (attrValue == null || attrValue.isBlank()) {
                    errors.rejectValue("cardAttributes[" + i + "].attrValue", "NotBlank.attrValue", new String[] {"속성 값"}, "속성 값은 비어있을 수 없습니다.");
                }

            }
        }

        // 애착 사도가 있을 경우에만 검증을 수행
        if (!cardForm.isHasAttachment()) {
            return;
        }

        // 애착 사도가 있을 경우 (hasAttachment == true)
        if (cardForm.getCharacterId() == null)
            errors.rejectValue("characterId", "NotNull", "캐릭터 id는 널이어서는 안됩니다. (디폴트 메시지)");

        // 애착 아티팩트 스킬 정보 필드 검증
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "attachmentSkillName", "NotBlank");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "attachmentSkillDescription", "NotBlank");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "attachmentLv3Description", "NotBlank");

        // 애착 아티팩트 스킬 특성 검증
        List<Attribute> attachmentAttributes = cardForm.getAttachmentAttributes();
        if (attachmentAttributes == null || attachmentAttributes.isEmpty()) {
            errors.rejectValue("attachmentAttributes", "NotEmpty.attributes", "애착 아티팩트 스킬 특성은 하나 이상 존재해야 합니다.");
        }

        // 애착 아티팩트 특성 검증 (빈 값 허용 x)
        if (attachmentAttributes != null) {
            for (int i=0; i< attachmentAttributes.size(); i++) {
                Attribute attachmentAttribute = attachmentAttributes.get(i);
                String attrName = attachmentAttribute.getAttrName();
                String attrValue = attachmentAttribute.getAttrValue();

                if (attrName == null || attrName.isBlank()) {
                    errors.rejectValue("attachmentAttributes[" + i + "].attrName", "NotBlank", new String[] {"속성 이름", String.valueOf(i)}, "속성 이름은 비어있을 수 없습니다.");
                }

                if (attrValue == null || attrValue.isBlank()) {
                    errors.rejectValue("attachmentAttributes[" + i + "].attrValue", "NotBlank", new String[] {"속성 값", String.valueOf(i)}, "속성 값은 비어있을 수 없습니다.");
                }

            }
        }


    }

}
