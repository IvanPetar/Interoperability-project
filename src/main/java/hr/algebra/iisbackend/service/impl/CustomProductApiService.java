package hr.algebra.iisbackend.service.impl;

import hr.algebra.iisbackend.dto.ProductDTO;
import hr.algebra.iisbackend.entity.Product;
import hr.algebra.iisbackend.repository.ProductRepository;
import hr.algebra.iisbackend.service.ProductApiService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomProductApiService implements ProductApiService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductDTO> getAll() {
        return productRepository.findAll().stream()
                .map(ProductDTO::fromEntity)
                .toList();
    }

    @Override
    public ProductDTO getById(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
        return ProductDTO.fromEntity(p);
    }

    @Override
    public ProductDTO create(ProductDTO dto) {
        dto.setId(null);
        Product saved = productRepository.save(dto.toEntity());
        return ProductDTO.fromEntity(saved);
    }

    @Override
    public ProductDTO update(Long id, ProductDTO dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
        existing.setName(dto.getNaziv());
        existing.setPrice(dto.getCijena());
        existing.setCategory(dto.getKategorija());
        existing.setDescription(dto.getOpis());
        existing.setKolicina(dto.getKolicina());
        return ProductDTO.fromEntity(productRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }
}
