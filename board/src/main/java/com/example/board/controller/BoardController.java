package com.example.board.controller;

import com.example.board.entity.Board;
import com.example.board.entity.Comment;
import com.example.board.entity.User;
import com.example.board.service.BoardService;
import com.example.board.service.CommentService;
import com.example.board.entity.File;
import com.example.board.service.FileService;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    // ファイルアップロード追加
    private final FileService fileService;

    //　掲示板リスト一覧(ページング追加,検索機能追加)
    @GetMapping("/list")
    public String boardList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "type", required = false, defaultValue = "all") String type,
            Model model){
        // 一ページにごとに見せる数
        int size = 3;
        // ページングされた作成文リスト
        //　Page<Board> boardPage = boardService.getBoardsWithPaging(page, size);
        //　model.addAttribute("boardPage", boardPage);

        // ページングされた作成文データ
        Page<Board> boardPage;

        // 検索keywordがある場合
        if(!keyword.isEmpty()){
            boardPage = boardService.searchByTitleOrContent(keyword, type, page, size);
        //　検索keywordがいない場合
        } else {
            boardPage = boardService.getBoardsWithPaging(page, size);
        }

        //　リスト全てのをHTMLにわたす
        model.addAttribute("boardPage", boardPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);
        // List<Board> boards = boardService.getAllBoards();
        // model.addAttribute("boards",boards);
        return "board/list";
    }

    //　掲示板作成ページ
    @GetMapping("/write")
    public String writeForm(HttpSession session,
                            RedirectAttributes redirectAttributes){
        //　sessionからログイン情報を持ってくる
        User loginUser = (User) session.getAttribute("loginUser");
        //　ログインしてない場合
        if(loginUser == null){
            redirectAttributes.addFlashAttribute("error","ログインが必要です！！");
            return "redirect:/login";
        }
        return "board/write";
    }

    //　作成処理
    @PostMapping("/write")
    public String write(@RequestParam String title,
                        @RequestParam String context,
                        @RequestParam(value = "files", required = false) List<MultipartFile> files,
                        HttpSession session,
                        RedirectAttributes redirectAttributes){
        User loginUser = (User)session.getAttribute("loginUser");
        if(loginUser == null){
            redirectAttributes.addFlashAttribute("error","ログインが必要です！！");
            return "redirect:/login";
        }

        Board board = boardService.createBoard(title, context, loginUser);

        // ファイルアップロード追加
        //　実際にデータを持っているデータをセーブ
        if (files != null && !files.isEmpty()){
            for (MultipartFile file : files){
                //　ファイルが選択された場合
                if (!file.isEmpty()){
                    try {
                        //　ファイルセーブ
                        fileService.saveFile(file, board);
                    } catch (IOException e) {
                        //　失敗した場合
                        redirectAttributes.addFlashAttribute("error", "ファイルアップロードに失敗しました!");
                    }
                }
            }
        }

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

        //　ファイルリストを追加
        List<File> files=  fileService.getFilesByBoardId(id);

        model.addAttribute("board",board);
        model.addAttribute("comments", comments);
        model.addAttribute("commentCount", commentCount);
        model.addAttribute("files", files);

        return "board/detail";
    }
    //　修正
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, HttpSession session,
                           RedirectAttributes redirectAttributes){
        User loginUser = (User)session.getAttribute("loginUser");
        if(loginUser == null){
            redirectAttributes.addFlashAttribute("error","ログインが必要です！！");
            return "redirect:/login";
        }
        Board board = boardService.getIdBoard(id);
        //　作成した人とsessionが違う場合
        if(!board.getUser().getId().equals(loginUser.getId())){
            redirectAttributes.addFlashAttribute("error","自分が作成した物だけ修正できます！！");
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
                       HttpSession session,
                       RedirectAttributes redirectAttributes){
        User loginUser = (User)session.getAttribute("loginUser");

        if (loginUser == null) {
            redirectAttributes.addFlashAttribute("error","ログインが必要です！！");
            return "redirect:/login";
        }

        Board board = boardService.getIdBoard(id);
        if(!board.getUser().getId().equals(loginUser.getId())){
            redirectAttributes.addFlashAttribute("error","自分が作成した物だけ修正できます！！");
            return "redirect:/board/list";
        }

        boardService.updateBoard(id, title, context);
        redirectAttributes.addFlashAttribute("success", "掲示板を修正しました!");
        return "redirect:/board/"+id;

    }
    //　削除
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session,RedirectAttributes redirectAttributes){
        User loginUser = (User)session.getAttribute("loginUser");

        if (loginUser == null) {
            redirectAttributes.addFlashAttribute("error","ログインが必要です！！");
            return "redirect:/login";
        }
        Board board = boardService.getIdBoard(id);
        if(!board.getUser().getId().equals(loginUser.getId())){
            redirectAttributes.addFlashAttribute("error","自分が作成した物だけ修正できます！！");
            return "redirect:/board/list";
        }
        //　削除処理
        boardService.deleetBoard(id);
        redirectAttributes.addFlashAttribute("success", "掲示板を削除しました!");
        return "redirect:/board/list";
    }
}
