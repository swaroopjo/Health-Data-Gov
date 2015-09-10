package com.lio.gov.bolt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.mortbay.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lio.log.db.DerbyDBUtil;
import com.lio.service.EmailService;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class IPPSDataParser extends BaseRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OutputCollector collector;
	private static final Logger logger = LoggerFactory
			.getLogger(IPPSDataParser.class);

	
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		System.out.println("Prepare Method called");
		this.collector = collector;

	}
	StringBuffer buffer = new StringBuffer(); 
	public void execute(Tuple input) {
		
		String line = (String) input.getValue(0);
		buffer.append(line);
		
		if(line.endsWith("]")){
			line = buffer.toString();
			buffer = new StringBuffer();
			JSONArray array;
			try {
				array = (JSONArray)new JSONParser().parse(line);
				for(int i=0;i<array.size();i++){
					JSONObject json = (JSONObject)array.get(i);
					Integer provider_id = Integer.parseInt((String)json.get("provider_id"));
					collector.emit(new Values(provider_id,json));
					logger.info("ParserBolt emitting Line: "+json);
					Thread.sleep(3000);
				}
				collector.ack(input);
			} catch (Exception e) {
				logger.error("Could not parse/Extract Json object from the data" + e);
			}
		}
		
		
		
		
	}
	
	

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("provider_id","json"));

	}
}
