package hdxian.monatium_darknet.security;

import hdxian.monatium_darknet.domain.notice.Member;
import hdxian.monatium_darknet.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DBUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Optional<Member> find = memberRepository.findByLoginId(loginId);
        if (find.isEmpty()) {
            throw new UsernameNotFoundException("User not found with loginId: " + loginId);
        }

        Member member = find.get();
        String memberRole = member.getRole().name(); // SUPER, NORMAL
//        log.info("find userDetail from DB: {}", member.getLoginId());
        return new CustomUserDetails(member);
//        return User
//                .withUsername(member.getLoginId())
//                .password(member.getPassword())
//                .roles(memberRole)
//                .build();
    }

}
