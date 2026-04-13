package hr.algebra.iisbackend.soap;

import hr.algebra.iisbackend.soap.generated.ProductSoap;
import hr.algebra.iisbackend.soap.generated.SearchProductsRequest;
import hr.algebra.iisbackend.soap.generated.SearchProductsResponse;
import hr.algebra.iisbackend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import hr.algebra.iisbackend.service.ProductApiService;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.List;

@Endpoint
@RequiredArgsConstructor
public class ProductEndpoint {

    private static final String NAMESPACE_URI = "http://iis.algebra.hr/soap";

    private final ProductApiService productApiService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SearchProductsRequest")
    @ResponsePayload
    public SearchProductsResponse searchProducts(@RequestPayload SearchProductsRequest request) throws Exception {

        // 1. Dohvati sve proizvode i generiraj XML
        String xmlContent = generateXml();

        // 2. XPath pretraga po pojmu
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = factory.newDocumentBuilder().parse(new InputSource(new java.io.StringReader(xmlContent)));

        XPath xpath = XPathFactory.newInstance().newXPath();
        String pojam = request.getPojam();
        String expression = "//product[contains(translate(naziv, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + pojam.toLowerCase() + "')]";

        NodeList nodes = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);

        // 3. Vrati rezultate
        SearchProductsResponse response = new SearchProductsResponse();
        List<ProductSoap> products = response.getProducts();

        for (int i = 0; i < nodes.getLength(); i++) {
            org.w3c.dom.Node node = nodes.item(i);
            ProductSoap p = new ProductSoap();
            p.setId(Long.parseLong(getNodeValue(node, "id")));
            p.setNaziv(getNodeValue(node, "naziv"));
            p.setCijena(new java.math.BigDecimal(getNodeValue(node, "cijena")));
            String kat = getNodeValue(node, "kategorija");
            if (kat != null) p.setKategorija(kat);
            String opis = getNodeValue(node, "opis");
            if (opis != null) p.setOpis(opis);
            String kol = getNodeValue(node, "kolicina");
            if (kol != null && !kol.isEmpty()) p.setKolicina(Integer.parseInt(kol));
            products.add(p);
        }

        return response;
    }

    private String generateXml() throws Exception {
        var products = productApiService.getAll();
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><products>");
        for (var p : products) {
            sb.append("<product>");
            sb.append("<id>").append(p.getId()).append("</id>");
            sb.append("<naziv>").append(p.getNaziv()).append("</naziv>");
            sb.append("<cijena>").append(p.getCijena()).append("</cijena>");
            sb.append("<kategorija>").append(p.getKategorija() != null ? p.getKategorija() : "").append("</kategorija>");
            sb.append("<opis>").append(p.getOpis() != null ? p.getOpis() : "").append("</opis>");
            sb.append("<kolicina>").append(p.getKolicina() != null ? p.getKolicina() : "").append("</kolicina>");
            sb.append("</product>");
        }
        sb.append("</products>");
        return sb.toString();
    }

    private String getNodeValue(org.w3c.dom.Node node, String tagName) {
        NodeList list = ((org.w3c.dom.Element) node).getElementsByTagName(tagName);
        if (list.getLength() > 0) return list.item(0).getTextContent();
        return null;
    }
}
