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
 * Represents a symbol table that stores label addresses during the first pass
 * of the assembler process.
 * <p>
 * This table is typically used to:
 * <ul>
 *     <li>Record label names and their corresponding memory addresses</li>
 *     <li>Check if a label already exists</li>
 *     <li>Retrieve the address of a specific label</li>
 * </ul>
 *
 * <h2>Usage example:</h2>
 * <pre>{@code
 * SymbolTable table = new SymbolTable();
 * table.define("LOOP", 0x004);
 * if (table.contains("LOOP")) {
 *     int address = table.addressOf("LOOP");
 *     System.out.println("Address of LOOP: " + address);
 * }
 * }</pre>
 *
 * <h2>Key Features:</h2>
 * <table border="1">
 *   <caption>SymbolTable Responsibilities</caption>
 *   <tr>
 *     <th>Method</th>
 *     <th>Description</th>
 *   </tr>
 *   <tr>
 *     <td>{@link #define(String, int)}</td>
 *     <td>Defines or overwrites a label address</td>
 *   </tr>
 *   <tr>
 *     <td>{@link #contains(String)}</td>
 *     <td>Checks if a label exists</td>
 *   </tr>
 *   <tr>
 *     <td>{@link #addressOf(String)}</td>
 *     <td>Retrieves the address of a label</td>
 *   </tr>
 *   <tr>
 *     <td>{@link #asMap()}</td>
 *     <td>Returns the internal map of labels and addresses</td>
 *   </tr>
 * </table>
 *
 * @author Pablo
 * @version 1.0
 * @since 2025-08-14
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
	
	/**
     * Returns the internal mapping of labels to addresses.
     * <p>
     * <b>Note:</b> This exposes the internal map directly; modifications to the returned map
     * will affect the symbol table.
     *
     * @return a map containing all labels and their addresses
     */
	public Map<String, Integer> asMap(){
		return labels;
	}
	
}
