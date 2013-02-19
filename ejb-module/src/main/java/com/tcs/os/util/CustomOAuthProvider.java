package com.tcs.os.util;

import java.net.HttpURLConnection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.UserTransaction;

import org.jboss.resteasy.auth.oauth.OAuthConsumer;
import org.jboss.resteasy.auth.oauth.OAuthException;
import org.jboss.resteasy.auth.oauth.OAuthProvider;
import org.jboss.resteasy.auth.oauth.OAuthRequestToken;
import org.jboss.resteasy.auth.oauth.OAuthToken;
import org.jboss.seam.annotations.Name;

import com.tcs.os.persistence.AccessTokenOAuth;
import com.tcs.os.persistence.ConsumersOAuth;
import com.tcs.os.persistence.RequestTokenOAuth;

@Name("customOAuthProvider")
public class CustomOAuthProvider implements OAuthProvider {

	/**
	 * Not Defined here as Customer Registration considered Out of Scope.
	 * Called when:
	 * 1. Consumer is first registered
	 */
	@Override
	public OAuthConsumer registerConsumer(final String consumerKey,
			final String displayName, final String connectURI)
			throws OAuthException {
		// TODO Implement this method to provide support for Customer Registration
		System.out.println(consumerKey);
		return null;
	}

	/**
	 * Not Defined here as Customer Registration considered Out of Scope
	 * Called when:
	 * 1. Consumer Scope, Permission is registered
	 */
	@Override
	public void registerConsumerPermissions(final String consumerKey,
			final String[] permissions) throws OAuthException {
		// TODO Implement this method to provide support for Customer Registration
		System.out.println(consumerKey);
	}

	/**
	 * Not Defined here as Customer Registration considered Out of Scope
	 * Called when:
	 * 1. Consumer Scope, Permission is registered
	 */
	@Override
	public void registerConsumerScopes(final String consumerKey,
			final String[] scopes) throws OAuthException {
		// TODO Implement this method to provide support for Customer Registration
		System.out.println(consumerKey);
	}

	/**
	 * Utility method to check timestamp for prohibiting multiple use of the same token.
	 */
	@Override
	public void checkTimestamp(final OAuthToken token, final long timestamp)
			throws OAuthException {
		if (token.getTimestamp() > timestamp) {
			throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED,
					"Invalid timestamp");
		}
	}

	/**
	 * Get Realm for the provider.
	 */
	@Override
	public String getRealm() {
		return "default";
	}

	/**
	 * Get consumer details from DB.
	 * Called when:
	 * 1. Request comes for getting Request-Token
	 * 2. Final REST Service is called
	 */
	@Override
	public OAuthConsumer getConsumer(final String consumerKey)
			throws OAuthException {
		OAuthConsumer consumer = null;

		final Map<String, Object> returnMap = CommonUtil.getEntityManager();
		final EntityManager entityManager = (EntityManager) returnMap
				.get(CommonUtil.ENTITY_MANAGER);
		final UserTransaction tx = (UserTransaction) returnMap
				.get(CommonUtil.USER_TRANSACTION);
		try {
			try {
				tx.begin();
				entityManager.joinTransaction();

				final ConsumersOAuth consumersOAuth = ConsumersOAuth
						.getConsumersOAuthByKey(entityManager, consumerKey);
				if (consumersOAuth != null) {
					consumer = new OAuthConsumer(
							consumersOAuth.getConsumerKey(),
							consumersOAuth.getConsumerSecret(), null, null,
							null);
					final String[] scopes = new String[1];
					scopes[0] = consumersOAuth.getConsumerScope();
					consumer.setScopes(scopes);
				} else {
					throw new OAuthException(
							HttpURLConnection.HTTP_UNAUTHORIZED,
							"No such consumer key " + consumerKey);
				}
				entityManager.flush();
				tx.commit();
			} catch (RollbackException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (HeuristicMixedException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (HeuristicRollbackException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (NotSupportedException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (javax.transaction.SystemException exception) {
				exception.printStackTrace();
				tx.rollback();
			}
		} catch (javax.transaction.SystemException exception) {
			exception.printStackTrace();
		}

		return consumer;
	}

	/**
	 * Generate Request Token and Token Secret.
	 * Called when:
	 * 1. Request comes for getting Request-Token
	 *
	 */
	@Override
	public OAuthToken makeRequestToken(final String consumerKey,
			final String callback, final String[] scopes,
			final String[] permissions) throws OAuthException {
		OAuthRequestToken oAuthRequestToken = null;

		final Map<String, Object> returnMap = CommonUtil.getEntityManager();
		final EntityManager entityManager = (EntityManager) returnMap
				.get(CommonUtil.ENTITY_MANAGER);
		final UserTransaction tx = (UserTransaction) returnMap
				.get(CommonUtil.USER_TRANSACTION);
		final Date curDate = new Date();
		try {
			try {
				tx.begin();
				entityManager.joinTransaction();

				final ConsumersOAuth consumersOAuth = ConsumersOAuth
						.getConsumersOAuthByKey(entityManager, consumerKey);

				final String token = makeRandomString();
				final String secret = makeRandomString();
				final RequestTokenOAuth requestTokenOAuth = new RequestTokenOAuth();
				requestTokenOAuth.setRequestToken(token);
				requestTokenOAuth.setRequestSecret(secret);
				requestTokenOAuth.setConsumersOAuth(consumersOAuth);
				requestTokenOAuth.setCreatedBy(3L);
				requestTokenOAuth.setUpdatedBy(3L);
				requestTokenOAuth.setLastCreated(curDate);
				requestTokenOAuth.setLastUpdated(curDate);
				entityManager.persist(requestTokenOAuth);

				final OAuthConsumer consumer = new OAuthConsumer(
						consumersOAuth.getConsumerKey(),
						consumersOAuth.getConsumerSecret(), null, null, null);
				oAuthRequestToken = new OAuthRequestToken(token, secret, null,
						null, null, -1, consumer);

				entityManager.flush();
				tx.commit();
			} catch (RollbackException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (HeuristicMixedException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (HeuristicRollbackException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (NotSupportedException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (javax.transaction.SystemException exception) {
				exception.printStackTrace();
				tx.rollback();
			}
		} catch (javax.transaction.SystemException exception) {
			exception.printStackTrace();
		}
		return oAuthRequestToken;
	}

	/**
	 * Get Request Token Object as stored in the DB with corresponding verifier.
	 * Called when:
	 * 1. Token Authorization is requested
	 * 2. Token Authorization is confirmed by the user
	 * 3. Access Token is requested
	 *
	 */
	@Override
	public OAuthRequestToken getRequestToken(final String consumerKey,
			final String requestToken) throws OAuthException {
		OAuthRequestToken newToken = null;

		final Map<String, Object> returnMap = CommonUtil.getEntityManager();
		final EntityManager entityManager = (EntityManager) returnMap
				.get(CommonUtil.ENTITY_MANAGER);
		final UserTransaction tx = (UserTransaction) returnMap
				.get(CommonUtil.USER_TRANSACTION);
		try {
			try {
				tx.begin();
				entityManager.joinTransaction();

				final RequestTokenOAuth requestTokenOAuth = RequestTokenOAuth
						.getRequestTokenOAuthByToken(entityManager,
								requestToken);
				if (consumerKey != null
						&& !requestTokenOAuth.getConsumersOAuth()
								.getConsumerKey().equals(consumerKey)) {
					throw new OAuthException(
							HttpURLConnection.HTTP_UNAUTHORIZED,
							"No such consumer key " + consumerKey);
				}

				final OAuthConsumer consumer = new OAuthConsumer(
						requestTokenOAuth.getConsumersOAuth().getConsumerKey(),
						requestTokenOAuth.getConsumersOAuth()
								.getConsumerSecret(), null, null, null);
				newToken = new OAuthRequestToken(
						requestTokenOAuth.getRequestToken(),
						requestTokenOAuth.getRequestSecret(), null, null, null,
						-1, consumer);
				newToken.setVerifier(requestTokenOAuth.getRequestVerifier());
				entityManager.flush();
				tx.commit();
			} catch (RollbackException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (HeuristicMixedException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (HeuristicRollbackException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (NotSupportedException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (javax.transaction.SystemException exception) {
				exception.printStackTrace();
				tx.rollback();
			}
		} catch (javax.transaction.SystemException exception) {
			exception.printStackTrace();
		}
		return newToken;
	}

	/**
	 * Authorize Request token by setting the verifier.
	 * Called when:
	 * 1. Token Authorization is confirmed by the user
	 */
	@Override
	public String authoriseRequestToken(final String consumerKey,
			final String requestToken) throws OAuthException {
		String verifier = null;

		final Map<String, Object> returnMap = CommonUtil.getEntityManager();
		final EntityManager entityManager = (EntityManager) returnMap
				.get(CommonUtil.ENTITY_MANAGER);
		final UserTransaction tx = (UserTransaction) returnMap
				.get(CommonUtil.USER_TRANSACTION);
		try {
			try {
				tx.begin();
				entityManager.joinTransaction();

				verifier = makeRandomString();

				final RequestTokenOAuth requestTokenOAuth = RequestTokenOAuth
						.getRequestTokenOAuthByToken(entityManager,
								requestToken);
				requestTokenOAuth.setRequestVerifier(verifier);
				entityManager.merge(requestTokenOAuth);
				entityManager.flush();
				tx.commit();
			} catch (RollbackException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (HeuristicMixedException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (HeuristicRollbackException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (NotSupportedException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (javax.transaction.SystemException exception) {
				exception.printStackTrace();
				tx.rollback();
			}
		} catch (javax.transaction.SystemException exception) {
			exception.printStackTrace();
		}
		return verifier;
	}

	/**
	 * Generate Access Token
	 * Called when:
	 * 1. Access Token is requested
	 */
	@Override
	public OAuthToken makeAccessToken(final String consumerKey,
			final String requestTokenKey, final String verifier)
			throws OAuthException {
		OAuthToken oAuthToken = null;

		final OAuthRequestToken requestToken = verifyAndRemoveRequestToken(
				consumerKey, requestTokenKey, verifier);
		final Map<String, Object> returnMap = CommonUtil.getEntityManager();
		final EntityManager entityManager = (EntityManager) returnMap
				.get(CommonUtil.ENTITY_MANAGER);
		final UserTransaction tx = (UserTransaction) returnMap
				.get(CommonUtil.USER_TRANSACTION);
		final Date curDate = new Date();

		try {
			try {
				tx.begin();
				entityManager.joinTransaction();

				final ConsumersOAuth consumersOAuth = ConsumersOAuth
						.getConsumersOAuthByKey(entityManager, consumerKey);

				final String token = makeRandomString();
				final String secret = makeRandomString();
				final AccessTokenOAuth accessTokenOAuth = new AccessTokenOAuth();
				accessTokenOAuth.setAccessToken(token);
				accessTokenOAuth.setAccessSecret(secret);
				accessTokenOAuth.setConsumersOAuth(consumersOAuth);
				accessTokenOAuth.setCreatedBy(3L);
				accessTokenOAuth.setUpdatedBy(3L);
				accessTokenOAuth.setLastCreated(curDate);
				accessTokenOAuth.setLastUpdated(curDate);
				entityManager.persist(accessTokenOAuth);

				oAuthToken = new OAuthToken(token, secret, null, null, -1,
						requestToken.getConsumer());
				entityManager.flush();
				tx.commit();
			} catch (RollbackException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (HeuristicMixedException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (HeuristicRollbackException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (NotSupportedException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (javax.transaction.SystemException exception) {
				exception.printStackTrace();
				tx.rollback();
			}
		} catch (javax.transaction.SystemException exception) {
			exception.printStackTrace();
		}
		return oAuthToken;
	}
	
	/**
	 * Get Access Token Object
	 * Called when:
	 * 1. Final REST Service is called
	 */
	@Override
	public OAuthToken getAccessToken(final String consumerKey,
			final String accessToken) throws OAuthException {
		OAuthToken oAuthToken = null;

		final Map<String, Object> returnMap = CommonUtil.getEntityManager();
		final EntityManager entityManager = (EntityManager) returnMap
				.get(CommonUtil.ENTITY_MANAGER);
		final UserTransaction tx = (UserTransaction) returnMap
				.get(CommonUtil.USER_TRANSACTION);
		try {
			try {
				tx.begin();
				entityManager.joinTransaction();

				final AccessTokenOAuth accessTokenOAuth = AccessTokenOAuth
						.getAccessTokenOAuthByToken(entityManager, accessToken);
				if (consumerKey != null
						&& !accessTokenOAuth.getConsumersOAuth()
								.getConsumerKey().equals(consumerKey)) {
					throw new OAuthException(
							HttpURLConnection.HTTP_UNAUTHORIZED,
							"Invalid customer key");
				}

				final OAuthConsumer consumer = new OAuthConsumer(
						accessTokenOAuth.getConsumersOAuth().getConsumerKey(),
						accessTokenOAuth.getConsumersOAuth()
								.getConsumerSecret(), null, null, null);

				oAuthToken = new OAuthToken(accessTokenOAuth.getAccessToken(),
						accessTokenOAuth.getAccessSecret(), null, null, -1,
						consumer);

				entityManager.flush();
				tx.commit();
			} catch (RollbackException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (HeuristicMixedException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (HeuristicRollbackException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (NotSupportedException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (javax.transaction.SystemException exception) {
				exception.printStackTrace();
				tx.rollback();
			}
		} catch (javax.transaction.SystemException exception) {
			exception.printStackTrace();
		}
		return oAuthToken;
	}

	private OAuthRequestToken verifyAndRemoveRequestToken(
			final String consumerKey, final String requestToken,
			final String verifier) throws OAuthException {
		final OAuthRequestToken token = getRequestToken(consumerKey,
				requestToken);
		checkCustomerKey(token, consumerKey);

		if (verifier == null || !verifier.equals(token.getVerifier())) {
			throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED,
					"Invalid verifier code for token " + requestToken);
		}

		final Map<String, Object> returnMap = CommonUtil.getEntityManager();
		final EntityManager entityManager = (EntityManager) returnMap
				.get(CommonUtil.ENTITY_MANAGER);
		final UserTransaction tx = (UserTransaction) returnMap
				.get(CommonUtil.USER_TRANSACTION);
		try {
			try {
				tx.begin();
				entityManager.joinTransaction();

				RequestTokenOAuth.removeByToken(entityManager, token);

				entityManager.flush();
				tx.commit();
			} catch (RollbackException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (HeuristicMixedException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (HeuristicRollbackException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (NotSupportedException exception) {
				exception.printStackTrace();
				tx.rollback();
			} catch (javax.transaction.SystemException exception) {
				exception.printStackTrace();
				tx.rollback();
			}
		} catch (javax.transaction.SystemException exception) {
			exception.printStackTrace();
		}

		return token;
	}

	private void checkCustomerKey(final OAuthToken token,
			final String customerKey) throws OAuthException {
		if (customerKey != null
				&& !customerKey.equals(token.getConsumer().getKey())) {
			throw new OAuthException(HttpURLConnection.HTTP_UNAUTHORIZED,
					"Invalid customer key");
		}
	}

	private static String makeRandomString() {
		return UUID.randomUUID().toString();
	}

	@Override
	public Set<String> convertPermissionsToRoles(final String[] permissions) {
		if (permissions.length > 0) {
			System.out.println(permissions[0]);
		}
		return null;
	}
}
