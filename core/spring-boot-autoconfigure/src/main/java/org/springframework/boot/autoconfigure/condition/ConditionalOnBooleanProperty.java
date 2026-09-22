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
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;

/**
 * {@link Conditional @Conditional} that checks if the specified properties have a
 * specific boolean value. By default the properties must be present in the
 * {@link Environment} and equal to {@code true}. The {@link #havingValue()} and
 * {@link #matchIfMissing()} attributes allow further customizations.
 * <p>{@link Conditional @Conditional}，用于检查指定属性是否具有特定的布尔值。默认情况下，这些属性必须存在于 {@link Environment} 中且等于 {@code true}。{@link #havingValue()} 和 {@link #matchIfMissing()} 属性允许进一步自定义。</p>
 *
 * <p>
 * If the property is not contained in the {@link Environment} at all, the
 * {@link #matchIfMissing()} attribute is consulted. By default missing attributes do not
 * match.
 *
 * <p>如果属性完全未包含在 {@link Environment} 中，则会查询 {@link #matchIfMissing()} 属性。默认情况下，缺少的属性不匹配。</p>
 *
 * @author Phillip Webb
 * @since 3.5.0
 * @see ConditionalOnProperty
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Conditional(OnPropertyCondition.class)
@Repeatable(ConditionalOnBooleanProperties.class)
public @interface ConditionalOnBooleanProperty {

	/**
	 * Alias for {@link #name()}.
	 * <p>{@link #name()} 的别名。</p>
	 *
	 * @return the names
	 * <p>名称</p>
	 */
	String[] value() default {};

	/**
	 * A prefix that should be applied to each property. The prefix automatically ends
	 * with a dot if not specified. A valid prefix is defined by one or more words
	 * separated with dots (e.g. {@code "acme.system.feature"}).
	 * <p>应应用于每个属性的前缀。如果未指定，前缀自动以点结尾。有效的前缀由一个或多个以点分隔的单词定义（例如 {@code "acme.system.feature"}）。</p>
	 *
	 * @return the prefix
	 * <p>前缀</p>
	 */
	String prefix() default "";

	/**
	 * The name of the properties to test. If a prefix has been defined, it is applied to
	 * compute the full key of each property. For instance if the prefix is
	 * {@code app.config} and one value is {@code my-value}, the full key would be
	 * {@code app.config.my-value}
	 * <p>要测试的属性的名称。如果已定义前缀，则会将其应用于计算每个属性的完整键。例如，如果前缀是 {@code app.config} 且某个值为 {@code my-value}，则完整键将是 {@code app.config.my-value}</p>
	 *
	 * <p>
	 * Use the dashed notation to specify each property, that is all lower case with a "-"
	 * to separate words (e.g. {@code my-long-property}).
	 * <p>使用短横线表示法指定每个属性，即所有小写字母并用 "-" 分隔单词（例如 {@code my-long-property}）。</p>
	 *
	 * <p>
	 * If multiple names are specified, all of the properties have to pass the test for
	 * the condition to match.
	 * <p>如果指定了多个名称，则所有属性都必须通过测试才能使条件匹配。</p>
	 *
	 * @return the names
	 * <p>名称</p>
	 */
	String[] name() default {};

	/**
	 * The expected value for the properties. If not specified, the property must be equal
	 * to {@code true}.
	 * <p>属性的预期值。如果未指定，则属性必须等于 {@code true}。</p>
	 *
	 * @return the expected value
	 * <p>预期值</p>
	 */
	boolean havingValue() default true;

	/**
	 * Specify if the condition should match if the property is not set. Defaults to
	 * {@code false}.
	 * <p>指定如果属性未设置时条件是否应匹配。默认值为 {@code false}。</p>
	 *
	 * @return if the condition should match if the property is missing
	 * <p>如果属性缺失时条件是否应匹配</p>
	 */
	boolean matchIfMissing() default false;

}