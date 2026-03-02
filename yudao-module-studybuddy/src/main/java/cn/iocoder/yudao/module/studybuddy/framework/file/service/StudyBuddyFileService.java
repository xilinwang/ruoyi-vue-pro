package cn.iocoder.yudao.module.studybuddy.framework.file.service;

import cn.iocoder.yudao.module.studybuddy.framework.file.config.StudyBuddyFileProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * StudyBuddy 文件存储服务
 *
 * 支持本地存储和 MinIO 存储的切换
 *
 * @author StudyBuddy
 */
@Service
@Slf4j
public class StudyBuddyFileService {

    @Resource
    private StudyBuddyFileProperties fileProperties;

    /**
     * 上传文件到本地存储
     *
     * @param file 上传的文件
     * @param directory 子目录（如 papers, answers 等）
     * @return 存储后的文件路径（相对于存储根目录）
     */
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        if ("minio".equalsIgnoreCase(fileProperties.getStorage())) {
            return uploadToMinIO(file, directory);
        } else {
            return uploadToLocal(file, directory);
        }
    }

    /**
     * 上传文件到本地存储
     */
    private String uploadToLocal(MultipartFile file, String directory) throws IOException {
        // 创建日期目录
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String relativePath = directory + "/" + datePath;

        // 构建完整目录路径
        String basePath = fileProperties.getLocal().getBasePath();
        Path fullPath = Paths.get(basePath, relativePath);

        // 创建目录
        Files.createDirectories(fullPath);

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFileName = UUID.randomUUID().toString().replace("-", "") + extension;

        // 保存文件
        Path filePath = fullPath.resolve(newFileName);
        file.transferTo(filePath.toFile());

        log.info("[uploadToLocal] 文件上传成功，原始文件名: {}, 保存路径: {}", originalFilename, filePath);

        // 返回相对路径
        return relativePath + "/" + newFileName;
    }

    /**
     * 上传文件到 MinIO（预留接口）
     * 注意：需要先部署 MinIO 服务并配置相关参数
     */
    private String uploadToMinIO(MultipartFile file, String directory) throws IOException {
        log.warn("[uploadToMinIO] MinIO 存储未实现，请先部署 MinIO 服务并完成集成");

        // 这里预留 MinIO 集成接口
        // 实际实现需要添加 MinIO 依赖并配置客户端

        // 目前回退到本地存储
        return uploadToLocal(file, directory);
    }

    /**
     * 获取文件的完整访问 URL
     *
     * @param relativePath 相对路径
     * @return 完整访问 URL
     */
    public String getFileUrl(String relativePath) {
        if ("minio".equalsIgnoreCase(fileProperties.getStorage())) {
            return getMinioFileUrl(relativePath);
        } else {
            return getLocalFileUrl(relativePath);
        }
    }

    /**
     * 获取本地存储的文件 URL
     */
    private String getLocalFileUrl(String relativePath) {
        String domain = fileProperties.getLocal().getDomain();
        return domain + "/studybuddy-files/" + relativePath;
    }

    /**
     * 获取 MinIO 存储的文件 URL
     */
    private String getMinioFileUrl(String relativePath) {
        String domain = fileProperties.getMinio().getDomain();
        if (domain != null && !domain.isEmpty()) {
            return domain + "/" + relativePath;
        }
        String endpoint = fileProperties.getMinio().getEndpoint();
        String bucket = fileProperties.getMinio().getBucket();
        return endpoint + "/" + bucket + "/" + relativePath;
    }

    /**
     * 获取本地存储的绝对路径
     *
     * @param relativePath 相对路径
     * @return 绝对路径
     */
    public String getLocalFilePath(String relativePath) {
        String basePath = fileProperties.getLocal().getBasePath();
        return Paths.get(basePath, relativePath).toString();
    }

    /**
     * 删除文件
     *
     * @param relativePath 相对路径
     */
    public void deleteFile(String relativePath) {
        if ("minio".equalsIgnoreCase(fileProperties.getStorage())) {
            deleteFromMinIO(relativePath);
        } else {
            deleteFromLocal(relativePath);
        }
    }

    /**
     * 从本地存储删除文件
     */
    private void deleteFromLocal(String relativePath) {
        try {
            String basePath = fileProperties.getLocal().getBasePath();
            Path filePath = Paths.get(basePath, relativePath);
            Files.deleteIfExists(filePath);
            log.info("[deleteFromLocal] 文件删除成功: {}", filePath);
        } catch (IOException e) {
            log.error("[deleteFromLocal] 文件删除失败: {}", relativePath, e);
        }
    }

    /**
     * 从 MinIO 删除文件
     */
    private void deleteFromMinIO(String relativePath) {
        log.warn("[deleteFromMinIO] MinIO 存储删除功能未实现");
    }

    /**
     * 获取存储类型
     */
    public String getStorageType() {
        return fileProperties.getStorage();
    }

    /**
     * 获取本地存储根目录
     */
    public String getLocalBasePath() {
        return fileProperties.getLocal().getBasePath();
    }

}