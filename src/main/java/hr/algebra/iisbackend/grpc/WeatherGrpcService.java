package hr.algebra.iisbackend.grpc;

import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;

@GrpcService
public class WeatherGrpcService extends WeatherServiceGrpc.WeatherServiceImplBase {

    private static final String DHMZ_URL = "https://vrijeme.hr/hrvatska_n.xml";

    @Override
    public void getTemperature(CityRequest request, StreamObserver<TemperatureResponse> responseObserver) {
        try {
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new URL(DHMZ_URL).openStream());

            doc.getDocumentElement().normalize();

            String pojam = request.getCityName().toLowerCase();
            TemperatureResponse.Builder responseBuilder = TemperatureResponse.newBuilder();

            NodeList gradovi = doc.getElementsByTagName("Grad");

            for (int i = 0; i < gradovi.getLength(); i++) {
                org.w3c.dom.Element grad = (org.w3c.dom.Element) gradovi.item(i);

                NodeList imeList = grad.getElementsByTagName("GradIme");
                NodeList tempList = grad.getElementsByTagName("Temp");

                if (imeList.getLength() > 0 && tempList.getLength() > 0) {
                    String ime = imeList.item(0).getTextContent().trim();
                    String temp = tempList.item(0).getTextContent().trim();

                    if (ime.toLowerCase().contains(pojam)) {
                        CityTemperature ct = CityTemperature.newBuilder()
                                .setCityName(ime)
                                .setTemperature(temp)
                                .build();
                        responseBuilder.addCities(ct);
                    }
                }
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}
