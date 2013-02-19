package com.tcs.os.service.form;

import javax.ws.rs.FormParam;

public class CustomResponseForm {

	@FormParam("newsFeed")
	private String newsFeed;

	public String getNewsFeed() {
		return newsFeed;
	}

}
