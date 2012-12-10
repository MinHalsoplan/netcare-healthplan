/**
 * Copyright (C) 2011,2012 Callista Enterprise AB <info@callistaenterprise.se>
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
package org.callistasoftware.netcare.model.entity;

public enum CountyCouncil {
	
	BLEKINGE(10, "Blekinge läns landsting"),
	DALARNA(20, "Dalarnas läns landsting"),
	GOTLAND(9, "Region Gotland"),
	GAVLEBORD(21, "Gävleborgs läns landsting"),
	HALLAND(13, "Region Halland"),
	JAMTLAND(23, "Jämtlands läns landsting"),
	JONKOPING(6, "Jönköpings läns landsting"),
	KALMAR(8, "Kalmar läns landsting"),
	KRONOBERG(7, "Kronobergs läns landsting"),
	NORRBOTTEN(25, "Norrbottens läns landsting"),
	SKANE(12, "Region Skåne"),
	STOCKHOLM(1, "Stockholms läns landsting"),
	SORMLAND(4, "Södermanlands läns landsting"),
	UPPSALA(3, "Uppsala läns landsting"),
	VARMLAND(17, "Värmlands läns landsting"),
	VASTERBOTTEN(24, "Västerbottens läns landsting"),
	VASTERNORRLAND(22, "Västernorrlands läns landsting"),
	VASTMANLAND(19, "Västmanlands läns landsting"),
	VASTRA_GOTALAND(14, "Västra Götalandsregionen"),
	OREBRO(18, "Örebro läns landsting"),
	OSTERGOTLAND(5, "Östergötlands läns landsting");
	
	private final int code;
	private final String name;
	
	private CountyCouncil(final int code, final String name) {
		this.code = code;
		this.name = name;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public String getName() {
		return this.name;
	}
	
	public CountyCouncil fromCode(final int code) {
		final CountyCouncil[] opts = CountyCouncil.values();
		for (final CountyCouncil o : opts) {
			if (o.getCode() == code) {
				return o;
			}
		}
		
		throw new IllegalArgumentException("County council with code " + code + " is not present in enumeration " + CountyCouncil.class.getSimpleName());
	}
}
