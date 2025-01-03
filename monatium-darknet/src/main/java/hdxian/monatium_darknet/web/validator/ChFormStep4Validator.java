package hdxian.monatium_darknet.web.validator;

import hdxian.monatium_darknet.domain.Attribute;
import hdxian.monatium_darknet.web.controller.management.character.ChFormStep4;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class ChFormStep4Validator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ChFormStep4.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ChFormStep4 chForm = (ChFormStep4) target;

        // 어사이드 활성화 안 돼있으면 검증 건너 뜀.
        if (!chForm.isEnableAside()) {
            return;
        }

        // asideName, asideDescription에 대한 @NotBlank
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "asideName", "NotBlank");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "asideDescription", "NotBlank");

        // asideLv1Name, asideLv1Description에 대한 @NotBlank
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "asideLv1Name", "NotBlank");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "asideLv1Description", "NotBlank");

        List<Attribute> asideLv1Attributes = chForm.getAsideLv1Attributes();

        if (asideLv1Attributes == null || asideLv1Attributes.isEmpty()) {
            errors.rejectValue("asideLv1Attributes", "NotEmpty.attributes", new String[] {"어사이드 1레벨"}, "어사이드 1레벨 특성은 하나 이상 존재해야 합니다.");
        }

        if (asideLv1Attributes != null) {
            for(int i=0; i<asideLv1Attributes.size(); i++) {
                Attribute attribute = asideLv1Attributes.get(i);
                String attrName = attribute.getAttrName();
                String attrValue = attribute.getAttrValue();

                if (attrName == null || attrName.isBlank()) {
                    errors.rejectValue("asideLv1Attributes[" + i + "].attrName", "NotBlank.attrName", "속성 이름은 비어있을 수 없습니다.");
                }

                if (attrValue == null || attrValue.isBlank()) {
                    errors.rejectValue("asideLv1Attributes[" + i + "].attrValue", "NotBlank.attrValue", "속성 값은 비어있을 수 없습니다.");
                }

            }
        }

        // asideLv2Name, asideLv2Description에 대한 @NotBlank
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "asideLv2Name", "NotBlank");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "asideLv2Description", "NotBlank");

        List<Attribute> asideLv2Attributes = chForm.getAsideLv2Attributes();

        if (asideLv2Attributes == null || asideLv2Attributes.isEmpty()) {
            errors.rejectValue("asideLv2Attributes", "NotEmpty.attributes", new String[] {"어사이드 2레벨"}, "어사이드 2레벨 특성은 하나 이상 존재해야 합니다.");
        }

        if (asideLv2Attributes != null) {
            for(int i=0; i<asideLv2Attributes.size(); i++) {
                Attribute attribute = asideLv2Attributes.get(i);
                String attrName = attribute.getAttrName();
                String attrValue = attribute.getAttrValue();

                if (attrName == null || attrName.isBlank()) {
                    errors.rejectValue("asideLv2Attributes[" + i + "].attrName", "NotBlank.attrName", "속성 이름은 비어있을 수 없습니다.");
                }

                if (attrValue == null || attrValue.isBlank()) {
                    errors.rejectValue("asideLv2Attributes[" + i + "].attrValue", "NotBlank.attrValue", "속성 값은 비어있을 수 없습니다.");
                }

            }
        }

        // asideLv3Name, asideLv3Description에 대한 @NotBlank
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "asideLv3Name", "NotBlank");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "asideLv3Description", "NotBlank");

        List<Attribute> asideLv3Attributes = chForm.getAsideLv3Attributes();
        if (asideLv3Attributes == null || asideLv3Attributes.isEmpty()) {
            errors.rejectValue("asideLv3Attributes", "NotEmpty.attributes", new String[] {"어사이드 3레벨"}, "어사이드 3레벨 특성은 하나 이상 존재해야 합니다.");
        }

        if (asideLv3Attributes != null) {
            for(int i=0; i<asideLv3Attributes.size(); i++) {
                Attribute attribute = asideLv3Attributes.get(i);
                String attrName = attribute.getAttrName();
                String attrValue = attribute.getAttrValue();

                if (attrName == null || attrName.isBlank()) {
                    errors.rejectValue("asideLv3Attributes[" + i + "].attrName", "NotBlank.attrName", "속성 이름은 비어있을 수 없습니다.");
                }

                if (attrValue == null || attrValue.isBlank()) {
                    errors.rejectValue("asideLv3Attributes[" + i + "].attrValue", "NotBlank.attrValue", "속성 값은 비어있을 수 없습니다.");
                }

            }
        }

    }

}
