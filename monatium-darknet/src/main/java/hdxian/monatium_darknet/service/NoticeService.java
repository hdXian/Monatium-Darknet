package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.controller.management.HtmlContentUtil;
import hdxian.monatium_darknet.domain.notice.Member;
import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.file.FileDto;
import hdxian.monatium_darknet.file.FileStorageService;
import hdxian.monatium_darknet.repository.NoticeRepository;
import hdxian.monatium_darknet.service.dto.NoticeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberService memberService;
    private final FileStorageService fileStorageService;

    private final HtmlContentUtil htmlContentUtil;

    @Value("${file.tempDir}")
    private String tempDir;

    @Value("${file.noticeDir}")
    private String noticeBaseDir;

    // TODO - 공지사항 리스트 불러올 시 페이징 추가 필요

    // 공지사항 추가 기능
    @Transactional
    public Long createNewNotice(Long memberId, NoticeDto noticeDto) {

        Member member = memberService.findOne(memberId);
        String title = noticeDto.getTitle();
        NoticeCategory category = noticeDto.getCategory();
        String htmlContent = noticeDto.getContent();

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
            try {
                changedImgSrcs = moveImagesFromTemp(noticeId, imgSrcs);
            } catch (IOException e) { // 파일 작업 중에 예외 터지면 롤백
                throw new IllegalArgumentException(e);
            }

            String baseUrl = "/notices/" + noticeId + "/images/";
            String updatedContent = htmlContentUtil.updateImgSrc(htmlContent, baseUrl, changedImgSrcs);

            notice.setContent(updatedContent);
        }

        return notice.getId();
    }

    @Transactional
    public Long updateNotice(Long noticeId, NoticeDto updateParam) {
        Notice notice = findOne(noticeId);

        notice.setCategory(updateParam.getCategory());
        notice.setTitle(updateParam.getTitle());

        // 업데이트할 html 콘텐츠. notice에 저장된 경로, temp에 저장된 경로 모두 있음
        String htmlContent = updateParam.getContent();

        // 공지사항 본문에서 temp 경로의 src 속성들 추출
        List<String> imgSrcs = htmlContentUtil.getImgSrc(htmlContent);

        if (!imgSrcs.isEmpty()) {
            List<String> changedFileNames;

            try {
                changedFileNames = moveImagesFromTemp(noticeId, imgSrcs);
            } catch (IOException e) { // 파일 작업 중에 예외 터지면 롤백
                throw new IllegalArgumentException(e);
            }

            String baseUrl = "/notices/" + noticeId + "/images/";
            String updatedContent = htmlContentUtil.updateImgSrc(htmlContent, baseUrl, changedFileNames);
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

    // 임시 저장 경로에 있던 공지사항 이미지들을 정식 경로에 저장
    public List<String> moveImagesFromTemp(Long noticeId, List<String> imgSrcs) throws IOException {

        List<String> changedFileNames = new ArrayList<>();

        String targetDir = noticeBaseDir + (noticeId + "/");

        // imgSrc = /api/images/o2p2aRmhWArow8cHh5x9_awsec2_logo.png
        // noticeId로 폴더를 만들고, 해당 폴더에 이미지 저장
        int seq = 1;
        for (String src : imgSrcs) {
            // temp 경로인 파일만 이동하고 changedFileNames에 추가
            if (src.startsWith("/api")) {
                String fileName = fileStorageService.extractFileName(src);
                String ext = fileStorageService.extractExt(fileName);

                FileDto from = new FileDto(tempDir, fileName);
                FileDto to = new FileDto(targetDir, String.format("img_%02d%s", seq, ext));

                changedFileNames.add(to.getFileName()); // add("img_01.png")

                fileStorageService.moveFile(from, to);
            }
            seq++;
        }

        return changedFileNames;
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        Notice notice = findOne(noticeId);

        Member member = notice.getMember();
        if (member != null) {
            member.removeNotice(notice);
        }

        noticeRepository.delete(notice);
    }

    // 공지사항 조회 기능
    @Transactional
    public Notice findOne(Long noticeId) {
        Optional<Notice> find = noticeRepository.findOne(noticeId);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 공지사항이 없습니다. id=" + noticeId);
        }
        Notice notice = find.get();
        notice.incrementView();
        return notice;
    }

    public List<Notice> findByMemberId(Long memberId) {
        return noticeRepository.findByMemberId(memberId);
    }

    public List<Notice> findByCategory(NoticeCategory category) {
        return noticeRepository.findByNoticeCategory(category);
    }

    public List<Notice> findAll() {
        return noticeRepository.findAll();
    }

    public String getNoticeImageUrl(Long noticeId, String imageName) {
        return fileStorageService.getFullPath(noticeBaseDir + (noticeId + "/" + imageName));
    }

}
