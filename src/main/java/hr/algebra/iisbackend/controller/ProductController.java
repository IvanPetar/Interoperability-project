package hr.algebra.iisbackend.controller;

import tools.jackson.databind.ObjectMapper;
import hr.algebra.iisbackend.dto.ProductDTO;
import hr.algebra.iisbackend.service.JakartaXmlValidationService;
import hr.algebra.iisbackend.service.ProductApiService;
import hr.algebra.iisbackend.service.ProductService;
import hr.algebra.iisbackend.validation.JsonValidator;
import hr.algebra.iisbackend.validation.XmlValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    // Routers to public (Supabase REST) or custom (JPA) based on api.mode
    private final ProductApiService productApiService;

    // Used only for Jakarta XML validation (generates XML from JPA data)
    private final ProductService productService;

    private final XmlValidator xmlValidator;
    private final JsonValidator jsonValidator;
    private final JakartaXmlValidationService jakartaXmlValidationService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAll() {
        return ResponseEntity.ok(productApiService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productApiService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody ProductDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productApiService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
        return ResponseEntity.ok(productApiService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productApiService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validate-xml")
    public ResponseEntity<?> validateXml() throws Exception {
        var products = productService.getAll();
        String xmlContent = jakartaXmlValidationService.generateXmlFromProducts(products);
        List<String> errors = jakartaXmlValidationService.validateXml(xmlContent);

        if (errors.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "message", "XML je validan!",
                    "xml", xmlContent
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "errors", errors
            ));
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam(value = "xmlFile", required = false) MultipartFile xmlFile,
            @RequestParam(value = "jsonFile", required = false) MultipartFile jsonFile) throws Exception {

        List<String> errors = new ArrayList<>();

        if (xmlFile == null && jsonFile == null) {
            return ResponseEntity.badRequest().body(Map.of("validationErrors", List.of("Odaberite barem jednu datoteku!")));
        }

        byte[] jsonBytes = null;
        byte[] xmlBytes = null;

        if (xmlFile != null && !xmlFile.isEmpty()) {
            xmlBytes = xmlFile.getBytes();
            List<String> xmlErrors = xmlValidator.validate(new java.io.ByteArrayInputStream(xmlBytes));
            if (!xmlErrors.isEmpty()) {
                errors.addAll(xmlErrors.stream().map(e -> "XML: " + e).toList());
            }
        }

        if (jsonFile != null && !jsonFile.isEmpty()) {
            jsonBytes = jsonFile.getBytes();
            List<String> jsonErrors = jsonValidator.validate(new java.io.ByteArrayInputStream(jsonBytes));
            if (!jsonErrors.isEmpty()) {
                errors.addAll(jsonErrors.stream().map(e -> "JSON: " + e).toList());
            }
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("validationErrors", errors));
        }

        if (jsonBytes != null) {
            ProductDTO dto = objectMapper.readValue(jsonBytes, ProductDTO.class);
            return ResponseEntity.status(HttpStatus.CREATED).body(productApiService.create(dto));
        }

        if (xmlBytes != null) {
            javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(new java.io.ByteArrayInputStream(xmlBytes));
            doc.getDocumentElement().normalize();

            ProductDTO dto = new ProductDTO();
            dto.setNaziv(getXmlValue(doc, "naziv"));
            dto.setCijena(new java.math.BigDecimal(getXmlValue(doc, "cijena")));
            dto.setKategorija(getXmlValue(doc, "kategorija"));
            dto.setOpis(getXmlValue(doc, "opis"));
            String kolicina = getXmlValue(doc, "kolicina");
            if (kolicina != null && !kolicina.isEmpty()) dto.setKolicina(Integer.parseInt(kolicina));

            return ResponseEntity.status(HttpStatus.CREATED).body(productApiService.create(dto));
        }

        return ResponseEntity.ok(Map.of("message", "Validacija uspješna!"));
    }

    private String getXmlValue(org.w3c.dom.Document doc, String tag) {
        org.w3c.dom.NodeList list = doc.getElementsByTagName(tag);
        if (list.getLength() > 0) return list.item(0).getTextContent();
        return null;
    }
}
