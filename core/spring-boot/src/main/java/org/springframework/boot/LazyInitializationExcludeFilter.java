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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

/**
 * Filter that can be used to exclude beans definitions from having their
 * {@link AbstractBeanDefinition#setLazyInit(boolean) lazy-init} set by the
 * {@link LazyInitializationBeanFactoryPostProcessor}.
 * <p>过滤器，可用于排除 bean 定义，使其不由 {@link LazyInitializationBeanFactoryPostProcessor} 设置 {@link AbstractBeanDefinition#setLazyInit(boolean) lazy-init}。</p>
 * <p>
 * Primarily intended to allow downstream projects to deal with edge-cases in which it is
 * not easy to support lazy-loading (such as in DSLs that dynamically create additional
 * beans). Adding an instance of this filter to the application context can be used for
 * these edge cases.
 * <p>主要旨在允许下游项目处理难以支持延迟加载的边缘情况（例如在动态创建额外 bean 的 DSL 中）。将此类过滤器的实例添加到应用程序上下文可用于这些边缘情况。</p>
 * <p>
 * A typical example would be something like this: <pre>
 * &#64;Bean
 * public static LazyInitializationExcludeFilter integrationLazyInitializationExcludeFilter() {
 *   return LazyInitializationExcludeFilter.forBeanTypes(IntegrationFlow.class);
 * }
 * </pre>
 * <p>典型示例如下：</p>
 * <p>
 * NOTE: Beans of this type will be instantiated very early in the spring application
 * lifecycle so they should generally be declared static and not have any dependencies.
 * <p>注意：此类型的 Bean 将在 Spring 应用程序生命周期的非常早期被实例化，因此它们通常应声明为静态且不应有任何依赖。</p>
 *
 * @author Tyler Van Gorder
 * @author Philip Webb
 * @since 2.2.0
 */
@FunctionalInterface
public interface LazyInitializationExcludeFilter {

	/**
	 * Returns {@code true} if the specified bean definition should be excluded from
	 * having {@code lazy-init} automatically set.
	 * <p>如果指定的 bean 定义应被排除在自动设置 {@code lazy-init} 之外，则返回 {@code true}。</p>
	 * @param beanName the bean name
	 * <p>bean 名称</p>
	 * @param beanDefinition the bean definition
	 * <p>bean 定义</p>
	 * @param beanType the bean type
	 * <p>bean 类型</p>
	 * @return {@code true} if {@code lazy-init} should not be automatically set
	 * <p>如果不应自动设置 {@code lazy-init}，则为 {@code true}</p>
	 */
	boolean isExcluded(String beanName, BeanDefinition beanDefinition, Class<?> beanType);

	/**
	 * Factory method that creates a filter for the given bean types.
	 * <p>为给定的 bean 类型创建过滤器的工厂方法。</p>
	 * @param types the filtered types
	 * <p>过滤的类型</p>
	 * @return a new filter instance
	 * <p>新的过滤器实例</p>
	 */
	static LazyInitializationExcludeFilter forBeanTypes(Class<?>... types) {
		return (beanName, beanDefinition, beanType) -> {
			for (Class<?> type : types) {
				if (type.isAssignableFrom(beanType)) {
					return true;
				}
			}
			return false;
		};
	}

}