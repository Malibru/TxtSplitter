package split;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class TxtSplitter {

    public static List<Path> splitByLines(Path inputFile, Path outputDir, int linesPerPart)
        throws IOException { 
            if (linesPerPart < 1) throw new IllegalArgumentException("Quantiade linhas deve ser maior que 0");
            if (inputFile == null || !Files.exists(inputFile)) throw new IllegalArgumentException("Arquivonão invalido");
            if (outputDir == null) throw new IllegalArgumentException("Pasta de saída inválida");
            Files.createDirectories(outputDir);

        String fileName = inputFile.getFileName().toString();
        int idx = fileName.lastIndexOf('.');

        String base = (idx > 0 ? fileName.substring(0, idx) : fileName);
		String ext = (idx > 0 ? fileName.substring(idx) : ".txt");

		List<Path> outputs = new ArrayList<>();
		int part = 1;
		int linesInCurrent = 0;
		BufferedWriter writer = null;

		try (java.io.BufferedReader br = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8)) {
			String line;
			while ((line = br.readLine()) != null) {
				if (writer == null || linesInCurrent == 0) {
					String outName = String.format("%s_part%03d%s", base, part, ext);
					Path out = outputDir.resolve(outName);
					writer = Files.newBufferedWriter(out, StandardCharsets.UTF_8,
						StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
					outputs.add(out);
				}

				writer.write(line);
				writer.newLine();
				linesInCurrent++;

				if (linesInCurrent >= linesPerPart) {
					writer.close();
					writer = null;
					linesInCurrent = 0;
					part++;
				}
			}
		} finally {
			if (writer != null) {
				try { writer.close(); } catch (IOException ignore) {}
			}
		}
		return outputs;
	}
}

