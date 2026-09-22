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

import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * Strategy interface for creating the {@link ConfigurableApplicationContext} used by a
 * {@link SpringApplication}. Created contexts should be returned in their default form,
 * with the {@code SpringApplication} responsible for configuring and refreshing the
 * context.
 * <p>用于创建由 {@link SpringApplication} 使用的 {@link ConfigurableApplicationContext} 的策略接口。创建的上下文应以默认形式返回，由 {@code SpringApplication} 负责配置和刷新上下文。</p>
 *
 * @author Andy Wilkinson
 * @author Phillip Webb
 * @since 2.4.0
 */
@FunctionalInterface
public interface ApplicationContextFactory {

	/**
	 * A default {@link ApplicationContextFactory} implementation that will create an
	 * appropriate context for the {@link WebApplicationType}.
	 * <p>一个默认的 {@link ApplicationContextFactory} 实现，将为 {@link WebApplicationType} 创建适当的上下文。</p>
	 */
	ApplicationContextFactory DEFAULT = new DefaultApplicationContextFactory();

	/**
	 * Return the {@link Environment} type expected to be set on the
	 * {@link #create(WebApplicationType) created} application context. The result of this
	 * method can be used to convert an existing environment instance to the correct type.
	 * <p>返回预期要在 {@link #create(WebApplicationType) 创建的} 应用程序上下文上设置的 {@link Environment} 类型。此方法的结果可用于将现有环境实例转换为正确的类型。</p>
	 * @param webApplicationType the web application type or {@code null}
	 * <p>Web 应用程序类型或 {@code null}</p>
	 * @return the expected application context type or {@code null} to use the default
	 * <p>预期的应用程序上下文类型，或使用默认值时返回 {@code null}</p>
	 * @since 2.6.14
	 */
	default @Nullable Class<? extends ConfigurableEnvironment> getEnvironmentType(
			@Nullable WebApplicationType webApplicationType) {
		return null;
	}

	/**
	 * Create a new {@link Environment} to be set on the
	 * {@link #create(WebApplicationType) created} application context. The result of this
	 * method must match the type returned by
	 * {@link #getEnvironmentType(WebApplicationType)}.
	 * <p>创建一个新的 {@link Environment}，以设置到 {@link #create(WebApplicationType) 创建的} 应用程序上下文上。此方法的结果必须与 {@link #getEnvironmentType(WebApplicationType)} 返回的类型匹配。</p>
	 * @param webApplicationType the web application type or {@code null}
	 * <p>Web 应用程序类型或 {@code null}</p>
	 * @return an environment instance or {@code null} to use the default
	 * <p>环境实例，或使用默认值时返回 {@code null}</p>
	 * @since 2.6.14
	 */
	default @Nullable ConfigurableEnvironment createEnvironment(@Nullable WebApplicationType webApplicationType) {
		return null;
	}

	/**
	 * Creates the {@link ConfigurableApplicationContext application context} for a
	 * {@link SpringApplication}, respecting the given {@code webApplicationType}.
	 * <p>为 {@link SpringApplication} 创建 {@link ConfigurableApplicationContext 应用程序上下文}，并遵循给定的 {@code webApplicationType}。</p>
	 * @param webApplicationType the web application type
	 * <p>Web 应用程序类型</p>
	 * @return the newly created application context
	 * <p>新创建的应用程序上下文</p>
	 */
	@Nullable ConfigurableApplicationContext create(@Nullable WebApplicationType webApplicationType);

	/**
	 * Creates an {@code ApplicationContextFactory} that will create contexts by
	 * instantiating the given {@code contextClass} through its primary constructor.
	 * <p>创建一个 {@code ApplicationContextFactory}，它将通过给定 {@code contextClass} 的主构造函数实例化来创建上下文。</p>
	 * @param contextClass the context class
	 * <p>上下文类</p>
	 * @return the factory that will instantiate the context class
	 * <p>将实例化上下文类的工厂</p>
	 * @see BeanUtils#instantiateClass(Class)
	 */
	static ApplicationContextFactory ofContextClass(Class<? extends ConfigurableApplicationContext> contextClass) {
		return of(() -> BeanUtils.instantiateClass(contextClass));
	}

	/**
	 * Creates an {@code ApplicationContextFactory} that will create contexts by calling
	 * the given {@link Supplier}.
	 * <p>创建一个 {@code ApplicationContextFactory}，它将通过调用给定的 {@link Supplier} 来创建上下文。</p>
	 * @param supplier the context supplier, for example
	 * {@code AnnotationConfigApplicationContext::new}
	 * <p>上下文供应器，例如 {@code AnnotationConfigApplicationContext::new}</p>
	 * @return the factory that will instantiate the context class
	 * <p>将实例化上下文类的工厂</p>
	 */
	static ApplicationContextFactory of(Supplier<ConfigurableApplicationContext> supplier) {
		return (webApplicationType) -> supplier.get();
	}

}