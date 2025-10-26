package com.example.board.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// DBとマッチングされるクラス
@Entity
//　Table名
@Table(name = "comment")
//　Getter、Setterメソッド自動生成
@Getter
@Setter
//　値のいないコンストラクタ自動生成
@NoArgsConstructor
//　全てのコンストラクタ自動生成
@AllArgsConstructor
public class Comment {
    // PK
    @Id
    //　値順番に自動生成
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //　長い文章を保存できるタイプ
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    //　一つの掲示板に多数のコメントを作成できように
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;
    //　一人が多数のコメントを作成できるように
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    //　コメントを作成日時
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    //　DBに保存される直前に実行される
    @PrePersist
    protected void onCreate(){
        //　コメントの作成時間
        createdAt = LocalDateTime.now();
    }
}
