package com.mysql.sbb.Category;


import com.mysql.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category getCategory(Integer id){
        Optional<Category> optionalCategory = this.categoryRepository.findById(id);
        if(optionalCategory.isPresent()){
            return optionalCategory.get();
        }else{
            throw new DataNotFoundException("해당 카테고리가 없습니다");
        }
    }
}
