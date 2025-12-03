package com.example.board.controller;

import com.example.board.entity.User;
// userServiceを使用ため宣言する
import com.example.board.service.UserService;
//　コンストラクタ自動生成
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
//　WebRequestを処理するControllerアノテーション
import org.springframework.stereotype.Controller;
//　Viewにデータを渡すオブジェクト
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
//　Requestパラメータをもらう
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //　会員登録ページ
    @GetMapping("/signup")
    public String signupForm(){
        //　templates/signup.htmlを作る
        return "signup";
    }

    //　会員登録処理
    @PostMapping("/signup")
    public String signup(@RequestParam String username, @RequestParam String password,
                         @RequestParam String email, Model model){
        try{
            //　会員登録実行
            User user = userService.signup(username, password, email);
            model.addAttribute("successMessage", "会員登録が完了しました!" );
            return "redirect:/?success=signup";
        //　Error処理
        }catch (IllegalArgumentException e){
            model.addAttribute("errorMessage",e.getMessage());
            //　また会員登録ページへ
            return "signup";
        }
    }
    //　ログインページ
    @GetMapping("/login")
    public String loginForm(){
        //　templates/login.html
        return "login";
    }

    // ログイン処理
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session, Model model){
        try{
            //　ログインを試し、成功すれば会員の情報を持ってくる
            User user = userService.login(username, password);
            session.setAttribute("loginUser", user);
            //　ホーム画面え戻る
            return "redirect:/?success=login";
        }catch(IllegalArgumentException e){
            // model.addAttribute("IDまたはPASSWORDが一致しません、もう一度確認してください。", e.getMessage());
            model.addAttribute("errorMessage", "IDまたはPASSWORDが一致しません、もう一度確認してください。");
            //　失敗すると再び同じログインページへ
            return "login";
        }
    }

    //　ログアウト処理
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        // ホームに戻る
        return "redirect:/?success=logout";
    }
}
