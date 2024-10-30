package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.skin.SkinCategory;
import hdxian.monatium_darknet.repository.SkinCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SkinCategoryService {

    // 지금 SkinCategory에는 별도의 SKin 추가 등의 기능이 없음

    private final SkinCategoryRepository categoryRepository;

    // 카테고리 추가
    // 카테고리에는 여러 스킨이 속할 수 있음
    @Transactional
    public Long createNewSkinCategory(String name) {
        SkinCategory skinCategory = SkinCategory.createSkinCategory(name);
        return categoryRepository.save(skinCategory);
    }

    // 카테고리 검색
    public SkinCategory findOne(Long skinCategoryId) {
        return categoryRepository.findOne(skinCategoryId);
    }

    // 스킨 기반 카테고리 검색
    public List<SkinCategory> findBySkin(Long skinId) {
        return categoryRepository.findBySkin(skinId);
    }

}
