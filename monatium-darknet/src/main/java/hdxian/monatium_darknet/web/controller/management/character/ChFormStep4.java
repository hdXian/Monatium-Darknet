package hdxian.monatium_darknet.web.controller.management.character;

import hdxian.monatium_darknet.domain.Attribute;
import hdxian.monatium_darknet.domain.aside.Aside;
import hdxian.monatium_darknet.domain.aside.AsideSpec;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

// 캐릭터 등록 폼 step 4 (어사이드)
@Data
public class ChFormStep4 {

    private boolean enableAside = true;

    // 어사이드 기본 정보
    private MultipartFile asideImage;
    private String asideName;
    private String asideDescription;

    // 어사이드 1단계
    private MultipartFile asideLv1Image;
    private String asideLv1Name;
    private String asideLv1Description;

    private List<Attribute> asideLv1Attributes = new ArrayList<>();
    public List<Attribute> getAsideLv1Attributes() {
        if (asideLv1Attributes.isEmpty()) {
            asideLv1Attributes.add(new Attribute("", ""));
        }
        return asideLv1Attributes;
    }

    // 어사이드 2단계
    private MultipartFile asideLv2Image;
    private String asideLv2Name;
    private String asideLv2Description;

    private List<Attribute> asideLv2Attributes = new ArrayList<>();
    public List<Attribute> getAsideLv2Attributes() {
        if (asideLv2Attributes.isEmpty()) {
            asideLv2Attributes.add(new Attribute("", ""));
        }
        return asideLv2Attributes;
    }

    // 어사이드 3단계
    private MultipartFile asideLv3Image;
    private String asideLv3Name;
    private String asideLv3Description;

    private List<Attribute> asideLv3Attributes = new ArrayList<>();
    public List<Attribute> getAsideLv3Attributes() {
        if (asideLv3Attributes.isEmpty()) {
            asideLv3Attributes.add(new Attribute("", ""));
        }
        return asideLv3Attributes;
    }

    // 어사이드 스펙 객체 만들어서 리턴 (컨트롤러에서 사용. 렌더링에 사용 x)
    public Aside generateAside() {
        if (isEnableAside()) {
            AsideSpec lv1 = AsideSpec.createAsideSpec(asideLv1Name, asideLv1Description, asideLv1Attributes);
            AsideSpec lv2 = AsideSpec.createAsideSpec(asideLv2Name, asideLv2Description, asideLv2Attributes);
            AsideSpec lv3 = AsideSpec.createAsideSpec(asideLv3Name, asideLv3Description, asideLv3Attributes);
            return Aside.createAside(asideName, asideDescription, lv1, lv2, lv3);
        }
        else {
            return null;
        }
    }

    // === 수정 페이지 등에서 Model에 정보를 담아 보낼 때 사용 ===
    public void setAsideFields(Aside aside) {
        this.asideName = aside.getName();
        this.asideDescription = aside.getDescription();

        AsideSpec level1 = aside.getLevel1();
        this.asideLv1Name = level1.getName();
        this.asideLv1Description = level1.getDescription();
        this.asideLv1Attributes = level1.getAttributes();

        AsideSpec level2 = aside.getLevel2();
        this.asideLv2Name = level2.getName();
        this.asideLv2Description = level2.getDescription();
        this.asideLv2Attributes = level2.getAttributes();

        AsideSpec level3 = aside.getLevel3();
        this.asideLv3Name = level3.getName();
        this.asideLv3Description = level3.getDescription();
        this.asideLv3Attributes = level3.getAttributes();
    }

}
