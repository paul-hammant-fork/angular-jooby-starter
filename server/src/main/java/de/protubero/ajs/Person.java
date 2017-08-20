/**
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */
package de.protubero.ajs;

import org.jsimpledb.annotation.JField;
import org.jsimpledb.annotation.JSimpleClass;

/**
 * Simple person class for demo purposes.
 * 
 * @author MSchaefer
 *
 */
@JSimpleClass
public class Person {

	private int id;
	private String name;
	private String email;
	private int age;

	@JField(indexed = true)
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	@JField
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@JField
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	@JField
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}

	public void updateWith(Person upToDatePerson) {
		this.setAge(upToDatePerson.getAge());
		this.setEmail(upToDatePerson.getEmail());
		this.setName(upToDatePerson.getName());
	}

	@Override
	public String toString() {
		return "Person{" +
				"id=" + getId() +
				", name='" + getName() + '\'' +
				", email='" + getEmail() + '\'' +
				", age=" + getAge() +
				'}';
	}

}
