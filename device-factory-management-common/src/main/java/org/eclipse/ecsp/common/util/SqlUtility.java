/*
 *  *******************************************************************************
 *  Copyright (c) 2023-24 Harman International
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  *******************************************************************************
 */

package org.eclipse.ecsp.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.common.SharedConstants;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The `SqlUtility` class provides utility methods for SQL operations.
 * It contains methods for constructing SQL queries, preparing SQL statements,
 * and handling common SQL operations such as sorting, filtering, and parameter binding.
 */
@Slf4j
public class SqlUtility {

    public static final String COMMA = ",";
    public static final String SINGLE_QUOTE = "'";
    public static final String AND = " and ";
    public static final String EQUAL_TO = "=";
    public static final String ORDER_BY = " ORDER BY \"";
    public static final int CAPACITY = 100;
    public static final int LENGTH = 2;
    public static final int INDEX = -1;

    /**
     * Private constructor to prevent instantiation of the {@code SqlUtility} class.
     */
    private SqlUtility() {

    }

    /**
     * Constructs a prepared SQL statement based on the provided prefix, operator, and attribute-value map.
     *
     * @param prefix            the prefix to be added to the SQL statement
     * @param operator          the operator to be used in the SQL statement
     * @param attributeValueMap the map containing attribute-value pairs
     * @return the prepared SQL statement
     */
    public static String getPreparedSql(String prefix, String operator, Map<String, Object> attributeValueMap) {
        String sql;
        if (attributeValueMap == null || attributeValueMap.isEmpty() || prefix == null || prefix.isEmpty()
            //operator == null
            || operator.isEmpty()) {
            log.error("getPreparedSql:attributeValueMap or prefix or operator can not be null or empty");
            return null;
        }
        int count = 0;
        StringBuilder preparedSuffix = new StringBuilder();
        for (String attribute : attributeValueMap.keySet()) {
            if (count == 0) {
                preparedSuffix.append(attribute).append(" = ?").toString();
            } else {
                preparedSuffix.append(operator).append(attribute).append(" = ?").toString();
            }
            count++;
            log.debug("preparedSuffix:::{}", preparedSuffix);
        }
        sql = prefix + preparedSuffix;
        log.debug("finalSQl: {}", sql);

        return sql;
    }

    /**
     * Retrieves the values from the given ordered map and returns them as an array.
     *
     * @param orderedMap the ordered map containing the values to retrieve
     * @return an array containing the values from the ordered map
     */
    public static Object[] getArrayValues(Map<String, Object> orderedMap) {
        if (orderedMap == null || orderedMap.isEmpty()) {
            log.error("getArrayValues:orderedMap can not be null or empty");
            return ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        Object[] array = orderedMap.entrySet().stream().map(Map.Entry::getValue).toArray();

        log.debug("Values from Map: {}", orderedMap);
        return array;
    }

    /**
     * Prepares a SQL query with LIKE conditions based on the provided field and value lists.
     *
     * @param containsLikeFieldList The list of fields to apply LIKE conditions on.
     * @param containsLikeValueList The list of values to match against the fields.
     * @return The prepared SQL query string.
     */
    public static String prepareLikeQuery(List<String> containsLikeFieldList, List<String> containsLikeValueList) {
        if (containsLikeFieldList == null || containsLikeValueList == null || containsLikeFieldList.isEmpty()
            || containsLikeValueList.isEmpty()) {
            return "";
        }
        if (containsLikeFieldList.size() != containsLikeValueList.size()) {
            log.warn("containsLikeFieldList and containsLikeValueList should be of same size");
            return "";
        }
        StringBuilder stringQueryFilter = null;
        for (int i = 0; i < containsLikeFieldList.size(); i++) {
            String field = containsLikeFieldList.get(i);
            String value = containsLikeValueList.get(i);
            if (stringQueryFilter == null) {
                stringQueryFilter = new StringBuilder();
            } else {
                stringQueryFilter.append(AND);
            }
            stringQueryFilter.append(" \"" + field + "\" LIKE '%");
            stringQueryFilter.append(value.replace("'", "''"));
            stringQueryFilter.append("%' ");
        }
        return stringQueryFilter.toString();
    }

    /**
     * Prepares a range query based on the given range field list and range value list.
     *
     * @param rangeFieldList  the list of range fields
     * @param rangeValueList  the list of range values
     * @return the prepared range query as a string
     */
    public static String prepareRangeQuery(List<String> rangeFieldList, List<String> rangeValueList) {
        if (rangeFieldList == null || rangeValueList == null || rangeFieldList.isEmpty() || rangeValueList.isEmpty()) {
            return "";
        }
        if (rangeFieldList.size() != rangeValueList.size()) {
            log.warn("rangeFieldList and rangeValueList should be of same size");
            return "";
        }
        StringBuilder stringQueryFilter = null;
        for (int i = 0; i < rangeFieldList.size(); i++) {
            String field = rangeFieldList.get(i);
            String range = rangeValueList.get(i);
            if (range.split(SharedConstants.UNDERSCORE).length != LENGTH) {
                log.warn("Invalid range value can not be used in query:{}", range);
                continue;
            }
            String start = range.split(SharedConstants.UNDERSCORE)[0];
            String end = range.split(SharedConstants.UNDERSCORE)[1];
            if (stringQueryFilter == null) {
                stringQueryFilter = new StringBuilder();
            } else {
                stringQueryFilter.append(AND);
            }
            stringQueryFilter.append(
                " \"" + field + "\" >= TO_TIMESTAMP(" + start + "/1000) and \"" + field + "\" <= TO_TIMESTAMP(" + end
                    + "/1000) ");
        }
        log.debug("prepareRangeQuery:{}", stringQueryFilter.toString());
        return stringQueryFilter.toString();
    }

    /**
     * Prepares an SQL query for ordering the results based on the specified sorting order and column.
     *
     * @param sortingOrder The sorting order, either "asc" for ascending or "desc" for descending.
     * @param sortBy The column to sort by.
     * @return The prepared SQL query for ordering the results.
     */
    public static String prepareOrderByQuery(String sortingOrder, String sortBy) {
        StringBuilder queryOrderBy = new StringBuilder();

        if (StringUtils.isNotEmpty(sortBy) && StringUtils.isNotEmpty(sortingOrder)
            && sortingOrder.equalsIgnoreCase("asc")) {
            queryOrderBy.append(ORDER_BY);
            queryOrderBy.append(sortBy);
            queryOrderBy.append("\" COLLATE \"C\" ASC ");
        } else if (StringUtils.isNotEmpty(sortBy) && StringUtils.isNotEmpty(sortingOrder)
            && sortingOrder.equalsIgnoreCase("desc")) {
            queryOrderBy.append(ORDER_BY);
            queryOrderBy.append(sortBy);
            queryOrderBy.append("\" COLLATE \"C\" DESC ");
        } else {
            queryOrderBy.append(" ORDER BY \"ID\" ASC ");
        }

        return queryOrderBy.toString();
    }

    /**
     * Splits a string into a list of substrings based on a specified separator.
     *
     * @param paramValue The string to be split.
     * @param separator  The separator used to split the string.
     * @return A list of substrings obtained by splitting the input string.
     */
    public static List<String> getList(String paramValue, String separator) {
        if (StringUtils.isEmpty(paramValue)) {
            return Collections.emptyList();
        }
        paramValue = paramValue.replace(" ", "");
        return new ArrayList<>(Arrays.asList(paramValue.split(separator)));

    }

    /**
     * Prepares a query string for sorting by a specific column and ordering in ascending or descending order.
     *
     * @param sortBy  the column to sort by
     * @param orderBy the order to apply (asc or desc)
     * @return the prepared query string
     */
    public static String prepareSortByAndOrderByQuery(String sortBy, String orderBy) {
        StringBuilder queryOrderBy = new StringBuilder();

        if (StringUtils.isNotEmpty(sortBy) && StringUtils.isNotEmpty(orderBy)) {
            queryOrderBy.append(ORDER_BY);
            queryOrderBy.append(sortBy);
            if ("asc".equalsIgnoreCase(orderBy)) {
                queryOrderBy.append("\" ASC ");
            } else {
                queryOrderBy.append("\" DESC ");
            }
        }
        return queryOrderBy.toString();
    }

    /**
     * Prepares a query string for sorting and ordering the results based on the given parameters.
     *
     * @param sortBy  the column name to sort by
     * @param orderBy the order (ascending or descending)
     * @param table   the table name to apply the sorting and ordering
     * @return the prepared query string
     */
    public static String prepareSortByAndOrderByQuery(String sortBy, String orderBy, String table) {
        StringBuilder queryOrderBy = new StringBuilder();

        if (StringUtils.isNotEmpty(sortBy) && StringUtils.isNotEmpty(orderBy)) {
            queryOrderBy.append(ORDER_BY + table + "\"" + ".\"");
            queryOrderBy.append(sortBy);
            if ("asc".equalsIgnoreCase(orderBy)) {
                queryOrderBy.append("\" ASC ");
            } else {
                queryOrderBy.append("\" DESC ");
            }
        }
        return queryOrderBy.toString();
    }

    /**
     * Constructs a SQL query with an IN clause based on the provided SQL string and attribute map.
     *
     * @param sql          the SQL string to construct the query from
     * @param attributeMap the map containing attribute names as keys and attribute values as values
     * @param <T>          the type of the attribute values
     * @return the constructed SQL query string
     */
    public static <T> String constructQueryWithInClause(String sql, Map<String, T> attributeMap) {
        StringBuilder sqlSb = new StringBuilder(sql);
        attributeMap.forEach((k, v) -> {
            if (v != null) {
                sqlSb.append(k).append(constructInClause(v)).append(AND);
            }
        });
        //remove and at last
        int lastIndexOfAnd = sqlSb.lastIndexOf(AND.trim());
        return lastIndexOfAnd != INDEX ? sqlSb.substring(0, lastIndexOfAnd).trim() + ";" : sqlSb.toString();
    }

    /**
     * Constructs an SQL IN clause based on the provided object.
     *
     * @param object the object to construct the IN clause from
     * @param <T> the type of the object
     * @return the constructed IN clause as a string
     */
    public static <T> String constructInClause(T object) {
        StringBuilder inClauseSb = new StringBuilder(CAPACITY);
        inClauseSb.append(" in(");
        if (object instanceof List) {
            for (Object obj : (List<?>) object) {
                inClauseSb.append(SINGLE_QUOTE).append(obj).append(SINGLE_QUOTE).append(COMMA);
            }
        } else if (object instanceof String string) {
            inClauseSb.append(SINGLE_QUOTE).append(string).append(SINGLE_QUOTE).append(COMMA);
        }
        inClauseSb.deleteCharAt(inClauseSb.lastIndexOf(COMMA));
        inClauseSb.append(")");
        return inClauseSb.toString();
    }

    /**
     * Constructs an SQL update query based on the provided parameters.
     *
     * @param prefixSql     the prefix SQL statement to be included in the query
     * @param attributeMap  a map containing attribute names and their corresponding values to be updated
     * @param conditionMap  a map containing condition names and their corresponding values for the WHERE clause
     * @return the constructed SQL update query
     */
    public static String constructUpdateQuery(String prefixSql, Map<String, Object> attributeMap,
                                              Map<String, Object> conditionMap) {
        StringBuilder sqlSb = new StringBuilder(prefixSql);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
        attributeMap.forEach((k, v) -> {
            if (v != null) {
                // handle timestamp data type
                if (v instanceof Timestamp timestamp) {
                    sqlSb.append(k).append(EQUAL_TO).append(SINGLE_QUOTE)
                        .append(dateFormatter.format((timestamp).getTime())).append(SINGLE_QUOTE).append(COMMA);
                } else {
                    sqlSb.append(k).append(EQUAL_TO).append(SINGLE_QUOTE).append(v).append(SINGLE_QUOTE).append(COMMA);
                }
            }
        });
        sqlSb.deleteCharAt(sqlSb.lastIndexOf(COMMA));
        if (conditionMap != null && conditionMap.size() != 0) {
            sqlSb.append(" where ");
            conditionMap.forEach((k, v) -> {
                if (v != null) {
                    sqlSb.append(k).append(EQUAL_TO).append(SINGLE_QUOTE).append(v).append(SINGLE_QUOTE).append(AND);
                }
            });
        }
        int lastIndexOfAnd = sqlSb.lastIndexOf(AND.trim());
        return lastIndexOfAnd != INDEX ? sqlSb.substring(0, lastIndexOfAnd).trim() : sqlSb.toString();
    }
}
