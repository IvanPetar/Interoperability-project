package hr.algebra.iisbackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import hr.algebra.iisbackend.entity.Product;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;

    @NotBlank
    @JsonProperty("naziv")
    private String naziv;

    @NotNull
    @DecimalMin("0.0")
    @JsonProperty("cijena")
    private BigDecimal cijena;

    @JsonProperty("kategorija")
    private String kategorija;

    @JsonProperty("opis")
    private String opis;

    @JsonProperty("kolicina")
    private Integer kolicina;

    public static ProductDTO fromEntity(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setNaziv(product.getName());
        dto.setCijena(product.getPrice());
        dto.setKategorija(product.getCategory());
        dto.setOpis(product.getDescription());
        dto.setKolicina(product.getKolicina());
        return dto;
    }

    public Product toEntity() {
        Product p = new Product();
        p.setId(this.id);
        p.setName(this.naziv);
        p.setPrice(this.cijena);
        p.setCategory(this.kategorija);
        p.setDescription(this.opis);
        p.setKolicina(this.kolicina);
        return p;
    }
}
