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

package org.tectuinno.model;

import org.tectuinno.utils.FileType;

/**
 * Represents a source code file loaded or managed by the Tectuinno IDE.
 *
 * This class stores metadata such as the file name and its type (e.g., Assembly or C++).
 * It serves as a lightweight model to associate a file with an editor tab, 
 * perform type-specific logic, or track open files within a session.
 *
 * Future extensions may include attributes like file path, last modified time, 
 * content buffer, or change tracking status.
 *
 * Example:
 * <pre>
 * FileModel asmFile = new FileModel("main.asm", FileType.ASSEMBLY_FILE);
 * </pre>
 */
public class FileModel {
	
	private String name;
	private FileType fileType;
	
	public FileModel(String fileName, FileType fileType) {
		this.name = fileName;
		this.fileType = fileType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FileType getFileType() {
		return fileType;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	};		
	
	
	
}
