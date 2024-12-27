package hdxian.monatium_darknet.web.validator;

import hdxian.monatium_darknet.web.controller.management.character.ChFormStep4;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

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

        // asideLv2Name, asideLv2Description에 대한 @NotBlank
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "asideLv2Name", "NotBlank");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "asideLv2Description", "NotBlank");

        // asideLv3Name, asideLv3Description에 대한 @NotBlank
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "asideLv3Name", "NotBlank");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "asideLv3Description", "NotBlank");
    }

}
