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

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.ClassUtils;

/**
 * An enumeration of possible types of web application.
 * <p>可能的 Web 应用程序类型的枚举。</p>
 *
 * @author Andy Wilkinson
 * @author Brian Clozel
 * @author Phillip Webb
 * @since 2.0.0
 */
public enum WebApplicationType {

	/**
	 * The application should not run as a web application and should not start an
	 * embedded web server.
	 * <p>应用程序不应作为 Web 应用程序运行，也不应启动嵌入式 Web 服务器。</p>
	 */
	NONE,

	/**
	 * The application should run as a servlet-based web application and should start an
	 * embedded servlet web server.
	 * <p>应用程序应作为基于 Servlet 的 Web 应用程序运行，并应启动嵌入式 Servlet Web 服务器。</p>
	 */
	SERVLET,

	/**
	 * The application should run as a reactive web application and should start an
	 * embedded reactive web server.
	 * <p>应用程序应作为响应式 Web 应用程序运行，并应启动嵌入式响应式 Web 服务器。</p>
	 */
	REACTIVE;

	private static final String[] SERVLET_INDICATOR_CLASSES = { "jakarta.servlet.Servlet",
			"org.springframework.web.context.ConfigurableWebApplicationContext" };

	/**
	 * Deduce the {@link WebApplicationType} from the current classpath.
	 * <p>从当前类路径推断 {@link WebApplicationType}。</p>
	 *
	 * @return the deduced web application
	 * <p>推断出的 Web 应用程序</p>
	 * @since 4.0.1
	 */
	public static WebApplicationType deduce() {
		for (Deducer deducer : SpringFactoriesLoader.forDefaultResourceLocation().load(Deducer.class)) {
			WebApplicationType deduced = deducer.deduceWebApplicationType();
			if (deduced != null) {
				return deduced;
			}
		}
		return isServletApplication() ? WebApplicationType.SERVLET : WebApplicationType.NONE;
	}

	private static boolean isServletApplication() {
		for (String servletIndicatorClass : SERVLET_INDICATOR_CLASSES) {
			if (!ClassUtils.isPresent(servletIndicatorClass, null)) {
				return false;
			}
		}
		return true;
	}

	static class WebApplicationTypeRuntimeHints implements RuntimeHintsRegistrar {

		@Override
		public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
			for (String servletIndicatorClass : SERVLET_INDICATOR_CLASSES) {
				registerTypeIfPresent(servletIndicatorClass, classLoader, hints);
			}
		}

		private void registerTypeIfPresent(String typeName, @Nullable ClassLoader classLoader, RuntimeHints hints) {
			if (ClassUtils.isPresent(typeName, classLoader)) {
				hints.reflection().registerType(TypeReference.of(typeName));
			}
		}

	}

	/**
	 * Strategy that may be implemented by a module that can deduce the
	 * {@link WebApplicationType}.
	 * <p>可由能够推断 {@link WebApplicationType} 的模块实现的策略。</p>
	 *
	 * @since 4.0.1
	 */
	@FunctionalInterface
	public interface Deducer {

		/**
		 * Deduce the web application type.
		 * <p>推断 Web 应用程序类型。</p>
		 * @return the deduced web application type or {@code null}
		 * <p>推断出的 Web 应用程序类型或 {@code null}</p>
		 */
		@Nullable WebApplicationType deduceWebApplicationType();

	}

}