/*
 * Copyright © 2017 zhiyifang and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package xidian.synchronizer.syn.utils.rediskey;

public abstract class BasePrefix implements KeyPrefix{

	private int expireSeconds;
	private String prefix;
	
	public BasePrefix(String prefix) {
		this(0,prefix);
	}

	public BasePrefix(int expireSeconds, String prefix) {
		this.expireSeconds = expireSeconds;
		this.prefix = prefix;
	}

	// 用抽象类来实现共用的实现方式，就是返回两个字段的值
	// 其他的前缀类就只需要确定如何构造这两个字段的值就好
	@Override
	public int expireSeconds() {
		return expireSeconds;
	}

	@Override
	public String getPrefix() {
		return prefix;
	}
	
	
}
