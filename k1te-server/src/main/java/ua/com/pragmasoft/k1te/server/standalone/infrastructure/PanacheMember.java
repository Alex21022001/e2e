/* LGPL 3.0 ©️ Dmytro Zemnytskyi, pragmasoft@gmail.com, 2023 */
package ua.com.pragmasoft.k1te.server.standalone.infrastructure;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

import ua.com.pragmasoft.k1te.backend.router.domain.Member;

@Entity
@Table(name = "Member")
public class PanacheMember extends PanacheEntityBase implements Member {

  @EmbeddedId private MemberPK memberPK;

  @Column(nullable = false)
  private String userName;

  @Column(nullable = false)
  private boolean host;

  @Column(nullable = false, unique = true)
  private String connectionUri;

  private String peerMemberId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "channelName", nullable = false, insertable = false, updatable = false)
  private PanacheChannel channel;

  public static PanacheMember of(
      String memberId,
      String channelName,
      String userName,
      boolean host,
      String connectionUri,
      String peerMemberId) {
    return new PanacheMember(
        id(memberId, channelName), userName, host, connectionUri, peerMemberId);
  }

  public static MemberPK id(String memberId, String channelName) {
    return new MemberPK(memberId, channelName);
  }

  public PanacheMember() {}

  public PanacheMember(
      MemberPK memberPK, String userName, boolean host, String connectionUri, String peerMemberId) {
    this.memberPK = memberPK;
    this.userName = userName;
    this.host = host;
    this.connectionUri = connectionUri;
    this.peerMemberId = peerMemberId;
  }

  @Override
  public String getId() {
    return this.memberPK.memberId;
  }

  @Override
  public String getChannelName() {
    return this.memberPK.channelName;
  }

  @Override
  public String getUserName() {
    return this.userName;
  }

  @Override
  public boolean isHost() {
    return this.host;
  }

  @Override
  public String getConnectionUri() {
    return this.connectionUri;
  }

  @Override
  public String getPeerMemberId() {
    return this.peerMemberId;
  }

  public MemberPK getMemberPK() {
    return memberPK;
  }

  public void setMemberPK(MemberPK memberPK) {
    this.memberPK = memberPK;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void setHost(boolean host) {
    this.host = host;
  }

  public void setConnectionUri(String connectionUri) {
    this.connectionUri = connectionUri;
  }

  public void setPeerMemberId(String peerMemberId) {
    this.peerMemberId = peerMemberId;
  }

  public void setChannel(PanacheChannel channel) {
    this.channel = channel;
  }

  public PanacheChannel getChannel() {
    return channel;
  }

  @Embeddable
  public static class MemberPK implements Serializable {

    private String channelName;
    private String memberId;

    public MemberPK() {}

    public MemberPK(String memberId, String channelName) {
      this.memberId = memberId;
      this.channelName = channelName;
    }

    public String getMemberId() {
      return memberId;
    }

    public void setMemberId(String memberId) {
      this.memberId = memberId;
    }

    public String getChannelName() {
      return channelName;
    }

    public void setChannelName(String channelName) {
      this.channelName = channelName;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      MemberPK memberPK = (MemberPK) o;

      if (!Objects.equals(channelName, memberPK.channelName)) return false;
      return Objects.equals(memberId, memberPK.memberId);
    }

    @Override
    public int hashCode() {
      int result = channelName != null ? channelName.hashCode() : 0;
      result = 31 * result + (memberId != null ? memberId.hashCode() : 0);
      return result;
    }
  }
}