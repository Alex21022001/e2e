/* LGPL 3.0 ©️ Dmytro Zemnytskyi, pragmasoft@gmail.com, 2023 */
package ua.com.pragmasoft.k1te.backend.router.domain.payload;

public final class Pong implements Payload {

  @Override
  public Type type() {
    return Type.PONG;
  }
}
