package com.example.board.repository;

//　Entityクラス
import com.example.board.entity.User;
//　UserEntityクラスをLong(PKであるidのタイプ)タイプで継承する(extends)
import org.springframework.data.jpa.repository.JpaRepository;
// このインタフェースがデータベースにアクセスするRepostitoryというアノテーション
import org.springframework.stereotype.Repository;
// Nullを変換する可能背がある値を安全に扱うためのクラス
import java.util.Optional;

@Repository
//　JpaRepositoryの継承もらうことでCRUDメソッドを使用可能
public interface UserRepository extends JpaRepository<User, Long> {

    // DBにusernameが存在する場合情報を持ってくる
    Optional<User> findByUsername(String username);
    // データベースにusernameが存在するか否かを確認する
    // SELECT COUNT(*) FROM users WHERE usernameと同じ役割
    boolean existsByUsername(String username);
}
