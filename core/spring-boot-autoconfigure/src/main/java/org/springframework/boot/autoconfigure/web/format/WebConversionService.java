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
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.number.NumberFormatAnnotationFormatterFactory;
import org.springframework.format.number.money.CurrencyUnitFormatter;
import org.springframework.format.number.money.Jsr354NumberFormatAnnotationFormatterFactory;
import org.springframework.format.number.money.MonetaryAmountFormatter;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringValueResolver;

/**
 * {@link org.springframework.format.support.FormattingConversionService} dedicated to web
 * applications for formatting and converting values to/from the web.
 * <p>专用于 Web 应用程序的 {@link org.springframework.format.support.FormattingConversionService}，用于在 Web 中格式化值以及在 Web 之间转换值。</p>
 *
 * @author Brian Clozel
 * @since 2.0.0
 */
public class WebConversionService extends DefaultFormattingConversionService {

	private static final boolean JSR_354_PRESENT = ClassUtils.isPresent("javax.money.MonetaryAmount",
			WebConversionService.class.getClassLoader());

	/**
	 * Create a new WebConversionService that configures formatters with the provided
	 * date, time, and date-time formats, or registers the default if no custom format is
	 * provided.
	 * <p>创建一个新的 WebConversionService，它使用提供的日期、时间和日期时间格式配置格式化器，如果未提供自定义格式，则注册默认格式。</p>
	 * @param dateTimeFormatters the formatters to use for date, time, and date-time
	 * formatting
	 * <p>用于日期、时间和日期时间格式化的格式化器</p>
	 * @since 2.3.0
	 */
	public WebConversionService(DateTimeFormatters dateTimeFormatters) {
		this(null, dateTimeFormatters);
	}

	/**
	 * Create a new WebConversionService that configures formatters with the provided
	 * date, time, and date-time formats, or registers the default if no custom format is
	 * provided. The given {@code embeddedValueResolver} is used to resolve embedded
	 * values such as property placeholders in
	 * {@link org.springframework.format.annotation.DateTimeFormat#pattern()} patterns.
	 * <p>创建一个新的 WebConversionService，它使用提供的日期、时间和日期时间格式配置格式化器，如果未提供自定义格式，则注册默认格式。给定的 {@code embeddedValueResolver} 用于解析嵌入值，例如 {@link org.springframework.format.annotation.DateTimeFormat#pattern()} 模式中的属性占位符。</p>
	 * @param embeddedValueResolver the embedded value resolver to use, or {@code null}
	 * <p>要使用的嵌入值解析器，或 {@code null}</p>
	 * @param dateTimeFormatters the formatters to use for date, time, and date-time
	 * formatting
	 * <p>用于日期、时间和日期时间格式化的格式化器</p>
	 * @since 4.1.0
	 */
	public WebConversionService(@Nullable StringValueResolver embeddedValueResolver,
			DateTimeFormatters dateTimeFormatters) {
		super(embeddedValueResolver, false);
		if (dateTimeFormatters.isCustomized()) {
			addFormatters(dateTimeFormatters);
		}
		else {
			addDefaultFormatters(this);
		}
	}

	private void addFormatters(DateTimeFormatters dateTimeFormatters) {
		addFormatterForFieldAnnotation(new NumberFormatAnnotationFormatterFactory());
		if (JSR_354_PRESENT) {
			addFormatter(new CurrencyUnitFormatter());
			addFormatter(new MonetaryAmountFormatter());
			addFormatterForFieldAnnotation(new Jsr354NumberFormatAnnotationFormatterFactory());
		}
		registerJsr310(dateTimeFormatters);
		registerJavaDate(dateTimeFormatters);
	}

	private void registerJsr310(DateTimeFormatters dateTimeFormatters) {
		DateTimeFormatterRegistrar dateTime = new DateTimeFormatterRegistrar();
		configure(dateTimeFormatters::getDateFormatter, dateTime::setDateFormatter);
		configure(dateTimeFormatters::getTimeFormatter, dateTime::setTimeFormatter);
		configure(dateTimeFormatters::getDateTimeFormatter, dateTime::setDateTimeFormatter);
		dateTime.registerFormatters(this);
	}

	private void configure(Supplier<@Nullable DateTimeFormatter> supplier, Consumer<DateTimeFormatter> consumer) {
		DateTimeFormatter formatter = supplier.get();
		if (formatter != null) {
			consumer.accept(formatter);
		}
	}

	private void registerJavaDate(DateTimeFormatters dateTimeFormatters) {
		DateFormatterRegistrar dateFormatterRegistrar = new DateFormatterRegistrar();
		String datePattern = dateTimeFormatters.getDatePattern();
		if (datePattern != null) {
			DateFormatter dateFormatter = new DateFormatter(datePattern);
			dateFormatterRegistrar.setFormatter(dateFormatter);
		}
		dateFormatterRegistrar.registerFormatters(this);
	}

}