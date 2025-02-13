package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.guide.UserGuide;
import hdxian.monatium_darknet.domain.guide.UserGuideCategory;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.exception.IllegalLangCodeException;
import hdxian.monatium_darknet.exception.notice.NoticeCategoryNotFoundException;
import hdxian.monatium_darknet.exception.notice.NoticeImageProcessException;
import hdxian.monatium_darknet.file.FileDto;
import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.repository.UserGuideCategoryRepository;
import hdxian.monatium_darknet.repository.UserGuideRepository;
import hdxian.monatium_darknet.repository.dto.UserGuideSearchCond;
import hdxian.monatium_darknet.service.dto.UserGuideDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserGuideService {

    private final UserGuideRepository userGuideRepository;
    private final UserGuideCategoryRepository categoryRepository;

    private final LocalFileStorageService fileStorageService;

    private final ImagePathService imagePathService;
    private final ImageUrlService imageUrlService;

    private final HtmlContentUtil htmlContentUtil;

    @Transactional
    public Long createNewUserGuide(UserGuideDto guideDto) {

        LangCode langCode = guideDto.getLangCode();
        String title = guideDto.getTitle();
        String content = guideDto.getContent();

        Long categoryId = guideDto.getCategoryId();
        Optional<UserGuideCategory> findCategory = categoryRepository.findOne(categoryId);
        if (findCategory.isEmpty()) {
            throw new RuntimeException("해당 가이드 카테고리가 없습니다.");
        }

        UserGuideCategory userGuideCategory = findCategory.get();
        if (langCode != userGuideCategory.getLangCode()) {
            throw new RuntimeException("가이드 카테고리의 언어 코드가 맞지 않습니다.");
        }

        // html 콘텐츠 필터링 (이상한 태그, 요소 제거)
        String cleanedContent = htmlContentUtil.cleanHtmlContent(content);

        // 1. 우선 가이드를 저장해 id 획득
        UserGuide userGuide = UserGuide.createUserGuide(langCode, userGuideCategory, title, cleanedContent);
        Long guideId = userGuideRepository.save(userGuide);

        // 2. 가이드라인 본문에서 img 태그들의 src 속성들을 추출
        List<String> imgSrcs = htmlContentUtil.getImgSrc(cleanedContent);

        // 3. 이미지 속성이 1개이상 존재할 경우, 파일 수정 작업 수행
        if (!imgSrcs.isEmpty()) {
            List<String> changedImgSrcs;

            changedImgSrcs = moveUserGuideImageFiles(guideId, imgSrcs);

            String baseSrc = imageUrlService.getUserGuideImageBaseUrl() + (guideId + "/");
            String updatedContent = htmlContentUtil.updateImgSrc(cleanedContent, baseSrc, changedImgSrcs);

            userGuide.setContent(updatedContent);
        }

        return userGuide.getId();
    }

    @Transactional
    public Long updateUserGuide(Long guideId, UserGuideDto updateParam) {
        UserGuide userGuide = findOne(guideId);

        if (userGuide.getLangCode() != updateParam.getLangCode()) {
            throw new IllegalLangCodeException("언어 코드가 맞지 않습니다.");
        }

        // 변경하려는 카테고리 유무 검증
        Long categoryId = updateParam.getCategoryId();
        Optional<UserGuideCategory> findCategory = categoryRepository.findOne(categoryId);
        if (findCategory.isEmpty()) {
            throw new NoticeCategoryNotFoundException("해당 가이드 카테고리가 없습니다.");
        }

        // 공지사항-카테고리 간 언어코드 일치 검증
        UserGuideCategory category = findCategory.get();
        if (userGuide.getLangCode() != category.getLangCode()) {
            throw new IllegalLangCodeException("카테고리와 가이드의 언어 코드가 맞지 않습니다. Category LangCode = " + category.getLangCode() + ", UserGuide LangCode = " + userGuide.getLangCode());
        }

        userGuide.setCategory(category);
        userGuide.setTitle(updateParam.getTitle());

        // 업데이트할 html 콘텐츠. userGuide에 저장된 경로, temp에 저장된 경로 모두 있음
        String cleanedContent = htmlContentUtil.cleanHtmlContent(updateParam.getContent());

        // 가이드 본문에서 src 속성들 추출
        List<String> imgSrcs = htmlContentUtil.getImgSrc(cleanedContent);

        if (!imgSrcs.isEmpty()) {
            List<String> changedFileNames;

            changedFileNames = moveUserGuideImageFiles(guideId, imgSrcs);

            // img 태그의 src에 사용할 url. 서버 스토리지에 저장되는 경로와 다름.
            String baseSrc = imageUrlService.getUserGuideImageBaseUrl() + (guideId + "/");
            String updatedContent = htmlContentUtil.updateImgSrc(cleanedContent, baseSrc, changedFileNames);
            userGuide.setContent(updatedContent);
        }
        else {
            // 변경할 이미지가 없으면 그대로 html 본문만 업데이트
            userGuide.setContent(cleanedContent);
        }

        // 업데이트 시간으로 변경
        userGuide.setDate(LocalDateTime.now());

        return userGuide.getId();
    }

    public UserGuide findOne(Long guideId) {
        Optional<UserGuide> find = userGuideRepository.findOne(guideId);
        if (find.isEmpty()) {
            throw new RuntimeException("해당 가이드를 찾을 수 없습니다.");
        }
        return find.get();
    }

    // 조건부 검색
    public List<UserGuide> findAll(UserGuideSearchCond searchCond) {
        return userGuideRepository.findAll(searchCond);
    }

    // 조건 없이 모두 검색
    public List<UserGuide> findAll() {
        return userGuideRepository.findAll(new UserGuideSearchCond());
    }

    public List<String> moveUserGuideImageFiles(Long guideId, List<String> imgSrcs) {
        String tempDir = fileStorageService.getTempDir();

        List<String> changedFileNames = new ArrayList<>();

        // 공지사항을 저장할 폴더 경로
        String targetDir = imagePathService.getUserGuideDir(guideId);

        int seq = 1;
        String fileName, ext, saveFileName;
        FileDto from, to;

        // src = "/api/images/o2p2aRmhWArow8cHh5x9_awsec2_logo.png"
        for (String src : imgSrcs) {
            fileName = fileStorageService.extractFileName(src); // fileName = "o2p2aRmhWArow8cHh5x9_awsec2_logo.png"
            ext = fileStorageService.extractExt(fileName); // ext = ".png"
            saveFileName = String.format("img_%02d%s", seq, ext); // saveFileName = "img_01.png"

            // url이 /api로 시작하면 temp 경로에 있는 파일임
            if (src.startsWith("/api/images/tmp")) {
                from = new FileDto(tempDir, fileName); // from : temp 폴더에 저장돼있는 "o2p2aRmhWArow8cHh5x9_awsec2_logo.png" 이라는 이름의 파일
                to = new FileDto(targetDir, saveFileName); // to : noticeBaseDir에 noticeId를 붙여 만든 경로에 "img_01.png" 라는 이름의 파일(로 저장하겠다)
            }
            else { // 그 외에는 기존 공지사항 폴더에 있던 파일. 파일명 변경해야 함.
                from = new FileDto(targetDir, fileName); // from : 기존 noticeBaseDir에 저장돼있던 "img_01.png" 라는 이름의 파일
                to = new FileDto(targetDir, saveFileName); // to : 기존 noticeBaseDir에 "img_02.png" 등으로 숫자를 바꾸어 다시 저장(하겠다)
            }

            changedFileNames.add(to.getFileName()); // add("img_#.ext")

            try {
                fileStorageService.copyFile(from, to);
            } catch (IOException e) {
                throw new NoticeImageProcessException(e);
            }

            seq++;
        }

        return changedFileNames;
    }


    // === UserGuideCategory ===
    @Transactional
    public Long createNewUserGuideCategory(LangCode langCode, String name) {
        UserGuideCategory category = UserGuideCategory.createUserGuideCategory(langCode, name);
        return categoryRepository.save(category);
    }

    @Transactional
    public Long updateUserGuideCategory(Long categoryId, String name) {
        UserGuideCategory category = findOneCategory(categoryId);
        category.setName(name);

        return category.getId();
    }

    public UserGuideCategory findOneCategory(Long categoryId) {
        Optional<UserGuideCategory> find = categoryRepository.findOne(categoryId);
        if (find.isEmpty()) {
            throw new RuntimeException("해당 가이드 카테고리를 찾을 수 없습니다.");
        }
        return find.get();
    }

    public List<UserGuideCategory> findCategoriesByLangCode(LangCode langCode) {
        return categoryRepository.findByLangCode(langCode);
    }

    public List<UserGuideCategory> findAllUserGuideCategories() {
        return categoryRepository.findAll();
    }

}
