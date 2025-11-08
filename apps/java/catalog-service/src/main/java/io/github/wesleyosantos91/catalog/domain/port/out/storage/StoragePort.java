package io.github.wesleyosantos91.catalog.domain.port.out.storage;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface StoragePort {

    String uploadFile(MultipartFile file) throws IOException;

    byte[] downloadFile(String fileKey);

    void deleteFile(String fileKey);

}
