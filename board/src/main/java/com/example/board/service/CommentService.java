package com.example.board.service;

import com.example.board.entity.Board;
import com.example.board.entity.Comment;
import com.example.board.entity.User;
import com.example.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//　サービスアノテーション
@Service
//　finalフィールドのコンストラクタを自動生成
@RequiredArgsConstructor
public class CommentService {
    //　CommentRepository使用
    private final CommentRepository commentRepository;

    //　特定掲示板のコメントリストを検索
    public List<Comment> getCommentsByBoardId(Long boardId){
        //　掲示板のIDで検索、最新のコメントを一番上に表示
        return commentRepository.findByBoard_IdOrderByCreatedAtDesc(boardId);
    }

    //　トランザクション処理のアノテーション
    @Transactional
    //　コメント作成
    public Comment createComment(String content, Board board, User user){
        //　Commentオブジェクト生成
        Comment comment = new Comment();
        //　コメントの内容、どの掲示文にするか、誰がコメントを作成したのかSetterで値設定
        comment.setContent(content);
        comment.setBoard(board);
        comment.setUser(user);
        //　Insert
        return commentRepository.save(comment);
    }

    //　コメント削除
    @Transactional
    public void deleteComment(Long commentId){
        Comment comment = commentRepository.findById(commentId)
                //　コメントがいない場合
                .orElseThrow(() -> new IllegalArgumentException("コメントが見つかりません！！"));
        //　Deleteクエリ実行
        commentRepository.deleteById(commentId);
    }

    //　特定のコメントの数検索
    public Long getCommentCount(Long boardId){
        return commentRepository.countByBoard_id(boardId);
    }

    //　コメントの詳細(作成者確認用)
    public Comment getCommentById(Long commentId){
        return commentRepository.findById(commentId)
                //　コメントがいない場合
                .orElseThrow(() -> new IllegalArgumentException("コメントが見つかりません!!"));
    }
}
