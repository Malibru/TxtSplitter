package split;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class TxtSplitter {

    public static java.util.List<Path> splitIntoParts(Path inputFile, Path outputDir, int parts) throws IOException {
        if (parts < 1) throw new IllegalArgumentException("parts deve ser >= 1");
        if (inputFile == null || !Files.exists(inputFile)) throw new IllegalArgumentException("Arquivo de entrada inválido");
        if (outputDir == null) throw new IllegalArgumentException("Pasta de saída inválida");
        Files.createDirectories(outputDir);
    
        long total = 0;
        try (java.io.BufferedReader br = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8)) {
            while (br.readLine() != null) total++;
        }
        if (total == 0) return java.util.Collections.emptyList();
        if (parts > total) parts = (int) total;
    
        long base = total / parts;           
        long resto = total % parts;          
    
        String fileName = inputFile.getFileName().toString();
        int idx = fileName.lastIndexOf('.');
        String baseName = (idx > 0 ? fileName.substring(0, idx) : fileName);
        String ext = (idx > 0 ? fileName.substring(idx) : ".txt");
    
        java.util.List<Path> outputs = new java.util.ArrayList<>(parts);

        try (java.io.BufferedReader br = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8)) {
            for (int p = 1; p <= parts; p++) {
                long alvo = base + (p <= resto ? 1 : 0); 
                String outName = String.format("%s_part%03d%s", baseName, p, ext);
                Path out = outputDir.resolve(outName);
                try (java.io.BufferedWriter w = Files.newBufferedWriter(out, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
                    for (long i = 0; i < alvo; i++) {
                        String line = br.readLine();
                        if (line == null) break;
                        w.write(line);
                        w.newLine();
                    }
                }
                outputs.add(out);
            }
        }
    
        return outputs;
    }}

