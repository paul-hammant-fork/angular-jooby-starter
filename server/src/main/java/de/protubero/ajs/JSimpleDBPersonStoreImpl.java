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

import org.jsimpledb.*;
import org.jsimpledb.core.Database;
import org.jsimpledb.kv.sqlite.SQLiteKVDatabase;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple collection to hold the person objects. If it looks unfamiliar,
 * thats because i played around with the javaslang lib (now named vavr).
 * 
 * @author MSchaefer
 *
 */
@Singleton
public class JSimpleDBPersonStoreImpl implements PersonStore {

	private AtomicInteger idCounter = new AtomicInteger();
	private JSimpleDB jdb;
	private SQLiteKVDatabase kvdb;

	public JSimpleDBPersonStoreImpl() {
//		kvdb = new SQLiteKVDatabase().withDatabaseFile(..);  // wished for
		kvdb = new SQLiteKVDatabase();
		try {
			// TODO shouldn't be a temp file
			kvdb.setDatabaseFile(File.createTempFile("people", "sqlite"));
		} catch (IOException e) {
			throw new IllegalArgumentException("couldn't create temp file");
		}
		kvdb.start();
		jdb = new JSimpleDBFactory()
				.setDatabase(new Database(kvdb))
				.setSchemaVersion(1)
				.setModelClasses(Person.class)
				.newJSimpleDB();
	}

	@Override
	public void stop() {
		kvdb.stop();
	}

	@Override
	public Person insert(Person person) {
		final JTransaction txn = createTransaction();
		try {
			Person p = txn.create(Person.class);
			p.setAge(person.getAge());
			p.setEmail(person.getEmail());
			p.setName(person.getName());
			int id = idCounter.incrementAndGet();
			p.setId(id);
			return p;
		} finally {
			txn.commit();
		}
	}

	@Override
	public Optional<Person> selectById(int id) {
		final JTransaction txn = createTransaction();
		try {
			NavigableMap<Integer, NavigableSet<Person>> map = txn.queryIndex(Person.class,
					"id", Integer.class).asMap();
			if (!map.containsKey(id)) {
				return Optional.empty();
			}
			Person first = map.get(id).first();

			// this because of 'org.jsimpledb.core.StaleTransactionException: transaction cannot be accessed because it is no longer usable'
			// wishing for a toVanilla() or toValueOject()/toVanillaImmutable() so that users don't call setters
			Person rv = toVanilla(first);

			return Optional.of(rv);
		} finally {
			txn.commit();
		}
	}

	private Person toVanilla(Person first) {
		Person rv = new Person();
		rv.setId(first.getId());
		rv.updateWith(first);
		return rv;
	}

	@Override
	public java.util.List<Person> selectAll() {
		final JTransaction txn = createTransaction();
		try {
			NavigableSet<Person> all = txn.getAll(Person.class);
			// this because of 'org.jsimpledb.core.StaleTransactionException: transaction cannot be accessed because it is no longer usable'
			// also wishing for a toVanilla() or toValueOject()/toVanillaImmutable() so that users don't call setters
			List<Person> rv = new ArrayList<>();
			for (Person p : all) {
				rv.add(toVanilla(p));
			}
			return rv;
		} finally {
			txn.commit();
		}
	}

	@Override
	public boolean delete(int id) {
		final JTransaction txn = createTransaction();
		try {
			try {
				Person p = txn.queryIndex(Person.class,
						"id", Integer.class).asMap().get(id).first();
				((JObject) p).delete();
				return true;
			} catch (NoSuchElementException e) {
				return false;
			}
		} finally {
			txn.commit();
		}
	}

	private JTransaction createTransaction() {
		return jdb.createTransaction(true, ValidationMode.MANUAL);
	}

}
