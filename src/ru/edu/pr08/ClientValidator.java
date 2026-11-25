package ru.edu.pr08;

import java.util.ArrayList;
import java.util.List;

public class ClientValidator {

    public List<String> validate(ClientDraft d) {

        // Список ошибок одной карточки клиента
        List<String> errors = new ArrayList<>();

        // Проверяем наличие карточки
        if (d == null) {
            errors.add("карточка отсутствует");
            return errors;
        }

        // Проверяем ИНН
        String inn = d.inn();

        if (inn == null ||
                (inn.length() != 10 && inn.length() != 12)) {

            errors.add("ИНН должен содержать 10 или 12 цифр");

        } else if (!inn.matches("[0-9]+")) {

            errors.add("ИНН должен содержать только цифры");
        }

        // Проверяем имя клиента
        String name = d.name();

        if (name == null || name.isEmpty()) {

            errors.add("имя не должно быть пустым");

        } else if (!name.equals(name.trim())) {

            errors.add("пробелы по краям имени запрещены");
        }

        // Проверяем лишние пробелы в ИНН
        if (inn != null && !inn.equals(inn.trim())) {
            errors.add("пробелы по краям ИНН запрещены");
        }

        // Проверяем email
        String email = d.email();

        if (email == null || email.isEmpty()) {

            errors.add("email не должен быть пустым");

        } else if (!email.equals(email.trim())) {

            errors.add("пробелы по краям email запрещены");

        } else {

            // Находим положение символа @
            int at = email.indexOf('@');

            if (at < 0) {

                errors.add("email должен содержать @");

            } else if (at == 0) {

                errors.add("email: пустая левая часть");

            } else if (at == email.length() - 1) {

                errors.add("email: пустая правая часть");

            } else if (at != email.lastIndexOf('@')) {

                errors.add("email должен содержать ровно один @");
            }
        }

        // Возвращаем найденные ошибки
        return errors;
    }
}