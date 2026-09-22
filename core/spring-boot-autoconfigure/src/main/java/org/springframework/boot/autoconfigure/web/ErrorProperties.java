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

import org.springframework.beans.factory.annotation.Value;

/**
 * Configuration properties for web error handling.
 * <p>用于 Web 错误处理的配置属性。</p>
 *
 * @author Michael Stummvoll
 * @author Stephane Nicoll
 * @author Vedran Pavic
 * @author Scott Frederick
 * @since 1.3.0
 */
public class ErrorProperties {

	/**
	 * Path of the error controller.
	 *
	 * <p>错误控制器的路径。</p>
	 */
	@Value("${error.path:/error}")
	private String path = "/error";

	/**
	 * Include the "exception" attribute.
	 *
	 * <p>包含 "exception" 属性。</p>
	 */
	private boolean includeException;

	/**
	 * When to include the "trace" attribute.
	 *
	 * <p>何时包含 "trace" 属性。</p>
	 */
	private IncludeAttribute includeStacktrace = IncludeAttribute.NEVER;

	/**
	 * When to include "message" attribute.
	 *
	 * <p>何时包含 "message" 属性。</p>
	 */
	private IncludeAttribute includeMessage = IncludeAttribute.NEVER;

	/**
	 * When to include "errors" attribute.
	 *
	 * <p>何时包含 "errors" 属性。</p>
	 */
	private IncludeAttribute includeBindingErrors = IncludeAttribute.NEVER;

	/**
	 * When to include "path" attribute.
	 *
	 * <p>何时包含 "path" 属性。</p>
	 */
	private IncludeAttribute includePath = IncludeAttribute.ALWAYS;

	private final Whitelabel whitelabel = new Whitelabel();

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isIncludeException() {
		return this.includeException;
	}

	public void setIncludeException(boolean includeException) {
		this.includeException = includeException;
	}

	public IncludeAttribute getIncludeStacktrace() {
		return this.includeStacktrace;
	}

	public void setIncludeStacktrace(IncludeAttribute includeStacktrace) {
		this.includeStacktrace = includeStacktrace;
	}

	public IncludeAttribute getIncludeMessage() {
		return this.includeMessage;
	}

	public void setIncludeMessage(IncludeAttribute includeMessage) {
		this.includeMessage = includeMessage;
	}

	public IncludeAttribute getIncludeBindingErrors() {
		return this.includeBindingErrors;
	}

	public void setIncludeBindingErrors(IncludeAttribute includeBindingErrors) {
		this.includeBindingErrors = includeBindingErrors;
	}

	public IncludeAttribute getIncludePath() {
		return this.includePath;
	}

	public void setIncludePath(IncludeAttribute includePath) {
		this.includePath = includePath;
	}

	public Whitelabel getWhitelabel() {
		return this.whitelabel;
	}

	/**
	 * Include error attributes options.
	 *
	 * <p>包含错误属性的选项。</p>
	 */
	public enum IncludeAttribute {

		/**
		 * Never add error attribute.
		 *
		 * <p>从不添加错误属性。</p>
		 */
		NEVER,

		/**
		 * Always add error attribute.
		 *
		 * <p>始终添加错误属性。</p>
		 */
		ALWAYS,

		/**
		 * Add error attribute when the appropriate request parameter is not "false".
		 *
		 * <p>当适当的请求参数不是 "false" 时添加错误属性。</p>
		 */
		ON_PARAM

	}

	public static class Whitelabel {

		/**
		 * Whether to enable the default error page displayed in browsers in case of a
		 * server error.
		 *
		 * <p>是否启用在服务器错误时在浏览器中显示的默认错误页面。</p>
		 */
		private boolean enabled = true;

		public boolean isEnabled() {
			return this.enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

	}

}