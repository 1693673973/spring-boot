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

package org.springframework.boot.autoconfigure.web.format;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import org.jspecify.annotations.Nullable;

import org.springframework.util.StringUtils;

/**
 * {@link DateTimeFormatter Formatters} for dates, times, and date-times.
 * <p>{@link DateTimeFormatter 格式化器} 用于日期、时间和日期时间。</p>
 *
 * @author Andy Wilkinson
 * @author Gaurav Pareek
 * @since 2.3.0
 */
public class DateTimeFormatters {

	private @Nullable DateTimeFormatter dateFormatter;

	private @Nullable String datePattern;

	private @Nullable DateTimeFormatter timeFormatter;

	private @Nullable DateTimeFormatter dateTimeFormatter;

	/**
	 * Configures the date format using the given {@code pattern}.
	 * <p>使用给定的 {@code pattern} 配置日期格式。</p>
	 * @param pattern the pattern for formatting dates
	 * <p>用于格式化日期的模式</p>
	 * @return {@code this} for chained method invocation
	 * <p>{@code this} 用于链式方法调用</p>
	 */
	public DateTimeFormatters dateFormat(@Nullable String pattern) {
		if (isIso(pattern)) {
			this.dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
			this.datePattern = "yyyy-MM-dd";
		}
		else {
			this.dateFormatter = formatter(pattern);
			this.datePattern = pattern;
		}
		return this;
	}

	/**
	 * Configures the time format using the given {@code pattern}.
	 * <p>使用给定的 {@code pattern} 配置时间格式。</p>
	 * @param pattern the pattern for formatting times
	 * <p>用于格式化时间的模式</p>
	 * @return {@code this} for chained method invocation
	 * <p>{@code this} 用于链式方法调用</p>
	 */
	public DateTimeFormatters timeFormat(@Nullable String pattern) {
		this.timeFormatter = isIso(pattern) ? DateTimeFormatter.ISO_LOCAL_TIME
				: (isIsoOffset(pattern) ? DateTimeFormatter.ISO_OFFSET_TIME : formatter(pattern));
		return this;
	}

	/**
	 * Configures the date-time format using the given {@code pattern}.
	 * <p>使用给定的 {@code pattern} 配置日期时间格式。</p>
	 * @param pattern the pattern for formatting date-times
	 * <p>用于格式化日期时间的模式</p>
	 * @return {@code this} for chained method invocation
	 * <p>{@code this} 用于链式方法调用</p>
	 */
	public DateTimeFormatters dateTimeFormat(@Nullable String pattern) {
		this.dateTimeFormatter = isIso(pattern) ? DateTimeFormatter.ISO_LOCAL_DATE_TIME
				: (isIsoOffset(pattern) ? DateTimeFormatter.ISO_OFFSET_DATE_TIME : formatter(pattern));
		return this;
	}

	@Nullable DateTimeFormatter getDateFormatter() {
		return this.dateFormatter;
	}

	@Nullable String getDatePattern() {
		return this.datePattern;
	}

	@Nullable DateTimeFormatter getTimeFormatter() {
		return this.timeFormatter;
	}

	@Nullable DateTimeFormatter getDateTimeFormatter() {
		return this.dateTimeFormatter;
	}

	boolean isCustomized() {
		return this.dateFormatter != null || this.timeFormatter != null || this.dateTimeFormatter != null;
	}

	private static @Nullable DateTimeFormatter formatter(@Nullable String pattern) {
		return StringUtils.hasText(pattern)
				? DateTimeFormatter.ofPattern(pattern).withResolverStyle(ResolverStyle.SMART) : null;
	}

	private static boolean isIso(@Nullable String pattern) {
		return "iso".equalsIgnoreCase(pattern);
	}

	private static boolean isIsoOffset(@Nullable String pattern) {
		return "isooffset".equalsIgnoreCase(pattern) || "iso-offset".equalsIgnoreCase(pattern);
	}

}