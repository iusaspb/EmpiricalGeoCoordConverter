package ru.spb.iusa.transport.egcc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
/**
 * An application converts points (OSM nodes) from arg[0] by a converter from arg[1] and saves the results in arg[2] (OSM nodes).
 * @author YSA
 *
 */
public class FileConverter2 {
	
	private static final String EL_NODE_NAME = "node";
	
	private static final String AT_LAT_NAME = "lat";
	
	private static final int LAT_INDEX = 0;
	
	private static final String AT_LON_NAME = "lon";
	
	private static final int LON_INDEX = 1;
	
	/**
	 * 
	 * @param 
	 * 		args[0] - points as OSM nodes
	 *  	args[1] - a converter
	 *  	args[2] - results as OSM nodes
	 */
	
	public static void main(String[] args) {
		
		if (args.length < 3) {
			
			System.out.println("Usage: initOSMFileName converterFileName resultOSMFileName");
			
			System.exit(0);
		}
		
		XMLEventReader  reader = null;
		
		XMLEventWriter writer = null;
		
		long startTimeMillis = System.currentTimeMillis(), processedNodes = 0;
		
		try {
			
			reader = XMLInputFactory.newInstance().createXMLEventReader(new FileInputStream(args[0]));
			
		    ConverterImpl converter = ConverterImpl.restore(args[1]);
		    
		    if (converter == null) throw new IllegalArgumentException();
		    
		    writer = XMLOutputFactory.newInstance().createXMLEventWriter(new FileOutputStream(args[2]));
		    
		    XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		    
		    System.out.println("Convert ["+args[0]+"] by ["+args[1]+"] and save into ["+args[2]+"]");
		    
		    System.out.println(converter);
		    
	        while(reader.hasNext()){
	        	
	        	XMLEvent event = (XMLEvent)reader.next();
	        	
	        	if(event.getEventType() == XMLStreamConstants.START_ELEMENT){
	        		
	            	if (!EL_NODE_NAME.equals(((StartElement) event).getName().getLocalPart())) {
	            		
	            		writer.add(event);
	            		
	            		continue;
	            		
	            	}
	            	
	            	StartElement startElement = (StartElement)event;
	            	
	        		@SuppressWarnings("unchecked")
					Iterator<Attribute> initAttrs = startElement.getAttributes();
	        		
	        		List<Attribute> newAttrs= new ArrayList<Attribute>();
	        		
	        		double initLatLon [] = {0,0};
	        		
	        		int completeFlag = 3;
	        		
            		while (initAttrs.hasNext()) {
            			
            			Attribute attr = initAttrs.next();
            			
            			try {
            				
                			if (AT_LON_NAME.equals(attr.getName().getLocalPart())) {
                				
                				initLatLon[LON_INDEX] = Double.valueOf(attr.getValue());
                				
                				completeFlag -= 1;
                				
                			} else if (AT_LAT_NAME.equals(attr.getName().getLocalPart())) {
                				
                				initLatLon[LAT_INDEX] = Double.valueOf(attr.getValue());
                				
                				completeFlag -= 2;
                				
                			} else {
                				
                				newAttrs.add(attr);
                			}
            				
            			} catch (NumberFormatException skip){};
            			
            		} // loop on attributes
	        		
	        		if (completeFlag != 0) {
	        			
	        			System.err.println("Could not find lat/lon for node "+ event+". Save the node unchanged.");
	        			
	            		writer.add(event);
	            		
	            		writer.add(eventFactory.createEndElement(startElement.getName(), startElement.getNamespaces()));
	            		
	            		continue;
	        			
	        		}
	        		
        			//
	        		
	        		double newLatLon [] = converter.convert(initLatLon);
	        		
	        		//
	        		// save new latitude and longitude
	        		//
	        		
        			newAttrs.add(eventFactory.createAttribute(AT_LAT_NAME, Double.toString(newLatLon[LAT_INDEX])));
        			
        			newAttrs.add(eventFactory.createAttribute(AT_LON_NAME, Double.toString(newLatLon[LON_INDEX])));
        			
        			//
        			// add node element
        			//
        			
            		writer.add(eventFactory.createStartElement(startElement.getName(), newAttrs.iterator(), startElement.getNamespaces()));
            		
            		writer.add(eventFactory.createEndElement(startElement.getName(), startElement.getNamespaces()));
            		
            		//
            		
            		processedNodes++;
        			
	        		
	        	} else if(event.getEventType() == XMLStreamConstants.END_ELEMENT){
	        		
	        		
	            	if (EL_NODE_NAME.equals(((EndElement) event).getName().getLocalPart())) {
	            		
	            		// skip processing as we have added the end element while processed the start element.
	            		
	            	} else {
	            		
	            		writer.add(event);
	            	}
	        		
	        	}else {
	        		
	        		writer.add(event);  // write without processing
	        		
	        	}
	        	
	        };	// reader's loop
	        
	        writer.flush();
	        
	        writer.close();
	        
	        writer = null;
	        
	        reader.close();
	        
	        reader = null;
	        
	        System.out.println("Convert "+processedNodes+" nodes in "+(TimeUnit.SECONDS.convert(System.currentTimeMillis()-startTimeMillis, TimeUnit.MILLISECONDS))+" "+TimeUnit.SECONDS);
	        
		} catch (Exception e) {
			
			e.printStackTrace();
			
		} finally {
			
			if (reader != null) try { reader.close(); } catch (XMLStreamException skip) {}
			
			if (writer != null) try { writer.close(); } catch (XMLStreamException skip) {}
			
		}
	}

}
