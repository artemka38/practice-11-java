
package ru.edu.pr08;

public class ClientCsvParser {

    public ClientDraft parse(String line, int lineNumber) {

        // Проверяем, что строка не равна null
        if (line == null) {
            throw new IllegalArgumentException(
                    "строка " + lineNumber + ": строка равна null"
            );
        }

        // Разделяем строку по точке с запятой
        // -1 сохраняет пустые поля
        String[] parts = line.split(";", -1);

        // Проверяем, что строка содержит ровно 3 поля
        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "строка " + lineNumber
                    + ": ожидается 3 поля, получено "
                    + parts.length
            );
        }

        // Создаём объект с полученными данными
        return new ClientDraft(
                parts[0],
                parts[1],
                parts[2]
        );
    }
}
