gears
=====

A common set of classes based on powerful apis, to provide a fast, simple and easy way for programming.

Features.
- Create a consice data store class.
- datastore can execute updates, functions and queries
    - insert, update, should receive in the following way:
    ds.evaluate(sql, Object...)
    ds.evaluate(sql, Object, ..
    ds.evaluate(sql).put

    -Implements a new way of doing it???
    http://docs.geotools.org/latest/userguide/library/opengis/filter.html

    - select(query, SimpleResultVisitor<T>, Object...), retuns object of type T, replaces the query mapped with ?
            in the same oreder that parameters are sent.
    - select(query, Object context, SimpleResultVisitor<T>), returns a T object, but uses the values contained in
            the context object (pojo, map, dictionary)
    - void select(query, ObjectResultVisitor<T>, Object...
    - void select(query, Object context, ObjectResultVisitor<T>)
    - T selectOne(query, Class<T>, Object...)
    - T selectOne(query, Object, Class<T>)
    - List<T> selectList(query, class<T>, Object...)
    - List<T> selectList(query, Object, class<T>)

- datastore, can recover a datatable structure
    -

