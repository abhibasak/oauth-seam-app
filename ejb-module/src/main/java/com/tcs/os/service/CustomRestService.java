package com.tcs.os.service;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jboss.resteasy.annotations.Form;

import com.tcs.os.exception.RestServicesException;
import com.tcs.os.service.form.CustomResponseForm;

@Path("/news-feed")
public interface CustomRestService {

	@GET
	@Path("/{category}")
	@Produces("text/plain")
	public String getNewsFeed(@PathParam("category") final String category)
			throws RestServicesException;

	/**
	 * This service is called for saving one news feed into the application.
	 *
	 * @param customResponseForm
	 * @return
	 * @throws RestServicesException
	 */
	@POST
	@Path("/post-news")
	public String receiveNewsFeed(
			@Form final CustomResponseForm customResponseForm)
			throws RestServicesException;
}
