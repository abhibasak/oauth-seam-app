package com.tcs.os.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.Name;

@Entity
@Name("consumersOAuth")
@Table(name = "consumers_oauth")

@NamedQueries(value = {
    @NamedQuery(name = ConsumersOAuth.Fields.GET_CONSUMERS_BY_KEY,
            	query = "select consumersOAuth from ConsumersOAuth consumersOAuth where "
                      + " consumersOAuth.consumerKey = :" + ConsumersOAuth.Fields.CONSUMER_KEY)
})
public class ConsumersOAuth extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 2262085826039191652L;

    public ConsumersOAuth() {
        super();
    }

    @Id
	@SequenceGenerator(name = "consumers_oauth_ID_GENERATOR", sequenceName = "consumers_oauth_SEQ")
    @GeneratedValue(generator = "consumers_oauth_ID_GENERATOR")
    @Column(unique = true, nullable = false)
	private Long id;

    @NotNull
    @NotEmpty
    @Column(name = "key")
    private String consumerKey;

    @NotNull
    @NotEmpty
    @Column(name = "secret")
    private String consumerSecret;
    
    @Column(name = "scope")
    private String consumerScope;
    
    public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public void setConsumerKey(final String consumerKey) {
		this.consumerKey = consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public void setConsumerSecret(final String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}
	
	public String getConsumerScope() {
		return consumerScope;
	}

	public void setConsumerScope(final String consumerScope) {
		this.consumerScope = consumerScope;
	}

	public static class Fields {
        public static final String GET_CONSUMERS_BY_KEY = "GET_CONSUMERS_BY_KEY";
        
        public static final String CONSUMER_KEY = "CONSUMER_KEY";
    }
	
	public static ConsumersOAuth getConsumersOAuthByKey(
			final EntityManager entityManager, final String consumerKeyIP) {
		return ((ConsumersOAuth) entityManager
				.createNamedQuery(Fields.GET_CONSUMERS_BY_KEY)
				.setParameter(Fields.CONSUMER_KEY, consumerKeyIP)
				.getSingleResult());
	}
}
