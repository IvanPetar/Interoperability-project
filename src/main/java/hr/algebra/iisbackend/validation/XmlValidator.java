package hr.algebra.iisbackend.validation;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class XmlValidator {

    public List<String> validate(InputStream xmlInput) throws IOException {
        List<String> errors = new ArrayList<>();
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new ClassPathResource("product.xsd").getURL());
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xmlInput));
        } catch (SAXException e) {
            errors.add(e.getMessage());
        }
        return errors;
    }
}
