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

import java.lang.annotation.Annotation;
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
 * {@link Conditional @Conditional} that only matches when beans meeting all the specified
 * requirements are already contained in the {@link BeanFactory}. All the requirements
 * must be met for the condition to match, but they do not have to be met by the same
 * bean.
 * <p>{@link Conditional @Conditional}，仅当满足所有指定要求的 bean 已包含在 {@link BeanFactory} 中时才匹配。所有要求都必须满足才能使条件匹配，但它们不必由同一个 bean 满足。</p>
 *
 * <p>
 * When placed on a {@link Bean @Bean} method and none of {@link #value}, {@link #type},
 * {@link #name}, or {@link #annotation} has been specified, the bean type to match
 * defaults to the return type of the {@code @Bean} method:
 * <p>当放置在 {@link Bean @Bean} 方法上且未指定 {@link #value}、{@link #type}、{@link #name} 或 {@link #annotation} 中的任何一个时，要匹配的 bean 类型默认为 {@code @Bean} 方法的返回类型：</p>
 *
 * <pre class="code">
 * &#064;Configuration
 * public class MyAutoConfiguration {
 *
 *     &#064;ConditionalOnBean
 *     &#064;Bean
 *     public MyService myService() {
 *         ...
 *     }
 *
 * }</pre>
 * <p>
 * In the sample above the condition will match if a bean of type {@code MyService} is
 * already contained in the {@link BeanFactory}.
 * <p>在上面的示例中，如果 {@link BeanFactory} 中已包含类型为 {@code MyService} 的 bean，则条件将匹配。</p>
 *
 * <p>
 * The condition can only match the bean definitions that have been processed by the
 * application context so far and, as such, it is strongly recommended to use this
 * condition on auto-configuration classes only. If a candidate bean may be created by
 * another auto-configuration, make sure that the one using this condition runs after.
 * <p>该条件只能匹配到目前为止已由应用程序上下文处理的 bean 定义，因此，强烈建议仅在自动配置类上使用此条件。如果候选 bean 可能由另一个自动配置创建，请确保使用此条件的自动配置在其之后运行。</p>
 *
 * @author Phillip Webb
 * @since 1.0.0
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnBeanCondition.class)
public @interface ConditionalOnBean {

	/**
	 * The class types of beans that should be checked. The condition matches when beans
	 * of all classes specified are contained in the {@link BeanFactory}. Beans that are
	 * not autowire candidates or that are not default candidates are ignored.
	 * <p>应检查的 bean 的类类型。当 {@link BeanFactory} 中包含所有指定类的 bean 时，条件匹配。不是自动装配候选者或不是默认候选者的 bean 将被忽略。</p>
	 *
	 * <p>
	 * Since this annotation is parsed by loading class bytecode, it is safe to specify
	 * classes here that may ultimately not be on the classpath, but only if this
	 * annotation is directly on the affected component and <b>not</b> if this annotation
	 * is used as a composed, meta-annotation. In order to use this annotation as a
	 * meta-annotation, only use the {@link #type} attribute.
	 * <p>由于此注解是通过加载类字节码来解析的，因此在此指定最终可能不在类路径上的类是安全的，但前提是此注解直接位于受影响的组件上，而 <b>不是</b> 将此注解用作组合元注解。为了将此注解用作元注解，请仅使用 {@link #type} 属性。</p>
	 *
	 * @return the class types of beans to check
	 * <p>要检查的 bean 的类类型</p>
	 * @see Bean#autowireCandidate()
	 * @see BeanDefinition#isAutowireCandidate
	 * @see Bean#defaultCandidate()
	 * @see AbstractBeanDefinition#isDefaultCandidate
	 */
	Class<?>[] value() default {};

	/**
	 * The class type names of beans that should be checked. The condition matches when
	 * beans of all classes specified are contained in the {@link BeanFactory}. Beans that
	 * are not autowire candidates or that are not default candidates are ignored.
	 * <p>应检查的 bean 的类类型名称。当 {@link BeanFactory} 中包含所有指定类的 bean 时，条件匹配。不是自动装配候选者或不是默认候选者的 bean 将被忽略。</p>
	 *
	 * @return the class type names of beans to check
	 * <p>要检查的 bean 的类类型名称</p>
	 * @see Bean#autowireCandidate()
	 * @see BeanDefinition#isAutowireCandidate
	 * @see Bean#defaultCandidate()
	 * @see AbstractBeanDefinition#isDefaultCandidate
	 */
	String[] type() default {};

	/**
	 * The annotation type decorating a bean that should be checked. The condition matches
	 * when all the annotations specified are defined on beans in the {@link BeanFactory}.
	 * Beans that are not autowire candidates or that are not default candidates are
	 * ignored.
	 * <p>应检查的修饰 bean 的注解类型。当 {@link BeanFactory} 中的 bean 上定义了所有指定的注解时，条件匹配。不是自动装配候选者或不是默认候选者的 bean 将被忽略。</p>
	 *
	 * <p>
	 * Since this annotation is parsed by loading class bytecode, it is safe to specify
	 * classes here that may ultimately not be on the classpath, but only if this
	 * annotation is directly on the affected component and <b>not</b> if this annotation
	 * is used as a composed, meta-annotation.
	 * <p>由于此注解是通过加载类字节码来解析的，因此在此指定最终可能不在类路径上的类是安全的，但前提是此注解直接位于受影响的组件上，而 <b>不是</b> 将此注解用作组合元注解。</p>
	 *
	 * @return the class-level annotation types to check
	 * <p>要检查的类级别注解类型</p>
	 * @see Bean#autowireCandidate()
	 * @see BeanDefinition#isAutowireCandidate
	 * @see Bean#defaultCandidate()
	 * @see AbstractBeanDefinition#isDefaultCandidate
	 */
	Class<? extends Annotation>[] annotation() default {};

	/**
	 * The names of beans to check. The condition matches when all the bean names
	 * specified are contained in the {@link BeanFactory}.
	 * <p>要检查的 bean 的名称。当 {@link BeanFactory} 中包含所有指定的 bean 名称时，条件匹配。</p>
	 *
	 * @return the names of beans to check
	 * <p>要检查的 bean 的名称</p>
	 */
	String[] name() default {};

	/**
	 * Strategy to decide if the application context hierarchy (parent contexts) should be
	 * considered.
	 * <p>用于决定是否应考虑应用程序上下文层次结构（父上下文）的策略。</p>
	 *
	 * @return the search strategy
	 * <p>搜索策略</p>
	 */
	SearchStrategy search() default SearchStrategy.ALL;

	/**
	 * Additional classes that may contain the specified bean types within their generic
	 * parameters. For example, an annotation declaring {@code value=Name.class} and
	 * {@code parameterizedContainer=NameRegistration.class} would detect both
	 * {@code Name} and {@code NameRegistration<Name>}.
	 * <p>可能在其泛型参数中包含指定 bean 类型的附加类。例如，声明 {@code value=Name.class} 和 {@code parameterizedContainer=NameRegistration.class} 的注解将同时检测 {@code Name} 和 {@code NameRegistration<Name>}。</p>
	 *
	 * <p>
	 * Since this annotation is parsed by loading class bytecode, it is safe to specify
	 * classes here that may ultimately not be on the classpath, but only if this
	 * annotation is directly on the affected component and <b>not</b> if this annotation
	 * is used as a composed, meta-annotation.
	 * <p>由于此注解是通过加载类字节码来解析的，因此在此指定最终可能不在类路径上的类是安全的，但前提是此注解直接位于受影响的组件上，而 <b>不是</b> 将此注解用作组合元注解。</p>
	 *
	 * @return the container types
	 * <p>容器类型</p>
	 * @since 2.1.0
	 */
	Class<?>[] parameterizedContainer() default {};

}