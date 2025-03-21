package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.notice.*;
import hdxian.monatium_darknet.exception.IllegalLangCodeException;
import hdxian.monatium_darknet.exception.notice.NoticeCategoryNotFoundException;
import hdxian.monatium_darknet.exception.notice.NoticeImageProcessException;
import hdxian.monatium_darknet.exception.notice.NoticeNotFoundException;
import hdxian.monatium_darknet.file.FileDto;
import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.repository.NoticeCategoryRepository;
import hdxian.monatium_darknet.repository.NoticeRepository;
import hdxian.monatium_darknet.repository.dto.NoticeSearchCond;
import hdxian.monatium_darknet.service.dto.NoticeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeCategoryRepository noticeCategoryRepository;
    private final MemberService memberService;
    private final LocalFileStorageService fileStorageService;

    private final ImagePathService imagePathService;
    private final ImageUrlService imageUrlService;

    private final HtmlContentUtil htmlContentUtil;

    // 공지사항 추가 기능
    @Transactional
    public Long createNewNotice(Long memberId, NoticeDto noticeDto, String thumbnailFilePath) {

        Member member = memberService.findOne(memberId);
        String title = noticeDto.getTitle();
        LangCode langCode = noticeDto.getLangCode();

        Long categoryId = noticeDto.getCategoryId();
        Optional<NoticeCategory> findCategory = noticeCategoryRepository.findOne(categoryId);
        if (findCategory.isEmpty()) {
            throw new NoticeCategoryNotFoundException("해당 공지사항 카테고리가 없습니다.");
        }

        NoticeCategory category = findCategory.get();
        if (langCode != category.getLangCode()) {
            throw new IllegalLangCodeException("공지사항 카테고리의 언어 코드가 맞지 않습니다. Category LangCode = " + category.getLangCode() + ", Notice LangCode = " + langCode);
        }

        String htmlContent = htmlContentUtil.cleanHtmlContent(noticeDto.getContent()); // html 콘텐츠 필터링

        // 1. 우선 공지사항을 저장해 ID 획득
        Notice notice = Notice.createNotice(langCode, member, category, title, htmlContent);
        Long noticeId = noticeRepository.save(notice);

        // 2. 공지사항 본문에서 img 태그들의 src 속성들을 추출 ("/api/images/abcdef.png")
        List<String> imgSrcs = htmlContentUtil.getImgSrc(htmlContent);

        // 3. 이미지 속성이 있을 경우에만 파일 수정 작업을 수행
        if (!imgSrcs.isEmpty()) {
            // 3. 추출한 src를 바탕으로 이미지 파일명 수정 및 경로 변경 (서버 내 경로)
            // {basePath}/temp/abcdef.png -> {basePath}}/notices/{noticeId}/img_01.png
            List<String> changedImgSrcs;

            changedImgSrcs = moveNoticeImageFiles(noticeId, imgSrcs);

            String baseSrc = imageUrlService.getNoticeImageBaseUrl() + (noticeId + "/");
            String updatedContent = htmlContentUtil.updateImgSrc(htmlContent, baseSrc, changedImgSrcs);

            notice.setContent(updatedContent);
        }

        // 썸네일 이미지를 저장해야 함. 인자로 이미지 파일 이름이 들어올텐데,
        // 이게 null이면 카테고리에 따라 기본 썸네일 이미지로 저장해야 함
        // null이 아니면 해당 이미지로 썸네일을 저장해야 함.
        String thumbnailFileName = imagePathService.saveNoticeThumbnail(notice.getId(), thumbnailFilePath); // thumbnail.ext
        notice.setThumbnailFileName(thumbnailFileName);

        return notice.getId();
    }

    @Transactional
    public Long updateNotice(Long noticeId, NoticeDto updateParam, String thumbnailFilePath) {
        Notice notice = findOne(noticeId);

        if (notice.getLangCode() != updateParam.getLangCode()) {
            throw new IllegalLangCodeException("수정하는 공지사항의 언어 코드가 맞지 않습니다. id = " + notice.getId() + ", langCode = " + notice.getLangCode());
        }

        // 해당 카테고리 유무 검증
        Long categoryId = updateParam.getCategoryId();
        Optional<NoticeCategory> findCategory = noticeCategoryRepository.findOne(categoryId);
        if (findCategory.isEmpty()) {
            throw new NoticeCategoryNotFoundException("해당 공지사항 카테고리가 없습니다.");
        }

        // 공지사항-카테고리 간 언어코드 일치 검증
        NoticeCategory category = findCategory.get();
        if (notice.getLangCode() != category.getLangCode()) {
            throw new IllegalLangCodeException("카테고리와 공지사항의 언어 코드가 맞지 않습니다. Category LangCode = " + category.getLangCode() + ", Notice LangCode = " + notice.getLangCode());
        }

        notice.setCategory(category);

        notice.setTitle(updateParam.getTitle());

        // 업데이트할 html 콘텐츠. notice에 저장된 경로, temp에 저장된 경로 모두 있음
        String htmlContent = htmlContentUtil.cleanHtmlContent(updateParam.getContent());

        // 공지사항 본문에서 src 속성들 추출
        List<String> imgSrcs = htmlContentUtil.getImgSrc(htmlContent);

        if (!imgSrcs.isEmpty()) {
            List<String> changedFileNames;

            changedFileNames = moveNoticeImageFiles(noticeId, imgSrcs);

            // img 태그의 src에 사용할 url. 서버 스토리지에 저장되는 경로와 다름.
            String baseSrc = imageUrlService.getNoticeImageBaseUrl() + (noticeId + "/");
            String updatedContent = htmlContentUtil.updateImgSrc(htmlContent, baseSrc, changedFileNames);
            notice.setContent(updatedContent);
        }
        else {
            // 변경할 이미지가 없으면 그대로 html 본문만 업데이트
            notice.setContent(htmlContent);
        }

        // 업데이트 시간으로 변경
        notice.setDate(LocalDateTime.now());

        // 썸네일 이미지를 업데이트 해야함. null이면 업데이트 안함.
        if (thumbnailFilePath != null) {
            // 다른 imagePathService의 이미지 저장 메서드들과 다르게, saveNoticeThumbnail()는 이미지 경로로 null이 들어오면 기본 이미지를 저장해버림.
            // 공지사항은 썸네일 이미지를 지정하지 않으면 기본 썸네일을 적용한다는 규칙이 있어, 동작이 다르기 때문.
            // 공지사항 업데이트 로직은 업데이트 할 이미지가 없으면 기존 썸네일을 유지해야 하기 때문에, 기본 이미지로 저장하는 해당 메서드를 아예 호출하면 안 됨.
            // 여기서 메서드에 전달하는 thumbnailFilePath는 썸네일이 변경되어 임시저장 경로에 있는 이미지 뿐임.
            String thumbnailFileName = imagePathService.saveNoticeThumbnail(notice.getId(), thumbnailFilePath);
            notice.setThumbnailFileName(thumbnailFileName);
        }

        return notice.getId();
    }

    @Transactional
    public void incrementView(Long noticeId) {
        Notice notice = findOne(noticeId);
        notice.incrementView();
    }

    @Transactional
    public void publishNotice(Long noticeId) {
        Notice notice = findOne(noticeId);
        notice.setStatus(NoticeStatus.PUBLIC);
    }

    @Transactional
    public void unPublishNotice(Long noticeId) {
        Notice notice = findOne(noticeId);
        notice.setStatus(NoticeStatus.PRIVATE);
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        Notice notice = findOne(noticeId);
        notice.updateStatus(NoticeStatus.DELETED);
    }

    // 공지사항 조회 기능
    @Transactional
    public Notice findOne(Long noticeId) {
        Optional<Notice> find = noticeRepository.findOne(noticeId);
        if (find.isEmpty()) {
            throw new NoticeNotFoundException("해당 공지사항을 찾을 수 없습니다. noticeId = " + noticeId);
        }
        return find.get();
    }

    @Transactional
    public Notice findOnePublic(Long noticeId) {
        Optional<Notice> find = noticeRepository.findOne(noticeId);
        // 찾는게 없거나, 공지사항이 공개가 아니거나, 해당 카테고리가 공개가 아니면 결과를 리턴하지 않음.
        if (find.isEmpty() || find.get().getStatus() != NoticeStatus.PUBLIC || find.get().getCategory().getStatus() != NoticeCategoryStatus.PUBLIC) {
            throw new NoticeNotFoundException("해당 공지사항을 찾을 수 없습니다. noticeId = " + noticeId);
        }
        return find.get();
    }

    public List<Notice> findAll() {
        NoticeSearchCond searchCond = new NoticeSearchCond();
        return noticeRepository.findAll(searchCond);
    }

    public List<Notice> findAll(NoticeSearchCond searchCond) {
        return noticeRepository.findAll(searchCond);
    }

    public Page<Notice> findAll_Paging(NoticeSearchCond searchCond, Integer pageNumber) {
        int pageSize = 10; // 기본 페이지 사이즈는 10

        if (pageNumber < 1) pageNumber = 1;
        PageRequest request = PageRequest.of(pageNumber-1, pageSize);
        return noticeRepository.findAll(searchCond, request);
    }

    // 임시 저장 경로에 있던 공지사항 이미지들을 정식 경로에 저장
    public List<String> moveNoticeImageFiles(Long noticeId, List<String> imgSrcs) {

        String tempDir = fileStorageService.getTempDir();

        List<String> changedFileNames = new ArrayList<>();

        // 공지사항을 저장할 폴더 경로
        String targetDir = imagePathService.getNoticeDir(noticeId);

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


    // === NoticeCategory ===
    @Transactional
    public Long createNewNoticeCategory(LangCode langCode, String name) {
        NoticeCategory category = NoticeCategory.createNoticeCategory(langCode, name);
        return noticeCategoryRepository.save(category);
    }

    @Transactional
    public Long updateNoticeCategory(Long categoryId, String name, NoticeCategoryStatus status) {
        // 카테고리 업데이트할 때는 langCode를 아예 인자로 받지 않음.
        NoticeCategory category = findOneCategory(categoryId);
        category.setName(name);
        category.setStatus(status);

        return category.getId();
    }

    public NoticeCategory findOneCategory(Long categoryId) {
        Optional<NoticeCategory> find = noticeCategoryRepository.findOne(categoryId);
        if (find.isEmpty()) {
            throw new NoticeCategoryNotFoundException("해당 공지사항 카테고리가 없습니다. id=" + categoryId);
        }
        return find.get();
    }

    public List<NoticeCategory> findCategoriesByLangCode(LangCode langCode) {
        return noticeCategoryRepository.findByLangCode(langCode);
    }

    public List<NoticeCategory> findAllNoticeCategories() {
        return noticeCategoryRepository.findAll();
    }

}
