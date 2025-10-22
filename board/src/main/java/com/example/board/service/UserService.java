package com.example.board.service;

//　UserEntityインポート
import com.example.board.entity.User;
//　UserRepositoryの中のメソッドを使用するためにインポート
//　IDチェックメソッドuserRepository.existByUsername(username);を使用
import com.example.board.repository.UserRepository;
//　コンストラクタ自動生成lombok機能
import lombok.RequiredArgsConstructor;
//　サービスアノテーション
import org.springframework.stereotype.Service;
//　会員登録メソッド全ての過程をトランザクション処理するためのアノテーション
//　会員登録過程中にErrorが起きると全ての過程を取り消しします（ROLLBACK、トランザクションの原子性）
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    //　会員登録データ入力＆データベースセーブ
    @Transactional
    public User signup(String username, String password, String email){

        if(userRepository.existsByUsername(username)){
            throw new IllegalArgumentException("使用中のIDです他のIDを入力してください。");
        }

        User user =  new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        return userRepository.save(user);
    }

    //　ID重複検査用
    public boolean usernameCheck(String username){
        return userRepository.existsByUsername(username);
    }

    public User login(String username, String password){
        // usernameで会員を検索し、ない場合は.orElseを使いNullを変換する
        User user = userRepository.findByUsername(username).orElse(null);
        //　会員情報がいない場合
        if(user == null){
            throw new IllegalArgumentException("IDまたはPASSWORDが一致しません、もう一度確認してください。");
        }
        //　パスワードが合ってない場合
        if(!user.getPassword().equals(password)){
            throw new IllegalArgumentException("IDまたはPASSWORDが一致しません、もう一度確認してください。");
        }
        return user;
    }
}
