package pl.orzechsoft.tiercalculator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Locale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

class ClientMessageServiceTest {

  @Test
  @DisplayName("Should get message")
  void getMessage() {
    MessageSource messageSource = mock(MessageSource.class);
    String expected = "Some kind of a message";
    when(messageSource.getMessage(anyString(), any(), eq(Locale.getDefault()))).thenReturn(
        expected);

    ClientMessageService service = new ClientMessageService(messageSource);

    String code = "some.kind.of.a.code";
    String argument1 = "argument 1";
    String argument2 = "argument 2";
    String message = service.getMessage(code, argument1, argument2);

    assertEquals(expected, message);
    verify(messageSource, times(1)).getMessage(code, new String[]{argument1, argument2},
        Locale.getDefault());
  }
}