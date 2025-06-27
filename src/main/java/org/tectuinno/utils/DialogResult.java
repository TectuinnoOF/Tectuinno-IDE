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

package org.tectuinno.utils;

/**
 * Represents the result of an operation, following a pattern similar to DialogResult in C#.
 * 
 * This enum allows consistent handling of operation outcomes such as success, cancellation,
 * or unexpected/invalid states. It is commonly used to simplify flow control in response to user actions
 * or system events throughout the IDE.
 *
 * Usage examples:
 * <pre>
 * DialogResult result = saveFile();
 * if (result == DialogResult.OK) {
 *     // proceed
 * } else if (result == DialogResult.ABORT) {
 *     // handle cancellation
 * }
 * </pre>
 * 
 * Enum values:
 * - {@code OK}: The operation completed successfully.
 * - {@code NULL}: The result is undefined or invalid; must be handled properly to avoid null behavior.
 * - {@code ABORT}: The operation was canceled intentionally (e.g., by the user).
 */
public enum DialogResult {

	/**
	 * used to retunr an ok state after operations
	 */
	OK,
	
	/**
	 *using to return a null value, you shoould handle te error result after implements 
	 * */
	NULL,
	
	/**
	 * To specify that the operation has been canceled
	 */
	ABORT,

	
	
}
