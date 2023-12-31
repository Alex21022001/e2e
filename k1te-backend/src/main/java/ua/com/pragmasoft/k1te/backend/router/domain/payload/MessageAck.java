/* LGPL 3.0 ©️ Dmytro Zemnytskyi, pragmasoft@gmail.com, 2023 */
package ua.com.pragmasoft.k1te.backend.router.domain.payload;

import java.time.Instant;
import java.util.Objects;

public record MessageAck(String messageId, String destinationMessageId, Instant delivered)
    implements Payload {

  public MessageAck(String messageId, String destinationMessageId, Instant delivered) {
    Objects.requireNonNull(messageId, "messageId");
    this.messageId = messageId;
    Objects.requireNonNull(destinationMessageId, "destinationMessageId");
    this.destinationMessageId = destinationMessageId;
    this.delivered = delivered;
  }

  public MessageAck(String messageId, String destiationMessageId) {
    this(messageId, destiationMessageId, Instant.now());
  }

  public MessageAck(String messageId) {
    this(messageId, messageId);
  }

  @Override
  public Type type() {
    return Type.ACK;
  }
}
