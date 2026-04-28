package com.zcst.upload.task;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.OSSObjectSummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component("ossCleanupTask")
public class OssCleanupTask {

    @Value("${dromara.x-file-storage.aliyun-oss[0].access-key:}")
    private String accessKey;

    @Value("${dromara.x-file-storage.aliyun-oss[0].secret-key:}")
    private String secretKey;

    @Value("${dromara.x-file-storage.aliyun-oss[0].end-point:}")
    private String endpoint;

    @Value("${dromara.x-file-storage.aliyun-oss[0].bucket-name:}")
    private String bucketName;

    @Value("${dromara.x-file-storage.aliyun-oss[0].base-path:test/}")
    private String basePath;

    public void cleanExpired7Days()
    {
        cleanExpiredDays(7);
    }

    public void cleanExpiredDays(Integer expireDays)
    {
        int days = expireDays == null || expireDays <= 0 ? 7 : expireDays;
        if (isBlank(endpoint) || isBlank(accessKey) || isBlank(secretKey) || isBlank(bucketName)) {
            log.warn("OSS 清理任务跳过：OSS 配置不完整");
            return;
        }

        String base = normalizeBasePath(basePath);
        Instant cutoff = Instant.now().minus(days, ChronoUnit.DAYS);

        try (OssClientHolder holder = new OssClientHolder(endpoint, accessKey, secretKey)) {
            int deletedSchedule = deleteExpiredByPrefix(holder.client, base + "schedule/", cutoff);
            int deletedUploads = deleteExpiredByPrefix(holder.client, base + "uploads/", cutoff);
            log.info("OSS 清理完成：expireDays={}, scheduleDeleted={}, uploadsDeleted={}", days, deletedSchedule, deletedUploads);
        } catch (Exception e) {
            log.error("OSS 清理失败", e);
            throw e;
        }
    }

    private int deleteExpiredByPrefix(OSS client, String prefix, Instant cutoff)
    {
        int deleted = 0;
        String marker = null;
        ListObjectsRequest req = new ListObjectsRequest(bucketName).withPrefix(prefix).withMaxKeys(1000);

        while (true) {
            req.setMarker(marker);
            ObjectListing listing = client.listObjects(req);

            List<String> batch = new ArrayList<>();
            for (OSSObjectSummary summary : listing.getObjectSummaries()) {
                Date lastModified = summary.getLastModified();
                if (lastModified == null) {
                    continue;
                }
                if (lastModified.toInstant().isBefore(cutoff)) {
                    batch.add(summary.getKey());
                    if (batch.size() >= 1000) {
                        deleted += deleteBatch(client, batch);
                        batch.clear();
                    }
                }
            }
            if (!batch.isEmpty()) {
                deleted += deleteBatch(client, batch);
            }

            if (!listing.isTruncated()) {
                break;
            }
            marker = listing.getNextMarker();
        }

        return deleted;
    }

    private int deleteBatch(OSS client, List<String> keys)
    {
        if (keys == null || keys.isEmpty()) {
            return 0;
        }
        DeleteObjectsRequest dor = new DeleteObjectsRequest(bucketName).withKeys(keys).withQuiet(true);
        return client.deleteObjects(dor).getDeletedObjects().size();
    }

    private String normalizeBasePath(String base)
    {
        String b = base == null ? "" : base.trim();
        if (b.isEmpty()) {
            return "";
        }
        if (!b.endsWith("/")) {
            b = b + "/";
        }
        while (b.startsWith("/")) {
            b = b.substring(1);
        }
        return b;
    }

    private boolean isBlank(String v)
    {
        return v == null || v.trim().isEmpty();
    }

    private static class OssClientHolder implements AutoCloseable {
        private final OSS client;

        private OssClientHolder(String endpoint, String accessKey, String secretKey) {
            this.client = new OSSClientBuilder().build(endpoint, accessKey, secretKey);
        }

        @Override
        public void close() {
            try {
                client.shutdown();
            } catch (Exception ignored) {
            }
        }
    }
}

