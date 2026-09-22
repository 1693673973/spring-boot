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

import javax.naming.NamingException;

import org.jspecify.annotations.Nullable;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.jndi.JndiLocatorSupport;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link Condition} that checks for JNDI locations.
 * <p>{@link Condition}，用于检查 JNDI 位置。</p>
 *
 * @author Phillip Webb
 * @see ConditionalOnJndi
 */
@Order(Ordered.LOWEST_PRECEDENCE - 20)
class OnJndiCondition extends SpringBootCondition {

	@Override
	public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
		AnnotationAttributes attributes = AnnotationAttributes
				.fromMap(metadata.getAnnotationAttributes(ConditionalOnJndi.class.getName()));
		Assert.state(attributes != null, "'attributes' must not be null");
		String[] locations = attributes.getStringArray("value");
		try {
			return getMatchOutcome(locations);
		}
		catch (NoClassDefFoundError ex) {
			return ConditionOutcome
					.noMatch(ConditionMessage.forCondition(ConditionalOnJndi.class).because("JNDI class not found"));
		}
	}

	private ConditionOutcome getMatchOutcome(String[] locations) {
		if (!isJndiAvailable()) {
			return ConditionOutcome
					.noMatch(ConditionMessage.forCondition(ConditionalOnJndi.class).notAvailable("JNDI environment"));
		}
		if (locations.length == 0) {
			return ConditionOutcome
					.match(ConditionMessage.forCondition(ConditionalOnJndi.class).available("JNDI environment"));
		}
		JndiLocator locator = getJndiLocator(locations);
		String location = locator.lookupFirstLocation();
		String details = "(" + StringUtils.arrayToCommaDelimitedString(locations) + ")";
		if (location != null) {
			return ConditionOutcome.match(ConditionMessage.forCondition(ConditionalOnJndi.class, details)
					.foundExactly("\"" + location + "\""));
		}
		return ConditionOutcome.noMatch(ConditionMessage.forCondition(ConditionalOnJndi.class, details)
				.didNotFind("any matching JNDI location")
				.atAll());
	}

	protected boolean isJndiAvailable() {
		return JndiLocatorDelegate.isDefaultJndiEnvironmentAvailable();
	}

	protected JndiLocator getJndiLocator(String[] locations) {
		return new JndiLocator(locations);
	}

	protected static class JndiLocator extends JndiLocatorSupport {

		private final String[] locations;

		public JndiLocator(String[] locations) {
			this.locations = locations;
		}

		public @Nullable String lookupFirstLocation() {
			for (String location : this.locations) {
				try {
					lookup(location);
					return location;
				}
				catch (NamingException ex) {
					// Swallow and continue
					// 忽略并继续
				}
			}
			return null;
		}

	}

}