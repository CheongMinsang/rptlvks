package com.example.board.repository;

import com.example.board.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    // 特定掲示板のファイルリスト検索
    List<File> findByBoard_IdOrderByCreatedAtAsc(Long boardId);

    //　特定掲示板のファイルの数をカウント
    Long countByBoard_id(Long boardId);
}
