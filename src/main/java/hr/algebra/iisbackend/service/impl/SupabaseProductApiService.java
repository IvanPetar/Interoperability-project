package hr.algebra.iisbackend.service.impl;

import hr.algebra.iisbackend.dto.ProductDTO;
import hr.algebra.iisbackend.service.ProductApiService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class SupabaseProductApiService implements ProductApiService {

    @Value("${supabase.url:https://placeholder.supabase.co}")
    private String supabaseUrl;

    @Value("${supabase.anon-key:placeholder}")
    private String anonKey;

    private RestClient restClient;

    @PostConstruct
    private void init() {
        restClient = RestClient.builder()
                .baseUrl(supabaseUrl)
                .defaultHeader("apikey", anonKey)
                .defaultHeader("Authorization", "Bearer " + anonKey)
                .build();
    }

    @Override
    public List<ProductDTO> getAll() {
        return restClient.get()
                .uri("/rest/v1/products?select=*")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public ProductDTO getById(Long id) {
        List<ProductDTO> results = restClient.get()
                .uri("/rest/v1/products?id=eq." + id + "&select=*")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        if (results == null || results.isEmpty()) {
            throw new EntityNotFoundException("Product not found: " + id);
        }
        return results.getFirst();
    }

    @Override
    public ProductDTO create(ProductDTO dto) {
        dto.setId(null);
        List<ProductDTO> results = restClient.post()
                .uri("/rest/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Prefer", "return=representation")
                .body(dto)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        if (results == null || results.isEmpty()) {
            throw new RuntimeException("Supabase did not return created product");
        }
        return results.getFirst();
    }

    @Override
    public ProductDTO update(Long id, ProductDTO dto) {
        dto.setId(null);
        List<ProductDTO> results = restClient.patch()
                .uri("/rest/v1/products?id=eq." + id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Prefer", "return=representation")
                .body(dto)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        if (results == null || results.isEmpty()) {
            throw new EntityNotFoundException("Product not found: " + id);
        }
        return results.getFirst();
    }

    @Override
    public void delete(Long id) {
        restClient.delete()
                .uri("/rest/v1/products?id=eq." + id)
                .retrieve()
                .toBodilessEntity();
    }
}
