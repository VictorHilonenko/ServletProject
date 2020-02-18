package beauty.scheduler.dao.core;

import beauty.scheduler.dao.annotation.DBColumn;
import beauty.scheduler.dao.annotation.DBTable;
import beauty.scheduler.util.ExceptionKind;
import beauty.scheduler.util.ExtendedException;
import beauty.scheduler.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static beauty.scheduler.util.AppConstants.ID_FIELD;
import static beauty.scheduler.util.AppConstants.TABLENAME;

public class SQLStatementsGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLStatementsGenerator.class);

    private Map<String, String> cachedQueriesTexts = new HashMap<>();

    //NOTE: now it's designed only for oneToMany cases, manyToMany is not used in this project
    String getGeneralSelectQueryFor(Class entityClass, String addition) throws ExtendedException {
        String key;
        if (StringUtils.isEmpty(addition)) {
            key = "select_" + entityClass.getName();
        } else {
            key = "select_" + addition + "_" + entityClass.getName();
        }

        if (cachedQueriesTexts.containsKey(key)) {
            return cachedQueriesTexts.get(key);
        }

        SelectGenerator selectGenerator = new SelectGenerator(entityClass);
        String query = selectGenerator.toString();
        if (!StringUtils.isEmpty(addition)) {
            query += " " + addition.replace(TABLENAME, selectGenerator.getTableName());
        }

        cachedQueriesTexts.put(key, query);

        return query;
    }

    String getGeneralSelectQueryFor(Class entityClass) throws ExtendedException {
        return getGeneralSelectQueryFor(entityClass, "");
    }

    String getGeneralCountQueryFor(Class entityClass, String addition) throws ExtendedException {
        String key;
        if (StringUtils.isEmpty(addition)) {
            key = "count_" + entityClass.getName();
        } else {
            key = "count_" + addition + "_" + entityClass.getName();
        }

        if (cachedQueriesTexts.containsKey(key)) {
            return cachedQueriesTexts.get(key);
        }

        CountGenerator countGenerator = new CountGenerator(entityClass);
        String query = countGenerator.toString();
        if (!StringUtils.isEmpty(addition)) {
            query += " " + addition.replace(TABLENAME, countGenerator.getTableName());
        }

        cachedQueriesTexts.put(key, query);

        return query;
    }

    String getGeneralCountQueryFor(Class entityClass) throws ExtendedException {
        return getGeneralCountQueryFor(entityClass, "");
    }

    String getGeneralInsertStatementFor(Class entityClass) throws ExtendedException {
        String key = "insert_" + entityClass.getName();
        if (cachedQueriesTexts.containsKey(key)) {
            return cachedQueriesTexts.get(key);
        }

        InsertGenerator insertGenerator = new InsertGenerator(entityClass);
        String query = insertGenerator.toString();

        cachedQueriesTexts.put(key, query);

        return query;
    }

    String getGeneralUpdateStatementFor(Class entityClass, String addition) throws ExtendedException {
        String key;
        if (StringUtils.isEmpty(addition)) {
            key = "update_" + entityClass.getName();
        } else {
            key = "update_" + addition + "_" + entityClass.getName();
        }

        if (cachedQueriesTexts.containsKey(key)) {
            return cachedQueriesTexts.get(key);
        }

        UpdateGenerator updateGenerator = new UpdateGenerator(entityClass);
        String query = updateGenerator.toString();

        if (StringUtils.isEmpty(addition)) {
            addition = "WHERE " + ID_FIELD + " = ?";
        }

        query += " " + addition.replace(TABLENAME, updateGenerator.getTableName());

        cachedQueriesTexts.put(key, query);

        return query;
    }

    String getGeneralUpdateStatementFor(Class entityClass) throws ExtendedException {
        return getGeneralUpdateStatementFor(entityClass, "");
    }

    String getGeneralDeleteStatementFor(Class entityClass, String addition) throws ExtendedException {
        String key;
        if (StringUtils.isEmpty(addition)) {
            key = "delete_" + entityClass.getName();
        } else {
            key = "delete_" + addition + "_" + entityClass.getName();
        }

        if (cachedQueriesTexts.containsKey(key)) {
            return cachedQueriesTexts.get(key);
        }

        DeleteGenerator deleteGenerator = new DeleteGenerator(entityClass);
        String query = deleteGenerator.toString();

        if (StringUtils.isEmpty(addition)) {
            addition = "WHERE " + ID_FIELD + " = ?";
        }
        query += " " + addition.replace(TABLENAME, deleteGenerator.getTableName());

        cachedQueriesTexts.put(key, query);

        return query;
    }

    String getGeneralDeleteStatementFor(Class entityClass) throws ExtendedException {
        return getGeneralDeleteStatementFor(entityClass, "");
    }

    static String annotatedTableName(Class entityClass) throws ExtendedException {
        if (!entityClass.isAnnotationPresent(DBTable.class)) {
            throw new ExtendedException(ExceptionKind.WRONG_CONFIGURATION);
        }

        DBTable tableAnnotation = (DBTable) entityClass.getAnnotation(DBTable.class);
        return tableAnnotation.name();
    }

    private class SelectGenerator {
        private Class entityClass;
        private String tableName;
        private String tableAlias;
        private String joiningColumn;
        private StringJoiner sjSELECT;
        private StringJoiner sjFROM;

        SelectGenerator(Class entityClass) throws ExtendedException {
            this.entityClass = entityClass;
            this.tableName = annotatedTableName(entityClass);
            this.tableAlias = tableName;
            this.joiningColumn = "";

            this.sjSELECT = new StringJoiner(", \n");
            this.sjFROM = new StringJoiner("\n");

            addStatements();
        }

        SelectGenerator(Class entityClass, String tableAlias, String joiningColumn, StringJoiner sjSELECT, StringJoiner sjFROM) throws ExtendedException {
            this.entityClass = entityClass;
            this.tableName = annotatedTableName(entityClass);
            this.tableAlias = tableAlias;
            this.joiningColumn = joiningColumn;

            this.sjSELECT = sjSELECT;
            this.sjFROM = sjFROM;

            addStatements();
        }

        void addStatements() throws ExtendedException {
            if (sjFROM.length() == 0) {
                sjFROM.add(tableAlias);
            } else {
                sjFROM.add("LEFT JOIN " + tableName + " " + tableAlias + " ON " + joiningColumn + " = " + tableAlias + ".id");
            }

            for (Field field : entityClass.getDeclaredFields()) {
                if (!field.isAnnotationPresent(DBColumn.class)) {
                    continue;
                }
                String columnName = tableAlias + "." + field.getAnnotation(DBColumn.class).name();

                if (field.getType().isAnnotationPresent(DBTable.class)) {
                    new SelectGenerator(field.getType(), field.getName(), columnName, sjSELECT, sjFROM);
                } else {
                    sjSELECT.add(columnName + " AS " + columnName.replace(".", "_"));
                }
            }
        }

        //NOTE: in this project is considered, that ID column of any table will have only ID_FIELD name/identifier

        public String getTableName() {
            return tableName;
        }

        @Override
        public String toString() {
            return "SELECT\n" + sjSELECT.toString() + "\n" +
                    "FROM\n" + sjFROM.toString() + "\n";
        }
    }

    private class CountGenerator {
        private Class entityClass;
        private String tableName;
        private String tableAlias;
        private String joiningColumn;
        private StringJoiner sjFROM;

        CountGenerator(Class entityClass) throws ExtendedException {
            this.entityClass = entityClass;
            this.tableName = annotatedTableName(entityClass);
            this.tableAlias = tableName;
            this.joiningColumn = "";

            this.sjFROM = new StringJoiner("\n");

            addStatements();
        }

        CountGenerator(Class entityClass, String tableAlias, String joiningColumn, StringJoiner sjFROM) throws ExtendedException {
            this.entityClass = entityClass;
            this.tableName = annotatedTableName(entityClass);
            this.tableAlias = tableAlias;
            this.joiningColumn = joiningColumn;

            this.sjFROM = sjFROM;

            addStatements();
        }

        void addStatements() throws ExtendedException {
            if (sjFROM.length() == 0) {
                sjFROM.add(tableAlias);
            } else {
                sjFROM.add("LEFT JOIN " + tableName + " " + tableAlias + " ON " + joiningColumn + " = " + tableAlias + ".id");
            }

            for (Field field : entityClass.getDeclaredFields()) {
                if (!field.isAnnotationPresent(DBColumn.class)) {
                    continue;
                }
                String columnName = tableAlias + "." + field.getAnnotation(DBColumn.class).name();

                if (field.getType().isAnnotationPresent(DBTable.class)) {
                    new CountGenerator(field.getType(), field.getName(), columnName, sjFROM);
                }
            }
        }

        //NOTE: in this project is considered, that ID column of any table will have only ID_FIELD name/identifier

        public String getTableName() {
            return tableName;
        }

        @Override
        public String toString() {
            return "SELECT COUNT(*)\n" +
                    "FROM\n" + sjFROM.toString() + "\n";
        }
    }

    private class InsertGenerator {
        private Class entityClass;
        private String tableName;
        private StringJoiner sjINSERT;
        private StringJoiner sjVALUES;

        InsertGenerator(Class entityClass) throws ExtendedException {
            this.entityClass = entityClass;
            this.tableName = annotatedTableName(entityClass);

            this.sjINSERT = new StringJoiner(", ", "INSERT INTO " + this.tableName + " (", ")");
            this.sjVALUES = new StringJoiner(", ", "VALUES (", ")");

            addStatements();
        }

        void addStatements() {
            for (Field field : entityClass.getDeclaredFields()) {
                if (!field.isAnnotationPresent(DBColumn.class)) {
                    continue;
                }
                String columnName = field.getAnnotation(DBColumn.class).name();
                if (columnName.equals(ID_FIELD)) {
                    continue;
                }

                sjINSERT.add(columnName);
                sjVALUES.add("?");
            }
        }

        public String getTableName() {
            return tableName;
        }

        @Override
        public String toString() {
            return sjINSERT.toString() + "\n" +
                    sjVALUES.toString();
        }
    }

    private class UpdateGenerator {
        private Class entityClass;
        private String tableName;
        private StringJoiner sjSET;

        UpdateGenerator(Class entityClass) throws ExtendedException {
            this.entityClass = entityClass;
            this.tableName = annotatedTableName(entityClass);

            this.sjSET = new StringJoiner(", ", "UPDATE " + this.tableName + " SET\n", "");

            addStatements();
        }

        void addStatements() {
            for (Field field : entityClass.getDeclaredFields()) {
                if (!field.isAnnotationPresent(DBColumn.class)) {
                    continue;
                }
                String columnName = field.getAnnotation(DBColumn.class).name();
                if (columnName.equals(ID_FIELD)) {
                    //we don't update primary keys
                    continue;
                }

                sjSET.add(columnName + " = ?");
            }
        }

        public String getTableName() {
            return tableName;
        }

        @Override
        public String toString() {
            return sjSET.toString();
        }
    }

    private class DeleteGenerator {
        private String tableName;
        private String strDELETE;

        DeleteGenerator(Class entityClass) throws ExtendedException {
            this.tableName = annotatedTableName(entityClass);

            this.strDELETE = "DELETE FROM " + this.tableName + " \n";
        }

        public String getTableName() {
            return tableName;
        }

        @Override
        public String toString() {
            return strDELETE;
        }
    }
}