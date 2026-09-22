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

import org.springframework.context.ApplicationContext;

/**
 * Interface that can be used to add or remove code that should run when the JVM is
 * shutdown. Shutdown handlers are similar to JVM {@link Runtime#addShutdownHook(Thread)
 * shutdown hooks} except that they run sequentially rather than concurrently.
 * <p>用于添加或移除应在 JVM 关闭时运行的代码的接口。关闭处理器类似于 JVM 的 {@link Runtime#addShutdownHook(Thread) 关闭钩子}，不同之处在于它们按顺序运行而不是并发运行。</p>
 *
 * <p>
 * Shutdown handlers are guaranteed to be called only after registered
 * {@link ApplicationContext} instances have been closed and are no longer active.
 * <p>保证关闭处理器仅在已注册的 {@link ApplicationContext} 实例关闭且不再活动之后才被调用。</p>
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @since 2.5.1
 * @see SpringApplication#getShutdownHandlers()
 * @see SpringApplication#setRegisterShutdownHook(boolean)
 */
public interface SpringApplicationShutdownHandlers {

	/**
	 * Add an action to the handlers that will be run when the JVM exits.
	 * <p>向处理器添加一个将在 JVM 退出时运行的操作。</p>
	 * @param action the action to add
	 * <p>要添加的操作</p>
	 */
	void add(Runnable action);

	/**
	 * Remove a previously added an action so that it no longer runs when the JVM exits.
	 * <p>移除之前添加的操作，使其在 JVM 退出时不再运行。</p>
	 * @param action the action to remove
	 * <p>要移除的操作</p>
	 */
	void remove(Runnable action);

}