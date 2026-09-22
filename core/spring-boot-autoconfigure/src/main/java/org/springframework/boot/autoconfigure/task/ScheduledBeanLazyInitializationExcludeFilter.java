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
 * 除非适用法律要求或书面同意，按许可证分发的软件是基于"按原样"基础分发的，
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 不附带任何明示或暗示的保证或条件。
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 请查看许可证以了解管辖权限和限制的具体语言。
 */

package org.springframework.boot.autoconfigure.task;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.LazyInitializationExcludeFilter;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.util.ClassUtils;

/**
 * A {@link LazyInitializationExcludeFilter} that detects bean methods annotated with
 * {@link Scheduled} or {@link Schedules}.
 * <p>一个 {@link LazyInitializationExcludeFilter}，用于检测使用 {@link Scheduled} 或 {@link Schedules} 注解的 bean 方法。</p>
 *
 * @author Stephane Nicoll
 */
class ScheduledBeanLazyInitializationExcludeFilter implements LazyInitializationExcludeFilter {

	private final Set<Class<?>> nonAnnotatedClasses = ConcurrentHashMap.newKeySet(64);

	ScheduledBeanLazyInitializationExcludeFilter() {
		// Ignore AOP infrastructure such as scoped proxies.
		// 忽略 AOP 基础设施，例如作用域代理。
		this.nonAnnotatedClasses.add(AopInfrastructureBean.class);
		this.nonAnnotatedClasses.add(TaskScheduler.class);
		this.nonAnnotatedClasses.add(ScheduledExecutorService.class);
	}

	@Override
	public boolean isExcluded(String beanName, BeanDefinition beanDefinition, Class<?> beanType) {
		return hasScheduledTask(beanType);
	}

	private boolean hasScheduledTask(Class<?> type) {
		Class<?> targetType = ClassUtils.getUserClass(type);
		if (!this.nonAnnotatedClasses.contains(targetType)
				&& AnnotationUtils.isCandidateClass(targetType, Arrays.asList(Scheduled.class, Schedules.class))) {
			Map<Method, Set<Scheduled>> annotatedMethods = MethodIntrospector.selectMethods(targetType,
					(MethodIntrospector.MetadataLookup<Set<Scheduled>>) (method) -> {
						Set<Scheduled> scheduledAnnotations = AnnotatedElementUtils
								.getMergedRepeatableAnnotations(method, Scheduled.class, Schedules.class);
						return (!scheduledAnnotations.isEmpty() ? scheduledAnnotations : null);
					});
			if (annotatedMethods.isEmpty()) {
				this.nonAnnotatedClasses.add(targetType);
			}
			return !annotatedMethods.isEmpty();
		}
		return false;
	}

}