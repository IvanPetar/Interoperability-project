package hr.algebra.iisbackend.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JsonValidator {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<String> validate(InputStream jsonInput) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(jsonInput);

        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        JsonSchema schema = factory.getSchema(new ClassPathResource("product-schema.json").getInputStream());

        Set<ValidationMessage> errors = schema.validate(jsonNode);
        return errors.stream()
                .map(ValidationMessage::getMessage)
                .collect(Collectors.toList());
    }
}
