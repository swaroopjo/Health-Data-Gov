package com.lio.gov.util;

import java.io.Serializable;

import org.apache.solr.client.solrj.impl.HttpSolrServer;

public class SerializableSolrTemplate extends org.springframework.data.solr.core.SolrTemplate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SerializableSolrTemplate(org.apache.solr.client.solrj.impl.HttpSolrServer solrServer,String core) {
		super(solrServer,core);
		// TODO Auto-generated constructor stub
	}

	
}
