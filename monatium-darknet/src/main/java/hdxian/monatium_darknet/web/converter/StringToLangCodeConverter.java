package hdxian.monatium_darknet.web.converter;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.exception.IllegalLangCodeException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StringToLangCodeConverter implements Converter<String, LangCode> {

    // 컨버팅 실패 시 IllegalArgumentException(커스텀 예외) throw
    @Override
    public LangCode convert(String source) {
        try {
            return LangCode.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalLangCodeException(e);
        }
    }

}
