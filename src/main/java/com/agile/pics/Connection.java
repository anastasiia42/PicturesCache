package com.agile.pics;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.HashSet;

public class Connection {
    String authToken = "";

    public void updateToken() throws IOException, InterruptedException {
        var values = new HashMap<String, String>() {{
            put("apiKey", "23567b218376f79d9415");
        }};

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper
                .writeValueAsString(values);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://interview.agileengine.com/auth"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        JSONObject json = new JSONObject(response.body());
        this.authToken = json.get("token").toString();
    }

    int getPagesAmount() throws IOException, InterruptedException {
        this.updateToken();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://interview.agileengine.com/images"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        JSONObject json = new JSONObject(response.body());
        return Integer.parseInt(json.get("pageCount").toString());
    }

    HashSet<String> getImageIdsFromPage(int N) throws IOException, InterruptedException {
        String URL = "http://interview.agileengine.com/images?page=" + N;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        HashSet<String> picturesIds = new HashSet<>();
        JSONObject json = new JSONObject(response.body());
        JSONArray picturesArray = json.getJSONArray("pictures");
        for (int i = 0; i < picturesArray.length(); i++) {
            picturesIds.add((String) ((JSONObject) picturesArray.get(i)).get("id"));
        }

        return picturesIds;
    }

    public HashSet<String> getPictureIds() throws IOException, InterruptedException {
        HashSet<String> picturesIds = new HashSet<>();
        int n = this.getPagesAmount();
        for (int i = 1; i <= n; i++) {
            picturesIds.addAll(getImageIdsFromPage(i));
        }
        return picturesIds;
    }

    public Picture getPictureInfo(String id) throws IOException, InterruptedException {
        String URL = "http://interview.agileengine.com/images/" + id;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        return new Picture(id, response.body());
    }

//    public static void main(String [] args) {
//        Connection c = new Connection();
//        try {
//            c.getPictureIds();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

}
