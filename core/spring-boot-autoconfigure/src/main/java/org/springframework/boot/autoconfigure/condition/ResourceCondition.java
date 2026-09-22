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

package org.springframework.boot.autoconfigure.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionMessage.Builder;
import org.springframework.boot.autoconfigure.condition.ConditionMessage.Style;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * {@link SpringBootCondition} used to check if a resource can be found using a
 * configurable property and optional default location(s).
 * <p>{@link SpringBootCondition}，用于检查是否可以使用可配置属性和可选的默认位置找到一个资源。</p>
 *
 * @author Stephane Nicoll
 * @author Phillip Webb
 * @author Madhura Bhave
 * @since 1.3.0
 */
public abstract class ResourceCondition extends SpringBootCondition {

	private final String name;

	private final String property;

	private final String[] resourceLocations;

	/**
	 * Create a new condition.
	 * <p>创建一个新的条件。</p>
	 *
	 * @param name the name of the component
	 *             <p>组件的名称</p>
	 * @param property the configuration property
	 *                 <p>配置属性</p>
	 * @param resourceLocations default location(s) where the configuration file can be
	 * found if the configuration key is not specified
	 *                          <p>如果未指定配置键，则可以在其中找到配置文件的默认位置</p>
	 * @since 2.0.0
	 */
	protected ResourceCondition(String name, String property, String... resourceLocations) {
		this.name = name;
		this.property = property;
		this.resourceLocations = resourceLocations;
	}

	@Override
	public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
		if (context.getEnvironment().containsProperty(this.property)) {
			return ConditionOutcome.match(startConditionMessage().foundExactly("property " + this.property));
		}
		return getResourceOutcome(context, metadata);
	}

	/**
	 * Check if one of the default resource locations actually exists.
	 * <p>检查某个默认资源位置是否实际存在。</p>
	 *
	 * @param context the condition context
	 *                <p>条件上下文</p>
	 * @param metadata the annotation metadata
	 *                 <p>注解元数据</p>
	 * @return the condition outcome
	 * <p>条件结果</p>
	 */
	protected ConditionOutcome getResourceOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
		List<String> found = new ArrayList<>();
		for (String location : this.resourceLocations) {
			Resource resource = context.getResourceLoader().getResource(location);
			if (resource != null && resource.exists()) {
				found.add(location);
			}
		}
		if (found.isEmpty()) {
			ConditionMessage message = startConditionMessage().didNotFind("resource", "resources")
					.items(Style.QUOTE, Arrays.asList(this.resourceLocations));
			return ConditionOutcome.noMatch(message);
		}
		ConditionMessage message = startConditionMessage().found("resource", "resources").items(Style.QUOTE, found);
		return ConditionOutcome.match(message);
	}

	protected final Builder startConditionMessage() {
		return ConditionMessage.forCondition("ResourceCondition", "(" + this.name + ")");
	}

}