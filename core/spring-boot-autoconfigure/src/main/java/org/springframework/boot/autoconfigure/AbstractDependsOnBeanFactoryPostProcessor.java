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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 请查看许可证以了解管辖权限和限制的具体语言。
 */

package org.springframework.boot.autoconfigure;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

/**
 * Abstract base class for a {@link BeanFactoryPostProcessor} that can be used to
 * dynamically declare that all beans of a specific type should depend on specific other
 * beans identified by name or type.
 * <p>用于 {@link BeanFactoryPostProcessor} 的抽象基类，可用于动态声明特定类型的所有 bean 应依赖于通过名称或类型标识的特定其他 bean。</p>
 *
 * @author Marcel Overdijk
 * @author Dave Syer
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @author Dmytro Nosan
 * @since 1.3.0
 * @see BeanDefinition#setDependsOn(String[])
 */
public abstract class AbstractDependsOnBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {

	private final Class<?> beanClass;

	private final @Nullable Class<? extends FactoryBean<?>> factoryBeanClass;

	private final Function<ListableBeanFactory, Set<String>> dependsOn;

	/**
	 * Create an instance with target bean, factory bean classes, and dependency names.
	 * <p>使用目标 bean、工厂 bean 类和依赖名称创建一个实例。</p>
	 *
	 * @param beanClass target bean class
	 *                  <p>目标 bean 类</p>
	 * @param factoryBeanClass target factory bean class
	 *                         <p>目标工厂 bean 类</p>
	 * @param dependsOn dependency names
	 *                  <p>依赖名称</p>
	 */
	protected AbstractDependsOnBeanFactoryPostProcessor(Class<?> beanClass,
			@Nullable Class<? extends FactoryBean<?>> factoryBeanClass, String... dependsOn) {
		this.beanClass = beanClass;
		this.factoryBeanClass = factoryBeanClass;
		this.dependsOn = (beanFactory) -> new HashSet<>(Arrays.asList(dependsOn));
	}

	/**
	 * Create an instance with target bean, factory bean classes, and dependency types.
	 * <p>使用目标 bean、工厂 bean 类和依赖类型创建一个实例。</p>
	 *
	 * @param beanClass target bean class
	 *                  <p>目标 bean 类</p>
	 * @param factoryBeanClass target factory bean class
	 *                         <p>目标工厂 bean 类</p>
	 * @param dependencyTypes dependency types
	 *                        <p>依赖类型</p>
	 * @since 2.1.7
	 */
	protected AbstractDependsOnBeanFactoryPostProcessor(Class<?> beanClass,
			@Nullable Class<? extends FactoryBean<?>> factoryBeanClass, Class<?>... dependencyTypes) {
		this.beanClass = beanClass;
		this.factoryBeanClass = factoryBeanClass;
		this.dependsOn = (beanFactory) -> Arrays.stream(dependencyTypes)
				.flatMap((dependencyType) -> getBeanNames(beanFactory, dependencyType).stream())
				.collect(Collectors.toSet());
	}

	/**
	 * Create an instance with target bean class and dependency names.
	 * <p>使用目标 bean 类和依赖名称创建一个实例。</p>
	 *
	 * @param beanClass target bean class
	 *                  <p>目标 bean 类</p>
	 * @param dependsOn dependency names
	 *                  <p>依赖名称</p>
	 * @since 2.0.4
	 */
	protected AbstractDependsOnBeanFactoryPostProcessor(Class<?> beanClass, String... dependsOn) {
		this(beanClass, null, dependsOn);
	}

	/**
	 * Create an instance with target bean class and dependency types.
	 * <p>使用目标 bean 类和依赖类型创建一个实例。</p>
	 *
	 * @param beanClass target bean class
	 *                  <p>目标 bean 类</p>
	 * @param dependencyTypes dependency types
	 *                        <p>依赖类型</p>
	 * @since 2.1.7
	 */
	protected AbstractDependsOnBeanFactoryPostProcessor(Class<?> beanClass, Class<?>... dependencyTypes) {
		this(beanClass, null, dependencyTypes);
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		for (String beanName : getBeanNames(beanFactory)) {
			BeanDefinition definition = getBeanDefinition(beanName, beanFactory);
			String[] dependencies = definition.getDependsOn();
			for (String dependencyName : this.dependsOn.apply(beanFactory)) {
				dependencies = StringUtils.addStringToArray(dependencies, dependencyName);
			}
			definition.setDependsOn(dependencies);
		}
	}

	@Override
	public int getOrder() {
		return 0;
	}

	private Set<String> getBeanNames(ListableBeanFactory beanFactory) {
		Set<String> names = getBeanNames(beanFactory, this.beanClass);
		if (this.factoryBeanClass != null) {
			names.addAll(getBeanNames(beanFactory, this.factoryBeanClass));
		}
		return names;
	}

	private static Set<String> getBeanNames(ListableBeanFactory beanFactory, Class<?> beanClass) {
		String[] names = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, beanClass, true, false);
		return Arrays.stream(names).map(BeanFactoryUtils::transformedBeanName).collect(Collectors.toSet());
	}

	private static BeanDefinition getBeanDefinition(String beanName, ConfigurableListableBeanFactory beanFactory) {
		try {
			return beanFactory.getBeanDefinition(beanName);
		}
		catch (NoSuchBeanDefinitionException ex) {
			BeanFactory parentBeanFactory = beanFactory.getParentBeanFactory();
			if (parentBeanFactory instanceof ConfigurableListableBeanFactory listableBeanFactory) {
				return getBeanDefinition(beanName, listableBeanFactory);
			}
			throw ex;
		}
	}

}