package com.example.board.service;

import com.example.board.entity.Board;
import com.example.board.entity.File;
import com.example.board.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    // FileRepositoryを使用
    private final FileRepository fileRepository;

    // @Valueでpropertiesで値を持ってくる(ファイルセーブ経路)
    @Value("${file.upload.directory}")
    private String uploadDirectory;

    //　ファイルDBにセーブ
    @Transactional
    public File saveFile(MultipartFile file, Board board) throws IOException {

        System.out.println("===　ファイルセーブスタート　===");
        System.out.println("アップロードpath　:　" + uploadDirectory);
        System.out.println("原本ファイル名 : " + file.getOriginalFilename());
        System.out.println("ファイル容量 : " + file.getSize());

        if (uploadDirectory == null) {
            System.err.println("エラー: uploadDirectory is NULL!");
            throw new RuntimeException("Upload directory not configured.");
        }

        // アップロードdirectory生成
        java.io.File uploadDIr = new java.io.File(uploadDirectory);
        System.out.println("ファイル経路 : " + uploadDIr.getAbsolutePath());

        //　フォルダが存在しない場合
        if (!uploadDIr.exists()){
            boolean created = uploadDIr.mkdirs();
            System.out.println("Directory生成 : " + created);

            if (!created){
                throw new IOException("Directoryを生成できませんでした:" + uploadDIr.getAbsolutePath());
            }
        } else {
            System.out.println("Directoryが既に存在しております！");
        }

        //　原本ファイル名
        String oriFileName = file.getOriginalFilename();
        if (oriFileName == null || oriFileName.isEmpty()) {
            throw new IOException("ファイル名がいないです");
        }

        //　セーブするファイル名生成
        String savedFileName = createSavedFileName(oriFileName);
        System.out.println("セーブするファイル名: " + savedFileName);

        //　セーブ経路生成
        String filePath = uploadDirectory + java.io.File.separator + savedFileName;
        System.out.println("セーブするファイル経路: " + filePath);

        //　ファイルセーブ
        try {
            Path path = Paths.get(filePath);
            Files.write(path, file.getBytes());
            System.out.println("ファイルセーブ完了！！");
        } catch (IOException e){
            System.err.println("ファイルセーブに失敗！！" + e.getMessage());
            throw e;
        }

        File fileEntity = new File();
        fileEntity.setOriFileName(oriFileName);
        fileEntity.setSavedFileName(savedFileName);
        fileEntity.setFilePath(filePath);
        fileEntity.setFileSize(file.getSize());
        fileEntity.setFileType(file.getContentType());
        fileEntity.setBoard(board);

        File saved = fileRepository.save(fileEntity);
        System.out.println("DB セーブ完了: ID=" + saved.getId());
        System.out.println("=== ファイルセーブ終了 ===");

        return saved;
    }

    //　セーブするファイル名生成
    private String createSavedFileName(String oriFileName){

        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        return dateTime + "_" + oriFileName;
    }

    //　特定掲示板のファイルリストけんさ
    public List<File> getFilesByBoardId(Long boardId){
        //　掲示板のIDで検索
        return fileRepository.findByBoard_IdOrderByCreatedAtAsc(boardId);
    }

    //　ファイル削除
    @Transactional
    public void deleteFile(Long fileId) throws IOException{

        //　ファイル情報検索
        File fileEntity = fileRepository.findById(fileId).orElseThrow(() -> new IllegalArgumentException("ファイルが見つかりません。"));

        //　削除
        Path path = Paths.get(fileEntity.getFilePath());
        Files.deleteIfExists(path);
        fileRepository.deleteById(fileId);
    }

    //  ファイルの情報検索
    public File getFileById(Long fileId){
        return fileRepository.findById(fileId).orElseThrow(() -> new IllegalArgumentException("ファイルが見つかりません。"));
    }
}
