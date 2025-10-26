package com.example.board.controller;

import com.example.board.entity.Board;
import com.example.board.entity.Comment;
import com.example.board.entity.User;
import com.example.board.service.BoardService;
import com.example.board.service.CommentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

//　WebRequest処理アノテーション
@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    //  CommentService使用
    private final CommentService commentService;
    //　掲示板の情報
    private final BoardService boardService;

    //　コメント作成
    @PostMapping("/create")
    public String createComment(@RequestParam Long boardId,
                                @RequestParam String content,
                                HttpSession session){
        //　ログイン確認
        User loginUser = (User) session.getAttribute("loginUser");
        //　ロジックをしてない場合
        if(loginUser == null){
            //　後でalert追加("ログインが必要な機能です！！")
            return "redirect:/login";
        }

        //　コメントを作成する掲示文を探す
        Board board = boardService.getIdBoard(boardId);
        //　コメントを作成
        commentService.createComment(content, board, loginUser);
        //　コメントを作成した掲示文の詳細ページに移動
        return "redirect:/board/" + boardId;
    }

    //　コメント削除
    @PostMapping("/delete/{commentId}")
    public String deleteComment(@PathVariable Long commentId,
                                @RequestParam Long boardId,
                                HttpSession session){
        //　ログイン確認
        User loginUser = (User) session.getAttribute("loginUser");
        //　ロジックをしてない場合
        if(loginUser == null){
            //　後でalert追加("ログインが必要な機能です！！")
            return "redirect:/login";
        }
        //　作成したコメントを検索
        Comment comment = commentService.getCommentById(commentId);
        //　コメントの作成者とロジックした人が違う場合
        if (!comment.getUser().getId().equals(loginUser.getId())){
            //　後でalert追加("自分が作成したコメントのみ削除可能です！！")
            return "redirect:/board/" + boardId;
        }
        //　コメントを削除
        commentService.deleteComment(commentId);
        //　コメントを削除した掲示文の詳細ページに移動
        return "redirect:/board/" + boardId;
    }
}
