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

import java.util.List;
import java.util.function.Supplier;

import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * {@link Condition} that checks if a property whose value is a list is defined in the
 * environment.
 * <p>{@link Condition}，用于检查值为列表的属性是否在环境中定义。</p>
 *
 * @author Eneias Silva
 * @author Stephane Nicoll
 * @since 2.0.5
 */
public abstract class OnPropertyListCondition extends SpringBootCondition {

	private static final Bindable<List<String>> STRING_LIST = Bindable.listOf(String.class);

	private final String propertyName;

	private final Supplier<ConditionMessage.Builder> messageBuilder;

	/**
	 * Create a new instance with the property to check and the message builder to use.
	 * <p>使用要检查的属性和要使用的消息构建器创建一个新实例。</p>
	 *
	 * @param propertyName the name of the property
	 *                     <p>属性的名称</p>
	 * @param messageBuilder a message builder supplier that should provide a fresh
	 * instance on each call
	 *                       <p>一个消息构建器提供者，每次调用时应提供一个新实例</p>
	 */
	protected OnPropertyListCondition(String propertyName, Supplier<ConditionMessage.Builder> messageBuilder) {
		this.propertyName = propertyName;
		this.messageBuilder = messageBuilder;
	}

	@Override
	public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
		BindResult<?> property = Binder.get(context.getEnvironment()).bind(this.propertyName, STRING_LIST);
		ConditionMessage.Builder messageBuilder = this.messageBuilder.get();
		if (property.isBound()) {
			return ConditionOutcome.match(messageBuilder.found("property").items(this.propertyName));
		}
		return ConditionOutcome.noMatch(messageBuilder.didNotFind("property").items(this.propertyName));
	}

}