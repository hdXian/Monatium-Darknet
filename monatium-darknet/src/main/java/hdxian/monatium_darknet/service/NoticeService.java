package hdxian.monatium_darknet.service;

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

    @Value("${file.tempDir}")
    private String tempDir;

    @Value("${file.noticeDir}")
    private String noticeBaseDir;

    // TODO - 공지사항 리스트 불러올 시 페이징 추가 필요

    // 공지사항 추가 기능
    @Transactional
    public Long createNewNotice(Long memberId, NoticeDto noticeDto) {

        Member member = memberService.findOne(memberId);
        Notice notice = Notice.createNotice(
                member,
                noticeDto.getCategory(),
                noticeDto.getTitle(),
                noticeDto.getContent()
        );

        return noticeRepository.save(notice);
    }

    @Transactional
    public Long updateNotice(Long noticeId, NoticeDto updateParam) {
        Notice notice = findOne(noticeId);

        notice.setCategory(updateParam.getCategory());
        notice.setTitle(updateParam.getTitle());
        notice.setContent(updateParam.getContent());

        // 업데이트 시간으로 변경
        notice.setDate(LocalDateTime.now());

        return notice.getId();
    }

    // 임시 저장 경로에 있던 공지사항 이미지들을 정식 경로에 저장
    @Transactional
    public List<String> moveImagesFromTemp(Long noticeId, List<String> imgSrcs) throws IOException {

        List<String> changedFileNames = new ArrayList<>();

        String targetDir = noticeBaseDir + (noticeId + "/");

        // imgSrc = /api/images/o2p2aRmhWArow8cHh5x9_awsec2_logo.png
        // noticeId로 폴더를 만들고, 해당 폴더에 이미지 저장
        int seq = 1;
        for (String src : imgSrcs) {
            String fileName = fileStorageService.extractFileName(src);
            String ext = fileStorageService.extractExt(fileName);

            FileDto from = new FileDto(tempDir, fileName);
            FileDto to = new FileDto(targetDir, String.format("img_%02d%s", seq++, ext));
            changedFileNames.add(to.getFileName());

            fileStorageService.moveFile(from, to);
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
