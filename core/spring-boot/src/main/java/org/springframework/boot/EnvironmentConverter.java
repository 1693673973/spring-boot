/*
 * Copyright 2012-present the original author or authors.
 * 版权所有 2012-至今 原始作者
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * 根据 Apache 许可证 2.0 版本（"许可证"）授权；
 * you may not use this file except in compliance with the License.
 * 您仅在遵守许可证的情况下才可使用本文件。
 * You may obtain a copy of the License at
 * 您可以从以下地址获取许可证副本：
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * 除非适用法律要求或书面同意，按许可证分发的软件是基于"按原样"基础分发的，
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 不附带任何明示或暗示的保证或条件。
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 请查看许可证以了解管辖权限和限制的具体语言。
 */

package org.springframework.boot;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.support.StandardServletEnvironment;

/**
 * Utility class for converting one type of {@link Environment} to another.
 * <p>用于将一种类型的 {@link Environment} 转换为另一种类型的工具类。</p>
 *
 * @author Ethan Rubinson
 * @author Andy Wilkinson
 * @author Madhura Bhave
 */
final class EnvironmentConverter {

	private static final String CONFIGURABLE_WEB_ENVIRONMENT_CLASS = "org.springframework.web.context.ConfigurableWebEnvironment";

	private static final Set<String> SERVLET_ENVIRONMENT_SOURCE_NAMES;

	static {
		Set<String> names = new HashSet<>();
		names.add(StandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME);
		names.add(StandardServletEnvironment.SERVLET_CONFIG_PROPERTY_SOURCE_NAME);
		names.add(StandardServletEnvironment.JNDI_PROPERTY_SOURCE_NAME);
		SERVLET_ENVIRONMENT_SOURCE_NAMES = Collections.unmodifiableSet(names);
	}

	private final ClassLoader classLoader;

	/**
	 * Creates a new {@link EnvironmentConverter} that will use the given
	 * {@code classLoader} during conversion.
	 * <p>创建一个新的 {@link EnvironmentConverter}，它将在转换期间使用给定的 {@code classLoader}。</p>
	 * @param classLoader the class loader to use
	 * <p>要使用的类加载器</p>
	 */
	EnvironmentConverter(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Converts the given {@code environment} to the given {@link StandardEnvironment}
	 * type. If the environment is already of the same type, no conversion is performed
	 * and it is returned unchanged.
	 * <p>将给定的 {@code environment} 转换为给定的 {@link StandardEnvironment} 类型。如果环境已经是相同类型，则不执行转换并原样返回。</p>
	 * @param environment the Environment to convert
	 * <p>要转换的 Environment</p>
	 * @param type the type to convert the Environment to
	 * <p>要将 Environment 转换为的类型</p>
	 * @return the converted Environment
	 * <p>转换后的 Environment</p>
	 */
	ConfigurableEnvironment convertEnvironmentIfNecessary(ConfigurableEnvironment environment,
			Class<? extends ConfigurableEnvironment> type) {
		if (type.equals(environment.getClass())) {
			return environment;
		}
		return convertEnvironment(environment, type);
	}

	private ConfigurableEnvironment convertEnvironment(ConfigurableEnvironment environment,
			Class<? extends ConfigurableEnvironment> type) {
		ConfigurableEnvironment result = createEnvironment(type);
		result.setActiveProfiles(environment.getActiveProfiles());
		result.setConversionService(environment.getConversionService());
		copyPropertySources(environment, result);
		return result;
	}

	private ConfigurableEnvironment createEnvironment(Class<? extends ConfigurableEnvironment> type) {
		try {
			Constructor<? extends ConfigurableEnvironment> constructor = type.getDeclaredConstructor();
			ReflectionUtils.makeAccessible(constructor);
			return constructor.newInstance();
		}
		catch (Exception ex) {
			return new ApplicationEnvironment();
		}
	}

	private void copyPropertySources(ConfigurableEnvironment source, ConfigurableEnvironment target) {
		removePropertySources(target.getPropertySources(), isServletEnvironment(target.getClass(), this.classLoader));
		for (PropertySource<?> propertySource : source.getPropertySources()) {
			if (!SERVLET_ENVIRONMENT_SOURCE_NAMES.contains(propertySource.getName())) {
				target.getPropertySources().addLast(propertySource);
			}
		}
	}

	private boolean isServletEnvironment(Class<?> conversionType, ClassLoader classLoader) {
		try {
			Class<?> webEnvironmentClass = ClassUtils.forName(CONFIGURABLE_WEB_ENVIRONMENT_CLASS, classLoader);
			return webEnvironmentClass.isAssignableFrom(conversionType);
		}
		catch (Throwable ex) {
			return false;
		}
	}

	private void removePropertySources(MutablePropertySources propertySources, boolean isServletEnvironment) {
		Set<String> names = new HashSet<>();
		for (PropertySource<?> propertySource : propertySources) {
			names.add(propertySource.getName());
		}
		for (String name : names) {
			if (!isServletEnvironment || !SERVLET_ENVIRONMENT_SOURCE_NAMES.contains(name)) {
				propertySources.remove(name);
			}
		}
	}

}