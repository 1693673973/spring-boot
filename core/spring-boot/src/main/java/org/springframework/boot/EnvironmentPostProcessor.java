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

import org.springframework.boot.bootstrap.BootstrapContext;
import org.springframework.boot.bootstrap.BootstrapRegistry;
import org.springframework.boot.bootstrap.ConfigurableBootstrapContext;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * Allows for customization of the application's {@link Environment} prior to the
 * application context being refreshed.
 * <p>允许在应用程序上下文刷新之前自定义应用程序的 {@link Environment}。</p>
 *
 * <p>
 * EnvironmentPostProcessor implementations have to be registered in
 * {@code META-INF/spring.factories}, using the fully qualified name of this class as the
 * key. Implementations may implement the {@link org.springframework.core.Ordered Ordered}
 * interface or use an {@link org.springframework.core.annotation.Order @Order} annotation
 * if they wish to be invoked in specific order.
 * <p>EnvironmentPostProcessor 实现必须在 {@code META-INF/spring.factories} 中注册，使用此类的完全限定名作为键。实现可以实现 {@link org.springframework.core.Ordered Ordered} 接口，或使用 {@link org.springframework.core.annotation.Order @Order} 注解，如果它们希望按特定顺序被调用。</p>
 *
 * <p>
 * {@code EnvironmentPostProcessor} implementations may optionally take the following
 * constructor parameters:
 * <p>{@code EnvironmentPostProcessor} 实现可以选择性地接受以下构造函数参数：</p>
 * <ul>
 * <li>{@link DeferredLogFactory} - A factory that can be used to create loggers with
 * output deferred until the application has been fully prepared (allowing the environment
 * itself to configure logging levels).</li>
 * <li>{@link DeferredLogFactory} - 一个工厂，可用于创建日志记录器，其输出会延迟到应用程序完全准备好之后（允许环境本身配置日志级别）。</li>
 * <li>{@link ConfigurableBootstrapContext} - A bootstrap context that can be used to
 * store objects that may be expensive to create, or need to be shared
 * ({@link BootstrapContext} or {@link BootstrapRegistry} may also be used).</li>
 * <li>{@link ConfigurableBootstrapContext} - 一个引导上下文，可用于存储可能创建成本高昂或需要共享的对象（也可以使用 {@link BootstrapContext} 或 {@link BootstrapRegistry}）。</li>
 * </ul>
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @since 4.0.0
 */
@FunctionalInterface
public interface EnvironmentPostProcessor {

	/**
	 * Post-process the given {@code environment}.
	 * <p>对给定的 {@code environment} 进行后处理。</p>
	 * @param environment the environment to post-process
	 * <p>要后处理的环境</p>
	 * @param application the application to which the environment belongs
	 * <p>环境所属的应用程序</p>
	 */
	void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application);

}