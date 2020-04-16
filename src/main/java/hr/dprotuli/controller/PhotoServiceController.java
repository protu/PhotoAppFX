package hr.dprotuli.controller;

import hr.dprotuli.model.Picture;
import javafx.scene.image.Image;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class PhotoServiceController {

    private String authCookie;
    private final String host = "http://localhost:8080/";


    public Picture[] getPictures() {

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(List.of(MediaType.ALL));

        authorize();
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(List.of(converter));
        String pictureResource = "http://localhost:8080/api/photo";

        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Content-Type", "application/json");
        header.add("Cookie", authCookie);

        ResponseEntity<Picture[]> response = restTemplate.exchange(pictureResource, HttpMethod.GET, new HttpEntity<>(header), Picture[].class);


        return (response.getBody());
    }

    public Image getPictureFile(Picture picture) {
        try {
            URL url = new URL(host + picture.getPath());
            URLConnection connection = url.openConnection();
            connection.addRequestProperty("Cookie", authCookie);
            Image image = new Image(connection.getInputStream(), 150, 0, true, true);
            return image;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deletePicture(Picture picture) {
        try {
            URL url = new URL(host + "api/photo/" + String.valueOf(picture.getId()));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("Cookie", authCookie);
            connection.setRequestMethod("DELETE");
            connection.connect();
            if (connection.getResponseCode() == 204) return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean modifyPicture(Picture picture) {
        RestTemplate restTemplate = new RestTemplate();
        String url = host + "api/photo/" + String.valueOf(picture.getId());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Cookie", authCookie);
        ResponseEntity<Picture> response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(picture, header), Picture.class);
        if (response.getStatusCode() == HttpStatus.OK) return true;
        return false;
    }

    public boolean addPicture(Picture picture, File pictureFile) {
        RestTemplate restTemplate = new RestTemplate();
        String url = host + "api/photo/new";
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        picture.setUsername("admin");
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Cookie", authCookie);
        MultiValueMap<String, Object> responseBody = new LinkedMultiValueMap<>();
        responseBody.add("file", new FileSystemResource(pictureFile));
        responseBody.add("picture", picture);

        ResponseEntity<Picture> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(responseBody, header), Picture.class);
        if (response.getStatusCode() == HttpStatus.CREATED) return true;
        return false;
    }

    private void authorize() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> postValues = new LinkedMultiValueMap<>();
        postValues.add("username", "admin");
        postValues.add("password", "admin");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(postValues, httpHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8080/login?username=admin&password=admin", request, String.class);
        authCookie = response.getHeaders().getFirst("Set-Cookie");

    }
}
