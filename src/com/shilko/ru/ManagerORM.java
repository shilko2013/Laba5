package com.shilko.ru;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Stream;

@Inherited
@Target(value = {ElementType.FIELD, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@interface PrimaryKey {
}

@Inherited
@Target(value = {ElementType.FIELD, ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@interface DataBase {
}

interface ORMInterface<T> {
    void create();

    int insert(T object);

    int update(T object);

    int insertOrUpdate(T object);

    int delete(T object);

    ResultSet executeQuery(String query);

    void removeTable();
}

abstract class AbstractManagerORM<T> implements ORMInterface<T> {

    private final Class classObject;
    private Connection connection;
    private String nameOfTable;
    private final List<AccessibleObject> elements;
    private final List<AccessibleObject> primaryKeys;

    {
        elements = new ArrayList<>();
        primaryKeys = new ArrayList<>();
    }

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public AbstractManagerORM(Class classObject, String dataBaseURL, String user, String password, boolean autoCommit) {
        this.classObject = classObject;

        setNameOfTable(classObject.toString().substring(classObject.toString().lastIndexOf(".") + 1).toUpperCase());
        try {
            setConnection(DriverManager.getConnection(dataBaseURL, user, password));
            getConnection().setAutoCommit(autoCommit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Stream.concat(
                Arrays.stream(getClassObject().getDeclaredFields()),
                Arrays.stream(getClassObject().getDeclaredMethods()))
                .forEach(e -> {
                    if (Arrays.stream(e.getAnnotations()).anyMatch(el -> el instanceof DataBase)) {
                        getElements().add(e);
                        if (Arrays.stream(e.getAnnotations()).anyMatch(el -> el instanceof PrimaryKey)) {
                            getPrimaryKeys().add(e);
                        }
                    }
                });
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setNameOfTable(String nameOfTable) {
        this.nameOfTable = nameOfTable;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getNameOfTable() {
        return nameOfTable;
    }

    public List<AccessibleObject> getElements() {
        return elements;
    }

    public Class getClassObject() {
        return classObject;
    }

    public List<AccessibleObject> getPrimaryKeys() {
        return primaryKeys;
    }
}

public class ManagerORM<T> extends AbstractManagerORM<T> {

    public ManagerORM(Class classObject, String dataBaseURL, String user, String password, boolean autoCommit) {
        super(classObject, dataBaseURL, user, password, autoCommit);
    }

    @Override
    public void create() {
        StringBuilder result = new StringBuilder("CREATE TABLE ");
        result.append(getNameOfTable());
        result.append(" ( ");
        getElements().forEach(e -> {
            String temp = "";
            temp += "\"" + getName(e) + "\"";
            temp += " ";
            temp += getType(e);
            temp += " NOT NULL, ";
            result.append(temp);
        });

        result.append("PRIMARY KEY (");
        getPrimaryKeys().forEach(e -> {
            result.append("\"");
            result.append(getName(e));
            result.append("\"");
            result.append(",");
        });
        result.deleteCharAt(result.length() - 1);
        result.append("));");
        try {
            getConnection().createStatement().execute(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int insert(T object) {
        StringBuilder result = new StringBuilder("INSERT INTO " + getNameOfTable() + " VALUES (");
        getElements().forEach(e -> {
            result.append(getValue(e, object)).append(",");
        });
        result.deleteCharAt(result.length() - 1);
        result.append(");");
        try {
            return getConnection().createStatement().executeUpdate(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Updating by primary key
     *
     * @param object
     * @return
     */
    @Override
    public int update(T object) {
        StringBuilder result = new StringBuilder("UPDATE " + getNameOfTable() + " SET ");
        getElements().forEach(e -> {
            String temp = "";
            temp += "\"";
            temp += getName(e);
            temp += "\"";
            temp += " = ";
            temp += getValue(e, object);
            temp += ",";
            result.append(temp);
        });
        result.deleteCharAt(result.length() - 1);
        result.append(" WHERE ");
        result.append(getPrimaryKeyConditions(object));
        result.delete(result.length() - 4, result.length());
        result.append(";");
        try {
            return getConnection().createStatement().executeUpdate(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int insertOrUpdate(T object) {
        StringBuilder result = new StringBuilder("SELECT COUNT(*) FROM " + getNameOfTable() + " WHERE ");
        result.append(getPrimaryKeyConditions(object));
        result.delete(result.length() - 4, result.length());
        result.append(";");
        try {
            ResultSet resultSet = getConnection().createStatement().executeQuery(result.toString());
            resultSet.next();
            if (resultSet.getInt(1) != 0)
                return update(object);
            else
                return insert(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int delete(T object) {
        StringBuilder result = new StringBuilder("DELETE FROM "+getNameOfTable()+" WHERE ");
        result.append(getPrimaryKeyConditions(object));
        result.delete(result.length() - 4, result.length());
        result.append(";");
        try {
            return getConnection().createStatement().executeUpdate(result.toString().toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public ResultSet executeQuery(String query) {
        try {
            return getConnection().createStatement().executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void removeTable() {
        try {
            getConnection().createStatement().execute("DROP TABLE " + getNameOfTable() + ";");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void commit() {
        try {
            if (!getConnection().getAutoCommit())
                getConnection().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAutoCommit(boolean autoCommit) {
        try {
            getConnection().setAutoCommit(autoCommit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getAutoCommit() {
        try {
            return getConnection().getAutoCommit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getValue(AccessibleObject accessibleObject, T object) {
        accessibleObject.setAccessible(true);
        Object temp = null;
        if (accessibleObject instanceof Field) {
            try {
                temp = ((Field) accessibleObject).get(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (accessibleObject instanceof Method) {
            try {
                temp = ((Method) accessibleObject).invoke(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (temp instanceof String)
            return "'" + temp + "'";
        else if (Integer.class.isInstance(temp))
            return Integer.toString((int) temp);
        return null;
    }

    private String getName(AccessibleObject accessibleObject) {
        if (accessibleObject instanceof Field)
            return ((Field) accessibleObject).getName();
        else if (accessibleObject instanceof Method)
            return ((Method) accessibleObject).getName().substring(3);
        return null;
    }

    private String getType(AccessibleObject accessibleObject) {
        Object type = null;
        if (accessibleObject instanceof Field) {
            type = ((Field) accessibleObject).getType();
        } else if (accessibleObject instanceof Method) {
            type = ((Method) accessibleObject).getReturnType();
        }
        if (type == String.class) {
            return "varchar(80)";
        } else if (type == int.class)
            return "integer";
        return null;
    }

    private String getPrimaryKeyConditions(T object) {
        StringBuilder result = new StringBuilder();
        getPrimaryKeys().forEach(e -> {
            String temp = "";
            temp += "\"";
            temp += getName(e);
            temp += "\"";
            temp += " = ";
            temp += getValue(e, object);
            temp += " AND ";
            result.append(temp);
        });
        return result.toString();
    }
}
