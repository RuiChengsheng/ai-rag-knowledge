package com.ruics.dev.tech.api;

import com.ruics.dev.tech.api.response.Response;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IRagService {

    Response queryRagNameList();

    Response uploadRagFiles(String ragName, List<MultipartFile> files);
}
