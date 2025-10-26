package com.example.board.controller;

import com.example.board.entity.Board;
import com.example.board.entity.Comment;
import com.example.board.entity.User;
import com.example.board.service.BoardService;
import com.example.board.service.CommentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//　Controllerアノテーション
@Controller
//　boardで始まるURL
@RequestMapping("/board")
//　コンストラクタ自動生成
@RequiredArgsConstructor
public class BoardController {

    //　BoardService使用
    private final BoardService boardService;
    //  コメント機能のために使用する
    private final CommentService commentService;

    //　掲示板リスト一覧(ページング追加)
    @GetMapping("/list")
    public String boardList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model){
        // 一ページにごとに見せる数
        int size = 3;
        // ページングされた作成文リスト
        Page<Board> boardPage = boardService.getBoardsWithPaging(page, size);
        model.addAttribute("boardPage", boardPage);

        //　リスト全てのをHTMLにわたす
        // List<Board> boards = boardService.getAllBoards();
        // model.addAttribute("boards",boards);
        return "board/list";
    }

    //　掲示板作成ページ
    @GetMapping("/write")
    public String writeForm(HttpSession session){
        //　sessionからログイン情報を持ってくる
        User loginUser = (User) session.getAttribute("loginUser");
        //　ログインしてない場合
        if(loginUser == null){
            // ！alert後で作る(”ログインが必要です！”)
            return "redirect:/login";
        }
        return "board/write";
    }

    //　作成処理
    @PostMapping("/write")
    public String write(@RequestParam String title,
                        @RequestParam String context,
                        HttpSession session){
        User loginUser = (User)session.getAttribute("loginUser");
        if(loginUser == null){
            // ！alert後で作る(”ログインが必要です！”)
            return "redirect:/login";
        }
        //　作成
        boardService.createBoard(title, context, loginUser);
        return "redirect:/board/list";
    }

    //　詳細なページみる
    @GetMapping("/{id}")
    public String boardDetail(@PathVariable Long id, Model model){
        //　クリック数アップ
        boardService.countViews(id);
        //　@PathVariable Long idでもらったid情報でDB検索
        Board board = boardService.getIdBoard(id);

        //  コメントリストとコメント数を検索
        List<Comment> comments = commentService.getCommentsByBoardId(id);
        Long commentCount = commentService.getCommentCount(id);

        model.addAttribute("board",board);
        model.addAttribute("comments", comments);
        model.addAttribute("commentCount", commentCount);

        return "board/detail";
    }
    //　修正
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, HttpSession session){
        User loginUser = (User)session.getAttribute("loginUser");
        if(loginUser == null){
            // ！alert後で作る(”ログインが必要です！”)
            return "redirect:/login";
        }
        Board board = boardService.getIdBoard(id);
        //　作成した人とsessionが違う場合
        if(!board.getUser().getId().equals(loginUser.getId())){
            // ！alert後で作る(”自分が作成した物のみ修正できません！”)
            return "redirect:/board/list";
        }
        model.addAttribute("board",board);
        return "board/edit";
    }
    //　修正処理
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @RequestParam String title,
                       @RequestParam String context,
                       HttpSession session){
        User loginUser = (User)session.getAttribute("loginUser");

        if (loginUser == null) {
            // ！alert後で作る(”ログインが必要です！”)
            return "redirect:/login";
        }

        Board board = boardService.getIdBoard(id);
        if(!board.getUser().getId().equals(loginUser.getId())){
            // ！alert後で作る(”自分が作成した物のみ修正できません！”)
            return "redirect:/board/list";
        }

        boardService.updateBoard(id, title, context);

        return "redirect:/board/"+id;

    }
    //　削除
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session){
        User loginUser = (User)session.getAttribute("loginUser");

        if (loginUser == null) {
            // ！alert後で作る(”ログインが必要です！”)
            return "redirect:/login";
        }
        Board board = boardService.getIdBoard(id);
        if(!board.getUser().getId().equals(loginUser.getId())){
            // ！alert後で作る(”自分が作成した物のみ修正できません！”)
            return "redirect:/board/list";
        }
        //　削除処理
        boardService.deleetBoard(id);
        return "redirect:/board/list";
    }
}
