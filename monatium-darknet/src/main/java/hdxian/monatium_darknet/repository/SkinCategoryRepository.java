package hdxian.monatium_darknet.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SkinCategoryRepository {

    // 언제 필요할지 몰라서 만들어놓음
    private final EntityManager em;


}
