package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.notice.*;
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

    private final HtmlContentUtil htmlContentUtil;

    @Value("${file.noticeDir}")
    private String noticeBaseDir;

    // 공지사항 추가 기능
    @Transactional
    public Long createNewNotice(Long memberId, NoticeDto noticeDto) {

        Member member = memberService.findOne(memberId);
        String title = noticeDto.getTitle();

        Long categoryId = noticeDto.getCategoryId();
        Optional<NoticeCategory> findCategory = noticeCategoryRepository.findOne(categoryId);
        if (findCategory.isEmpty()) {
            throw new RuntimeException("해당 공지사항 카테고리가 없습니다.");
        }
        NoticeCategory category = findCategory.get();

        String htmlContent = htmlContentUtil.cleanHtmlContent(noticeDto.getContent());

        // 1. 우선 공지사항을 저장해 ID 획득
        Notice notice = Notice.createNotice(member, category, title, htmlContent);
        Long noticeId = noticeRepository.save(notice);

        // 2. 공지사항 본문에서 img 태그들의 src 속성들을 추출 ("/api/images/abcdef.png")
        List<String> imgSrcs = htmlContentUtil.getImgSrc(htmlContent);

        // 3. 이미지 속성이 있을 경우에만 파일 수정 작업을 수행
        if (!imgSrcs.isEmpty()) {
            // 3. 추출한 src를 바탕으로 이미지 파일명 수정 및 경로 변경 (서버 내 경로)
            // {basePath}/temp/abcdef.png -> {basePath}}/notice/{noticeId}/img_01.png
            List<String> changedImgSrcs;

            changedImgSrcs = moveNoticeImageFiles(noticeId, imgSrcs);

            String baseSrc = "/notices/" + noticeId + "/images/";
            String updatedContent = htmlContentUtil.updateImgSrc(htmlContent, baseSrc, changedImgSrcs);

            notice.setContent(updatedContent);
        }

        return notice.getId();
    }

    @Transactional
    public Long updateNotice(Long noticeId, NoticeDto updateParam) {
        Notice notice = findOne(noticeId);

        Long categoryId = updateParam.getCategoryId();
        Optional<NoticeCategory> findCategory = noticeCategoryRepository.findOne(categoryId);
        if (findCategory.isEmpty()) {
            throw new RuntimeException("해당 공지사항 카테고리가 없습니다.");
        }
        NoticeCategory category = findCategory.get();
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
            String baseSrc = "/notices/" + noticeId + "/images/";
            String updatedContent = htmlContentUtil.updateImgSrc(htmlContent, baseSrc, changedFileNames);
            notice.setContent(updatedContent);
        }
        else {
            // 변경할 이미지가 없으면 그대로 html 본문만 업데이트
            notice.setContent(htmlContent);
        }

        // 업데이트 시간으로 변경
        notice.setDate(LocalDateTime.now());

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

    public String getNoticeImageUrl(Long noticeId, String imageName) {
        String noticeDir = getNoticeDir(noticeId);
        return fileStorageService.getFileFullPath(new FileDto(noticeDir, imageName));
    }

    // 임시 저장 경로에 있던 공지사항 이미지들을 정식 경로에 저장
    public List<String> moveNoticeImageFiles(Long noticeId, List<String> imgSrcs) {

        String tempDir = fileStorageService.getTempDir();

        List<String> changedFileNames = new ArrayList<>();

        // 공지사항을 저장할 폴더 경로
        String targetDir = getNoticeDir(noticeId);

        int seq = 1;
        String fileName, ext, saveFileName;
        FileDto from, to;

        // src = "/api/images/o2p2aRmhWArow8cHh5x9_awsec2_logo.png"
        for (String src : imgSrcs) {
            fileName = fileStorageService.extractFileName(src); // fileName = "o2p2aRmhWArow8cHh5x9_awsec2_logo.png"
            ext = fileStorageService.extractExt(fileName); // ext = ".png"
            saveFileName = String.format("img_%02d%s", seq, ext); // saveFileName = "img_01.png"

            // url이 /api로 시작하면 temp 경로에 있는 파일임
            if (src.startsWith("/api")) {
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

    private String getNoticeDir(Long noticeId) {
        return noticeBaseDir + (noticeId + "/");
    }


    // === NoticeCategory ===
    @Transactional
    public Long createNewNoticeCategory(String name) {
        NoticeCategory category = NoticeCategory.createNoticeCategory(name);
        return noticeCategoryRepository.save(category);
    }

    @Transactional
    public Long updateNoticeCategory(Long categoryId, String name, NoticeCategoryStatus status) {
        NoticeCategory category = findOneCategory(categoryId);
        category.setName(name);
        category.setStatus(status);

        return category.getId();
    }

    public NoticeCategory findOneCategory(Long categoryId) {
        Optional<NoticeCategory> find = noticeCategoryRepository.findOne(categoryId);
        if (find.isEmpty()) {
            throw new RuntimeException("해당 공지사항 카테고리가 없습니다. id=" + categoryId);
        }
        return find.get();
    }

    public List<NoticeCategory> findAllNoticeCategories() {
        return noticeCategoryRepository.findAll();
    }

}
