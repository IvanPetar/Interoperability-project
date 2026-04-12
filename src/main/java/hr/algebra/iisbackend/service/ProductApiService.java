package hr.algebra.iisbackend.service;

import hr.algebra.iisbackend.dto.ProductDTO;

import java.util.List;

public interface ProductApiService {
    List<ProductDTO> getAll();
    ProductDTO getById(Long id);
    ProductDTO create(ProductDTO dto);
    ProductDTO update(Long id, ProductDTO dto);
    void delete(Long id);
}
