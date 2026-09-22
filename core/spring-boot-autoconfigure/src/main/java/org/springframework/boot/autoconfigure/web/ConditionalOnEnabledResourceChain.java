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

package org.springframework.boot.autoconfigure.web;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Conditional;

/**
 * {@link Conditional @Conditional} that checks whether the Spring resource handling chain
 * is enabled. Matches if {@link WebProperties.Resources.Chain#getEnabled()} is
 * {@code true} or if one of {@code "org.webjars:webjars-locator-core"},
 * {@code "org.webjars:webjars-locator-lite"} is on the classpath.
 * <p>检查 Spring 资源处理链是否启用的 {@link Conditional @Conditional}。如果 {@link WebProperties.Resources.Chain#getEnabled()} 为 {@code true}，或者类路径上存在 {@code "org.webjars:webjars-locator-core"}、{@code "org.webjars:webjars-locator-lite"} 之一，则匹配。</p>
 * <p>
 * Note that support for {@code "org.webjars:webjars-locator-core"} is deprecated.
 * <p>请注意，对 {@code "org.webjars:webjars-locator-core"} 的支持已弃用。</p>
 *
 * @author Stephane Nicoll
 * @since 1.3.0
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnEnabledResourceChainCondition.class)
public @interface ConditionalOnEnabledResourceChain {

}