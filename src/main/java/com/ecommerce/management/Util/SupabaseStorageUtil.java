package com.ecommerce.management.Util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class SupabaseStorageUtil {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucket;

    public String uploadImage(MultipartFile file, String fileName)
            throws IOException, InterruptedException {

        String cleanName = fileName.replaceAll("\\s+", "-").toLowerCase();
        String filePath  = cleanName + "-" + System.currentTimeMillis()
                + getExtension(file.getOriginalFilename());

        String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucket + "/" + filePath;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uploadUrl))
                .header("Authorization",    "Bearer " + supabaseKey)
                .header("Content-Type",     file.getContentType())
                .header("x-upsert",         "true")
                .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            // Return public URL
            return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + filePath;
        } else {
            throw new IOException("Upload failed: " + response.body());
        }
    }

    // ===== Delete image from Supabase Storage =====
    public void deleteImage(String imageUrl)
            throws IOException, InterruptedException {

        if (imageUrl == null || imageUrl.isEmpty()) return;

        // Extract relative file path from the public URL
        String filePath = imageUrl.replace(
                supabaseUrl + "/storage/v1/object/public/" + bucket + "/", "");

        // Supabase batch-delete endpoint: DELETE /storage/v1/object/{bucket}
        // with JSON body {"prefixes":["<filePath>"]}
        String deleteUrl = supabaseUrl + "/storage/v1/object/" + bucket;
        String jsonBody  = "{\"prefixes\":[\"" + filePath + "\"]}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(deleteUrl))
                .header("Authorization",  "Bearer " + supabaseKey)
                .header("Content-Type",   "application/json")
                .method("DELETE", HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Image delete failed [" + response.statusCode()
                    + "]: " + response.body());
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return ".jpg";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : ".jpg";
    }
}