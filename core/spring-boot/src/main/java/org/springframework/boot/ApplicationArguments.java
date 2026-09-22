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

package org.springframework.boot;

import java.util.List;
import java.util.Set;

import org.jspecify.annotations.Nullable;

/**
 * Provides access to the arguments that were used to run a {@link SpringApplication}.
 * <p>提供对用于运行 {@link SpringApplication} 的参数的访问。</p>
 *
 * @author Phillip Webb
 * @since 1.3.0
 */
public interface ApplicationArguments {

	/**
	 * Return the raw unprocessed arguments that were passed to the application.
	 * <p>返回传递给应用程序的原始未处理参数。</p>
	 * @return the arguments
	 * <p>参数</p>
	 */
	String[] getSourceArgs();

	/**
	 * Return the names of all option arguments. For example, if the arguments were
	 * "--foo=bar --debug" would return the values {@code ["foo", "debug"]}.
	 * <p>返回所有选项参数的名称。例如，如果参数为 "--foo=bar --debug"，将返回值 {@code ["foo", "debug"]}。</p>
	 * @return the option names or an empty set
	 * <p>选项名称或空集合</p>
	 */
	Set<String> getOptionNames();

	/**
	 * Return whether the set of option arguments parsed from the arguments contains an
	 * option with the given name.
	 * <p>返回从参数解析的选项参数集合是否包含具有给定名称的选项。</p>
	 * @param name the name to check
	 * <p>要检查的名称</p>
	 * @return {@code true} if the arguments contain an option with the given name
	 * <p>如果参数包含具有给定名称的选项，则为 {@code true}</p>
	 */
	boolean containsOption(String name);

	/**
	 * Return the collection of values associated with the arguments option having the
	 * given name.
	 * <p>返回与具有给定名称的参数选项关联的值集合。</p>
	 * <ul>
	 * <li>if the option is present and has no argument (e.g.: "--foo"), return an empty
	 * collection ({@code []})</li>
	 * <li>如果选项存在且没有参数（例如："--foo"），则返回空集合 ({@code []})</li>
	 * <li>if the option is present and has a single value (e.g. "--foo=bar"), return a
	 * collection having one element ({@code ["bar"]})</li>
	 * <li>如果选项存在且具有单个值（例如："--foo=bar"），则返回包含一个元素的集合 ({@code ["bar"]})</li>
	 * <li>if the option is present and has multiple values (e.g. "--foo=bar --foo=baz"),
	 * return a collection having elements for each value ({@code ["bar", "baz"]})</li>
	 * <li>如果选项存在且具有多个值（例如："--foo=bar --foo=baz"），则返回为每个值包含元素的集合 ({@code ["bar", "baz"]})</li>
	 * <li>if the option is not present, return {@code null}</li>
	 * <li>如果选项不存在，则返回 {@code null}</li>
	 * </ul>
	 * @param name the name of the option
	 * <p>选项名称</p>
	 * @return a list of option values for the given name
	 * <p>给定名称的选项值列表</p>
	 */
	@Nullable List<String> getOptionValues(String name);

	/**
	 * Return the collection of non-option arguments parsed.
	 * <p>返回解析的非选项参数集合。</p>
	 * @return the non-option arguments or an empty list
	 * <p>非选项参数或空列表</p>
	 */
	List<String> getNonOptionArgs();

}