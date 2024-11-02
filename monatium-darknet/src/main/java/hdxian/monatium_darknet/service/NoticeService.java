package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.notice.Member;
import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.repository.NoticeRepository;
import hdxian.monatium_darknet.service.dto.NoticeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberService memberService;

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

    // 공지사항 조회 기능
    public Notice findOne(Long id) {
        Optional<Notice> find = noticeRepository.findOne(id);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 공지사항이 없습니다. id=" + id);
        }
        return find.get();
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

}
