package DrinkGo.DrinkGo_backend.service.jpa;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "application/pdf");

    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5 MB

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /**
     * Guarda un archivo en el subdirectorio indicado y devuelve la ruta relativa.
     * Ejemplo: guardar(file, "comprobantes") → "comprobantes/abc123.pdf"
     */
    public String guardar(MultipartFile file, String subdirectory) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo de 5 MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Tipo de archivo no permitido. Use JPG, PNG, WebP o PDF");
        }

        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }

        // Nombre único para evitar colisiones
        String fileName = UUID.randomUUID().toString() + extension;

        Path dirPath = Paths.get(uploadDir, subdirectory).toAbsolutePath().normalize();
        Files.createDirectories(dirPath);

        Path targetPath = dirPath.resolve(fileName).normalize();
        // Prevenir path traversal
        if (!targetPath.startsWith(dirPath)) {
            throw new SecurityException("Ruta de archivo inválida");
        }

        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return subdirectory + "/" + fileName;
    }

    /**
     * Elimina un archivo por su ruta relativa.
     */
    public void eliminar(String relativePath) throws IOException {
        if (relativePath == null || relativePath.isBlank()) return;
        Path filePath = Paths.get(uploadDir, relativePath).toAbsolutePath().normalize();
        Path basePath = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!filePath.startsWith(basePath)) {
            throw new SecurityException("Ruta de archivo inválida");
        }
        Files.deleteIfExists(filePath);
    }

    public Path resolverRuta(String relativePath) {
        return Paths.get(uploadDir, relativePath).toAbsolutePath().normalize();
    }
}
