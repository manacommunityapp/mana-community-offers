package com.manacommunity.offers.controller;

import com.manacommunity.offers.entity.BusinessCategoryEntity;
import com.manacommunity.offers.repository.BusinessCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BusinessCategoryController {

    private final BusinessCategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<BusinessCategoryEntity>> getCategories() {
        return ResponseEntity.ok(categoryRepository.findByActiveTrueOrderByDisplayOrderAsc());
    }
}
