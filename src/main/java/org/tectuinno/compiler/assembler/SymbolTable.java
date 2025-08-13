/*
 * This file is part of Tectuinno IDE.
 *
 * Tectuinno IDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * As a special exception, you may use this file as part of a free software
 * library without restriction. Specifically, if other files instantiate
 * templates or use macros or inline functions from this file, or you compile
 * this file and link it with other files to produce an executable, this
 * file does not by itself cause the resulting executable to be covered by
 * the GNU General Public License. This exception does not however
 * invalidate any other reasons why the executable file might be covered by
 * the GNU General Public License.
 *
 * Copyright 2025 Tectuinno Team (https://github.com/tectuinno)
 */

package org.tectuinno.compiler.assembler;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores symbol (label) addresses gathered during the first pass
 */
public final class SymbolTable {

	private final Map<String, Integer> labels = new HashMap<String, Integer>();
	
	/**
	 * define or overwrite a label address
	 * @param name
	 * @param address
	 */
	public void define(String name, int address) {
		labels.put(name, address);
	}
	
	/**
	 * Check if a label already exist
	 * @param name
	 * @return
	 */
	public boolean contains(String name) {
		return labels.containsKey(name);		
	}
	
	/**
	 * Get a label address (throws if missing)
	 * @param name
	 * @return
	 */
	public int addressOf(String name) {
		return labels.get(name);
	}
	
	
	public Map<String, Integer> asMap(){
		return labels;
	}
	
}
