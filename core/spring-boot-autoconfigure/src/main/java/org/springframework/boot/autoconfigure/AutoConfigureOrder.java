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

package org.springframework.boot.autoconfigure;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Auto-configuration specific variant of Spring Framework's {@link Order @Order}
 * annotation. Allows auto-configuration classes to be ordered among themselves without
 * affecting the order of configuration classes passed to
 * {@link AnnotationConfigApplicationContext#register(Class...)}.
 * <p>Spring Framework 的 {@link Order @Order} 注解的自动配置特定变体。允许自动配置类在彼此之间排序，而不影响传递给 {@link AnnotationConfigApplicationContext#register(Class...)} 的配置类的顺序。</p>
 *
 * <p>
 * As with standard {@link Configuration @Configuration} classes, the order in which
 * auto-configuration classes are applied only affects the order in which their beans are
 * defined. The order in which those beans are subsequently created is unaffected and is
 * determined by each bean's dependencies and any {@link DependsOn @DependsOn}
 * relationships.
 * <p>与标准的 {@link Configuration @Configuration} 类一样，自动配置类的应用顺序仅影响其 bean 的定义顺序。这些 bean 随后创建的顺序不受影响，而是由每个 bean 的依赖关系以及任何 {@link DependsOn @DependsOn} 关系决定。</p>
 *
 * @author Andy Wilkinson
 * @since 1.3.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
@Documented
public @interface AutoConfigureOrder {

	/**
	 * The default order value.
	 * <p>默认顺序值。</p>
	 */
	int DEFAULT_ORDER = 0;

	/**
	 * The order value. Default is {@code 0}.
	 * <p>顺序值。默认值为 {@code 0}。</p>
	 *
	 * @see Ordered#getOrder()
	 * @return the order value
	 * <p>顺序值</p>
	 */
	int value() default DEFAULT_ORDER;

}