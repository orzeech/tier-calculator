package pl.orzechsoft.tiercalculator.service;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientMessageService {

  private final MessageSource messageSource;

  public String getMessage(String code, String... args) {
    return messageSource.getMessage(code, args, Locale.getDefault());
  }
}
