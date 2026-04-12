package hr.algebra.iisbackend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "product")
@XmlAccessorType(XmlAccessType.FIELD)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlElement(name = "id")
    private Long id;

    @NotBlank
    @Column(name = "naziv", nullable = false)
    @JsonProperty("naziv")
    @XmlElement(name = "naziv")
    private String name;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "cijena", nullable = false)
    @JsonProperty("cijena")
    @XmlElement(name = "cijena")
    private BigDecimal price;

    @Column(name = "kategorija")
    @JsonProperty("kategorija")
    @XmlElement(name = "kategorija")
    private String category;

    @Column(name = "opis", columnDefinition = "TEXT")
    @JsonProperty("opis")
    @XmlElement(name = "opis")
    private String description;

    @Column(name = "kolicina")
    @JsonProperty("kolicina")
    @XmlElement(name = "kolicina")
    private Integer kolicina;
}
