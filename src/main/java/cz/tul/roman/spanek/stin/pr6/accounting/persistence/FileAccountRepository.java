package cz.tul.roman.spanek.stin.pr6.accounting.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.tul.roman.spanek.stin.pr6.accounting.model.ClientAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class FileAccountRepository implements AccountRepository {

    private static final TypeReference<List<ClientAccount>> ACCOUNT_LIST_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final Path storagePath;

    public FileAccountRepository(
            ObjectMapper objectMapper,
            @Value("${accounting.storage.file-path:data/accounts.json}") String storageFilePath
    ) {
        this.objectMapper = objectMapper.copy().findAndRegisterModules();
        this.storagePath = Path.of(storageFilePath).toAbsolutePath();
    }

    @Override
    public synchronized Map<String, ClientAccount> loadAccounts() {
        if (Files.notExists(storagePath)) {
            return new LinkedHashMap<>();
        }

        try {
            if (Files.size(storagePath) == 0L) {
                return new LinkedHashMap<>();
            }

            try (Reader reader = Files.newBufferedReader(storagePath, StandardCharsets.UTF_8)) {
                List<ClientAccount> accounts = objectMapper.readValue(reader, ACCOUNT_LIST_TYPE);

                return accounts.stream()
                        .collect(Collectors.toMap(
                                ClientAccount::getAccountNumber,
                                Function.identity(),
                                (existing, replacement) -> replacement,
                                LinkedHashMap::new
                        ));
            }
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not load accounts from file: " + storagePath,
                    exception
            );
        }
    }

    @Override
    public synchronized void saveAccounts(Map<String, ClientAccount> accounts) {
        try {
            Path parentDirectory = storagePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }

            Path tempFile = Files.createTempFile(
                    parentDirectory != null ? parentDirectory : Path.of("."),
                    "accounts-",
                    ".tmp"
            );

            List<ClientAccount> snapshot = new ArrayList<>(accounts.values());
            snapshot.sort(Comparator.comparing(ClientAccount::getAccountNumber));

            try (Writer writer = Files.newBufferedWriter(
                    tempFile,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING
            )) {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, snapshot);
            }

            moveIntoPlace(tempFile);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not save accounts to file: " + storagePath,
                    exception
            );
        }
    }

    private void moveIntoPlace(Path tempFile) throws IOException {
        try {
            Files.move(tempFile, storagePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(tempFile, storagePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}

