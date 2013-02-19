package com.tcs.os.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.NotEmpty;
import org.jboss.seam.annotations.Name;

@Entity
@Name("accessTokenOAuth")
@Table(name = "access_tokens_oauth")

@NamedQueries(value = {
    @NamedQuery(name = AccessTokenOAuth.Fields.GET_ACCESS_TOKEN_BY_TOKEN,
            	query = "select accessTokenOAuth from AccessTokenOAuth accessTokenOAuth where "
                      + " accessTokenOAuth.accessToken = :" + AccessTokenOAuth.Fields.TOKEN)
})
public class AccessTokenOAuth extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 2262675826039191652L;

    public AccessTokenOAuth() {
        super();
    }

    @Id
	@SequenceGenerator(name = "access_tokens_oauth_ID_GENERATOR", sequenceName = "access_tokens_oauth_SEQ")
    @GeneratedValue(generator = "access_tokens_oauth_ID_GENERATOR")
    @Column(unique = true, nullable = false)
	private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumer_key", referencedColumnName = "key")
    private ConsumersOAuth consumersOAuth;

    @NotEmpty
    @Column(name = "token")
    private String accessToken;
    
    @NotEmpty
    @Column(name = "secret")
    private String accessSecret;

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public ConsumersOAuth getConsumersOAuth() {
		return consumersOAuth;
	}

	public void setConsumersOAuth(final ConsumersOAuth consumersOAuth) {
		this.consumersOAuth = consumersOAuth;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(final String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessSecret() {
		return accessSecret;
	}

	public void setAccessSecret(final String accessSecret) {
		this.accessSecret = accessSecret;
	}
	
	public static class Fields {
        public static final String GET_ACCESS_TOKEN_BY_TOKEN = "GET_ACCESS_TOKEN_BY_TOKEN";
        
        public static final String TOKEN = "TOKEN";
    }

	public static AccessTokenOAuth getAccessTokenOAuthByToken(
			final EntityManager entityManager, final String token) {
		return ((AccessTokenOAuth) entityManager
				.createNamedQuery(Fields.GET_ACCESS_TOKEN_BY_TOKEN)
				.setParameter(Fields.TOKEN, token).getSingleResult());
	}
}
