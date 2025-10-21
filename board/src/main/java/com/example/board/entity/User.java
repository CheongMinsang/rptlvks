package com.example.board.entity;

//　JPAアノテーション
import jakarta.persistence.*;
//　ゲッターとセッターメソッド自動生成
import lombok.Getter;
import lombok.Setter;
//　デフォルトコンストラクタ自動生成
import lombok.NoArgsConstructor;
//　全てのフィールドを受けるコンストラクタ自動生成
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

//　データベーステーブルとマッピング
@Entity
//　テーブルの名前
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    // PK
    @Id
    // @GeneratedValueは基本キー値を自動生成
    // strategy = GenerationType.IDENTITYは自動的に増加する番号を生成
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }
}
