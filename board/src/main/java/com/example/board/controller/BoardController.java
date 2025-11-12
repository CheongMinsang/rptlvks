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

        try {
            Board board = boardService.createBoard(title, context, loginUser);
            System.out.println("掲示板生成完了！" + board.getId());


            // ファイルアップロード追加
            //　実際にデータを持っているデータをセーブ
            if (files != null && !files.isEmpty()) {
                System.out.println("ファイル数" + files.size());

                for (MultipartFile file : files) {
                    //　ファイルが選択された場合
                    if (!file.isEmpty()) {
                        System.out.println("ファイルアップロードテスト:" + files.size());

                        try {
                            //　ファイルセーブ
                            fileService.saveFile(file, board);
                            System.out.println("ファイルセーブ完了" + files.size());
                        } catch (IOException e) {
                            System.out.println("ファイルセーブ失敗" + e.getMessage());
                            e.printStackTrace();
                            //　失敗した場合
                            redirectAttributes.addFlashAttribute("error", "ファイルアップロードに失敗しました!"
                            + file.getOriginalFilename());
                        } catch (Exception e) { // ⬅️ 추가
                            System.err.println("FATAL ERROR during file upload loop: " + e.getMessage());
                            e.printStackTrace();
                            redirectAttributes.addFlashAttribute("error", "치명적인 파일 업로드 오류 발생!");
                        }
                    }
                }
            }

            redirectAttributes.addFlashAttribute("success", "掲示板を作成しました。");
            return "redirect:/board/list";

        } catch (Exception e) {
            System.err.println("掲示板削除失敗！" + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error","掲示板作成に失敗しました!");
            return "redirect:/board/write";
        }
    }

    //　詳細なページみる
    @GetMapping("/{id}")
    public String boardDetail(@PathVariable Long id, Model model) {
        try {
            //　クリック数アップ
            boardService.countViews(id);
            //　@PathVariable Long idでもらったid情報でDB検索
            Board board = boardService.getIdBoard(id);
            System.out.println("掲示板検索" + board.getId());

            //  コメントリストとコメント数を検索
            List<Comment> comments = commentService.getCommentsByBoardId(id);
            Long commentCount = commentService.getCommentCount(id);

            //　ファイルリストを追加
            List<File> files = fileService.getFilesByBoardId(id);
            System.out.println("ファイル数" + files.size());

            model.addAttribute("board", board);
            model.addAttribute("comments", comments);
            model.addAttribute("commentCount", commentCount);
            model.addAttribute("files", files);

            return "board/detail";

        } catch (Exception e) {
            System.err.println("詳細ページを見るのに失敗しました" + e.getMessage());
            e.printStackTrace();
            return "redirect:/board/list";
        }
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
