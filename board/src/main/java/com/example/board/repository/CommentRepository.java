package com.example.board.repository;

import com.example.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//　Repositoryアノテーション
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    //　特定掲示文のコメントのリストを検索
    // 降順にコメントを持ってくる
    List<Comment> findByBoard_IdOrderByCreatedAtDesc(Long boardId);
    //　特定掲示文のコメントの数を検索
    Long countByBoard_id(Long boardId);
}
