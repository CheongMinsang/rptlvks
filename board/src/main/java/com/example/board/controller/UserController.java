package com.example.board.controller;

import com.example.board.entity.User;
// userServiceを使用ため宣言する
import com.example.board.service.UserService;
//　コンストラクタ自動生成
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
        //　templates/signup.htmlを見せる
        return "signup";
    }

    //　会員登録処理
    @PostMapping("/signup")
    public String signup(@RequestParam String username, @RequestParam String password,
                         @RequestParam String email, Model model){
        try{

            //　会員登録実行
            User user = userService.signup(username, password, email);
            return "redirect:/";

        //　Error処理
        }catch (IllegalArgumentException e){
            model.addAttribute("申し訳ございません、不可能な要請です、再度確認してください",e.getMessage());
            //　また会員登録ページへ
            return "signup";
        }
    }
}
