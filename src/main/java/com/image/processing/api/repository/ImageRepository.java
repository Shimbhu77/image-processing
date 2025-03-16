package com.image.processing.api.repository;

import com.image.processing.api.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
    List<Image> findByProduct_ProductId(Integer productId);
}
