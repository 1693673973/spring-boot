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

import org.jspecify.annotations.Nullable;

/**
 * Low-level hook that can be used to attach a {@link SpringApplicationRunListener} to a
 * {@link SpringApplication} in order to observe or modify its behavior. Hooks are managed
 * on a per-thread basis providing isolation when multiple applications are executed in
 * parallel.
 * <p>低级钩子，可用于将 {@link SpringApplicationRunListener} 附加到 {@link SpringApplication}，以便观察或修改其行为。钩子按线程管理，当多个应用程序并行执行时提供隔离。</p>
 *
 * @author Andy Wilkinson
 * @author Phillip Webb
 * @since 3.0.0
 * @see SpringApplication#withHook
 */
@FunctionalInterface
public interface SpringApplicationHook {

	/**
	 * Return the {@link SpringApplicationRunListener} that should be hooked into the
	 * given {@link SpringApplication}.
	 * <p>返回应挂接到给定 {@link SpringApplication} 中的 {@link SpringApplicationRunListener}。</p>
	 * @param springApplication the source {@link SpringApplication} instance
	 * <p>源 {@link SpringApplication} 实例</p>
	 * @return the {@link SpringApplicationRunListener} to attach or {@code null}
	 * <p>要附加的 {@link SpringApplicationRunListener} 或 {@code null}</p>
	 */
	@Nullable SpringApplicationRunListener getRunListener(SpringApplication springApplication);

}