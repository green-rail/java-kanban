package ru.smg.kanban.kvserver;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final String baseUrl;

    private final String apiToken;
    private final HttpClient client;

    public KVTaskClient(String url) throws IOException, InterruptedException {
        //GET /register
        baseUrl = url;

        client = HttpClient.newHttpClient();
        URI uri = URI.create(baseUrl + "register");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        apiToken = response.body();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        //POST /save/<ключ>?API_TOKEN=
        URI uri = URI.create(baseUrl + "save/" + key + "?API_TOKEN=" + apiToken);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201) {
            System.out.println("Запись добавлена.");
        } else {
            System.out.println(response.body());
        }
    }

    public String load(String key) throws IOException, InterruptedException {
        //GET /load/<ключ>?API_TOKEN=
        URI uri = URI.create(baseUrl + "load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            System.out.println("Запись получена.");
            return response.body();
        } else {
            System.out.println(response.body());
        }
        return "";
    }
}
