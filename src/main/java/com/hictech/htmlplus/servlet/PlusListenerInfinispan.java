package com.hictech.htmlplus.servlet;

import static com.hictech.htmlplus.cache.PlusCacheInfinispan.CLUSTER;
import static com.hictech.htmlplus.cache.PlusCacheInfinispan.SESSIONS_CLIENTS;
import static com.hictech.htmlplus.cache.PlusCacheInfinispan.SESSIONS_HOSTS;
import static com.hictech.htmlplus.cache.PlusCacheInfinispan.lockGet;
import static com.hictech.htmlplus.cache.PlusCacheInfinispan.lockPut;

import java.util.HashMap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;
import org.infinispan.notifications.cachemanagerlistener.annotation.CacheStarted;
import org.infinispan.notifications.cachemanagerlistener.annotation.CacheStopped;
import org.infinispan.notifications.cachemanagerlistener.annotation.ViewChanged;
import org.infinispan.notifications.cachemanagerlistener.event.CacheStartedEvent;
import org.infinispan.notifications.cachemanagerlistener.event.CacheStoppedEvent;
import org.infinispan.notifications.cachemanagerlistener.event.ViewChangedEvent;
import org.infinispan.remoting.transport.Address;
import org.infinispan.remoting.transport.jgroups.JGroupsTransport;

import com.hictech.htmlplus.cache.PlusCacheInfinispan;
import com.hictech.util.h.HTree;

@WebListener
@Listener(sync=false, primaryOnly=true)
public class PlusListenerInfinispan implements PlusListener, ServletContextListener {
	
	private void init() {
		PlusListenerInfinispan listener = new PlusListenerInfinispan();
		
		EmbeddedCacheManager manager = PlusCacheInfinispan.cacheContainer();
		manager.addListener(listener);
		manager.getCache(CLUSTER).addListener(listener);
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
	
	@ViewChanged
	public void viewChanged(ViewChangedEvent event) throws Exception {
		EmbeddedCacheManager manager = event.getCacheManager();
		JGroupsTransport   transport = (JGroupsTransport) manager.getTransport();
		Cache<Object, Object>  cache = manager.getCache(CLUSTER);
		HTree               topology = HTree.wrap(cache.get("members"));

		System.out.println("<<<<<<<< "+event.getOldMembers());
		System.out.println("<<<<<<<< "+event.getNewMembers());
		System.out.println("<<<<<<<< "+event.getViewId());
		System.out.println("<<<<<<<< "+transport.getPhysicalAddresses());
		
		HashMap<Object, Object> members = new HashMap<>();
		for( Address address: manager.getMembers() ) {
			HashMap<Object, Object> info = new HashMap<>();
			
			String[] array = address.toString().split("/");
			info.put("name",    array[0]);
			info.put("cluster", array[1]);
			
			members.put(array[0], info); 
		}; 

		topology.remove("members");
		topology.put("members", members);
		
		System.out.println(topology);
	}
	
	@CacheStarted
	public void cacheCreated(CacheStartedEvent event) {
		if( event.getCacheName().equals(CLUSTER) ) {
			
			EmbeddedCacheManager  manager   = event.getCacheManager();
			Cache<Object, Object> cache     = manager.getCache(CLUSTER);
			PlusCacheInfinispan   plusCache = new PlusCacheInfinispan(cache);
			
			/*
			 * This if-else block is mandatory because the
			 * listener is setted up as async and the
			 * callback order will not be respected.
			 */
			if( lockGet(plusCache, "cluster") == null ) {
				lockPut(plusCache, "cluster", "created", ()-> {
					plusStart(event);
					monadeStart(event);
				});
			}
			else {
				monadeStart(event);
			}
		}
	}

	@CacheStopped 
	public void cacheStopped(CacheStoppedEvent event) {
		if( event.getCacheName().equals(CLUSTER) ) {
			plusClose(event);
		}
	}
	
	@CacheEntryCreated
	public void cacheEntryCreated(CacheEntryCreatedEvent<?, ?> event) {
		if( event.isPre() ) {
			return;
		}
		if( event.getCache().getName().equals(CLUSTER) ) {
		}
		else if( event.getCache().getName().equals(SESSIONS_HOSTS) ) {
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
		
		else if( event.getCache().getName().equals(SESSIONS_HOSTS) ) {
			hostClose(event);
		}
		else if( event.getCache().getName().equals(SESSIONS_CLIENTS) ) {
			clientClose(event);
		}
	}
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		init();
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		monadeClose(event);
	}

}
