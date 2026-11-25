
package ru.edu.pr08;

import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.util.*;

public class ClientImporter {

    private final ClientCsvParser parser = new ClientCsvParser();
    private final ClientValidator validator = new ClientValidator();
    private final ClientRepository repository;

    public ClientImporter(ClientRepository r) {
        repository = r;
    }

    public ImportResult importFile(Path path) throws IOException {

        // R7: проверяем существование файла
        if (path == null || !Files.exists(path)) {
            throw new IOException("Файл не найден: " + path);
        }

        ImportResult result = new ImportResult();

        // Файл автоматически закрывается
        try (BufferedReader reader =
                Files.newBufferedReader(path, StandardCharsets.UTF_8)) {

            // R1: пропускаем заголовок
            reader.readLine();

            String line;
            int lineNumber = 1;

            // R6: обрабатываем строки по очереди
            while ((line = reader.readLine()) != null) {

                lineNumber++;

                // Пропускаем пустые строки и комментарии
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Сохраняем ИНН текущей строки
                String inn = line.split(";", -1)[0];

                try {

                    // R2: разбираем CSV
                    ClientDraft draft =
                            parser.parse(line, lineNumber);

                    // R3 и R4: проверяем данные клиента
                    List<String> errors = validator.validate(draft);

                    if (!errors.isEmpty()) {
                        throw new IllegalArgumentException(
                                String.join("; ", errors)
                        );
                    }

                    // R8: вариант 25, часть А
                    // Повторный ИНН вызывает исключение
                    if (repository.findByInn(draft.inn()) != null) {
                        throw new IllegalArgumentException(
                                "дубликат ИНН: " + draft.inn()
                        );
                    }

                    // Сохраняем корректного клиента
                    repository.save(new Client(
                            draft.inn(),
                            draft.name(),
                            draft.email()
                    ));

                    // Увеличиваем количество принятых записей
                    result.addAccepted();

                } catch (IllegalArgumentException e) {

                    // R5: сохраняем ИНН и причину ошибки
                    result.addError(new ImportError(
                            lineNumber,
                            "ИНН [" + inn + "]: " + e.getMessage()
                    ));

                    // После ошибки цикл продолжается
                }
            }
        }

        return result;
    }

    // Вариант 25, часть Б
    // Получаем список ИНН, вызвавших ошибки
    public List<String> addErrorInnList(ImportResult r) {

        if (r == null) {
            throw new IllegalArgumentException("result is null");
        }

        List<String> inns = new ArrayList<>();

        // Перебираем ошибки из существующего ImportResult
        for (ImportError error : r.errors()) {

            String message = error.message();

            // Находим ИНН между "ИНН [" и "]"
            if (message != null && message.startsWith("ИНН [")) {

                int end = message.indexOf(']');

                if (end >= 5) {
                    String inn = message.substring(5, end);
                    inns.add(inn);
                }
            }
        }

        return inns;
    }
}
