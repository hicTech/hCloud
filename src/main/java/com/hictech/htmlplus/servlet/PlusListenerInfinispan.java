package com.hictech.htmlplus.servlet;

import static com.hictech.htmlplus.cache.PlusCacheInfinispan.SESSIONS_CLIENTS;
import static com.hictech.htmlplus.cache.PlusCacheInfinispan.SESSIONS_DEFAULT;
import static com.hictech.htmlplus.cache.PlusCacheInfinispan.SESSIONS_HOSTS;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;
import org.infinispan.notifications.cachemanagerlistener.annotation.CacheStarted;
import org.infinispan.notifications.cachemanagerlistener.annotation.CacheStopped;
import org.infinispan.notifications.cachemanagerlistener.event.CacheStartedEvent;
import org.infinispan.notifications.cachemanagerlistener.event.CacheStoppedEvent;

import com.hictech.htmlplus.cache.PlusCacheInfinispan;

@Listener
@WebListener
public class PlusListenerInfinispan implements PlusListener, ServletContextListener {
	
	private void init() {
		PlusListenerInfinispan listener = new PlusListenerInfinispan();
		
		EmbeddedCacheManager manager = PlusCacheInfinispan.cacheContainer();
		manager.addListener(listener);
		manager.getCache(SESSIONS_DEFAULT).addListener(listener);
		manager.getCache(SESSIONS_HOSTS)  .addListener(listener);
		manager.getCache(SESSIONS_CLIENTS).addListener(listener);
	}

	@Override
	public void plusStart(Object event) {
		System.out.println(">>>>>>>> plus start <<<<<<<<");
	}

	@Override
	public void plusClose(Object event) {
		System.out.println(">>>>>>>> plus close <<<<<<<<");
	}

	@Override
	public void monadeStart(Object event) {
		System.out.println(">>>>>>>> monade start <<<<<<<<");
	}

	@Override
	public void monadeClose(Object event) {
		System.out.println(">>>>>>>> monade close <<<<<<<<");
	}

	@Override
	public void hostStart(Object event) {
		System.out.println(">>>>>>>> host start <<<<<<<<");
	}

	@Override
	public void hostClose(Object event) {
		System.out.println(">>>>>>>> host close <<<<<<<<");
		
	}

	@Override
	public void clientStart(Object event) {
		System.out.println(">>>>>>>> client start <<<<<<<<");
		
	}

	@Override
	public void clientClose(Object event) {
		System.out.println(">>>>>>>> client close <<<<<<<<");
	}
	
	@CacheStarted
	public void cacheCreated(CacheStartedEvent event) {
		if( event.getCacheName().equals(SESSIONS_DEFAULT) ) {
			plusStart(event);
		}
	}
	
	@CacheStopped 
	public void cacheStopped(CacheStoppedEvent event) {
		if( event.getCacheName().equals(SESSIONS_DEFAULT) ) {
			plusClose(event);
		}
	}
	
	@CacheEntryCreated
	public void cacheEntryCreated(CacheEntryCreatedEvent<?, ?> event) {
		if( event.isPre() ) {
			return;
		}
		if( event.getCache().getName().equals(SESSIONS_HOSTS) ) {
			hostStart(event);
		}
		else if( event.getCache().getName().equals(SESSIONS_CLIENTS) ) {
			clientStart(event);
		}
	}
	
	@CacheEntryRemoved
	public void cacheEntryRemoved(CacheEntryRemovedEvent<?, ?> event) {
		if( event.isPre() ) {
			return;
		}
		
		if( event.getCache().getName().equals(SESSIONS_HOSTS) ) {
			hostClose(event);
		}
		else if( event.getCache().getName().equals(SESSIONS_CLIENTS) ) {
			clientClose(event);
		}
	}
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		init();
		
		monadeStart(event);
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}

}
