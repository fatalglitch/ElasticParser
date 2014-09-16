package net.wedjaa.elasticparser;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.HashMap;
import java.util.Map;

import net.wedjaa.elasticparser.pager.ESAggregationPager;
import net.wedjaa.elasticparser.pager.ESEmptyPager;
import net.wedjaa.elasticparser.pager.ESFacetsPager;
import net.wedjaa.elasticparser.pager.ESHitsPager;
import net.wedjaa.elasticparser.pager.ESResultsPager;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.json.JSONObject;

/**
 * @author mrwho
 *
 */
public class ESSearch  {
    	
	private ESResultsPager pager;
	private boolean keepClient = false;
    private Client esClient;
    private String mainSearch;
    private String cluster;
    private String [] indexes;
    private String [] types;
    private String strIndexes;
    private String strTypes;
    private String username;
    private String password;
    private String hostname;
    private int port;
    private int searchMode;
    
    private SearchResponse searchResponse;
    private static Logger logger = Logger.getLogger(ESSearch.class);

    public final static int ES_MODE_HITS = 0;
    public final static int ES_MODE_FACETS = 1;
    public final static int ES_MODE_AGGS = 2;

    public final static String ES_DEFAULT_HOST = "localhost";
    public final static int  ES_DEFAULT_PORT = 9300;
    public final static String ES_DEFAULT_CLUSTER = "elasticsearch";
    public final static int ES_DEFAULT_SEARCH_MODE = ES_MODE_HITS;
    
    /**
     * 
     * @return      a ESSearch initialized to connect to the local node and
     * 				search on all the indexes and types returning the <em>Hits</em>. 
     */
    public ESSearch() {
        this(null, null);
    }
  
    /**
     *
     * @param  indexes		a comma separated list of indexes that should be searched. If <em>null</em> all the
     * 						indexes will be searched.
     * @param  types		a comma separated list of types that should be searched.If <em>null</em> all the
     * 						types will be searched.
     * @param  searchMode	the type of results we want to obtain: hits, facets or aggregates
     *  
     * @return      a ESSearch object initialized to connect to the local node with the default connection
     * 				parameters, returning what was specified as a searchMode.
     */
    public ESSearch(String indexes, String types, int searchMode) {
        this(indexes, types, searchMode, ES_DEFAULT_HOST, ES_DEFAULT_PORT, null, null, ES_DEFAULT_CLUSTER);
    }

    /**
    *
    * @param  indexes		a comma separated list of indexes that should be searched. If <em>null</em> all the
    * 						indexes will be searched.
    * @param  types		a comma separated list of types that should be searched.If <em>null</em> all the
    * 						types will be searched.
    * @return      a ESSearch object initialized to connect to the local node with the default connection
    * 				parameters, returning <em>Hits</em>.
    */
    public ESSearch(String indexes, String types) {
        this(indexes, types, ES_DEFAULT_SEARCH_MODE, ES_DEFAULT_HOST, ES_DEFAULT_PORT, null, null, ES_DEFAULT_CLUSTER);
    }

    /**
    *
    * @param  indexes		a comma separated list of indexes that should be searched. If <em>null</em> all the
    * 						indexes will be searched.
    * @param  types			a comma separated list of types that should be searched.If <em>null</em> all the
    * 						types will be searched.
    * @param hostname	 	hostname to connect to - or IP address
    * 
    * @param port	 		a port to connect to for trasport. The default transport port is 9300.

    * @return      a ESSearch object initialized to connect to the specified node to search the
    * 				specified indexes and types, returning <em>Hits</em>.
    */
    public ESSearch(String indexes, String types, String hostname,int port) {
        this(indexes, types, ES_DEFAULT_SEARCH_MODE, hostname, port, null, null, ES_DEFAULT_CLUSTER);
    }

    /**
    *
    * @param  indexes		a comma separated list of indexes that should be searched. If <em>null</em> all the
    * 						indexes will be searched.
    * @param  types			a comma separated list of types that should be searched.If <em>null</em> all the
    * 						types will be searched.
    * 
    * @param  searchMode	the type of results we want to obtain: hits, facets or aggregates
    *  
    * @param  hostname	 	hostname to connect to - or IP address
    * 
    * @param  port	 		a port to connect to for trasport. The default transport port is 9300.

    * @return      a ESSearch object initialized to connect to the specified node to search the
    * 				specified indexes and types, returning hits, facets or aggregates depending on 
    * 				searchMode.
    */   
    public ESSearch(String indexes, String types, int searchMode, String hostname,int port) {
        this(indexes, types, searchMode, hostname, port, null, null, ES_DEFAULT_CLUSTER);
    }
    
    /**
    *
    * @param  indexes		a comma separated list of indexes that should be searched. If <em>null</em> all the
    * 						indexes will be searched.
    * @param  types			a comma separated list of types that should be searched.If <em>null</em> all the
    * 						types will be searched.
    *  
    * @param  hostname	 	hostname to connect to - or IP address
    * 
    * @param  port	 		a port to connect to for transport. The default transport port is 9300.
    * 
    * @param  cluster	 	a cluster name to join. The default cluster name is "elasticsearch".
    * 
    * @return      a ESSearch object initialized to connect to the specified node and cluster to search the
    * 				specified indexes and types, returning <em>Hits</em>.
    */
    public ESSearch(String indexes, String types, String hostname,int port, String cluster) {
        this(indexes, types, ES_DEFAULT_SEARCH_MODE, hostname, port, null, null, cluster);
    }
    
    /**
    *
    * @param  indexes		a comma separated list of indexes that should be searched. If <em>null</em> all the
    * 						indexes will be searched.
    * @param  types			a comma separated list of types that should be searched.If <em>null</em> all the
    * 						types will be searched.
    * 
    * @param  searchMode	the type of results we want to obtain: hits, facets or aggregates
    *  
    * @param  hostname	 	hostname to connect to - or IP address
    * 
    * @param  port	 		a port to connect to for transport. The default transport port is 9300.
    * 
    * @param  cluster	 	a cluster name to join. The default cluster name is "elasticsearch".
    * 
    * @return      a ESSearch object initialized to connect to the specified node and cluster to search the
    * 				specified indexes and types, returning hits, facets or aggregates depending on 
    * 				searchMode.
    */  
    public ESSearch(String indexes, String types, int searchMode, String hostname,int port, String cluster) {
        this(indexes, types, searchMode, hostname, port, null, null, cluster);
    }

    /**
    *
    * @param  indexes		a comma separated list of indexes that should be searched. If <em>null</em> all the
    * 						indexes will be searched.
    * @param  types			a comma separated list of types that should be searched.If <em>null</em> all the
    * 						types will be searched.
    * 
    * @param  searchMode	the type of results we want to obtain: hits, facets or aggregates
    *  
    * @param  hostname	 	hostname to connect to - or IP address
    * 
    * @param  port	 		a port to connect to for transport. The default transport port is 9300.
    * 
    * @param  username		the username to use to authenticate to ElasticSearch
    *  
    * @param  password	 	the password of the user specified to connect to ElasticSearch
    * 
    * 
    * @param  cluster	 	a cluster name to join. The default cluster name is "elasticsearch".
    * 
    * @return      a ESSearch object initialized to connect to the specified node and cluster to search the
    * 				specified indexes and types, returning hits, facets or aggregates depending on 
    * 				searchMode. This connection handles authentication with the username and password provided.
    */  
    public ESSearch(String indexes, String types, int searchMode, String hostname, int port, String username, String password, String cluster) {
        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.cluster = cluster;
        this.port = port;
        this.searchMode = searchMode;
        this.setIndexes(indexes);
        this.setTypes(types);       
        this.strIndexes = indexes;
        this.strTypes = types;
        this.esClient = null;
    }

    /*
     * @param		esClient	An already connected ElasticSearch client that can be used to execute the queries.
     * 
     * @param		searchMode	The mode to use to interpret the results of the search - hits, facets or aggregations
     * 
     * @returns		an instance of ESSearch that can be used to execute ES queries and get the results specified in searchMode.
     */
    public ESSearch(Client esClient, int searchMode) {
    	this.esClient = esClient;
    	this.keepClient= true;
    }
    
    /*
     * Creates a clone of this ESSearch, keeping the searchMode
     * 
     * @returns	a clone of this ESSearch
     */
    public ESSearch clone() {
    	if ( esClient != null ) {
    		return new ESSearch(esClient, searchMode);
    	}
    	return new ESSearch(strIndexes, strTypes, searchMode, hostname, port, username, password, cluster);
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    public void setSearch(String search) {
    	logger.debug("Setting ElasticSearch query: " + search);
        mainSearch = search;
    }
    
    public String getSearch() {
        return mainSearch;
    }

    public void setIndexes(String indexes) {
    	logger.debug("Setting search indexes: " + indexes);
    	
        this.indexes = new String[0];
        
        if ( indexes != null && indexes.length() > 0) {
        	this.indexes = indexes.split("\\s*,\\s*");
    	}        
        
    }
    
    public void setIndexes(String [] indexes) {
    	this.indexes = indexes;
    }
    
    public String [] getIndexes() {
        return this.indexes;
    }

    public void setTypes(String types) {

    	logger.debug("Setting search types: " + types);
        
        this.types = new String[0];
        if ( types != null && types.length() > 0) {
        	this.types = types.split("\\s*,\\s*");
        }
    }
    
    public void setTypes(String [] types) {
    	this.types = types;
    }
    
    public String [] getTypes() {
    	return this.types;
    }
    
 
    public void connect() {

    	if ( esClient != null && keepClient) {
    		logger.debug("We are already connected with a passed on client - not creating a new one");
    		return;
    	}

    	logger.debug("Creating new client to connect to: " + this.hostname);
    	
    	// Prepare a client for the ES Server
    	Settings settings =  ImmutableSettings.settingsBuilder()
    			.put("cluster.name", this.cluster)
    			.put("client.transport.sniff", true)
    			.build();
    	
    	InetSocketTransportAddress transportAddress = new InetSocketTransportAddress(this.hostname, this.port);
    	TransportClient transportClient = new TransportClient(settings);
    	transportClient.addTransportAddress(transportAddress);
 		this.esClient = transportClient;
        
    }
    

    
    private void runQuery(String query) {
    	    	
        logger.debug("Complete search request: " + query);
        
        connect();

        SearchRequestBuilder searchBuilder;
        if ( indexes.length > 0 ) {
        	searchBuilder = esClient.prepareSearch(indexes);
        } else {
        	searchBuilder = esClient.prepareSearch();
        	
        }
        if ( types.length> 0 ) {
        	searchBuilder.setTypes(types);
        }
        
        SearchResponse searchRes = 	searchBuilder
        	.setSource(query.getBytes())
        	.execute()
        	.actionGet();
        
        logger.debug("The query returns " + searchRes.getHits().getTotalHits() + " total matches.");
        logger.debug("Response: " + searchRes.toString());

        switch (searchMode) {
        case ESSearch.ES_MODE_HITS:
            JSONObject queryObject = new JSONObject(query);
            long maxHits = searchRes.getHits().getTotalHits();
            if ( queryObject.has("size") ) {
            	maxHits = queryObject.getLong("size");
            	logger.debug("Limiting query hits to stated size: " + maxHits);
            }
        	pager = new ESHitsPager(searchRes, query, maxHits);
        	break;
        case ESSearch.ES_MODE_FACETS:
        	pager = new ESFacetsPager(searchRes, query);
        	break;
        case ESSearch.ES_MODE_AGGS:
        	pager = new ESAggregationPager(searchRes, query);
        	break;
        default:
        	pager = new ESEmptyPager();	
        }
        
        logger.trace("OK, ready to process the results");

   }
   
    public void search(String query) {
        runQuery(query);
    } 
    
    public void search() {
        search(mainSearch);
    }
    
    public void close() {
    	if ( this.esClient != null && !keepClient ) {
    		logger.debug("Disconnecting client");
    		this.esClient.close();
    		this.esClient = null;
    	}
    	if ( this.esClient!=null && keepClient) {
    		logger.debug("Keeping client - it was not mine in the first place!");
    	}
    }
    
    public Map<String, Object> next() {

    	if ( pager.done() ) {
    		logger.debug("Pager is done - disposing of client.");
    		if ( this.esClient != null ) {
    			this.esClient.close();
    		}
    		return null;
    	}
    	
    	if ( !pager.hit_available() ) {
    		logger.debug("Gettin a page of hits");
    		// No hits are available - fetch the next
    		// page of hits for the query
        	searchResponse = esClient.prepareSearch(indexes)
                	.setTypes(types)
                	.setSource(pager.get_query().getBytes())
                	.setFrom((int) pager.next_page()) 
                	.setSize(pager.page_size())
                	.execute()
                	.actionGet();
        	pager.set_page_size(searchResponse.getHits().getHits().length);
        	logger.debug("Page hits: " + pager.page_size());
    	}
    	    	
    	logger.trace("Returning hit at: " + (pager.current_hit_idx() + 1) +  " of " + pager.page_size());
    	
        return pager.next(searchResponse);
        
    }
    
    public Map<String, Class<?>> getFields(String query) {
    	
    	Map<String,Class<?>> result = new HashMap<String, Class<?>>();
    	logger.debug("Getting fields using " + query);
    	
    	// Let runQuery prepare the pager
    	runQuery(query);
    	result = pager.getResponseFields();
    	close();
    	
    	logger.debug("Fields: " + result.toString());
    	return result;
    }
    
    public Map<String, Class<?>> getFields() {
        return getFields(mainSearch);
    }

    public void test() {
    	String search = getSearch();
    	setSearch("{ \"query\": { \"match_all\": {} }, \"size\": 0 }");
        getFields();
        setSearch(search);
    }
   
}
