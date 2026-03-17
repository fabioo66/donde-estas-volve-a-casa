package ttps.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class Georef_ar {

    public static Map<String, String> getDatos(String coordenadas) throws Exception {

        String lat = coordenadas.split(",")[0];
        String lon = coordenadas.split(",")[1];

        String url = "https://apis.datos.gob.ar/georef/api/ubicacion?lat=" + lat + "&lon=" + lon;

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.body());

        String provincia = json.get("ubicacion")
                .get("provincia")
                .get("nombre")
                .asText();

        String municipio = json.get("ubicacion")
                .get("municipio")
                .get("nombre")
                .asText();

        Map<String, String> resultado = new HashMap<>();
        resultado.put("provincia", provincia);
        resultado.put("municipio", municipio);

        return resultado;
    }
}