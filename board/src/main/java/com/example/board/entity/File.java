package com.example.board.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "file")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 原本ファイル名
    @Column(nullable = false)
    private String oriFileName;

    //　セーブされたファイル名
    @Column(nullable = false)
    private String savedFileName;

    // セーブ位置
    @Column(nullable = false)
    private String filePath;

    private Long fileSize;

    private String fileType;

    // 一つの掲示板に多数のファイルアップロード可能
    //　掲示板特定
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }
}
