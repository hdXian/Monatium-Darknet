package hdxian.monatium_darknet.web.validator;

import hdxian.monatium_darknet.web.controller.management.character.ChFormStep1;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class ChFormStep1Validator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ChFormStep1.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        ChFormStep1 chForm = (ChFormStep1) target;

        List<String> favorites = chForm.getFavorites();

        if (favorites == null || favorites.isEmpty()) {
            errors.rejectValue("favorites", "NotEmpty.favorites", "좋아하는 것을 하나 이상 입력해주세요.");
            return;
        }

        for(int i=0; i < favorites.size(); i++) {
            String favorite = favorites.get(i);
            if (favorite.isBlank())
                errors.rejectValue("favorites[" + i + "]", "NotBlank.favorite", "좋아하는 것을 입력해주세요.");
        }

    }

}
