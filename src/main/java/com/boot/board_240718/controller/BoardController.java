package com.boot.board_240718.controller;

import com.boot.board_240718.model.Board;
import com.boot.board_240718.repository.BoardRepository;
import com.boot.board_240718.service.BoardService;
import com.boot.board_240718.validator.BoardValidator;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/board")
public class BoardController {
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardValidator boardValidator;

    @Autowired
    private BoardService boardService;

    @GetMapping("/list")
//    public String list(Model model) {
//    public String list(Model model, Pageable pageable) {
//    public String list(Model model, @PageableDefault(size = 2) Pageable pageable, @RequestParam String searchText) {
    public String list(Model model, @PageableDefault(size = 2) Pageable pageable, @RequestParam(required = false, defaultValue = "") String searchText) {
        log.info("@# list()");
        log.info("@# searchText =>" + searchText);
//        List<Board> boards = boardRepository.findAll();
//        Page<Board> boards = boardRepository.findAll(PageRequest.of(1, 20));
//        Page<Board> boards = boardRepository.findAll(PageRequest.of(0, 20));
//        Page<Board> boards = boardRepository.findAll(pageable);
        Page<Board> boards = boardRepository.findByTitleContainingOrContent(searchText, searchText, pageable);
        int startPage = Math.max(1, boards.getPageable().getPageNumber() - 4);
        int endPage = Math.min(boards.getTotalPages(), boards.getPageable().getPageNumber() + 4);
//        최종: 4개 이전 페이지 표시 + 5개 이후 페이지 표시

        log.info("@# boards => " + boards);
//        boards.getTotalElements();

        model.addAttribute("boards", boards);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "board/list";
    }

    @GetMapping("/form")
//    public String form(Model model) {
//    public String form(Model model, @RequestParam Long id) {
    public String form(Model model, @RequestParam(required = false) Long id) {
        log.info("@# GetMapping form()");
        log.info("@# id => " + id);

        if(id != null) {
//            Optional<Board> board = boardRepository.findById(id);
            Board board = boardRepository.findById(id).orElse(null);
            model.addAttribute("board", board);
            log.info("@# board => " + board);
        }else {
            model.addAttribute("board", new Board());
        }

        return "board/form";
    }

    @PostMapping("/form")
//    public String form(@ModelAttribute Board board, Model model) {
    public String checkPersonInfo(@Valid Board board, BindingResult bindingResult) {
//        서버단에서 validation 체크
        boardValidator.validate(board, bindingResult);

        if (bindingResult.hasErrors()) {
            return "board/form";
        }

//        스프링 시큐리티 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
//        boardRepository.save(board);
        boardService.save(username, board);

        return "redirect:/board/list";
    }
}
