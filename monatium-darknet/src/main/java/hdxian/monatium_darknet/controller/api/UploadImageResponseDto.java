package hdxian.monatium_darknet.controller.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadImageResponseDto {
    private boolean success;
    private String imageUrl;
}
