package hr.algebra.iisbackend.controller;

import hr.algebra.iisbackend.dto.ProductDTO;
import hr.algebra.iisbackend.dto.ProductInput;
import hr.algebra.iisbackend.service.ProductApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductGraphQLController {

    private final ProductApiService productApiService;

    @QueryMapping
    @PreAuthorize("hasAnyRole('READ', 'ADMIN')")
    public List<ProductDTO> products() {
        return productApiService.getAll();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('READ', 'ADMIN')")
    public ProductDTO product(@Argument Long id) {
        return productApiService.getById(id);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDTO createProduct(@Argument ProductInput input) {
        ProductDTO dto = new ProductDTO();
        dto.setNaziv(input.naziv());
        dto.setCijena(input.cijena());
        dto.setKategorija(input.kategorija());
        dto.setOpis(input.opis());
        dto.setKolicina(input.kolicina());
        return productApiService.create(dto);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDTO updateProduct(@Argument Long id, @Argument ProductInput input) {
        ProductDTO dto = new ProductDTO();
        dto.setNaziv(input.naziv());
        dto.setCijena(input.cijena());
        dto.setKategorija(input.kategorija());
        dto.setOpis(input.opis());
        dto.setKolicina(input.kolicina());
        return productApiService.update(id, dto);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteProduct(@Argument Long id) {
        productApiService.delete(id);
        return true;
    }
}
