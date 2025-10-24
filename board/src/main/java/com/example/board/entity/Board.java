package com.example.board.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
// テーブルの名前
@Table(name = "Board")
// getter,setterメソッド自動生成
@Getter
@Setter
// パラメータがないメソッド自動生成
@NoArgsConstructor
// すべてのフィールドをパラメータとするコンストラクタ自動生成
@AllArgsConstructor
public class Board {
    // PK
    @Id
    // PK値自動生成
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 掲示板No
    private Long id;

    // 掲示板タイトル
    @Column(nullable = false, length = 200)
    private String title;

    // TEXT型の本文
    @Column(nullable = false, columnDefinition = "TEXT")
    private String context;

    // @ManyToOne=一人のUserが掲示板を何回も作成可能
    // @fetch = FetchType.LAZY=必要な時だけUserの情報を持ってくる
    @ManyToOne(fetch = FetchType.LAZY)
    // FK設定し、DBにuser_idという列を作る
    @JoinColumn(name = "user_id")
    private User user;

    // 作成日時
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    //　修正日時
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // DBに保存される直前の日時を保存
    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }

    // 本文のクリック数を記録する
    private Integer views = 0;
}
