package com.zcst.upload.service;

import java.util.Map;

public interface PythonFileAnalysisService {

    Map<String, Object> submit(String ossUrl, String originalFilename, String contentType, long size);

    Map<String, Object> submitTimetable(String ossUrl, String studentId, String originalFilename, String contentType, long size);
}
