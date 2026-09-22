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

import java.util.ArrayList;
import java.util.Collection;

import org.jspecify.annotations.Nullable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.core.Ordered;

/**
 * {@link BeanFactoryPostProcessor} to set lazy-init on bean definitions that are not
 * {@link LazyInitializationExcludeFilter excluded} and have not already had a value
 * explicitly set.
 * <p>用于在尚未显式设置值的 bean 定义上设置懒初始化的 {@link BeanFactoryPostProcessor}，这些定义未被 {@link LazyInitializationExcludeFilter 排除}。</p>
 *
 * <p>
 * Note that {@link SmartInitializingSingleton SmartInitializingSingletons} are
 * automatically excluded from lazy initialization to ensure that their
 * {@link SmartInitializingSingleton#afterSingletonsInstantiated() callback method} is
 * invoked.
 * <p>请注意，{@link SmartInitializingSingleton SmartInitializingSingletons} 会自动从懒初始化中排除，以确保它们的 {@link SmartInitializingSingleton#afterSingletonsInstantiated() 回调方法} 被调用。</p>
 *
 * <p>
 * Beans that are in the {@link BeanDefinition#ROLE_INFRASTRUCTURE infrastructure role}
 * are automatically excluded from lazy initialization, too.
 * <p>处于 {@link BeanDefinition#ROLE_INFRASTRUCTURE 基础设施角色} 的 Bean 也会自动从懒初始化中排除。</p>
 *
 * @author Andy Wilkinson
 * @author Madhura Bhave
 * @author Tyler Van Gorder
 * @author Phillip Webb
 * @author Moritz Halbritter
 * @since 2.2.0
 * @see LazyInitializationExcludeFilter
 */
public final class LazyInitializationBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		Collection<LazyInitializationExcludeFilter> filters = getFilters(beanFactory);
		for (String beanName : beanFactory.getBeanDefinitionNames()) {
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
			if (beanDefinition instanceof AbstractBeanDefinition abstractBeanDefinition) {
				postProcess(beanFactory, filters, beanName, abstractBeanDefinition);
			}
		}
	}

	private Collection<LazyInitializationExcludeFilter> getFilters(ConfigurableListableBeanFactory beanFactory) {
		// Take care not to force the eager init of factory beans when getting filters
		// 在获取过滤器时，注意不要强制工厂 bean 的急切初始化
		ArrayList<LazyInitializationExcludeFilter> filters = new ArrayList<>(
				beanFactory.getBeansOfType(LazyInitializationExcludeFilter.class, false, false).values());
		filters.add(LazyInitializationExcludeFilter.forBeanTypes(SmartInitializingSingleton.class));
		filters.add(new InfrastructureRoleLazyInitializationExcludeFilter());
		return filters;
	}

	private void postProcess(ConfigurableListableBeanFactory beanFactory,
			Collection<LazyInitializationExcludeFilter> filters, String beanName,
			AbstractBeanDefinition beanDefinition) {
		Boolean lazyInit = beanDefinition.getLazyInit();
		if (lazyInit != null) {
			return;
		}
		Class<?> beanType = getBeanType(beanFactory, beanName);
		if (!isExcluded(filters, beanName, beanDefinition, beanType)) {
			beanDefinition.setLazyInit(true);
		}
	}

	private @Nullable Class<?> getBeanType(ConfigurableListableBeanFactory beanFactory, String beanName) {
		try {
			return beanFactory.getType(beanName, false);
		}
		catch (NoSuchBeanDefinitionException ex) {
			return null;
		}
	}

	private boolean isExcluded(Collection<LazyInitializationExcludeFilter> filters, String beanName,
			AbstractBeanDefinition beanDefinition, @Nullable Class<?> beanType) {
		if (beanType != null) {
			for (LazyInitializationExcludeFilter filter : filters) {
				if (filter.isExcluded(beanName, beanDefinition, beanType)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	/**
	 * Excludes all {@link BeanDefinition bean definitions} which have the infrastructure
	 * role from lazy initialization.
	 * <p>从懒初始化中排除所有具有基础设施角色的 {@link BeanDefinition bean 定义}。</p>
	 */
	private static final class InfrastructureRoleLazyInitializationExcludeFilter
			implements LazyInitializationExcludeFilter {

		@Override
		public boolean isExcluded(String beanName, BeanDefinition beanDefinition, Class<?> beanType) {
			return beanDefinition.getRole() == BeanDefinition.ROLE_INFRASTRUCTURE;
		}

	}

}