package hr.algebra.iisbackend.service;

import hr.algebra.iisbackend.entity.Product;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Service
public class JakartaXmlValidationService {

    public String generateXmlFromProducts(List<Product> products) throws JAXBException {
        ProductList list = new ProductList(products);
        JAXBContext context = JAXBContext.newInstance(ProductList.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(list, writer);
        return writer.toString();
    }

    public List<String> validateXml(String xmlContent) throws IOException {
        List<String> errors = new ArrayList<>();
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new ClassPathResource("products-list.xsd").getURL());
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new StringReader(xmlContent)));
        } catch (SAXException e) {
            errors.add(e.getMessage());
        }
        return errors;
    }

    @XmlRootElement(name = "products")
    public static class ProductList {
        private List<Product> products;

        public ProductList() {}

        public ProductList(List<Product> products) {
            this.products = products;
        }

        @XmlElement(name = "product")
        public List<Product> getProducts() {
            return products;
        }
    }
}
