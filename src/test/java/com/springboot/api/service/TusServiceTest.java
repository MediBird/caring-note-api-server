package com.springboot.api.service;

import com.springboot.api.counselsession.model.CounselSession;
import com.springboot.api.counselsession.repository.CounselSessionRepository;
import com.springboot.api.exception.ResourceNotFoundException;
import com.springboot.api.tus.model.TusFileInfo;
import com.springboot.api.tus.properties.TusProperties;
import com.springboot.api.tus.repository.TusFileInfoRepository;
import com.springboot.api.tus.response.TusFileInfoRes;
import com.springboot.api.tus.service.TusService;
import com.springboot.api.util.FileUtil;
import de.huxhorn.sulky.ulid.ULID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.DelegatingServletInputStream;

import jakarta.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TusServiceTest {

    @Mock
    private TusFileInfoRepository tusFileInfoRepository;

    @Mock
    private CounselSessionRepository counselSessionRepository;

    @Mock
    private TusProperties tusProperties;

    @Mock
    private FileUtil fileUtil;

    @InjectMocks
    private TusService tusService;

    private final ULID ulid = new ULID();
    private String testFileId;
    private String testCounselSessionId;
    private CounselSession mockCounselSession;
    private TusFileInfo mockTusFileInfo;

    @BeforeEach
    void setUp() {
        testFileId = ulid.nextULID();
        testCounselSessionId = ulid.nextULID();

        // mockCounselSession doesn't need to be a spy unless we test methods on it directly
        mockCounselSession = CounselSession.builder().id(testCounselSessionId).build(); 
        
        // mockTusFileInfo can be a spy if we want to mock some of its methods while calling others
        // For this case, direct mocking of getCounselSession() is better if it's a mock object.
        // If mockTusFileInfo is a real object, then its counselSession field needs to be set.
        // The existing setup is fine as it uses a real TusFileInfo object.
        mockTusFileInfo = TusFileInfo.builder()
                .id(testFileId)
                .fileName(testFileId + ".webm")
                .filePath("/test/uploads/" + testCounselSessionId + "/" + testFileId + ".webm")
                .fileSize(1000L)
                .offset(0L)
                .counselSession(mockCounselSession) // This links it to mockCounselSession
                .isDefer(false)
                .build();
        
        // It's good practice to mock specific interactions if mockTusFileInfo were a @Mock
        // e.g., when(mockTusFileInfo.getCounselSession()).thenReturn(mockCounselSession);
        // but since it's a real object built with the builder, this is already set.

        when(tusProperties.getUploadPath()).thenReturn("/test/uploads");
        when(tusProperties.getUploadUrl()).thenReturn("/api/v1/tus/upload");
        when(tusProperties.getMergeFileExtension()).thenReturn(".webm");
    }

    // initUpload Tests
    @Test
    @DisplayName("initUpload - 성공 (isDefer=false)")
    void initUpload_Success_NotDefer() throws IOException {
        String metadata = "counselSessionId " + testCounselSessionId + ",isFinal true";
        Long contentLength = 1000L;

        when(counselSessionRepository.findById(testCounselSessionId)).thenReturn(Optional.of(mockCounselSession));
        when(fileUtil.createUploadFile(anyString(), anyString(), anyString())).thenReturn(mockTusFileInfo.getFilePath());
        when(tusFileInfoRepository.save(any(TusFileInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String fileId = tusService.initUpload(metadata, contentLength, false);

        assertNotNull(fileId);
        verify(counselSessionRepository).findById(testCounselSessionId);
        verify(fileUtil).createUploadFile(eq("/test/uploads/" + testCounselSessionId), anyString(), eq(".webm"));
        verify(tusFileInfoRepository).save(argThat(tusFileInfo ->
                tusFileInfo.getCounselSession().getId().equals(testCounselSessionId) &&
                tusFileInfo.getFileSize().equals(contentLength) &&
                !tusFileInfo.getIsDefer() &&
                tusFileInfo.getIsFinal() // from metadata
        ));
    }

    @Test
    @DisplayName("initUpload - 성공 (isDefer=true)")
    void initUpload_Success_IsDeferTrue() throws IOException {
        String metadata = "counselSessionId " + testCounselSessionId; // isFinal not present
        Long contentLength = 500L;

        when(counselSessionRepository.findById(testCounselSessionId)).thenReturn(Optional.of(mockCounselSession));
        when(fileUtil.createUploadFile(anyString(), anyString(), anyString())).thenReturn(mockTusFileInfo.getFilePath());
        when(tusFileInfoRepository.save(any(TusFileInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String fileId = tusService.initUpload(metadata, contentLength, true);

        assertNotNull(fileId);
        verify(tusFileInfoRepository).save(argThat(tusFileInfo ->
                tusFileInfo.getCounselSession().getId().equals(testCounselSessionId) &&
                tusFileInfo.getFileSize().equals(contentLength) &&
                tusFileInfo.getIsDefer() &&
                !tusFileInfo.getIsFinal() // default because not in metadata
        ));
    }

    @Test
    @DisplayName("initUpload - 실패 (잘못된 메타데이터 - 쉼표 누락)")
    void initUpload_Failure_InvalidMetadata_MissingComma() {
        String metadata = "counselSessionId " + testCounselSessionId + "isFinal true"; // Missing comma
        Long contentLength = 1000L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tusService.initUpload(metadata, contentLength, false);
        });
        assertTrue(exception.getMessage().contains("Invalid metadata format"));
    }
    
    @Test
    @DisplayName("initUpload - 실패 (잘못된 메타데이터 - 스페이스 누락)")
    void initUpload_Failure_InvalidMetadata_MissingSpace() {
        String metadata = "counselSessionId" + testCounselSessionId + ",isFinal true"; // Missing space
        Long contentLength = 1000L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tusService.initUpload(metadata, contentLength, false);
        });
        assertTrue(exception.getMessage().contains("Invalid metadata format"));
    }

    @Test
    @DisplayName("initUpload - 실패 (상담 세션 ID 없음)")
    void initUpload_Failure_CounselSessionNotFound() {
        String metadata = "counselSessionId " + testCounselSessionId;
        Long contentLength = 1000L;
        when(counselSessionRepository.findById(testCounselSessionId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            tusService.initUpload(metadata, contentLength, false);
        });
        assertEquals("CounselSession not found with id: " + testCounselSessionId, exception.getMessage());
    }

    @Test
    @DisplayName("initUpload - 실패 (FileUtil.createUploadFile IOException)")
    void initUpload_Failure_FileUtilCreateUploadFileThrowsIOException() throws IOException {
        String metadata = "counselSessionId " + testCounselSessionId;
        Long contentLength = 1000L;
        when(counselSessionRepository.findById(testCounselSessionId)).thenReturn(Optional.of(mockCounselSession));
        when(fileUtil.createUploadFile(anyString(), anyString(), anyString())).thenThrow(new IOException("Disk full"));

        IOException exception = assertThrows(IOException.class, () -> {
            tusService.initUpload(metadata, contentLength, false);
        });
        assertEquals("Disk full", exception.getMessage());
    }

    // getTusFileInfo Tests
    @Test
    @DisplayName("getTusFileInfo - 성공")
    void getTusFileInfo_Success() {
        when(tusFileInfoRepository.findById(testFileId)).thenReturn(Optional.of(mockTusFileInfo));

        TusFileInfoRes result = tusService.getTusFileInfo(testFileId);

        assertNotNull(result);
        assertEquals(mockTusFileInfo.getId(), result.getFileId());
        assertEquals(mockTusFileInfo.getOffset(), result.getOffset());
        assertTrue(result.getLocation().endsWith("/api/v1/tus/upload/" + testFileId));
        verify(tusFileInfoRepository).findById(testFileId);
    }

    @Test
    @DisplayName("getTusFileInfo - 실패 (파일 ID 없음)")
    void getTusFileInfo_Failure_FileIdNotFound() {
        when(tusFileInfoRepository.findById(testFileId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            tusService.getTusFileInfo(testFileId);
        });
        assertEquals("TusFileInfo not found with id: " + testFileId, exception.getMessage());
    }

    // appendData Tests
    @Test
    @DisplayName("appendData - 성공 (duration 있음)")
    void appendData_Success_WithDuration() throws IOException {
        long offset = 0L;
        long duration = 5000L; // 5 seconds
        byte[] data = "test data".getBytes();
        ServletInputStream inputStream = new DelegatingServletInputStream(new ByteArrayInputStream(data));
        mockTusFileInfo.setOffset(offset); // Ensure initial offset matches

        when(tusFileInfoRepository.findById(testFileId)).thenReturn(Optional.of(mockTusFileInfo));
        when(tusFileInfoRepository.save(any(TusFileInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mocking static Files.newOutputStream is tricky without PowerMock.
        // We'll test the logic around it. The actual file write won't happen in unit test.
        // We can verify the TusFileInfo update.
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            OutputStream mockOutputStream = mock(OutputStream.class);
            mockedFiles.when(() -> Files.newOutputStream(any(Path.class), any())).thenReturn(mockOutputStream);

            tusService.appendData(testFileId, offset, inputStream, duration);

            verify(mockOutputStream).write(data); // Verify data was "written"
        }

        verify(tusFileInfoRepository).findById(testFileId);
        verify(tusFileInfoRepository).save(argThat(savedInfo ->
                savedInfo.getOffset() == (offset + data.length) &&
                savedInfo.getDuration().equals(duration)
        ));
    }
    
    @Test
    @DisplayName("appendData - 성공 (duration 없음)")
    void appendData_Success_NoDuration() throws IOException {
        long offset = 0L;
        byte[] data = "test data".getBytes();
        ServletInputStream inputStream = new DelegatingServletInputStream(new ByteArrayInputStream(data));
        mockTusFileInfo.setOffset(offset);
        mockTusFileInfo.setDuration(1000L); // Existing duration

        when(tusFileInfoRepository.findById(testFileId)).thenReturn(Optional.of(mockTusFileInfo));
        when(tusFileInfoRepository.save(any(TusFileInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            OutputStream mockOutputStream = mock(OutputStream.class);
            mockedFiles.when(() -> Files.newOutputStream(any(Path.class), any())).thenReturn(mockOutputStream);
            tusService.appendData(testFileId, offset, inputStream, null); // duration is null
             verify(mockOutputStream).write(data);
        }

        verify(tusFileInfoRepository).save(argThat(savedInfo ->
                savedInfo.getOffset() == (offset + data.length) &&
                savedInfo.getDuration().equals(1000L) // Duration should not change
        ));
    }


    @Test
    @DisplayName("appendData - 실패 (파일 ID 없음)")
    void appendData_Failure_FileIdNotFound() throws IOException {
        long offset = 0L;
        ServletInputStream inputStream = new DelegatingServletInputStream(new ByteArrayInputStream("test".getBytes()));
        when(tusFileInfoRepository.findById(testFileId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            tusService.appendData(testFileId, offset, inputStream, null);
        });
        assertEquals("TusFileInfo not found with id: " + testFileId, exception.getMessage());
    }

    @Test
    @DisplayName("appendData - 실패 (오프셋 불일치)")
    void appendData_Failure_OffsetMismatch() throws IOException {
        long requestOffset = 10L; // Client sends wrong offset
        mockTusFileInfo.setOffset(0L); // Server expects 0
        ServletInputStream inputStream = new DelegatingServletInputStream(new ByteArrayInputStream("test".getBytes()));

        when(tusFileInfoRepository.findById(testFileId)).thenReturn(Optional.of(mockTusFileInfo));

        IOException exception = assertThrows(IOException.class, () -> {
            tusService.appendData(testFileId, requestOffset, inputStream, null);
        });
        assertTrue(exception.getMessage().contains("Offset mismatch"));
    }
    
    @Test
    @DisplayName("appendData - 실패 (IOException during file write - simulated via Files.newOutputStream throwing)")
    void appendData_Failure_IOExceptionDuringWrite() throws IOException {
        long offset = 0L;
        ServletInputStream inputStream = new DelegatingServletInputStream(new ByteArrayInputStream("test data".getBytes()));
        mockTusFileInfo.setOffset(offset);

        when(tusFileInfoRepository.findById(testFileId)).thenReturn(Optional.of(mockTusFileInfo));

        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.newOutputStream(any(Path.class), any())).thenThrow(new IOException("Disk space error"));

            IOException exception = assertThrows(IOException.class, () -> {
                tusService.appendData(testFileId, offset, inputStream, null);
            });
            assertEquals("Disk space error", exception.getMessage());
        }
        // Verify that TusFileInfo was not saved if write failed
        verify(tusFileInfoRepository, never()).save(any(TusFileInfo.class));
    }


    // mergeUploadedFile Tests
    @Test
    @DisplayName("mergeUploadedFile - 성공 (파일 있음)")
    void mergeUploadedFile_Success_FilesExist() throws IOException {
        TusFileInfo file1 = TusFileInfo.builder().filePath("/path/to/file1.webm").build();
        TusFileInfo file2 = TusFileInfo.builder().filePath("/path/to/file2.webm").build();
        List<TusFileInfo> filesToMerge = List.of(file1, file2);
        String expectedMergePath = "/test/uploads/" + testCounselSessionId + "/" + testCounselSessionId + ".webm";

        when(tusFileInfoRepository.findAllByCounselSessionIdAndIsDeferFalseOrderByCreatedAt(testCounselSessionId)).thenReturn(filesToMerge);
        when(fileUtil.mergeWebmFile(anyList(), eq(expectedMergePath))).thenReturn(expectedMergePath);

        String resultPath = tusService.mergeUploadedFile(testCounselSessionId);

        assertEquals(expectedMergePath, resultPath);
        verify(fileUtil).mergeWebmFile(argThat(list -> list.contains(file1.getFilePath()) && list.contains(file2.getFilePath())), eq(expectedMergePath));
    }

    @Test
    @DisplayName("mergeUploadedFile - 성공 (파일 없음)")
    void mergeUploadedFile_Success_NoFiles() throws IOException {
        when(tusFileInfoRepository.findAllByCounselSessionIdAndIsDeferFalseOrderByCreatedAt(testCounselSessionId)).thenReturn(Collections.emptyList());
        String expectedMergePath = "/test/uploads/" + testCounselSessionId + "/" + testCounselSessionId + ".webm";
        when(fileUtil.mergeWebmFile(eq(Collections.emptyList()), eq(expectedMergePath))).thenReturn(expectedMergePath);


        String resultPath = tusService.mergeUploadedFile(testCounselSessionId);

        assertEquals(expectedMergePath, resultPath);
        verify(fileUtil).mergeWebmFile(eq(Collections.emptyList()), eq(expectedMergePath));
    }

    @Test
    @DisplayName("mergeUploadedFile - 실패 (FileUtil.mergeWebmFile IOException)")
    void mergeUploadedFile_Failure_FileUtilMergeThrowsIOException() throws IOException {
        when(tusFileInfoRepository.findAllByCounselSessionIdAndIsDeferFalseOrderByCreatedAt(testCounselSessionId)).thenReturn(List.of(mockTusFileInfo));
        when(fileUtil.mergeWebmFile(anyList(), anyString())).thenThrow(new IOException("Merge failed"));

        IOException exception = assertThrows(IOException.class, () -> {
            tusService.mergeUploadedFile(testCounselSessionId);
        });
        assertEquals("Merge failed", exception.getMessage());
    }

    // getUploadedFile Tests
    @Test
    @DisplayName("getUploadedFile - 성공")
    void getUploadedFile_Success() throws IOException {
        Resource mockResource = mock(Resource.class);
        when(tusFileInfoRepository.findById(testFileId)).thenReturn(Optional.of(mockTusFileInfo));
        when(fileUtil.getUrlResource(mockTusFileInfo.getFilePath())).thenReturn(mockResource);

        Resource result = tusService.getUploadedFile(testFileId);

        assertNotNull(result);
        assertEquals(mockResource, result);
        verify(fileUtil).getUrlResource(mockTusFileInfo.getFilePath());
    }

    @Test
    @DisplayName("getUploadedFile - 실패 (파일 ID 없음)")
    void getUploadedFile_Failure_FileIdNotFound() {
        when(tusFileInfoRepository.findById(testFileId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            tusService.getUploadedFile(testFileId);
        });
        assertEquals("TusFileInfo not found with id: " + testFileId, exception.getMessage());
    }

    @Test
    @DisplayName("getUploadedFile - 실패 (FileUtil.getUrlResource IOException)")
    void getUploadedFile_Failure_FileUtilGetUrlResourceThrowsIOException() throws IOException {
        when(tusFileInfoRepository.findById(testFileId)).thenReturn(Optional.of(mockTusFileInfo));
        when(fileUtil.getUrlResource(mockTusFileInfo.getFilePath())).thenThrow(new IOException("Cannot read file"));

        IOException exception = assertThrows(IOException.class, () -> {
            tusService.getUploadedFile(testFileId);
        });
        assertEquals("Cannot read file", exception.getMessage());
    }

    // deleteUploadedFile Tests
    @Test
    @DisplayName("deleteUploadedFile - 성공")
    void deleteUploadedFile_Success() {
        String expectedFolderPath = Paths.get("/test/uploads", testCounselSessionId).toAbsolutePath().toString();
        when(tusFileInfoRepository.findById(testFileId)).thenReturn(Optional.of(mockTusFileInfo));
        // mockTusFileInfo.getCounselSession() already returns mockCounselSession due to builder setup
        // mockCounselSession.getId() already returns testCounselSessionId due to builder setup

        doNothing().when(fileUtil).deleteDirectory(expectedFolderPath);
        doNothing().when(tusFileInfoRepository).deleteAllByCounselSessionId(testCounselSessionId);

        assertDoesNotThrow(() -> tusService.deleteUploadedFile(testFileId));

        verify(tusFileInfoRepository).findById(testFileId);
        verify(fileUtil).deleteDirectory(expectedFolderPath);
        verify(tusFileInfoRepository).deleteAllByCounselSessionId(testCounselSessionId);
    }

    @Test
    @DisplayName("deleteUploadedFile - 실패 (FileUtil.deleteDirectory IOException, 저장소 삭제는 시도됨, RuntimeException으로 변환)")
    void deleteUploadedFile_Failure_FileUtilDeleteDirectoryThrowsIOException_RuntimeExceptionThrown() throws Exception {
        String expectedFolderPath = Paths.get("/test/uploads", testCounselSessionId).toAbsolutePath().toString();
        when(tusFileInfoRepository.findById(testFileId)).thenReturn(Optional.of(mockTusFileInfo));
        
        doThrow(new IOException("Cannot delete directory")).when(fileUtil).deleteDirectory(expectedFolderPath);
        // Repository deletion should still be attempted, and if it succeeds, the overall method still fails due to the prior exception.
        doNothing().when(tusFileInfoRepository).deleteAllByCounselSessionId(testCounselSessionId);


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tusService.deleteUploadedFile(testFileId);
        });
        assertTrue(exception.getMessage().contains("Failed to delete directory"));
        assertEquals(IOException.class, exception.getCause().getClass());
        
        verify(tusFileInfoRepository).findById(testFileId);
        verify(fileUtil).deleteDirectory(expectedFolderPath);
        // This verify confirms that even if deleteDirectory fails, deleteAllByCounselSessionId is still called.
        // However, the service method's current logic re-throws after the first failure,
        // so deleteAllByCounselSessionId might not be reached if deleteDirectory throws.
        // The refactored service method has try-catch for deleteDirectory and then another for deleteAllByCounselSessionId.
        // So, if deleteDirectory fails and throws, the method execution stops there and re-throws.
        // Let's adjust the test to reflect that the second operation (DB delete) won't be called if the first (directory delete) throws.
        // The service re-throws new RuntimeException wrapping the original.
        verify(tusFileInfoRepository, never()).deleteAllByCounselSessionId(testCounselSessionId);
    }
    
    @Test
    @DisplayName("deleteUploadedFile - 실패 (TusFileInfoRepository.deleteAllByCounselSessionId IOException, RuntimeException으로 변환)")
    void deleteUploadedFile_Failure_RepositoryDeleteThrowsException_RuntimeExceptionThrown() throws Exception {
        String expectedFolderPath = Paths.get("/test/uploads", testCounselSessionId).toAbsolutePath().toString();
        when(tusFileInfoRepository.findById(testFileId)).thenReturn(Optional.of(mockTusFileInfo));
        
        doNothing().when(fileUtil).deleteDirectory(expectedFolderPath); // Directory deletion succeeds
        doThrow(new RuntimeException("DB error")).when(tusFileInfoRepository).deleteAllByCounselSessionId(testCounselSessionId); // DB deletion fails

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tusService.deleteUploadedFile(testFileId);
        });
        assertTrue(exception.getMessage().contains("Failed to delete TusFileInfo records"));
        assertEquals("DB error", exception.getCause().getMessage());
        
        verify(tusFileInfoRepository).findById(testFileId);
        verify(fileUtil).deleteDirectory(expectedFolderPath);
        verify(tusFileInfoRepository).deleteAllByCounselSessionId(testCounselSessionId);
    }


    @Test
    @DisplayName("deleteUploadedFile - 실패 (파일 ID 없음)")
    void deleteUploadedFile_Failure_FileIdNotFound() {
        when(tusFileInfoRepository.findById(testFileId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tusService.deleteUploadedFile(testFileId);
        });
        assertTrue(exception.getMessage().contains("Tus 파일 정보를 찾을 수 없습니다. ID: " + testFileId));
        verify(fileUtil, never()).deleteDirectory(anyString());
        verify(tusFileInfoRepository, never()).deleteAllByCounselSessionId(anyString());
    }

    @Test
    @DisplayName("deleteUploadedFile - 실패 (CounselSession 없음)")
    void deleteUploadedFile_Failure_CounselSessionIsNull() {
        // Create a mock TusFileInfo that will return null for getCounselSession()
        TusFileInfo tusInfoWithoutSession = TusFileInfo.builder().id(testFileId).counselSession(null).build();
        // Or, if mockTusFileInfo is a @Mock: when(mockTusFileInfo.getCounselSession()).thenReturn(null);
        // Since mockTusFileInfo is a real object created by builder, we alter it or use a different one.
        // For simplicity, let's use a new one for this test.
        
        // To use the existing mockTusFileInfo and make its counselSession null for this test:
        // Need to make mockTusFileInfo a spy or ensure its internal state can be changed.
        // The current setup uses a real TusFileInfo object. Let's create a new one for this specific test.
        TusFileInfo fileInfoSpy = spy(mockTusFileInfo); // Spy on the existing instance
        when(fileInfoSpy.getCounselSession()).thenReturn(null); // Stub getCounselSession to return null

        when(tusFileInfoRepository.findById(testFileId)).thenReturn(Optional.of(fileInfoSpy));


        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            tusService.deleteUploadedFile(testFileId);
        });
        assertTrue(exception.getMessage().contains("TusFileInfo ID " + testFileId + " 에 연결된 상담 세션 정보가 없습니다."));

        verify(tusFileInfoRepository).findById(testFileId);
        verify(fileUtil, never()).deleteDirectory(anyString());
        verify(tusFileInfoRepository, never()).deleteAllByCounselSessionId(anyString());
    }
}
