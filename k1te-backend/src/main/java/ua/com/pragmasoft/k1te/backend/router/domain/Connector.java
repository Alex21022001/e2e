/* LGPL 3.0 ©️ Dmytro Zemnytskyi, pragmasoft@gmail.com, 2023 */
package ua.com.pragmasoft.k1te.backend.router.domain;

import java.util.Objects;
import ua.com.pragmasoft.k1te.backend.shared.KiteException;

public interface Connector {

  String id();

  void dispatch(RoutingContext context) throws KiteException;

  default String connectionUri(String rawConnection) {
    return id() + ':' + rawConnection;
  }

  static String rawConnection(String connectionUri) {
    var array = connectionUri.split(":", 2);
    Objects.checkIndex(1, array.length);
    return array[1];
  }

  static String connectorId(String connectionUri) {
    var array = connectionUri.split(":", 2);
    return array[0];
  }
}
