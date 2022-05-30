package be.steen.reproducer.quarkus.rabbitmq;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.StringJoiner;

public class SomeMessage {

  @JsonProperty("message")
  private String message;

  public String message() {
    return message;
  }

  public void setMessage(final String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", SomeMessage.class.getSimpleName() + "[", "]")
        .add("message='" + message + "'")
        .toString();
  }
}
