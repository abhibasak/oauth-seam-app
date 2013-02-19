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
import org.jboss.resteasy.auth.oauth.OAuthRequestToken;
import org.jboss.seam.annotations.Name;

@Entity
@Name("requestTokenOAuth")
@Table(name = "request_tokens_oauth")

@NamedQueries(value = {
    @NamedQuery(name = RequestTokenOAuth.Fields.GET_REQUEST_TOKEN_BY_TOKEN,
            	query = "select requestTokenOAuth from RequestTokenOAuth requestTokenOAuth where "
                      + " requestTokenOAuth.requestToken = :" + RequestTokenOAuth.Fields.TOKEN),
    @NamedQuery(name = RequestTokenOAuth.Fields.DELETE_REQUEST_TOKEN_BY_TOKEN,
                query = "delete from RequestTokenOAuth requestTokenOAuth where "
                      + " requestTokenOAuth.requestToken = :" + RequestTokenOAuth.Fields.TOKEN)
})
public class RequestTokenOAuth extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 2262675826039191652L;

    public RequestTokenOAuth() {
        super();
    }

    @Id
	@SequenceGenerator(name = "request_tokens_oauth_ID_GENERATOR", sequenceName = "request_tokens_oauth_SEQ")
    @GeneratedValue(generator = "request_tokens_oauth_ID_GENERATOR")
    @Column(unique = true, nullable = false)
	private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumer_key", referencedColumnName = "key")
    private ConsumersOAuth consumersOAuth;

    @NotEmpty
    @Column(name = "token")
    private String requestToken;
    
    @NotEmpty
    @Column(name = "secret")
    private String requestSecret;
    
    @Column(name = "verifier")
    private String requestVerifier;

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

	public String getRequestToken() {
		return requestToken;
	}

	public void setRequestToken(final String requestToken) {
		this.requestToken = requestToken;
	}

	public String getRequestSecret() {
		return requestSecret;
	}

	public void setRequestSecret(final String requestSecret) {
		this.requestSecret = requestSecret;
	}

	public String getRequestVerifier() {
		return requestVerifier;
	}

	public void setRequestVerifier(final String requestVerifier) {
		this.requestVerifier = requestVerifier;
	}
	
	public static class Fields {
        public static final String GET_REQUEST_TOKEN_BY_TOKEN = "GET_REQUEST_TOKEN_BY_TOKEN";
        
        public static final String DELETE_REQUEST_TOKEN_BY_TOKEN = "DELETE_REQUEST_TOKEN_BY_TOKEN";
        
        public static final String TOKEN = "TOKEN";
    }

	public static RequestTokenOAuth getRequestTokenOAuthByToken(
			final EntityManager entityManager, final String token) {
		return ((RequestTokenOAuth) entityManager
				.createNamedQuery(Fields.GET_REQUEST_TOKEN_BY_TOKEN)
				.setParameter(Fields.TOKEN, token).getSingleResult());
	}

	public static void removeByToken(final EntityManager entityManager,
			final OAuthRequestToken token) {
		entityManager
				.createNamedQuery(RequestTokenOAuth.Fields.DELETE_REQUEST_TOKEN_BY_TOKEN)
				.setParameter(RequestTokenOAuth.Fields.TOKEN, token.getToken())
				.executeUpdate();
	}
}
