/**
 * Copyright (C) 2011 Callista Enterprise AB <info@callistaenterprise.se>
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
package org.callistasoftware.netcare.core.api;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ApiUtil {
	static final Package PKG = ApiUtil.class.getPackage();
	static final String ENTITY_SUFFIX = "Entity";
	private static final Map<Class<?>, Class<?>> classMap = new ConcurrentHashMap<Class<?>, Class<?>>();

	@SuppressWarnings("unchecked")
	public static <T> T copy(Object source) throws Exception {
		Class<?> targetClass = getDtoClassFor(source.getClass());
		BeanProxy proxy = new BeanProxy();

		for (Method m : targetClass.getDeclaredMethods()) {
			if (m.getName().startsWith("set") && m.getParameterTypes().length == 1) {
				String s = proxy.getGetterFor(m);
				Method ms = methodExists(source.getClass(), s);
				if (ms != null) {
					proxy.set(s, marshal(m.getParameterTypes()[0], ms.invoke(source)));
				}
			}
		}
		return (T)proxy.createProxy(targetClass);
	}
	

	private static Method methodExists(Class<?> source, String methodName) {
		try {
			return source.getMethod(methodName);
		} catch (Exception e) {
			return null;
		}
	}


	// XXX: shallow support only.                                                                                          
	private static Object marshal(Class<?> returnType, Object value) throws Exception {
		if (value == null) {
			return null;
		}
		if (Collection.class.isAssignableFrom(returnType)) {
			Collection<?> theColl = (Collection<?>)value;
			for (Object o : theColl) {
				if (o.getClass().getSimpleName().endsWith(ENTITY_SUFFIX)) {
					LinkedList<Object> list = new LinkedList<Object>();
					for (Object item : theColl) {
						list.add(copy(item));
					}
					return list;					
				}
				break;
			}
		} else if (returnType.getPackage().equals(PKG) && !returnType.isEnum()) {
			return copy(value);
		}
		return value;
	}

	//                                                                                                                     
	private static Class<?> getDtoClassFor(Class<?> source) throws ClassNotFoundException {
		Class<?> t = classMap.get(source);
		if (t == null) {
			String name = source.getSimpleName();
			if (name.endsWith(ENTITY_SUFFIX)) {
				name = name.replace(ENTITY_SUFFIX, "");
			}
			t = Class.forName(PKG.getName() + "." + name);
			classMap.put(source, t);
		}
		return t;
	}	

}
