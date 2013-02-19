package com.tcs.os.service.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.UserTransaction;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.tcs.os.exception.RestServicesException;
import com.tcs.os.persistence.NewsFeed;
import com.tcs.os.service.CustomRestService;
import com.tcs.os.service.form.CustomResponseForm;

@Name("customRestServiceImpl")
@Scope(ScopeType.STATELESS)
public class CustomRestServiceImpl implements CustomRestService {
	
	@In
	private EntityManager entityManager;
	
	@Logger
    private Log log;
	
	private static final String SERVICE_STATUS_SUCCESS = "success";

	private static final String SERVICE_STATUS_ERROR = "error";
	
	private static final String SAVE_NEWS_FEED_ERROR_LOG = "CustomRestServiceImpl: saveNewsFeed - error";
	
	private static final String ROOT_ELEMENT_ERROR_LOG = "CustomRestServiceImpl: getRootElement - error";
	
	private static final String RECEIVE_NEWS_FEED_ELEMENT_ERROR_LOG = "CustomRestServiceImpl: receiveNewsFeed - error";

	@Override
	public String getNewsFeed(final String category) throws RestServicesException {
		log.info("CustomRestServiceImpl: receiveNewsFeed - starts");
		
		final UserTransaction userTx = (UserTransaction) Component
				.getInstance("org.jboss.seam.transaction.transaction");
		
		try {
			try {
				userTx.begin();
				entityManager.joinTransaction();
				
				final List<NewsFeed> newsFeeds = NewsFeed.getAllNewsFeedsForACategory(category, entityManager);
				final StringBuilder returnString = new StringBuilder("<news-feeds>");
				if (newsFeeds != null) {
					for (NewsFeed newsFeed : newsFeeds) {
						returnString.append("<news>")
									.append("<category>").append(newsFeed.getCategory()).append("</category>")
									.append("<description>").append(newsFeed.getDescription()).append("</description>")
									.append("</news>");
					}
				}
				
				returnString.append("</news-feeds>");
				userTx.commit();
				
				return returnString.toString();
			} catch (RollbackException exception) {
				userTx.rollback();
				log.error(SAVE_NEWS_FEED_ERROR_LOG);
				throw new RestServicesException(exception);
			} catch (HeuristicMixedException exception) {
				userTx.rollback();
				log.error(SAVE_NEWS_FEED_ERROR_LOG);
				throw new RestServicesException(exception);
			} catch (HeuristicRollbackException exception) {
				userTx.rollback();
				log.error(SAVE_NEWS_FEED_ERROR_LOG);
				throw new RestServicesException(exception);
			} catch (NotSupportedException exception) {
				userTx.rollback();
				log.error(SAVE_NEWS_FEED_ERROR_LOG);
				throw new RestServicesException(exception);
			} catch (javax.transaction.SystemException exception) {
				userTx.rollback();
				log.error(SAVE_NEWS_FEED_ERROR_LOG);
				throw new RestServicesException(exception);
			}
		} catch (javax.transaction.SystemException exception) {
			exception.printStackTrace();
			throw new RestServicesException(exception);
		}
	}

	@Override
	public String receiveNewsFeed(final CustomResponseForm customResponseForm)
			throws RestServicesException {
		log.info("CustomRestServiceImpl: receiveNewsFeed - starts");
		
		final Date sysDate = new Date();
		String status = SERVICE_STATUS_ERROR;
		
		try {
			// Receives News Feed XML
			final String newsFeed = URIUtil.decode(customResponseForm.getNewsFeed());
			
			// Extracts Root Element
			final Element rootElement = getRootElement(newsFeed);
			if (rootElement == null
					|| rootElement.getNodeType() != Node.ELEMENT_NODE) {
				log.error("CustomRestServiceImpl: receiveNewsFeed - blank XML, root element not found");
				return status;
			}
			
			// Extracts category and description
			String category = null;
			if (rootElement
					.getElementsByTagName("category") == null || rootElement
					.getElementsByTagName("category").item(0) == null) {
				log.error("CustomRestServiceImpl: receiveNewsFeed - category not found");
				return status;
			} else {
				category = rootElement.getElementsByTagName("category").item(0)
						.getTextContent();
			}
			
			String description = null;
			if (rootElement
					.getElementsByTagName("description") == null || rootElement
					.getElementsByTagName("description").item(0) == null) {
				log.info("CustomRestServiceImpl: receiveNewsFeed - description blank");
			} else {
				description = rootElement.getElementsByTagName("description")
						.item(0).getTextContent();
			}
			
			// Prepare NewsFeed entity object
			final NewsFeed newsFeedEntity = new NewsFeed();
			newsFeedEntity.setCategory(category);
			newsFeedEntity.setDescription(description);
			newsFeedEntity.setCreatedBy(3L);
			newsFeedEntity.setUpdatedBy(3L);
			newsFeedEntity.setLastCreated(sysDate);
			newsFeedEntity.setLastUpdated(sysDate);
			
			// Save to DB
			saveNewsFeed(newsFeedEntity);
			
			status = SERVICE_STATUS_SUCCESS;
		} catch (URIException uriException) {
			uriException.printStackTrace();
			log.error(RECEIVE_NEWS_FEED_ELEMENT_ERROR_LOG);
		} catch (RestServicesException restServicesException) {
			restServicesException.printStackTrace();
			log.error(RECEIVE_NEWS_FEED_ELEMENT_ERROR_LOG);
		}
		
		log.info("CustomRestServiceImpl: receiveNewsFeed - ends successfully");
		return status;
	}

	private Element getRootElement(final String inputString) throws RestServicesException {
		log.info("CustomRestServiceImpl: getRootElement - starts");
		Element rootElement = null;

		try {
			final DocumentBuilder builder = DocumentBuilderFactory
					.newInstance().newDocumentBuilder();
			final Document doc = builder.parse(new InputSource(
					new StringReader(inputString)));
			rootElement = doc.getDocumentElement();
		} catch (ParserConfigurationException exception) {
			log.error(ROOT_ELEMENT_ERROR_LOG);
			throw new RestServicesException(exception);
		} catch (SAXException exception) {
			log.error(ROOT_ELEMENT_ERROR_LOG);
			throw new RestServicesException(exception);
		} catch (IOException exception) {
			log.error(ROOT_ELEMENT_ERROR_LOG);
			throw new RestServicesException(exception);
		}
		log.info("CustomRestServiceImpl: getRootElement - ends successfully");
		return rootElement;
	}

	private void saveNewsFeed(final NewsFeed newsFeedEntity) throws RestServicesException {
		log.info("CustomRestServiceImpl: saveNewsFeed - starts");
		
		final UserTransaction userTx = (UserTransaction) Component
				.getInstance("org.jboss.seam.transaction.transaction");
		
		try {
			try {
				userTx.begin();
				entityManager.joinTransaction();
				entityManager.setFlushMode(FlushModeType.COMMIT);
				
				entityManager.persist(newsFeedEntity);
				
				entityManager.flush();
				userTx.commit();
			} catch (RollbackException exception) {
				userTx.rollback();
				log.error(SAVE_NEWS_FEED_ERROR_LOG);
				throw new RestServicesException(exception);
			} catch (HeuristicMixedException exception) {
				userTx.rollback();
				log.error(SAVE_NEWS_FEED_ERROR_LOG);
				throw new RestServicesException(exception);
			} catch (HeuristicRollbackException exception) {
				userTx.rollback();
				log.error(SAVE_NEWS_FEED_ERROR_LOG);
				throw new RestServicesException(exception);
			} catch (NotSupportedException exception) {
				userTx.rollback();
				log.error(SAVE_NEWS_FEED_ERROR_LOG);
				throw new RestServicesException(exception);
			} catch (javax.transaction.SystemException exception) {
				userTx.rollback();
				log.error(SAVE_NEWS_FEED_ERROR_LOG);
				throw new RestServicesException(exception);
			}
		} catch (javax.transaction.SystemException exception) {
			exception.printStackTrace();
			throw new RestServicesException(exception);
		}
		log.info("CustomRestServiceImpl: saveNewsFeed - ends successfully");
	}
}
