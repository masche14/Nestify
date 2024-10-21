package kopo.poly.service;

import kopo.poly.dto.RecommendDTO;

import java.util.List;

public interface ISearchService {
    List<RecommendDTO> searchResult(String query) throws Exception;
}
