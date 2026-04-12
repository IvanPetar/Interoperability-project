package hr.algebra.iisbackend.service;

import hr.algebra.iisbackend.dto.ProductDTO;
import hr.algebra.iisbackend.service.impl.CustomProductApiService;
import hr.algebra.iisbackend.service.impl.SupabaseProductApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Routes API calls to either the Supabase REST API or the local JPA implementation
 * based on the api.mode property in application.properties.
 *
 * api.mode=public  -> calls Supabase REST API directly
 * api.mode=custom  -> uses local JPA/Hibernate (default)
 */
@Service
@Primary
@RequiredArgsConstructor
public class ProductApiRouter implements ProductApiService {

    @Value("${api.mode:custom}")
    private String apiMode;

    private final CustomProductApiService customService;
    private final SupabaseProductApiService supabaseService;

    private ProductApiService active() {
        return "public".equals(apiMode) ? supabaseService : customService;
    }

    @Override
    public List<ProductDTO> getAll() {
        return active().getAll();
    }

    @Override
    public ProductDTO getById(Long id) {
        return active().getById(id);
    }

    @Override
    public ProductDTO create(ProductDTO dto) {
        return active().create(dto);
    }

    @Override
    public ProductDTO update(Long id, ProductDTO dto) {
        return active().update(id, dto);
    }

    @Override
    public void delete(Long id) {
        active().delete(id);
    }
}
