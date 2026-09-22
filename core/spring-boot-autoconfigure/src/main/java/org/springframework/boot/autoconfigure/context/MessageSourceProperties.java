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

package org.springframework.boot.autoconfigure.context;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.core.io.Resource;

/**
 * Configuration properties for Message Source.
 * <p>Message Source 的配置属性。</p>
 *
 * @author Stephane Nicoll
 * @author Kedar Joshi
 * @author Misagh Moayyed
 * @since 2.0.0
 */
@ConfigurationProperties("spring.messages")
public class MessageSourceProperties {

	/**
	 * List of basenames (essentially a fully-qualified classpath location), each
	 * following the ResourceBundle convention with relaxed support for slash based
	 * locations. If it doesn't contain a package qualifier (such as "org.mypackage"), it
	 * will be resolved from the classpath root.
	 *
	 * <p>基名列表（本质上是一个完全限定的类路径位置），每个都遵循 ResourceBundle 约定，
	 * 并放宽了对基于斜杠的位置的支持。如果它不包含包限定符（如 "org.mypackage"），
	 * 它将从类路径根解析。</p>
	 */
	private List<String> basename = new ArrayList<>(List.of("messages"));

	/**
	 * List of locale-independent property file resources containing common messages.
	 *
	 * <p>包含通用消息的与区域设置无关的属性文件资源列表。</p>
	 */
	private @Nullable List<Resource> commonMessages;

	/**
	 * Message bundles encoding.
	 *
	 * <p>消息包编码。</p>
	 */
	private Charset encoding = StandardCharsets.UTF_8;

	/**
	 * Loaded resource bundle files cache duration. When not set, bundles are cached
	 * forever. If a duration suffix is not specified, seconds will be used.
	 *
	 * <p>已加载资源包文件的缓存持续时间。未设置时，包将永久缓存。
	 * 如果未指定持续时间后缀，将使用秒。</p>
	 */
	@DurationUnit(ChronoUnit.SECONDS)
	private @Nullable Duration cacheDuration;

	/**
	 * Whether to fall back to the system Locale if no files for a specific Locale have
	 * been found. if this is turned off, the only fallback will be the default file (e.g.
	 * "messages.properties" for basename "messages").
	 *
	 * <p>如果未找到特定区域设置的文件，是否回退到系统区域设置。
	 * 如果关闭此选项，唯一的回退将是默认文件（例如，基名为 "messages" 的 "messages.properties"）。</p>
	 */
	private boolean fallbackToSystemLocale = true;

	/**
	 * Whether to always apply the MessageFormat rules, parsing even messages without
	 * arguments.
	 *
	 * <p>是否始终应用 MessageFormat 规则，即使解析没有参数的消息。</p>
	 */
	private boolean alwaysUseMessageFormat;

	/**
	 * Whether to use the message code as the default message instead of throwing a
	 * "NoSuchMessageException". Recommended during development only.
	 *
	 * <p>是否使用消息代码作为默认消息，而不是抛出 "NoSuchMessageException"。
	 * 仅在开发期间推荐。</p>
	 */
	private boolean useCodeAsDefaultMessage;

	public List<String> getBasename() {
		return this.basename;
	}

	public void setBasename(List<String> basename) {
		this.basename = basename;
	}

	public Charset getEncoding() {
		return this.encoding;
	}

	public void setEncoding(Charset encoding) {
		this.encoding = encoding;
	}

	public @Nullable Duration getCacheDuration() {
		return this.cacheDuration;
	}

	public void setCacheDuration(@Nullable Duration cacheDuration) {
		this.cacheDuration = cacheDuration;
	}

	public boolean isFallbackToSystemLocale() {
		return this.fallbackToSystemLocale;
	}

	public void setFallbackToSystemLocale(boolean fallbackToSystemLocale) {
		this.fallbackToSystemLocale = fallbackToSystemLocale;
	}

	public boolean isAlwaysUseMessageFormat() {
		return this.alwaysUseMessageFormat;
	}

	public void setAlwaysUseMessageFormat(boolean alwaysUseMessageFormat) {
		this.alwaysUseMessageFormat = alwaysUseMessageFormat;
	}

	public boolean isUseCodeAsDefaultMessage() {
		return this.useCodeAsDefaultMessage;
	}

	public void setUseCodeAsDefaultMessage(boolean useCodeAsDefaultMessage) {
		this.useCodeAsDefaultMessage = useCodeAsDefaultMessage;
	}

	public @Nullable List<Resource> getCommonMessages() {
		return this.commonMessages;
	}

	public void setCommonMessages(@Nullable List<Resource> commonMessages) {
		this.commonMessages = commonMessages;
	}

}