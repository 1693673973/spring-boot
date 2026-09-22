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

package org.springframework.boot.autoconfigure.web;

import org.jspecify.annotations.Nullable;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources.Chain;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.Contract;
import org.springframework.util.ClassUtils;

/**
 * {@link Condition} that checks whether the Spring resource handling chain is enabled.
 * <p>检查 Spring 资源处理链是否启用的 {@link Condition}。</p>
 *
 * @author Stephane Nicoll
 * @author Phillip Webb
 * @author Madhura Bhave
 * @author Brian Clozel
 * @see ConditionalOnEnabledResourceChain
 */
class OnEnabledResourceChainCondition extends SpringBootCondition {

	private static final String WEBJAR_ASSET_LOCATOR = "org.webjars.WebJarAssetLocator";

	private static final String WEBJAR_VERSION_LOCATOR = "org.webjars.WebJarVersionLocator";

	@Override
	public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
		ConfigurableEnvironment environment = (ConfigurableEnvironment) context.getEnvironment();
		boolean fixed = getEnabledProperty(environment, "strategy.fixed.", false);
		boolean content = getEnabledProperty(environment, "strategy.content.", false);
		Boolean chain = getEnabledProperty(environment, "", null);
		Boolean match = Chain.getEnabled(fixed, content, chain);
		ConditionMessage.Builder message = ConditionMessage.forCondition(ConditionalOnEnabledResourceChain.class);
		if (match == null) {
			if (ClassUtils.isPresent(WEBJAR_VERSION_LOCATOR, getClass().getClassLoader())) {
				return ConditionOutcome.match(message.found("class").items(WEBJAR_VERSION_LOCATOR));
			}
			if (ClassUtils.isPresent(WEBJAR_ASSET_LOCATOR, getClass().getClassLoader())) {
				return ConditionOutcome.match(message.found("class").items(WEBJAR_ASSET_LOCATOR));
			}
			return ConditionOutcome.noMatch(message.didNotFind("class").items(WEBJAR_VERSION_LOCATOR));
		}
		if (match) {
			return ConditionOutcome.match(message.because("enabled"));
		}
		return ConditionOutcome.noMatch(message.because("disabled"));
	}

	@Contract("_, _, !null -> !null")
	private @Nullable Boolean getEnabledProperty(ConfigurableEnvironment environment, String key,
			@Nullable Boolean defaultValue) {
		String name = "spring.web.resources.chain." + key + "enabled";
		if (defaultValue == null) {
			return environment.getProperty(name, Boolean.class);
		}
		return environment.getProperty(name, Boolean.class, defaultValue);
	}

}