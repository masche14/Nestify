package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.RecommendDTO;
import kopo.poly.service.ISearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/search")
public class SearchController {
    private final ISearchService searchService;

    @GetMapping("/search")
    public String showSearchPage(HttpSession session){
        return "/search/search";
    }

    @PostMapping("/searchProc")
    public String searchProc(HttpSession session, HttpServletRequest request) throws Exception {
        String query = request.getParameter("query");
        List<RecommendDTO> rList = searchService.searchResult(query);
        session.setAttribute("searchResult", rList);
        return "redirect:/search/searchResult";
    }

    @GetMapping("/searchResult")
    public String showSearchResult(HttpSession session){
        return "/search/searchResult";
    }
}
