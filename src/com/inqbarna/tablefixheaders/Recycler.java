/*
 * Copyright (C) 2013 Brais Gab�n (InQBarna) <braisgabin@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.inqbarna.tablefixheaders;

import java.util.Stack;

import android.view.View;

/**
 * The Recycler facilitates reuse of views across layouts.
 * 
 * @author Brais Gab�n (InQBarna)
 */
public class Recycler {

	private Stack<View>[] views;

	/**
	 * Constructor
	 * 
	 * @param size
	 *            The number of types of view to recycle.
	 */
	@SuppressWarnings("unchecked")
	public Recycler(int size) {
		views = new Stack[size];
		for (int i = 0; i < size; i++) {
			views[i] = new Stack<View>();
		}
	}

	/**
	 * Add a view to the Recycler. This view may be reused in the function
	 * {@link #getRecycledView(int)}
	 * 
	 * @param view
	 *            A view to add to the Recycler. It can no longer be used.
	 * @param type
	 *            the type of the view.
	 */
	public void addRecycledView(View view, int type) {
		views[type].push(view);
	}

	/**
	 * Returns, if exists, a view of the type <code>typeView</code>.
	 * 
	 * @param typeView
	 *            the type of view that you want.
	 * @return a view of the type <code>typeView</code>. <code>null</code> if
	 *         not found.
	 */
	public View getRecycledView(int typeView) {
		try {
			return views[typeView].pop();
		} catch (java.util.EmptyStackException e) {
			return null;

		}
	}
}
