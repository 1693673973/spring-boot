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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.context.annotation.DeterminableImports;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Class for storing auto-configuration packages for reference later (e.g. by JPA entity
 * scanner).
 * <p>用于存储自动配置包以供稍后引用（例如由 JPA 实体扫描器引用）的类。</p>
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Oliver Gierke
 * @since 1.0.0
 */
public abstract class AutoConfigurationPackages {

	private static final Log logger = LogFactory.getLog(AutoConfigurationPackages.class);

	private static final String BEAN = AutoConfigurationPackages.class.getName();

	/**
	 * Determine if the auto-configuration base packages for the given bean factory are
	 * available.
	 * <p>确定给定 bean 工厂的自动配置基础包是否可用。</p>
	 *
	 * @param beanFactory the source bean factory
	 *                    <p>源 bean 工厂</p>
	 * @return true if there are auto-config packages available
	 * <p>如果存在可用的自动配置包则返回 true</p>
	 */
	public static boolean has(BeanFactory beanFactory) {
		return beanFactory.containsBean(BEAN) && !get(beanFactory).isEmpty();
	}

	/**
	 * Return the auto-configuration base packages for the given bean factory.
	 * <p>返回给定 bean 工厂的自动配置基础包。</p>
	 *
	 * @param beanFactory the source bean factory
	 *                    <p>源 bean 工厂</p>
	 * @return a list of auto-configuration packages
	 * <p>自动配置包的列表</p>
	 * @throws IllegalStateException if auto-configuration is not enabled
	 *                               <p>如果自动配置未启用</p>
	 */
	public static List<String> get(BeanFactory beanFactory) {
		try {
			return beanFactory.getBean(BEAN, BasePackages.class).get();
		}
		catch (NoSuchBeanDefinitionException ex) {
			throw new IllegalStateException("Unable to retrieve @EnableAutoConfiguration base packages");
		}
	}

	/**
	 * Programmatically registers the auto-configuration package names. Subsequent
	 * invocations will add the given package names to those that have already been
	 * registered. You can use this method to manually define the base packages that will
	 * be used for a given {@link BeanDefinitionRegistry}. Generally it's recommended that
	 * you don't call this method directly, but instead rely on the default convention
	 * where the package name is set from your {@code @EnableAutoConfiguration}
	 * configuration class or classes.
	 * <p>以编程方式注册自动配置包名。后续调用会将给定的包名添加到已注册的包名中。
	 * 您可以使用此方法手动定义将用于给定 {@link BeanDefinitionRegistry} 的基础包。
	 * 通常建议不要直接调用此方法，而是依赖默认约定，即从您的 {@code @EnableAutoConfiguration}
	 * 配置类设置包名。</p>
	 *
	 * @param registry the bean definition registry
	 *                 <p>bean 定义注册表</p>
	 * @param packageNames the package names to set
	 *                     <p>要设置的包名</p>
	 */
	public static void register(BeanDefinitionRegistry registry, String... packageNames) {
		if (registry.containsBeanDefinition(BEAN)) {
			addBasePackages(registry.getBeanDefinition(BEAN), packageNames);
		}
		else {
			RootBeanDefinition beanDefinition = new RootBeanDefinition(BasePackages.class);
			beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			addBasePackages(beanDefinition, packageNames);
			registry.registerBeanDefinition(BEAN, beanDefinition);
		}
	}

	private static void addBasePackages(BeanDefinition beanDefinition, String[] additionalBasePackages) {
		ConstructorArgumentValues constructorArgumentValues = beanDefinition.getConstructorArgumentValues();
		if (constructorArgumentValues.hasIndexedArgumentValue(0)) {
			ValueHolder indexedArgumentValue = constructorArgumentValues.getIndexedArgumentValue(0, String[].class);
			Assert.state(indexedArgumentValue != null, "'indexedArgumentValue' must not be null");
			String[] existingPackages = (String[]) indexedArgumentValue.getValue();
			Stream<String> existingPackagesStream = (existingPackages != null) ? Stream.of(existingPackages)
					: Stream.empty();
			constructorArgumentValues.addIndexedArgumentValue(0,
					Stream.concat(existingPackagesStream, Stream.of(additionalBasePackages))
							.distinct()
							.toArray(String[]::new));
		}
		else {
			constructorArgumentValues.addIndexedArgumentValue(0, additionalBasePackages);
		}
	}

	/**
	 * {@link ImportBeanDefinitionRegistrar} to store the base package from the importing
	 * configuration.
	 * <p>{@link ImportBeanDefinitionRegistrar} 用于存储导入配置的基础包。</p>
	 */
	static class Registrar implements ImportBeanDefinitionRegistrar, DeterminableImports {

		@Override
		public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
			register(registry, new PackageImports(metadata).getPackageNames().toArray(new String[0]));
		}

		@Override
		public Set<Object> determineImports(AnnotationMetadata metadata) {
			return Collections.singleton(new PackageImports(metadata));
		}

	}

	/**
	 * Wrapper for a package import.
	 * <p>包导入的包装器。</p>
	 */
	private static final class PackageImports {

		private final List<String> packageNames;

		PackageImports(AnnotationMetadata metadata) {
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(metadata.getAnnotationAttributes(AutoConfigurationPackage.class.getName(), false));
			Assert.state(attributes != null, "'attributes' must not be null");
			List<String> packageNames = new ArrayList<>(Arrays.asList(attributes.getStringArray("basePackages")));
			for (Class<?> basePackageClass : attributes.getClassArray("basePackageClasses")) {
				packageNames.add(basePackageClass.getPackage().getName());
			}
			if (packageNames.isEmpty()) {
				packageNames.add(ClassUtils.getPackageName(metadata.getClassName()));
			}
			this.packageNames = Collections.unmodifiableList(packageNames);
		}

		List<String> getPackageNames() {
			return this.packageNames;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}
			return this.packageNames.equals(((PackageImports) obj).packageNames);
		}

		@Override
		public int hashCode() {
			return this.packageNames.hashCode();
		}

		@Override
		public String toString() {
			return "Package Imports " + this.packageNames;
		}

	}

	/**
	 * Holder for the base package (name may be null to indicate no scanning).
	 * <p>基础包的持有者（名称可能为 null 以表示不扫描）。</p>
	 */
	static final class BasePackages {

		private final List<String> packages;

		private boolean loggedBasePackageInfo;

		BasePackages(String... names) {
			List<String> packages = new ArrayList<>();
			for (String name : names) {
				if (StringUtils.hasText(name)) {
					packages.add(name);
				}
			}
			this.packages = packages;
		}

		List<String> get() {
			if (!this.loggedBasePackageInfo) {
				if (this.packages.isEmpty()) {
					if (logger.isWarnEnabled()) {
						logger.warn("@EnableAutoConfiguration was declared on a class "
								+ "in the default package. Automatic @Repository and "
								+ "@Entity scanning is not enabled.");
					}
				}
				else {
					if (logger.isDebugEnabled()) {
						String packageNames = StringUtils.collectionToCommaDelimitedString(this.packages);
						logger.debug("@EnableAutoConfiguration was declared on a class in the package '" + packageNames
								+ "'. Automatic @Repository and @Entity scanning is enabled.");
					}
				}
				this.loggedBasePackageInfo = true;
			}
			return this.packages;
		}

	}

}