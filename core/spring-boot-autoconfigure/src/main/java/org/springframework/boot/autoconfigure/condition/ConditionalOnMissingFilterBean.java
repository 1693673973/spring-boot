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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
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

import jakarta.servlet.Filter;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.AliasFor;

/**
 * {@link Conditional @Conditional} that only matches when no {@link Filter} beans of the
 * specified type are contained in the {@link BeanFactory}. This condition will detect
 * both directly registered {@link Filter} beans as well as those registered through a
 * {@link FilterRegistrationBean}.
 *
 * <p>一个 {@link Conditional @Conditional}，仅当 {@link BeanFactory} 中不包含指定类型的任何 {@link Filter} bean 时才匹配。此条件将检测直接注册的 {@link Filter} bean 以及通过 {@link FilterRegistrationBean} 注册的 bean。</p>
 *
 * <p>
 * When placed on a {@code @Bean} method, the bean class defaults to the return type of
 * the factory method or the type of the {@link Filter} if the bean is a
 * {@link FilterRegistrationBean}:
 *
 * <p>当放置在 {@code @Bean} 方法上时，bean 类默认为工厂方法的返回类型，或者如果 bean 是 {@link FilterRegistrationBean}，则为 {@link Filter} 的类型：</p>
 *
 * <pre class="code">
 * &#064;Configuration
 * public class MyAutoConfiguration {
 *
 *     &#064;ConditionalOnMissingFilterBean
 *     &#064;Bean
 *     public MyFilter myFilter() {
 *         ...
 *     }
 *
 * }</pre>
 * <p>
 * In the sample above the condition will match if no bean of type {@code MyFilter} or
 * {@code FilterRegistrationBean<MyFilter>} is already contained in the
 * {@link BeanFactory}.
 *
 * <p>在上面的示例中，如果 {@link BeanFactory} 中尚未包含类型为 {@code MyFilter} 或 {@code FilterRegistrationBean<MyFilter>} 的 bean，则条件将匹配。</p>
 *
 * @author Phillip Webb
 * @since 2.1.0
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnMissingBean(parameterizedContainer = FilterRegistrationBean.class)
public @interface ConditionalOnMissingFilterBean {

	/**
	 * The filter bean type that must not be present.
	 *
	 * <p>不能存在的过滤器 bean 类型。</p>
	 *
	 * @return the bean type
	 *
	 * <p>bean 类型</p>
	 */
	@AliasFor(annotation = ConditionalOnMissingBean.class)
	Class<? extends Filter>[] value() default {};

}