package com.lio.gov.topology;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lio.gov.bolt.IPPSDataParser;
import com.lio.gov.bolt.IPPSDataPublisher;
import com.lio.gov.spout.IPPSHttpSpout;
import com.lio.log.bolt.LogParserBolt;
import com.lio.log.bolt.PatternMatchingBolt;
import com.lio.log.spout.LogGrabberSpout;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;

public class IPPSTopology {

	private static final Logger logger = LoggerFactory
			.getLogger(IPPSTopology.class);
	
	public static void main(String[] args){
		
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("data-ipps-spout",new IPPSHttpSpout("https://data.cms.gov/resource/97k6-zzx3.json"));
		builder.setBolt("data-ipps-parser-bolt", new IPPSDataParser()).shuffleGrouping("data-ipps-spout");
		builder.setBolt("data-ipps-publisher-bolt", new IPPSDataPublisher()).shuffleGrouping("data-ipps-parser-bolt");
		
		Config conf = new Config();
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("test", conf, builder.createTopology());
		
	        //Utils.sleep(12000);
	        //cluster.killTopology("test");
	       // cluster.shutdown();
	}
}
