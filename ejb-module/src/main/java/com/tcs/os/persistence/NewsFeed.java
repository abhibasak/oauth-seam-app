package com.tcs.os.persistence;

import java.io.Serializable;
import java.util.List;

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
import org.jboss.seam.annotations.Name;

@Entity
@Name("newsFeed")
@Table(name = "news_feed")

@NamedQueries(value = {
    @NamedQuery(name = NewsFeed.Fields.GET_NEWS_FOR_A_CATEGORY,
            	query = "select nf from NewsFeed nf where "
                      + " nf.category = :" + NewsFeed.Fields.CATEGORY)
})
public class NewsFeed extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 2262675826039191652L;
	
	public NewsFeed() {
        super();
    }
	
    @Id
	@SequenceGenerator(name = "news_feed_ID_GENERATOR", sequenceName = "news_feed_ID_SEQ")
    @GeneratedValue(generator = "news_feed_ID_GENERATOR")
    @Column(unique = true, nullable = false)
	private Long id;
    
    @NotEmpty
    @Column(name = "category")
    private String category;
    
    @Column(name = "description", nullable = true)
    private String description;

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(final String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}
	
	public static class Fields {
        public static final String GET_NEWS_FOR_A_CATEGORY = "GET_NEWS_FOR_A_CATEGORY";
        
        public static final String CATEGORY = "CATEGORY";
    }

	@SuppressWarnings("unchecked")
	public static List<NewsFeed> getAllNewsFeedsForACategory(final String catg,
			final EntityManager entityManager) {
		return (List<NewsFeed>) entityManager.createNamedQuery(Fields.GET_NEWS_FOR_A_CATEGORY)
											 .setParameter(Fields.CATEGORY, catg)
											 .getResultList();
	}
}
