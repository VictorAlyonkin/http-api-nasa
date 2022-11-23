import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Main {

    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        HttpGet request = new HttpGet("https://api.nasa.gov/planetary/apod?api_key=lfkT40EygkaziXmr13maOFNzo7V5uQKaxq5xV7rl");

        CloseableHttpResponse response = httpClient.execute(request);

        Nasa nasaData = mapper.readValue(
                response.getEntity().getContent(),
                new TypeReference<>() {
                }
        );

        String fileName = nasaData.getUrl().substring(
                nasaData
                        .getUrl()
                        .lastIndexOf("/") + 1
        );
        BufferedImage img = ImageIO.read(new URL(nasaData.getUrl()));
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        ImageIO.write(img, "png", file);

        response.close();
        httpClient.close();
    }
}
