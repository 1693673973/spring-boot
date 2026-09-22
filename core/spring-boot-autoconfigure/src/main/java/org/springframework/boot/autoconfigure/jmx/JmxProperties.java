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

package org.springframework.boot.autoconfigure.jmx;

import org.jspecify.annotations.Nullable;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jmx.support.RegistrationPolicy;

/**
 * Configuration properties for JMX.
 * <p>JMX 的配置属性。</p>
 *
 * @author Scott Frederick
 * @since 2.7.0
 */
@ConfigurationProperties("spring.jmx")
public class JmxProperties {

	/**
	 * Expose Spring's management beans to the JMX domain.
	 * <p>将 Spring 的管理 bean 暴露到 JMX 域。</p>
	 */
	private boolean enabled;

	/**
	 * Whether unique runtime object names should be ensured.
	 * <p>是否应确保唯一的运行时对象名称。</p>
	 */
	private boolean uniqueNames;

	/**
	 * MBeanServer bean name.
	 * <p>MBeanServer bean 名称。</p>
	 */
	private String server = "mbeanServer";

	/**
	 * JMX domain name.
	 * <p>JMX 域名。</p>
	 */
	private @Nullable String defaultDomain;

	/**
	 * JMX Registration policy.
	 * <p>JMX 注册策略。</p>
	 */
	private RegistrationPolicy registrationPolicy = RegistrationPolicy.FAIL_ON_EXISTING;

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isUniqueNames() {
		return this.uniqueNames;
	}

	public void setUniqueNames(boolean uniqueNames) {
		this.uniqueNames = uniqueNames;
	}

	public String getServer() {
		return this.server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public @Nullable String getDefaultDomain() {
		return this.defaultDomain;
	}

	public void setDefaultDomain(@Nullable String defaultDomain) {
		this.defaultDomain = defaultDomain;
	}

	public RegistrationPolicy getRegistrationPolicy() {
		return this.registrationPolicy;
	}

	public void setRegistrationPolicy(RegistrationPolicy registrationPolicy) {
		this.registrationPolicy = registrationPolicy;
	}

}