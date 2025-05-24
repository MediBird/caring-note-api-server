package com.springboot.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.exception.ResourceNotFoundException;
import com.springboot.api.tus.TusHeaderKeys;
import com.springboot.api.tus.controller.TusController;
import com.springboot.api.tus.properties.TusProperties;
import com.springboot.api.tus.response.TusFileInfoRes;
import com.springboot.api.tus.service.TusService;
import de.huxhorn.sulky.ulid.ULID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TusController.class)
class TusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TusService tusService;

    @MockBean
    private TusProperties tusProperties;

    @Autowired
    private ObjectMapper objectMapper;

    private final ULID ulid = new ULID();
    private String testFileId;
    private String testCounselSessionId;
    private final String tusVersion = "1.0.0";
    private final String pathPrefix = "/v1/tus";


    @BeforeEach
    void setUp() {
        testFileId = ulid.nextULID();
        testCounselSessionId = ulid.nextULID();
        when(tusProperties.getVersion()).thenReturn(tusVersion);
        when(tusProperties.getPathPrefix()).thenReturn(pathPrefix); // Used by controller to build Location header
        when(tusProperties.getUploadUrl()).thenReturn(pathPrefix); // For Location in TusFileInfoRes
    }

    // OPTIONS /
    @Test
    @DisplayName("OPTIONS / - 성공")
    void processOptions_Success() throws Exception {
        mockMvc.perform(options(pathPrefix + "/"))
                .andExpect(status().isNoContent())
                .andExpect(header().string(TusHeaderKeys.TUS_RESUMABLE, tusVersion))
                .andExpect(header().string(TusHeaderKeys.TUS_VERSION, tusVersion))
                .andExpect(header().string(TusHeaderKeys.TUS_EXTENSION, "creation,creation-defer-length,termination"))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET, POST, PATCH, DELETE, OPTIONS, HEAD"))
                .andExpect(header().string("Access-Control-Expose-Headers", TusHeaderKeys.TUS_RESUMABLE + "," + TusHeaderKeys.TUS_VERSION + "," + TusHeaderKeys.UPLOAD_OFFSET + "," + TusHeaderKeys.UPLOAD_LENGTH + "," + TusHeaderKeys.UPLOAD_DEFER_LENGTH + "," + TusHeaderKeys.UPLOAD_METADATA + "," + TusHeaderKeys.LOCATION + "," + TusHeaderKeys.X_RECORDING_DURATION))
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    // POST /
    @Test
    @DisplayName("POST / - 업로드 시작 성공")
    void startUpload_Success() throws Exception {
        when(tusService.initUpload(anyString(), anyLong(), anyBoolean())).thenReturn(testFileId);

        mockMvc.perform(post(pathPrefix + "/")
                        .header(TusHeaderKeys.UPLOAD_METADATA, "counselSessionId " + testCounselSessionId)
                        .header(TusHeaderKeys.UPLOAD_LENGTH, 1000L))
                .andExpect(status().isCreated())
                .andExpect(header().string(TusHeaderKeys.LOCATION, pathPrefix + "/" + testFileId))
                .andExpect(header().string(TusHeaderKeys.TUS_RESUMABLE, tusVersion));

        verify(tusService).initUpload(eq("counselSessionId " + testCounselSessionId), eq(1000L), eq(false));
    }

    @Test
    @DisplayName("POST / - 지연 업로드 시작 성공")
    void startUpload_DeferSuccess() throws Exception {
        when(tusService.initUpload(anyString(), eq(0L), anyBoolean())).thenReturn(testFileId);

        mockMvc.perform(post(pathPrefix + "/")
                        .header(TusHeaderKeys.UPLOAD_METADATA, "counselSessionId " + testCounselSessionId)
                        .header(TusHeaderKeys.UPLOAD_DEFER_LENGTH, "1")) // Value '1' means true
                .andExpect(status().isCreated())
                .andExpect(header().string(TusHeaderKeys.LOCATION, pathPrefix + "/" + testFileId))
                .andExpect(header().string(TusHeaderKeys.TUS_RESUMABLE, tusVersion));

        verify(tusService).initUpload(eq("counselSessionId " + testCounselSessionId), eq(0L), eq(true));
    }

    @Test
    @DisplayName("POST / - 실패 (서비스 ResourceNotFoundException)")
    void startUpload_ServiceResourceNotFound() throws Exception {
        when(tusService.initUpload(anyString(), anyLong(), anyBoolean())).thenThrow(new ResourceNotFoundException("CounselSession not found"));

        mockMvc.perform(post(pathPrefix + "/")
                        .header(TusHeaderKeys.UPLOAD_METADATA, "counselSessionId " + testCounselSessionId)
                        .header(TusHeaderKeys.UPLOAD_LENGTH, 1000L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST / - 실패 (서비스 IllegalArgumentException - 잘못된 메타데이터)")
    void startUpload_ServiceIllegalArgument() throws Exception {
        when(tusService.initUpload(anyString(), anyLong(), anyBoolean())).thenThrow(new IllegalArgumentException("Invalid metadata"));

        mockMvc.perform(post(pathPrefix + "/")
                        .header(TusHeaderKeys.UPLOAD_METADATA, "invalid metadata")
                        .header(TusHeaderKeys.UPLOAD_LENGTH, 1000L))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST / - 실패 (메타데이터 헤더 누락)")
    void startUpload_MissingMetadataHeader() throws Exception {
        mockMvc.perform(post(pathPrefix + "/")
                        .header(TusHeaderKeys.UPLOAD_LENGTH, 1000L))
                .andExpect(status().isBadRequest()); // Controller validation
    }
    
    @Test
    @DisplayName("POST / - 실패 (Upload-Length 와 Upload-Defer-Length 모두 누락)")
    void startUpload_MissingLengthAndDeferLength() throws Exception {
        mockMvc.perform(post(pathPrefix + "/")
                        .header(TusHeaderKeys.UPLOAD_METADATA, "counselSessionId " + testCounselSessionId))
                .andExpect(status().isBadRequest()); // Controller validation
    }


    // HEAD /{fileId}
    @Test
    @DisplayName("HEAD /{fileId} - 성공 (non-deferred)")
    void getUploadStatus_Success_NonDeferred() throws Exception {
        TusFileInfoRes mockRes = TusFileInfoRes.builder()
                .fileId(testFileId).offset(500L).fileSize(1000L).isDefer(false).duration(12345L)
                .location(tusProperties.getUploadUrl() + "/" + testFileId) // Ensure location is set as TusService would
                .build();
        when(tusService.getTusFileInfo(testFileId)).thenReturn(mockRes);

        mockMvc.perform(head(pathPrefix + "/{fileId}", testFileId))
                .andExpect(status().isNoContent())
                .andExpect(header().longValue(TusHeaderKeys.UPLOAD_OFFSET, 500L))
                .andExpect(header().longValue(TusHeaderKeys.UPLOAD_LENGTH, 1000L))
                .andExpect(header().string(TusHeaderKeys.LOCATION, pathPrefix + "/" + testFileId))
                .andExpect(header().string(TusHeaderKeys.CACHE_CONTROL, "no-store"))
                .andExpect(header().string(TusHeaderKeys.TUS_RESUMABLE, tusVersion))
                .andExpect(header().longValue(TusHeaderKeys.X_RECORDING_DURATION, 12345L));
    }

    @Test
    @DisplayName("HEAD /{fileId} - 성공 (deferred)")
    void getUploadStatus_Success_Deferred() throws Exception {
        TusFileInfoRes mockRes = TusFileInfoRes.builder()
                .fileId(testFileId).offset(0L).fileSize(0L).isDefer(true).duration(0L) // fileSize might be 0 for deferred
                .location(tusProperties.getUploadUrl() + "/" + testFileId)
                .build();
        when(tusService.getTusFileInfo(testFileId)).thenReturn(mockRes);

        mockMvc.perform(head(pathPrefix + "/{fileId}", testFileId))
                .andExpect(status().isNoContent())
                .andExpect(header().longValue(TusHeaderKeys.UPLOAD_OFFSET, 0L))
                .andExpect(header().string(TusHeaderKeys.UPLOAD_DEFER_LENGTH, "1"))
                .andExpect(header().exists(TusHeaderKeys.UPLOAD_LENGTH)) // Should still exist, even if 0
                .andExpect(header().longValue(TusHeaderKeys.X_RECORDING_DURATION, 0L));
    }

    @Test
    @DisplayName("HEAD /{fileId} - 실패 (서비스 ResourceNotFoundException)")
    void getUploadStatus_ServiceResourceNotFound() throws Exception {
        when(tusService.getTusFileInfo(testFileId)).thenThrow(new ResourceNotFoundException("File not found"));

        mockMvc.perform(head(pathPrefix + "/{fileId}", testFileId))
                .andExpect(status().isNotFound());
    }

    // PATCH /{fileId}
    @Test
    @DisplayName("PATCH /{fileId} - 업로드 진행 성공")
    void uploadProcess_Success() throws Exception {
        byte[] content = "test data".getBytes();
        when(tusService.appendData(eq(testFileId), eq(0L), any(), any())).thenReturn( (long) content.length);

        mockMvc.perform(patch(pathPrefix + "/{fileId}", testFileId)
                        .header(TusHeaderKeys.UPLOAD_OFFSET, 0L)
                        .header(TusHeaderKeys.CONTENT_TYPE, "application/offset+octet-stream")
                        .content(content))
                .andExpect(status().isNoContent())
                .andExpect(header().longValue(TusHeaderKeys.UPLOAD_OFFSET, content.length))
                .andExpect(header().string(TusHeaderKeys.TUS_RESUMABLE, tusVersion));

        verify(tusService).appendData(eq(testFileId), eq(0L), any(), eq(null)); // duration not passed via header
    }
    
    @Test
    @DisplayName("PATCH /{fileId} - 업로드 진행 성공 (X-Recording-Duration 헤더 포함)")
    void uploadProcess_Success_WithDurationHeader() throws Exception {
        byte[] content = "test data".getBytes();
        long duration = 30000L; // 30 seconds
        when(tusService.appendData(eq(testFileId), eq(0L), any(), eq(duration))).thenReturn((long) content.length);

        mockMvc.perform(patch(pathPrefix + "/{fileId}", testFileId)
                        .header(TusHeaderKeys.UPLOAD_OFFSET, 0L)
                        .header(TusHeaderKeys.CONTENT_TYPE, "application/offset+octet-stream")
                        .header(TusHeaderKeys.X_RECORDING_DURATION, duration)
                        .content(content))
                .andExpect(status().isNoContent())
                .andExpect(header().longValue(TusHeaderKeys.UPLOAD_OFFSET, content.length))
                .andExpect(header().string(TusHeaderKeys.TUS_RESUMABLE, tusVersion));

        verify(tusService).appendData(eq(testFileId), eq(0L), any(), eq(duration));
    }


    @Test
    @DisplayName("PATCH /{fileId} - 실패 (서비스 오프셋 불일치 - IOException)")
    void uploadProcess_ServiceOffsetMismatch() throws Exception {
        // TusService's appendData throws IOException for offset mismatch in its internal check
        when(tusService.appendData(eq(testFileId), anyLong(), any(), any())).thenThrow(new IOException("Offset mismatch"));

        mockMvc.perform(patch(pathPrefix + "/{fileId}", testFileId)
                        .header(TusHeaderKeys.UPLOAD_OFFSET, 100L) // Client sends a different offset
                        .header(TusHeaderKeys.CONTENT_TYPE, "application/offset+octet-stream")
                        .content("data".getBytes()))
                .andExpect(status().isConflict()); // Controller catches IOException and returns 409
    }

    @Test
    @DisplayName("PATCH /{fileId} - 실패 (서비스 파일 없음 - ResourceNotFoundException)")
    void uploadProcess_ServiceFileNotFound() throws Exception {
        when(tusService.appendData(eq(testFileId), anyLong(), any(), any())).thenThrow(new ResourceNotFoundException("File not found"));

        mockMvc.perform(patch(pathPrefix + "/{fileId}", testFileId)
                        .header(TusHeaderKeys.UPLOAD_OFFSET, 0L)
                        .header(TusHeaderKeys.CONTENT_TYPE, "application/offset+octet-stream")
                        .content("data".getBytes()))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("PATCH /{fileId} - 실패 (서비스 IOException)")
    void uploadProcess_ServiceIOException() throws Exception {
        when(tusService.appendData(eq(testFileId), anyLong(), any(), any())).thenThrow(new IOException("Disk full"));

        mockMvc.perform(patch(pathPrefix + "/{fileId}", testFileId)
                        .header(TusHeaderKeys.UPLOAD_OFFSET, 0L)
                        .header(TusHeaderKeys.CONTENT_TYPE, "application/offset+octet-stream")
                        .content("data".getBytes()))
                .andExpect(status().isConflict()); // Controller's catch block for IOException
    }


    @Test
    @DisplayName("PATCH /{fileId} - 실패 (Upload-Offset 헤더 누락)")
    void uploadProcess_MissingOffsetHeader() throws Exception {
        mockMvc.perform(patch(pathPrefix + "/{fileId}", testFileId)
                        .header(TusHeaderKeys.CONTENT_TYPE, "application/offset+octet-stream")
                        .content("data".getBytes()))
                .andExpect(status().isBadRequest()); // Controller validation
    }

    @Test
    @DisplayName("PATCH /{fileId} - 실패 (잘못된 Content-Type)")
    void uploadProcess_IncorrectContentType() throws Exception {
        mockMvc.perform(patch(pathPrefix + "/{fileId}", testFileId)
                        .header(TusHeaderKeys.UPLOAD_OFFSET, 0L)
                        .header(TusHeaderKeys.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // Incorrect type
                        .content("data".getBytes()))
                .andExpect(status().isUnsupportedMediaType()); // Controller validation
    }

    // GET /merge/{counselSessionId}
    @Test
    @DisplayName("GET /merge/{counselSessionId} - 병합 성공")
    void mergeMediaFile_Success() throws Exception {
        when(tusService.mergeUploadedFile(testCounselSessionId)).thenReturn("/merged/path.webm");

        mockMvc.perform(get(pathPrefix + "/merge/{counselSessionId}", testCounselSessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("/merged/path.webm"));
    }

    @Test
    @DisplayName("GET /merge/{counselSessionId} - 병합 실패 (서비스 IOException)")
    void mergeMediaFile_ServiceIOException() throws Exception {
        when(tusService.mergeUploadedFile(testCounselSessionId)).thenThrow(new IOException("Merge failed"));

        mockMvc.perform(get(pathPrefix + "/merge/{counselSessionId}", testCounselSessionId))
                .andExpect(status().isInternalServerError());
    }

    // GET /{fileId}
    @Test
    @DisplayName("GET /{fileId} - 파일 다운로드 성공")
    void getMediaFile_Success() throws Exception {
        Resource mockResource = new ByteArrayResource("fake media data".getBytes(), "file.webm");
        when(tusService.getUploadedFile(testFileId)).thenReturn(mockResource);

        mockMvc.perform(get(pathPrefix + "/{fileId}", testFileId))
                .andExpect(status().isOk())
                .andExpect(header().string(TusHeaderKeys.CONTENT_TYPE, "audio/webm"))
                .andExpect(header().string(TusHeaderKeys.CONTENT_DISPOSITION, "attachment; filename=\"" + testFileId + ".webm\""))
                .andExpect(content().bytes("fake media data".getBytes()));
    }

    @Test
    @DisplayName("GET /{fileId} - 실패 (서비스 ResourceNotFoundException)")
    void getMediaFile_ServiceResourceNotFound() throws Exception {
        when(tusService.getUploadedFile(testFileId)).thenThrow(new ResourceNotFoundException("File not found"));

        mockMvc.perform(get(pathPrefix + "/{fileId}", testFileId))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("GET /{fileId} - 실패 (서비스 IOException)")
    void getMediaFile_ServiceIOException() throws Exception {
        when(tusService.getUploadedFile(testFileId)).thenThrow(new IOException("Error reading file"));

        mockMvc.perform(get(pathPrefix + "/{fileId}", testFileId))
                .andExpect(status().isInternalServerError());
    }


    // DELETE /{fileId}
    @Test
    @DisplayName("DELETE /{fileId} - 삭제 성공")
    void deleteUploadedFile_Success() throws Exception {
        // The controller's deleteUploadedFile method takes fileId but calls service.deleteUploadedFile(fileId)
        // The service's deleteUploadedFile expects counselSessionId.
        // For this unit test, we mock the service call with the fileId argument that the controller passes.
        // This assumes the service can handle it, or that this discrepancy is out of scope for controller unit test.
        doNothing().when(tusService).deleteUploadedFile(testFileId);

        mockMvc.perform(delete(pathPrefix + "/{fileId}", testFileId))
                .andExpect(status().isNoContent())
                .andExpect(header().string(TusHeaderKeys.TUS_RESUMABLE, tusVersion));

        verify(tusService).deleteUploadedFile(testFileId);
    }

    @Test
    @DisplayName("DELETE /{fileId} - 삭제 실패 (서비스 IOException)")
    void deleteUploadedFile_ServiceIOException() throws Exception {
        doThrow(new IOException("Deletion failed")).when(tusService).deleteUploadedFile(testFileId);

        mockMvc.perform(delete(pathPrefix + "/{fileId}", testFileId))
                .andExpect(status().isInternalServerError()); // Assuming IOException maps to 500
    }
}
