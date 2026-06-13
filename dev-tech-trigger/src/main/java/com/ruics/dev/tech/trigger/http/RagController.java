package com.ruics.dev.tech.trigger.http;

import com.ruics.dev.tech.api.IRagService;
import com.ruics.dev.tech.api.response.Response;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/rag/")
@Slf4j
public class RagController implements IRagService {

    @Resource
    private TokenTextSplitter tokenTextSplitter;

    @Resource
    private PgVectorStore pgVectorStore;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public Response queryRagNameList() {
        RList<String> ragNames = redissonClient.getList("ragTag");
        return Response.success(ragNames);
    }

    @Override
    public Response uploadRagFiles(@RequestParam String ragName, @RequestParam List<MultipartFile> files) {
        log.info("知识库上传开始");
        try{
            for (MultipartFile file : files) {
                TikaDocumentReader reader = new TikaDocumentReader(file.getResource());
                List<Document> documents = reader.get();
                List<Document> splitterDocuments = tokenTextSplitter.apply(documents);
                splitterDocuments.forEach(document -> document.getMetadata().put("knowledge", ragName));
                pgVectorStore.accept(splitterDocuments);
            }
        }catch (Exception e){
            log.error("知识库上传失败：{}",e.getMessage());
            return Response.error("0001", e.getMessage());
        }
        RList<String> ragNames = redissonClient.getList("ragTag");
        if (!ragNames.contains(ragName)) {
            ragNames.add(ragName);
        }
        log.info("知识库上传结束");
        return Response.success(null);
    }
}
