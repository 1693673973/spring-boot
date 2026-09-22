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

import org.jspecify.annotations.Nullable;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Outcome for a condition match, including log message.
 * <p>条件匹配的结果，包括日志消息。</p>
 *
 * @author Phillip Webb
 * @since 1.0.0
 * @see ConditionMessage
 */
public class ConditionOutcome {

	private final boolean match;

	private final ConditionMessage message;

	/**
	 * Create a new {@link ConditionOutcome} instance. For more consistent messages
	 * consider using {@link #ConditionOutcome(boolean, ConditionMessage)}.
	 * <p>创建一个新的 {@link ConditionOutcome} 实例。为了获得更一致的消息，请考虑使用 {@link #ConditionOutcome(boolean, ConditionMessage)}。</p>
	 *
	 * @param match if the condition is a match
	 *              <p>条件是否匹配</p>
	 * @param message the condition message
	 *                <p>条件消息</p>
	 */
	public ConditionOutcome(boolean match, String message) {
		this(match, ConditionMessage.of(message));
	}

	/**
	 * Create a new {@link ConditionOutcome} instance.
	 * <p>创建一个新的 {@link ConditionOutcome} 实例。</p>
	 *
	 * @param match if the condition is a match
	 *              <p>条件是否匹配</p>
	 * @param message the condition message
	 *                <p>条件消息</p>
	 */
	public ConditionOutcome(boolean match, ConditionMessage message) {
		Assert.notNull(message, "'message' must not be null");
		this.match = match;
		this.message = message;
	}

	/**
	 * Create a new {@link ConditionOutcome} instance for a 'match'.
	 * <p>为“匹配”创建一个新的 {@link ConditionOutcome} 实例。</p>
	 *
	 * @return the {@link ConditionOutcome}
	 * <p>该 {@link ConditionOutcome}</p>
	 */
	public static ConditionOutcome match() {
		return match(ConditionMessage.empty());
	}

	/**
	 * Create a new {@link ConditionOutcome} instance for 'match'. For more consistent
	 * messages consider using {@link #match(ConditionMessage)}.
	 * <p>为“匹配”创建一个新的 {@link ConditionOutcome} 实例。为了获得更一致的消息，请考虑使用 {@link #match(ConditionMessage)}。</p>
	 *
	 * @param message the message
	 *                <p>消息</p>
	 * @return the {@link ConditionOutcome}
	 * <p>该 {@link ConditionOutcome}</p>
	 */
	public static ConditionOutcome match(String message) {
		return new ConditionOutcome(true, message);
	}

	/**
	 * Create a new {@link ConditionOutcome} instance for 'match'.
	 * <p>为“匹配”创建一个新的 {@link ConditionOutcome} 实例。</p>
	 *
	 * @param message the message
	 *                <p>消息</p>
	 * @return the {@link ConditionOutcome}
	 * <p>该 {@link ConditionOutcome}</p>
	 */
	public static ConditionOutcome match(ConditionMessage message) {
		return new ConditionOutcome(true, message);
	}

	/**
	 * Create a new {@link ConditionOutcome} instance for 'no match'. For more consistent
	 * messages consider using {@link #noMatch(ConditionMessage)}.
	 * <p>为“不匹配”创建一个新的 {@link ConditionOutcome} 实例。为了获得更一致的消息，请考虑使用 {@link #noMatch(ConditionMessage)}。</p>
	 *
	 * @param message the message
	 *                <p>消息</p>
	 * @return the {@link ConditionOutcome}
	 * <p>该 {@link ConditionOutcome}</p>
	 */
	public static ConditionOutcome noMatch(String message) {
		return new ConditionOutcome(false, message);
	}

	/**
	 * Create a new {@link ConditionOutcome} instance for 'no match'.
	 * <p>为“不匹配”创建一个新的 {@link ConditionOutcome} 实例。</p>
	 *
	 * @param message the message
	 *                <p>消息</p>
	 * @return the {@link ConditionOutcome}
	 * <p>该 {@link ConditionOutcome}</p>
	 */
	public static ConditionOutcome noMatch(ConditionMessage message) {
		return new ConditionOutcome(false, message);
	}

	/**
	 * Return {@code true} if the outcome was a match.
	 * <p>如果结果为匹配，则返回 {@code true}。</p>
	 *
	 * @return {@code true} if the outcome matches
	 * <p>如果结果匹配则返回 {@code true}</p>
	 */
	public boolean isMatch() {
		return this.match;
	}

	/**
	 * Return an outcome message or {@code null}.
	 * <p>返回结果消息或 {@code null}。</p>
	 *
	 * @return the message or {@code null}
	 * <p>消息或 {@code null}</p>
	 */
	public @Nullable String getMessage() {
		return this.message.isEmpty() ? null : this.message.toString();
	}

	/**
	 * Return an outcome message.
	 * <p>返回结果消息。</p>
	 *
	 * @return the message
	 * <p>消息</p>
	 */
	public ConditionMessage getConditionMessage() {
		return this.message;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() == obj.getClass()) {
			ConditionOutcome other = (ConditionOutcome) obj;
			return (this.match == other.match && ObjectUtils.nullSafeEquals(this.message, other.message));
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Boolean.hashCode(this.match) * 31 + ObjectUtils.nullSafeHashCode(this.message);
	}

	@Override
	public String toString() {
		return (this.message != null) ? this.message.toString() : "";
	}

}