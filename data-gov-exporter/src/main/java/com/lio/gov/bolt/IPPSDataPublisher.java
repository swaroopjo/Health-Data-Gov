package com.lio.gov.bolt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.common.SolrInputDocument;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.mortbay.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;

import com.lio.log.db.DerbyDBUtil;
import com.lio.service.EmailService;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class IPPSDataPublisher extends BaseRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private SolrTemplate sTemplate = (SolrTemplate) new ClassPathXmlApplicationContext("context.xml").getBean("IPPS-SolrOps");
	private SolrTemplate sTemplate;
	public IPPSDataPublisher(){
		 
	}

	private OutputCollector collector;
	private static final Logger logger = LoggerFactory
			.getLogger(IPPSDataPublisher.class);

	
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;

	}

	public void execute(Tuple input) {
		
		String provider_id = ""+input.getValue(0);
		JSONObject json = (JSONObject) input.getValue(1);
		logger.info("Publisher Bolt received Line:"+json);
		sTemplate = new org.springframework.data.solr.core.SolrTemplate(new org.apache.solr.client.solrj.impl.HttpSolrServer("http://localhost:8983"),"IPPS-core"); 
		
		sTemplate.deleteById(provider_id);
		sTemplate.commit();
		logger.info("Deleting Document from solr instance: Provider ID: "+provider_id);
		SolrInputDocument doc = new SolrInputDocument();
		try {
			
			doc.addField("provider_id", Long.parseLong((String)json.get("provider_id")));
			doc.addField("drg_definition", json.get("drg_definition"));
			doc.addField("provider_name", json.get("provider_name"));
			doc.addField("provider_street", json.get("provider_street_address"));
			doc.addField("provider_city", json.get("provider_city"));
			doc.addField("provider_zipcode", json.get("provider_zip_code"));
			doc.addField("provider_state", json.get("provider_state"));
			doc.addField("hospital_referral_region_desc", json.get("hospital_referral_region_description"));
			doc.addField("total_discharges", Long.parseLong((String)json.get("total_discharges")));
			doc.addField("average_covered_charges",Double.parseDouble((String) json.get("average_covered_charges")));
			doc.addField("average_total_payments", Double.parseDouble((String)json.get("average_medicare_payments")));
			doc.addField("average_medicare_payments", Double.parseDouble((String)json.get("average_medicare_payments_2")));
			
			sTemplate.saveDocument(doc);
			logger.info("Document has been published. Provider ID: "+json.get("provider_id"));
			sTemplate.commit();
		} 
		catch(NullPointerException npe){
			logger.error("Could not add Document, Missing Mandatory Data");
		}
		catch (Exception e) {
			logger.error("unable to commit the document to the Solr Instance"+e);
		
		}
		sTemplate.commit();
			
	}
	
	public void publishDocument(JSONObject json){
		SolrInputDocument doc = new SolrInputDocument();
		 sTemplate = new org.springframework.data.solr.core.SolrTemplate(new org.apache.solr.client.solrj.impl.HttpSolrServer("org.apache.solr.client.solrj.impl.HttpSolrServer"),"IPPS-core"); 
		 
		try {
			
			doc.addField("provider_id", json.get("provider_id"));
			doc.addField("drg_definition", json.get("drg_definition"));
			doc.addField("provider_name", json.get("provider_name"));
			doc.addField("provider_street", json.get("provider_street_address"));
			doc.addField("provider_city", json.get("provider_city"));
			doc.addField("provider_zipcode", json.get("provider_zip_code"));
			doc.addField("provider_state", json.get("provider_state"));
			doc.addField("hospital_referral_region_desc", json.get("hospital_referral_region_description"));
			doc.addField("total_discharges", json.get("total_discharges"));
			doc.addField("average_covered_charges", json.get("average_covered_charges"));
			doc.addField("average_total_payments", json.get("average_medicare_payments"));
			doc.addField("average_medicare_payments", json.get("average_medicare_payments_2"));
			
			sTemplate.saveDocument(doc);
			logger.info("Document has been published. Provider ID: "+json.get("provider_id"));
			sTemplate.commit();
		} 
		catch(NullPointerException npe){
			logger.error("Could not add Document, Missing Mandatory Data");
		}
		catch (Exception e) {
			logger.error("unable to commit the document to the Solr Instance");
		
		}
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer arg0) {
		// TODO Auto-generated method stub
		
	}

}
