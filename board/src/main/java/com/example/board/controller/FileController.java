package com.example.board.controller;

import com.example.board.entity.File;
import com.example.board.entity.User;
import com.example.board.service.BoardService;
import com.example.board.service.FileService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final BoardService boardService;

    // ファイルダウンロード
    @GetMapping("/download/{fileId}")
    //　@PathVariableで{fileId}の値を持ってくる
    //　ResponseEntity<Resource>でファイルデータをHTTPresponseに送る
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId){
        try {
            //　ファイル情報検索
            File fileEntity = fileService.getFileById(fileId);

            //　ファイルセーブのための経路生成
            Path path = Paths.get(fileEntity.getFilePath());
            Resource resource = new UrlResource(path.toUri());

            // ファイルが存在するか確認
            if (!resource.exists() || !resource.isReadable()){
                throw new RuntimeException("ファイルを読み取れません。");
            }

            // ファイル名エンコーディング
            String encodedFileName = URLEncoder.encode(
                    fileEntity.getOriFileName(),
                    StandardCharsets.UTF_8
            ).replace("+", "%20");

            //　HTTPresponseヘッダー設定
            String contentDisposition = "attachment; filename*=UTF-8''" + encodedFileName;

            //　responseEntity生成とリターン
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .header(HttpHeaders.CONTENT_TYPE, fileEntity.getFileType())
                    .body(resource);

        } catch (MalformedURLException e) {
            //　ファイル経路Error
            throw new RuntimeException("ファイルパスエラー", e);
        } catch (Exception e){
            throw new RuntimeException("ファイルダウンロードエラー", e);
        }
    }

    //　ファイル削除
    @PostMapping("/delete/{fileId}")
    public String deleteFile(@PathVariable Long fileId, @RequestParam Long boardId,
                             HttpSession session, RedirectAttributes redirectAttributes){

        // ログイン確認
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null){
            redirectAttributes.addFlashAttribute("error","ログインが必要です!");
            return "redirect/login";
        }

        //　ファイルの情報検索
        File fileEntity = fileService.getFileById(fileId);

        //　作成者本人確認
        if (!fileEntity.getBoard().getUser().getId().equals(loginUser.getId())){
            redirectAttributes.addFlashAttribute("error","自分が作成した掲示板のファイルのみ削除可能です！");
            return "redirect:/board/" + boardId;
        }

        //　ファイル削除
        try{
            fileService.deleteFile(fileId);
            redirectAttributes.addFlashAttribute("success", "ファイルを削除しました!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "ファイル削除に失敗しました!");
        }

        //　詳細ページに移動
        return "redirect:/board/" + boardId;
    }
}
