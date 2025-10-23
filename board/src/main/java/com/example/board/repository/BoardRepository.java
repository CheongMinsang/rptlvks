package com.example.board.repository;

// BoardEntity
import com.example.board.entity.Board;
// JPAの機能を使用
import org.springframework.data.jpa.repository.JpaRepository;
//　Repositoryアノテーション
import org.springframework.stereotype.Repository;

import java.util.List;

// DBに接続するRepositoryであることをアノテーションで示す
@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    // 全ての作成した本文を持ってくる
    List<Board> findAllByOrderByCreatedAtDesc();
    //　特定のUserが作成した本文を持ってくる
    List<Board> findByUser_IdOrderByCreatedAtDesc(Long userId);
}
