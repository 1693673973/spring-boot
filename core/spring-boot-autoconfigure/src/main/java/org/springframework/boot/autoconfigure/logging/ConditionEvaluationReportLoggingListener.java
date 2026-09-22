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

package org.springframework.boot.autoconfigure.logging;

import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

/**
 * {@link ApplicationContextInitializer} that writes the {@link ConditionEvaluationReport}
 * to the log. Reports are logged at the {@link LogLevel#DEBUG DEBUG} level. A crash
 * report triggers an info output suggesting the user runs again with debug enabled to
 * display the report.
 * <p>{@link ApplicationContextInitializer}，用于将 {@link ConditionEvaluationReport} 写入日志。报告以 {@link LogLevel#DEBUG DEBUG} 级别记录。崩溃报告会触发一条信息输出，建议用户启用调试后再次运行以显示报告。</p>
 *
 * <p>
 * This initializer is not intended to be shared across multiple application context
 * instances.
 * <p>此初始化器不打算在多个应用程序上下文实例之间共享。</p>
 *
 * @author Greg Turnquist
 * @author Dave Syer
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @author Madhura Bhave
 * @since 2.0.0
 */
public class ConditionEvaluationReportLoggingListener
		implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private final LogLevel logLevel;

	public ConditionEvaluationReportLoggingListener() {
		this(LogLevel.DEBUG);
	}

	private ConditionEvaluationReportLoggingListener(LogLevel logLevel) {
		Assert.isTrue(isInfoOrDebug(logLevel), "'logLevel' must be INFO or DEBUG");
		this.logLevel = logLevel;
	}

	private boolean isInfoOrDebug(LogLevel logLevelForReport) {
		return LogLevel.INFO.equals(logLevelForReport) || LogLevel.DEBUG.equals(logLevelForReport);
	}

	/**
	 * Static factory method that creates a
	 * {@link ConditionEvaluationReportLoggingListener} which logs the report at the
	 * specified log level.
	 * <p>静态工厂方法，创建一个 {@link ConditionEvaluationReportLoggingListener}，以指定的日志级别记录报告。</p>
	 *
	 * @param logLevelForReport the log level to log the report at
	 *                          <p>记录报告所用的日志级别</p>
	 * @return a {@link ConditionEvaluationReportLoggingListener} instance.
	 * <p>一个 {@link ConditionEvaluationReportLoggingListener} 实例。</p>
	 * @since 3.0.0
	 */
	public static ConditionEvaluationReportLoggingListener forLogLevel(LogLevel logLevelForReport) {
		return new ConditionEvaluationReportLoggingListener(logLevelForReport);
	}

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		applicationContext.addApplicationListener(new ConditionEvaluationReportListener(applicationContext));
	}

	private final class ConditionEvaluationReportListener implements GenericApplicationListener {

		private final ConfigurableApplicationContext context;

		private final ConditionEvaluationReportLogger logger;

		private ConditionEvaluationReportListener(ConfigurableApplicationContext context) {
			this.context = context;
			Supplier<ConditionEvaluationReport> reportSupplier;
			if (context instanceof GenericApplicationContext) {
				// Get the report early when the context allows early access to the bean
				// factory in case the context subsequently fails to load
				// 当上下文允许早期访问 bean 工厂时，提前获取报告，以防上下文随后加载失败
				ConditionEvaluationReport report = getReport();
				reportSupplier = () -> report;
			}
			else {
				reportSupplier = this::getReport;
			}
			this.logger = new ConditionEvaluationReportLogger(ConditionEvaluationReportLoggingListener.this.logLevel,
					reportSupplier);
		}

		private ConditionEvaluationReport getReport() {
			return ConditionEvaluationReport.get(this.context.getBeanFactory());
		}

		@Override
		public int getOrder() {
			return Ordered.LOWEST_PRECEDENCE;
		}

		@Override
		public boolean supportsEventType(ResolvableType resolvableType) {
			Class<?> type = resolvableType.getRawClass();
			if (type == null) {
				return false;
			}
			return ContextRefreshedEvent.class.isAssignableFrom(type)
					|| ApplicationFailedEvent.class.isAssignableFrom(type);
		}

		@Override
		public boolean supportsSourceType(@Nullable Class<?> sourceType) {
			return true;
		}

		@Override
		public void onApplicationEvent(ApplicationEvent event) {
			if (event instanceof ContextRefreshedEvent contextRefreshedEvent) {
				if (contextRefreshedEvent.getApplicationContext() == this.context) {
					this.logger.logReport(false);
				}
			}
			else if (event instanceof ApplicationFailedEvent applicationFailedEvent
					&& applicationFailedEvent.getApplicationContext() == this.context) {
				this.logger.logReport(true);
			}
		}

	}

}