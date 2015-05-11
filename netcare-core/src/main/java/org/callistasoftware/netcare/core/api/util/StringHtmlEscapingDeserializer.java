/**
 * Copyright (C) 2011,2012 Landstinget i Joenkoepings laen <http://www.lj.se/minhalsoplan>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.callistasoftware.netcare.core.api.util;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.owasp.encoder.Encode;

import java.io.IOException;

public class StringHtmlEscapingDeserializer extends JsonDeserializer<String> {

	public StringHtmlEscapingDeserializer(){
		// Empty
	}

	@Override
	public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		JsonToken currentToken = jp.getCurrentToken();
		if (currentToken.equals(JsonToken.VALUE_STRING)) {
			String text = jp.getText().trim();
			text = Encode.forHtml(text);
			return text;
		} else if (currentToken.equals(JsonToken.VALUE_NULL)) {
			return getNullValue();
		}
		throw ctxt.mappingException(String.class);
	}
}
