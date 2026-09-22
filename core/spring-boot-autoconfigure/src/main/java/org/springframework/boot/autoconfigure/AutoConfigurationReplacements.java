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

package org.springframework.boot.autoconfigure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.core.io.UrlResource;
import org.springframework.util.Assert;

/**
 * Contains auto-configuration replacements used to handle deprecated or moved
 * auto-configurations which may still be referenced by
 * {@link AutoConfigureBefore @AutoConfigureBefore},
 * {@link AutoConfigureAfter @AutoConfigureAfter} or exclusions.
 * <p>包含用于处理已弃用或已移动的自动配置的替换项，这些自动配置可能仍被
 * {@link AutoConfigureBefore @AutoConfigureBefore}、
 * {@link AutoConfigureAfter @AutoConfigureAfter} 或排除项引用。</p>
 *
 * @author Phillip Webb
 */
final class AutoConfigurationReplacements {

	private static final String LOCATION = "META-INF/spring/%s.replacements";

	private final Map<String, String> replacements;

	private AutoConfigurationReplacements(Map<String, String> replacements) {
		this.replacements = Map.copyOf(replacements);
	}

	Set<String> replaceAll(Set<String> classNames) {
		Set<String> replaced = new LinkedHashSet<>(classNames.size());
		for (String className : classNames) {
			replaced.add(replace(className));
		}
		return replaced;
	}

	String replace(String className) {
		return this.replacements.getOrDefault(className, className);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		return this.replacements.equals(((AutoConfigurationReplacements) obj).replacements);
	}

	@Override
	public int hashCode() {
		return this.replacements.hashCode();
	}

	/**
	 * Loads the relocations from the classpath. Relocations are stored in files named
	 * {@code META-INF/spring/full-qualified-annotation-name.replacements} on the
	 * classpath. The file is loaded using {@link Properties#load(java.io.InputStream)}
	 * with each entry containing an auto-configuration class name as the key and the
	 * replacement class name as the value.
	 * <p>从类路径加载重定位。重定位存储在类路径上名为
	 * {@code META-INF/spring/full-qualified-annotation-name.replacements} 的文件中。
	 * 该文件使用 {@link Properties#load(java.io.InputStream)} 加载，
	 * 每个条目包含一个自动配置类名作为键，替换类名作为值。</p>
	 *
	 * @param annotation annotation to load
	 * <p>要加载的注解</p>
	 *
	 * @param classLoader class loader to use for loading
	 * <p>用于加载的类加载器</p>
	 *
	 * @return list of names of annotated classes
	 * <p>注解类的名称列表</p>
	 */
	static AutoConfigurationReplacements load(Class<?> annotation, @Nullable ClassLoader classLoader) {
		Assert.notNull(annotation, "'annotation' must not be null");
		ClassLoader classLoaderToUse = decideClassloader(classLoader);
		String location = String.format(LOCATION, annotation.getName());
		Enumeration<URL> urls = findUrlsInClasspath(classLoaderToUse, location);
		Map<String, String> replacements = new HashMap<>();
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			replacements.putAll(readReplacements(url));
		}
		return new AutoConfigurationReplacements(replacements);
	}

	private static ClassLoader decideClassloader(@Nullable ClassLoader classLoader) {
		if (classLoader == null) {
			return ImportCandidates.class.getClassLoader();
		}
		return classLoader;
	}

	private static Enumeration<URL> findUrlsInClasspath(ClassLoader classLoader, String location) {
		try {
			return classLoader.getResources(location);
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Failed to load configurations from location [" + location + "]", ex);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map<String, String> readReplacements(URL url) {
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new UrlResource(url).getInputStream(), StandardCharsets.UTF_8))) {
			Properties properties = new Properties();
			properties.load(reader);
			return (Map) properties;
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Unable to load replacements from location [" + url + "]", ex);
		}
	}

}