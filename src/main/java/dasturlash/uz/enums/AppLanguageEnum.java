package dasturlash.uz.enums;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

public enum AppLanguageEnum {
    UZ, RU, EN;

    public static AppLanguageEnum from(String language) {
        try {
            return valueOf(language.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported language: " + language);
        }
    }
}
