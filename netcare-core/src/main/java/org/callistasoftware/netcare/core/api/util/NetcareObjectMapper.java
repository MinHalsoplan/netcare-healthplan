package org.callistasoftware.netcare.core.api.util;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

/**
 *
 */
public class NetcareObjectMapper extends ObjectMapper {

	public NetcareObjectMapper() {
		SimpleModule module = new SimpleModule("JSONModule", new Version(2, 0, 0, null));
		module.addDeserializer(String.class, new StringHtmlEscapingDeserializer());
		registerModule(module);
 	}
}
