package hdxian.monatium_darknet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class NoticeServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    NoticeService noticeService;

    // TODO
    // TODO 2 - member, notice repo의 findOne() Optional로 바꾸고 null 들어있으면 service에서 예외 터뜨리는 로직 추가, 해당 로직 테스트 추가

    // 공지사항 저장

    // 회원별 공지사항 조회

    // 카테고리별 공지사항 조회

}