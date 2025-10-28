package com.example.board.service;


import com.example.board.entity.Board;
import com.example.board.entity.User;
import com.example.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//　ビジネスロジックを処理するServiceアノテーション
@Service
//　finalのコンストラクタ自動生成
@RequiredArgsConstructor
public class BoardService {

    //　BoardRepositoryDBをもらうために生成
    private final BoardRepository boardRepository;

    //　全ての掲示板リストを照会
    public List<Board> getAllBoards(){
        //　降順に(新しい物が上)持ってきて表示する
        return boardRepository.findAllByOrderByCreatedAtDesc();
    }

    //　ページングメソッド
    // int page = ページの番号　int size = 一ページに見れる作成文の数
    public Page<Board> getBoardsWithPaging(int page, int size) {
        //　PageRequest.of()　＝　ページング情報生成
        //　Sort.Direction.DESC　＝　降順
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,"createdAt"));

        return boardRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    //　作成したIDで探す
    public Board getIdBoard(Long id){
        return boardRepository.findById(id)
                //　いない場合
                .orElseThrow(() -> new IllegalArgumentException("作成したないようがございません。"));
    }

    //　トランザクション処理
    @Transactional
    //　掲示板作成
    public Board createBoard(String title, String context, User user){
        Board board = new Board();
        board.setTitle(title);
        board.setContext(context);
        board.setUser(user);
        board.setViews(0);
        return boardRepository.save(board);
    }

    //　掲示板修正
    @Transactional
    public Board updateBoard(Long id, String title, String context){
        Board board = getIdBoard(id);
        board.setTitle(title);
        board.setContext(context);
        return boardRepository.save(board);
    }

    //　掲示板削除
    @Transactional
    public void deleetBoard(Long id){
        Board board = getIdBoard(id);
        boardRepository.deleteById(id);
    }

    //　クリック数アップ
    @Transactional
    public void countViews(Long id){
        Board board = getIdBoard(id);
        //　０に登録したboard.getViews()をクリックするたび+1するため
        board.setViews(board.getViews() + 1);
        boardRepository.save(board);
    }

    // 検索メソッド
    //　タイトルで検索
    public Page<Board> searchByTitle(String keyword, int page, int size){
        // ページング情報生成
        // page = ページNo、size = 1ページの掲示文の数
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        //　タイトルのkeywordのみで検索
        return boardRepository.findByTitleContainingOrderByCreatedAtDesc(keyword, pageable);
    }
    //　内容で検索
    public Page<Board> searchByContent(String keyword, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        // 内容のkeywordのみで検索
        return boardRepository.findByContextContainingOrderByCreatedAtDesc(keyword, pageable);
    }
    //　タイトルまたは内容でのkeywordで検索
    public Page<Board> searchByTitleOrContent(String keyword, String type, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        //　検索条件(type)の選択によって違う検索方法を使用
        switch (type){
            //　タイトル
            case "title":
                return boardRepository.findByTitleContainingOrderByCreatedAtDesc(keyword, pageable);
            //　内容
            case "content":
                return boardRepository.findByContextContainingOrderByCreatedAtDesc(keyword, pageable);
            //　タイトルまたは内容
            case "all":
            default:
                //　タイトルと内容で検索するためにkeywordを2回渡す
                return boardRepository.findByTitleContainingOrContextContainingOrderByCreatedAtDesc(keyword, keyword, pageable);
        }
    }
}
