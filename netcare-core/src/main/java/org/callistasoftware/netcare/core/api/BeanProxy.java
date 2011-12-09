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

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class BeanProxy implements InvocationHandler, Serializable {
	private static final long serialVersionUID = 1L;
	private Map<String, Object> values = new HashMap<String, Object>();

	BeanProxy() {
	}

	public Object createProxy(Class<?> interfaceClass) {
		return Proxy.newProxyInstance(
				interfaceClass.getClassLoader(),
				new Class<?>[] { interfaceClass },
				this);
	}

	public void set(String name, Object value) {
		values.put(name, value);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		if ("hashCode".equals(method.getName())) {
			return proxyHashCode(proxy);
		} else if ("equals".equals(method.getName())) {
			return proxyEquals(proxy, args[0]);
		} else if ("toString".equals(method.getName())) {
			return proxyToString(proxy);
		}

		Object result = null;
		if (method.getName().startsWith("get")) {
			result = values.get(method.getName());
		} else if (method.getName().startsWith("set") && args.length == 1) {
			values.put(getGetterFor(method), args[0]);
		}

		return result;		
	}

	//                                                                                                                     
	protected String getGetterFor(Method method) {
		Class<?>[] t = method.getParameterTypes();
		String s = (t.length == 1 && t[0] == Boolean.class ? "is" : "get") + method.getName().substring(3);
		return s;
	}

	protected Integer proxyHashCode(Object proxy) {
		return new Integer(System.identityHashCode(proxy));
	}

	protected Boolean proxyEquals(Object proxy, Object other) {
		return (proxy == other ? Boolean.TRUE : Boolean.FALSE);
	}

	protected String proxyToString(Object proxy) {
		return proxy.getClass().getName() + '@' + Integer.toHexString(proxy.hashCode());
	}
}


