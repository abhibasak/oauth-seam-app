package com.tcs.os.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

import org.jboss.seam.annotations.Name;

@Name("commonUtil")
public class CommonUtil {
	
	public static final String ENTITY_MANAGER = "entityManager";

	public static final String USER_TRANSACTION = "localTransaction";
	
	private boolean concurrentUpdateFlag;

    public boolean isConcurrentUpdateFlag() {
        return concurrentUpdateFlag;
    }

    public void setConcurrentUpdateFlag(final boolean concurrentUpdateFlag) {
        this.concurrentUpdateFlag = concurrentUpdateFlag;
    }
	
    public static Date getDateInUTC(final Date date) {
        return changeTimeZone(date, TimeZone.getTimeZone("UTC"));
    }
    
    public static Date changeTimeZone(final Date date, final TimeZone timeZone) {
        final Calendar initialCalendar = Calendar.getInstance(timeZone);
        initialCalendar.setTimeInMillis(date.getTime());

        final Calendar returnCalendar = Calendar.getInstance();
        returnCalendar.set(Calendar.YEAR, initialCalendar.get(Calendar.YEAR));
        returnCalendar.set(Calendar.MONTH, initialCalendar.get(Calendar.MONTH));
        returnCalendar.set(Calendar.DAY_OF_MONTH, initialCalendar.get(Calendar.DAY_OF_MONTH));
        returnCalendar.set(Calendar.HOUR_OF_DAY, initialCalendar.get(Calendar.HOUR_OF_DAY));
        returnCalendar.set(Calendar.MINUTE, initialCalendar.get(Calendar.MINUTE));
        returnCalendar.set(Calendar.SECOND, initialCalendar.get(Calendar.SECOND));
        returnCalendar.set(Calendar.MILLISECOND, initialCalendar.get(Calendar.MILLISECOND));

        return returnCalendar.getTime();
    }
    
    public static Date changeTimeZoneToSystem(final Date date) {
        final Calendar systemCalendar = Calendar.getInstance();
        systemCalendar.setTimeInMillis(date.getTime());
        final Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utcCalendar.set(Calendar.YEAR, systemCalendar.get(Calendar.YEAR));
        utcCalendar.set(Calendar.MONTH, systemCalendar.get(Calendar.MONTH));
        utcCalendar.set(Calendar.DAY_OF_MONTH, systemCalendar.get(Calendar.DAY_OF_MONTH));
        utcCalendar.set(Calendar.HOUR_OF_DAY, systemCalendar.get(Calendar.HOUR_OF_DAY));
        utcCalendar.set(Calendar.MINUTE, systemCalendar.get(Calendar.MINUTE));
        utcCalendar.set(Calendar.SECOND, systemCalendar.get(Calendar.SECOND));
        utcCalendar.set(Calendar.MILLISECOND, systemCalendar.get(Calendar.MILLISECOND));
        systemCalendar.setTimeInMillis(utcCalendar.getTimeInMillis());
        return systemCalendar.getTime();
    }

	public static Map<String, Object> getEntityManager() {
		final Map<String, Object> returnMap = new HashMap<String, Object>();
		EntityManager entityManager = null;
		UserTransaction tx = null;

		try {
			final InitialContext ctx = new InitialContext();
			tx = (UserTransaction) ctx.lookup("UserTransaction");
			final EntityManagerFactory emf = (EntityManagerFactory) ctx
					.lookup("java:/oauthSeamEntityManagerFactory");
			entityManager = emf.createEntityManager();
			returnMap.put(ENTITY_MANAGER, entityManager);
			returnMap.put(USER_TRANSACTION, tx);
		} catch (NamingException namingException) {
			namingException.printStackTrace();
		}
		return returnMap;
	}
}
