package edu.cit.fernandez.goatedmeals.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    private final String BUCKET_NAME = "meal-images"; // Ensure this matches your Supabase Bucket name!

    public String uploadImage(MultipartFile file) throws Exception {
        // 1. Create a unique filename
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("\\s+", "_");

        // 2. Construct the Supabase Storage URL
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + BUCKET_NAME + "/" + fileName;

        // 3. Prepare the HTTP Request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uploadUrl))
                .header("Authorization", "Bearer " + supabaseKey)
                .header("Content-Type", file.getContentType())
                .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                .build();

        // 4. Execute the upload
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // 5. Return the PUBLIC URL of the uploaded image
            return supabaseUrl + "/storage/v1/object/public/" + BUCKET_NAME + "/" + fileName;
        } else {
            throw new RuntimeException("Supabase Upload Failed: " + response.body());
        }
    }
}