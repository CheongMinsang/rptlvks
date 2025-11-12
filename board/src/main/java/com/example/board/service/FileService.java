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

        // アップロードdirectory生成
        java.io.File uploadDIr = new java.io.File(uploadDirectory);

        //　フォルダが存在しない場合
        if (!uploadDIr.exists()){
            uploadDIr.mkdirs();
        }

        //　原本ファイル名
        String oriFileName = file.getOriginalFilename();

        //　セーブするファイル名生成
        String savedFileName = createSavedFileName(oriFileName);

        //　セーブ経路生成
        String filePath = uploadDirectory + java.io.File.separator + savedFileName;

        //　ファイルセーブ
        Path path = Paths.get(filePath);
        Files.write(path, file.getBytes());

        File fileEntity = new File();
        fileEntity.setOriFileName(oriFileName);
        fileEntity.setSavedFileName(savedFileName);
        fileEntity.setFilePath(filePath);
        fileEntity.setFileSize(file.getSize());
        fileEntity.setFileType(file.getContentType());
        fileEntity.setBoard(board);

        return fileRepository.save(fileEntity);
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
