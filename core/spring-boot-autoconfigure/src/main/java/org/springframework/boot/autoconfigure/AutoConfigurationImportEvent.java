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

import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.Set;

/**
 * Event fired when auto-configuration classes are imported.
 * <p>当自动配置类被导入时触发的事件。</p>
 *
 * @author Phillip Webb
 * @since 1.5.0
 */
public class AutoConfigurationImportEvent extends EventObject {

	private final List<String> candidateConfigurations;

	private final Set<String> exclusions;

	public AutoConfigurationImportEvent(Object source, List<String> candidateConfigurations, Set<String> exclusions) {
		super(source);
		this.candidateConfigurations = Collections.unmodifiableList(candidateConfigurations);
		this.exclusions = Collections.unmodifiableSet(exclusions);
	}

	/**
	 * Return the auto-configuration candidate configurations that are going to be
	 * imported.
	 * <p>返回将要导入的自动配置候选配置。</p>
	 *
	 * @return the auto-configuration candidates
	 * <p>自动配置候选</p>
	 */
	public List<String> getCandidateConfigurations() {
		return this.candidateConfigurations;
	}

	/**
	 * Return the exclusions that were applied.
	 * <p>返回已应用的排除项。</p>
	 *
	 * @return the exclusions applied
	 * <p>已应用的排除项</p>
	 */
	public Set<String> getExclusions() {
		return this.exclusions;
	}

}