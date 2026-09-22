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

package org.springframework.boot.autoconfigure.condition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Conditional;

/**
 * Configuration annotation for a conditional element that depends on the value of a SpEL
 * expression.
 * <p>用于条件元素的配置注解，该条件元素依赖于 SpEL 表达式的值。</p>
 *
 * <p>
 * Referencing a bean in the expression will cause that bean to be initialized very early
 * in context refresh processing. As a result, the bean won't be eligible for
 * post-processing (such as configuration properties binding) and its state may be
 * incomplete.
 * <p>在表达式中引用 bean 将导致该 bean 在上下文刷新处理的非常早期被初始化。因此，该 bean 将不符合后处理（例如配置属性绑定）的条件，且其状态可能不完整。</p>
 *
 * @author Dave Syer
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Conditional(OnExpressionCondition.class)
public @interface ConditionalOnExpression {

	/**
	 * The SpEL expression to evaluate. Expression should return {@code true} if the
	 * condition passes or {@code false} if it fails.
	 * <p>要评估的 SpEL 表达式。如果条件通过，表达式应返回 {@code true}；如果失败，则返回 {@code false}。</p>
	 *
	 * @return the SpEL expression
	 * <p>SpEL 表达式</p>
	 */
	String value() default "true";

}