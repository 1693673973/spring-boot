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

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Interface used to indicate that a bean should <em>run</em> when it is contained within
 * a {@link SpringApplication}. Multiple {@link CommandLineRunner} beans can be defined
 * within the same application context and can be ordered using the {@link Ordered}
 * interface or {@link Order @Order} annotation.
 * <p>
 * <p>用于指示当 bean 包含在 {@link SpringApplication} 中时应 <em>运行</em> 的接口。可以在同一个应用程序上下文中定义多个 {@link CommandLineRunner} bean，并可以使用 {@link Ordered} 接口或 {@link Order @Order} 注解进行排序。</p>
 *
 * <p>
 * If you need access to {@link ApplicationArguments} instead of the raw String array
 * consider using {@link ApplicationRunner}.
 * <p>如果您需要访问 {@link ApplicationArguments} 而不是原始 String 数组，请考虑使用 {@link ApplicationRunner}。</p>
 *
 * @author Dave Syer
 * @since 1.0.0
 * @see ApplicationRunner
 */
@FunctionalInterface
public interface CommandLineRunner extends Runner {

	/**
	 * Callback used to run the bean.
	 * <p>用于运行 bean 的回调。</p>
	 * @param args incoming main method arguments
	 * <p>传入的主方法参数</p>
	 * @throws Exception on error
	 * <p>发生错误时抛出异常</p>
	 */
	void run(String... args) throws Exception;

}