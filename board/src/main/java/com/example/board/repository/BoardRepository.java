package com.example.board.repository;

// BoardEntity
import com.example.board.entity.Board;
// JPAの機能を使用
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    // ページング処理のためのメソッド
    //　Page<Board>: ページングされたBoardのデータ
    //　 現在ページのデータ、全体の作成リストの数、全体ページの数、現在ページのナンバーの情報を持っている
    // Pageable pageable: ページングの情報
    //　 今何番目のページなのか、一ページに何個を見せてくれるか、どんな条件で見せるのかを確認できる
    //　findAll: extends JpaRepositoryで使用
    //　
    Page<Board> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
