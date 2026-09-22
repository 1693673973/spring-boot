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

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

/**
 * {@link Conditional @Conditional} that only matches when a bean of the specified class
 * is already contained in the {@link BeanFactory} and a single candidate can be
 * determined.
 * <p>{@link Conditional @Conditional}，仅当 {@link BeanFactory} 中已包含指定类的 bean 且可以确定唯一候选者时才匹配。</p>
 *
 * <p>
 * The condition will also match if multiple matching bean instances are already contained
 * in the {@link BeanFactory} but a primary candidate has been defined; essentially, the
 * condition match if auto-wiring a bean with the defined type will succeed.
 * <p>如果 {@link BeanFactory} 中已包含多个匹配的 bean 实例，但已定义了主要候选者，则条件也会匹配；本质上，如果使用定义的类型自动装配 bean 将成功，则条件匹配。</p>
 *
 * <p>
 * The condition can only match the bean definitions that have been processed by the
 * application context so far and, as such, it is strongly recommended to use this
 * condition on auto-configuration classes only. If a candidate bean may be created by
 * another auto-configuration, make sure that the one using this condition runs after.
 * <p>该条件只能匹配到目前为止已由应用程序上下文处理的 bean 定义，因此，强烈建议仅在自动配置类上使用此条件。如果候选 bean 可能由另一个自动配置创建，请确保使用此条件的自动配置在其之后运行。</p>
 *
 * @author Stephane Nicoll
 * @since 1.3.0
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnBeanCondition.class)
public @interface ConditionalOnSingleCandidate {

	/**
	 * The class type of bean that should be checked. The condition matches if a bean of
	 * the class specified is contained in the {@link BeanFactory} and a primary candidate
	 * exists in case of multiple instances. Beans that are not autowire candidates, that
	 * are not default candidates, or that are fallback candidates are ignored.
	 * <p>应检查的 bean 的类类型。如果 {@link BeanFactory} 中包含指定类的 bean，并且在多个实例的情况下存在主要候选者，则条件匹配。不是自动装配候选者、不是默认候选者或回退候选者的 bean 将被忽略。</p>
	 *
	 * <p>
	 * Since this annotation is parsed by loading class bytecode, it is safe to specify
	 * classes here that may ultimately not be on the classpath, but only if this
	 * annotation is directly on the affected component and <b>not</b> if this annotation
	 * is used as a composed, meta-annotation. In order to use this annotation as a
	 * meta-annotation, only use the {@link #type} attribute.
	 * <p>由于此注解是通过加载类字节码来解析的，因此在此指定最终可能不在类路径上的类是安全的，但前提是此注解直接位于受影响的组件上，而 <b>不是</b> 将此注解用作组合元注解。为了将此注解用作元注解，请仅使用 {@link #type} 属性。</p>
	 *
	 * <p>
	 * This attribute may <strong>not</strong> be used in conjunction with
	 * {@link #type()}, but it may be used instead of {@link #type()}.
	 * <p>此属性<strong>不得</strong>与 {@link #type()} 一起使用，但可以代替 {@link #type()} 使用。</p>
	 *
	 * @return the class type of the bean to check
	 * <p>要检查的 bean 的类类型</p>
	 * @see Bean#autowireCandidate()
	 * @see BeanDefinition#isAutowireCandidate
	 * @see Bean#defaultCandidate()
	 * @see AbstractBeanDefinition#isDefaultCandidate
	 */
	Class<?> value() default Object.class;

	/**
	 * The class type name of bean that should be checked. The condition matches if a bean
	 * of the class specified is contained in the {@link BeanFactory} and a primary
	 * candidate exists in case of multiple instances. Beans that are not autowire
	 * candidates, that are not default candidates, or that are fallback candidates are
	 * ignored.
	 * <p>应检查的 bean 的类类型名称。如果 {@link BeanFactory} 中包含指定类的 bean，并且在多个实例的情况下存在主要候选者，则条件匹配。不是自动装配候选者、不是默认候选者或回退候选者的 bean 将被忽略。</p>
	 *
	 * <p>
	 * This attribute may <strong>not</strong> be used in conjunction with
	 * {@link #value()}, but it may be used instead of {@link #value()}.
	 * <p>此属性<strong>不得</strong>与 {@link #value()} 一起使用，但可以代替 {@link #value()} 使用。</p>
	 *
	 * @return the class type name of the bean to check
	 * <p>要检查的 bean 的类类型名称</p>
	 * @see Bean#autowireCandidate()
	 * @see BeanDefinition#isAutowireCandidate
	 * @see Bean#defaultCandidate()
	 * @see AbstractBeanDefinition#isDefaultCandidate
	 */
	String type() default "";

	/**
	 * Strategy to decide if the application context hierarchy (parent contexts) should be
	 * considered.
	 * <p>用于决定是否应考虑应用程序上下文层次结构（父上下文）的策略。</p>
	 *
	 * @return the search strategy
	 * <p>搜索策略</p>
	 */
	SearchStrategy search() default SearchStrategy.ALL;

}