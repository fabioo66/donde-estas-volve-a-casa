package ttps.spring.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadPath;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public FileStorageService() throws IOException {
        // Crear directorio uploads si no existe
        this.uploadPath = Paths.get("uploads").toAbsolutePath().normalize();
        Files.createDirectories(this.uploadPath);
    }

    /**
     * Guarda una imagen desde Base64 y retorna la URL relativa
     */
    public String saveImageFromBase64(String base64Image, String prefix) throws IOException {
        // Remover el prefijo data:image/...;base64, si existe
        String base64Data = base64Image;
        if (base64Image.contains(",")) {
            base64Data = base64Image.split(",")[1];
        }

        // Decodificar Base64
        byte[] imageBytes = Base64.getDecoder().decode(base64Data);

        // Generar nombre único para el archivo
        String fileName = prefix + "_" + UUID.randomUUID().toString() + ".jpg";

        // Guardar archivo
        Path targetLocation = this.uploadPath.resolve(fileName);
        Files.write(targetLocation, imageBytes);

        // Retornar URL relativa
        return "/uploads/" + fileName;
    }

    /**
     * Guarda múltiples imágenes desde Base64 y retorna lista de URLs
     */
    public List<String> saveImagesFromBase64(List<String> base64Images, String prefix) throws IOException {
        List<String> urls = new ArrayList<>();
        for (String base64Image : base64Images) {
            if (base64Image != null && !base64Image.trim().isEmpty()) {
                String url = saveImageFromBase64(base64Image, prefix);
                urls.add(url);
            }
        }
        return urls;
    }

    /**
     * Elimina un archivo por su nombre
     */
    public void deleteFile(String fileName) throws IOException {
        if (fileName != null && fileName.startsWith("/uploads/")) {
            fileName = fileName.replace("/uploads/", "");
        }
        Path filePath = this.uploadPath.resolve(fileName).normalize();
        Files.deleteIfExists(filePath);
    }

    /**
     * Elimina múltiples archivos dadas sus URLs
     */
    public void deleteFiles(List<String> fileUrls) throws IOException {
        if (fileUrls != null) {
            for (String url : fileUrls) {
                try {
                    deleteFile(url);
                } catch (IOException e) {
                    // Log error pero continuar con los demás archivos
                    System.err.println("Error eliminando archivo: " + url);
                }
            }
        }
    }

    /**
     * Obtiene el path absoluto del directorio de uploads
     */
    public Path getUploadPath() {
        return uploadPath;
    }
}

