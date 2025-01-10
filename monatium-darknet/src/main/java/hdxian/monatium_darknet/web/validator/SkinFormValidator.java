package hdxian.monatium_darknet.web.validator;

import hdxian.monatium_darknet.web.controller.management.skin.SkinForm;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class SkinFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return SkinForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SkinForm skinForm = (SkinForm) target;

        List<Long> categoryIds = skinForm.getCategoryIds();
        if (categoryIds == null || categoryIds.isEmpty()) {
            errors.rejectValue("categoryIds", "NotEmpty.categoryIds", new String[]{"스킨 카테고리"}, "스킨 카테고리를 하나 이상 지정해야 합니다.");
        }

        // 각 카테고리 ID들에 대한 널 체크
        if (categoryIds != null) {
            for (int i=0; i< categoryIds.size(); i++) {
                if (categoryIds.get(i) == null) {
                    errors.rejectValue("categoryIds[" + i + "]", "NotNull.categoryId", new String[]{"카테고리 ID"}, "유효한 카테고리 ID를 입력해주세요.");
                }
            }
        }

    }

}
