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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Indexed;

/**
 * Indicates that a class provides Spring Boot application
 * {@link Configuration @Configuration}. Can be used as an alternative to the Spring's
 * standard {@code @Configuration} annotation so that configuration can be found
 * automatically (for example in tests).
 * <p>指示一个类提供 Spring Boot 应用程序 {@link Configuration @Configuration}。可用作 Spring 标准 {@code @Configuration} 注解的替代方案，以便自动发现配置（例如在测试中）。</p>
 *
 * <p>
 * Application should only ever include <em>one</em> {@code @SpringBootConfiguration} and
 * most idiomatic Spring Boot applications will inherit it from
 * {@code @SpringBootApplication}.
 * <p>应用程序只应包含<em>一个</em> {@code @SpringBootConfiguration}，大多数符合惯例的 Spring Boot 应用程序将从 {@code @SpringBootApplication} 继承它。</p>
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @since 1.4.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@Indexed
public @interface SpringBootConfiguration {

	/**
	 * Specify whether {@link Bean @Bean} methods should get proxied in order to enforce
	 * bean lifecycle behavior, e.g. to return shared singleton bean instances even in
	 * case of direct {@code @Bean} method calls in user code. This feature requires
	 * method interception, implemented through a runtime-generated CGLIB subclass which
	 * comes with limitations such as the configuration class and its methods not being
	 * allowed to declare {@code final}.
	 * <p>指定是否应对 {@link Bean @Bean} 方法进行代理，以强制执行 bean 生命周期行为，例如在用户代码中直接调用 {@code @Bean} 方法时也返回共享的单例 bean 实例。此功能需要方法拦截，通过运行时生成的 CGLIB 子类实现，这带来了一些限制，例如配置类及其方法不允许声明为 {@code final}。</p>
	 *
	 * <p>
	 * The default is {@code true}, allowing for 'inter-bean references' within the
	 * configuration class as well as for external calls to this configuration's
	 * {@code @Bean} methods, e.g. from another configuration class. If this is not needed
	 * since each of this particular configuration's {@code @Bean} methods is
	 * self-contained and designed as a plain factory method for container use, switch
	 * this flag to {@code false} in order to avoid CGLIB subclass processing.
	 * <p>默认值为 {@code true}，允许在配置类内进行"bean 间引用"，以及从另一个配置类等外部调用此配置的 {@code @Bean} 方法。如果不需要这样做，因为此特定配置的每个 {@code @Bean} 方法都是自包含的，并设计为供容器使用的普通工厂方法，请将此标志切换为 {@code false} 以避免 CGLIB 子类处理。</p>
	 *
	 * <p>
	 * Turning off bean method interception effectively processes {@code @Bean} methods
	 * individually like when declared on non-{@code @Configuration} classes, a.k.a.
	 * "@Bean Lite Mode" (see {@link Bean @Bean's javadoc}). It is therefore behaviorally
	 * equivalent to removing the {@code @Configuration} stereotype.
	 * <p>关闭 bean 方法拦截实际上会像在非 {@code @Configuration} 类上声明时那样单独处理 {@code @Bean} 方法，也称为"@Bean Lite 模式"（参见 {@link Bean @Bean 的 javadoc}）。因此，它在行为上等同于移除 {@code @Configuration} 构造型。</p>
	 *
	 * @return whether to proxy {@code @Bean} methods
	 * <p>是否代理 {@code @Bean} 方法</p>
	 * @since 2.2
	 */
	@AliasFor(annotation = Configuration.class)
	boolean proxyBeanMethods() default true;

}