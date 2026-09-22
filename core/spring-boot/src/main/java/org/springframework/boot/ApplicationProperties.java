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

import java.util.LinkedHashSet;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.context.properties.bind.BindableRuntimeHintsRegistrar;
import org.springframework.boot.logging.LoggingSystemProperty;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

/**
 * Spring application properties.
 * <p>Spring 应用程序属性。</p>
 *
 * @author Moritz Halbritter
 */
class ApplicationProperties {

	/**
	 * Whether bean definition overriding, by registering a definition with the same name
	 * as an existing definition, is allowed.
	 *
	 * <p>是否允许通过注册与现有定义同名的定义来覆盖 bean 定义。</p>
	 */
	private boolean allowBeanDefinitionOverriding;

	/**
	 * Whether to allow circular references between beans and automatically try to resolve
	 * them.
	 *
	 * <p>是否允许 bean 之间的循环引用并自动尝试解析它们。</p>
	 */
	private boolean allowCircularReferences;

	/**
	 * Mode used to display the banner when the application runs.
	 *
	 * <p>应用程序运行时用于显示横幅的模式。</p>
	 */
	private Banner.@Nullable Mode bannerMode;

	/**
	 * Whether to keep the application alive even if there are no more non-daemon threads.
	 *
	 * <p>即使没有更多非守护线程，是否保持应用程序存活。</p>
	 */
	private boolean keepAlive;

	/**
	 * Whether initialization should be performed lazily.
	 *
	 * <p>是否应延迟执行初始化。</p>
	 */
	private boolean lazyInitialization;

	/**
	 * Whether to log information about the application when it starts.
	 *
	 * <p>是否在应用程序启动时记录其相关信息。</p>
	 */
	private boolean logStartupInfo = true;

	/**
	 * Whether the application should have a shutdown hook registered.
	 *
	 * <p>应用程序是否应注册关闭钩子。</p>
	 */
	private boolean registerShutdownHook = true;

	/**
	 * Sources (class names, package names, or XML resource locations) to include in the
	 * ApplicationContext.
	 *
	 * <p>要包含在 ApplicationContext 中的源（类名、包名或 XML 资源位置）。</p>
	 */
	private Set<String> sources = new LinkedHashSet<>();

	/**
	 * Flag to explicitly request a specific type of web application. If not set,
	 * auto-detected based on the classpath.
	 *
	 * <p>用于显式请求特定类型 Web 应用程序的标志。如果未设置，则根据类路径自动检测。</p>
	 */
	private @Nullable WebApplicationType webApplicationType;

	boolean isAllowBeanDefinitionOverriding() {
		return this.allowBeanDefinitionOverriding;
	}

	void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
		this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
	}

	boolean isAllowCircularReferences() {
		return this.allowCircularReferences;
	}

	void setAllowCircularReferences(boolean allowCircularReferences) {
		this.allowCircularReferences = allowCircularReferences;
	}

	Mode getBannerMode(Environment environment) {
		if (this.bannerMode != null) {
			return this.bannerMode;
		}
		String applicationPropertyName = LoggingSystemProperty.CONSOLE_STRUCTURED_FORMAT.getApplicationPropertyName();
		Assert.state(applicationPropertyName != null, "applicationPropertyName must not be null");
		boolean structuredLoggingEnabled = environment.containsProperty(applicationPropertyName);
		return (structuredLoggingEnabled) ? Mode.OFF : Banner.Mode.CONSOLE;
	}

	void setBannerMode(@Nullable Mode bannerMode) {
		this.bannerMode = bannerMode;
	}

	boolean isKeepAlive() {
		return this.keepAlive;
	}

	void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	boolean isLazyInitialization() {
		return this.lazyInitialization;
	}

	void setLazyInitialization(boolean lazyInitialization) {
		this.lazyInitialization = lazyInitialization;
	}

	boolean isLogStartupInfo() {
		return this.logStartupInfo;
	}

	void setLogStartupInfo(boolean logStartupInfo) {
		this.logStartupInfo = logStartupInfo;
	}

	boolean isRegisterShutdownHook() {
		return this.registerShutdownHook;
	}

	void setRegisterShutdownHook(boolean registerShutdownHook) {
		this.registerShutdownHook = registerShutdownHook;
	}

	Set<String> getSources() {
		return this.sources;
	}

	void setSources(Set<String> sources) {
		this.sources = new LinkedHashSet<>(sources);
	}

	@Nullable WebApplicationType getWebApplicationType() {
		return this.webApplicationType;
	}

	void setWebApplicationType(@Nullable WebApplicationType webApplicationType) {
		this.webApplicationType = webApplicationType;
	}

	static class ApplicationPropertiesRuntimeHints implements RuntimeHintsRegistrar {

		@Override
		public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
			BindableRuntimeHintsRegistrar.forTypes(ApplicationProperties.class).registerHints(hints, classLoader);
		}

	}

}