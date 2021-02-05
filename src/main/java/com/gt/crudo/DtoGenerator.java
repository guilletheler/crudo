package com.gt.crudo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DtoGenerator {

	Connection connection;
	
	String sqlQuery;
	
	String packageName;
	
	String className;
	
	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String generateDto() {
		
		if(!className.endsWith("Dto")) {
			className += "Dto";
		}
		
		try {
			if(!connection.isValid(100)) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Conexión inválida");
				return null;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		StringBuilder sb = new StringBuilder();
		
		createClass(sb);

		if(!sqlQuery.toUpperCase().contains("LIMIT ")) {
			sqlQuery += "\nLIMIT 1";
		}
		
		ResultSet rs;
		try {
//			Logger.getLogger(getClass().getName()).log(Level.INFO, "Consultando " + sqlQuery);
			rs = connection.prepareStatement(sqlQuery).executeQuery();
			rs.next();
			createFields(rs.getMetaData(), sb);
			setFields(rs.getMetaData(), sb);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		sb.append("\n}\n");
		
		return sb.toString();
	}

	private void setFields(ResultSetMetaData metaData, StringBuilder sb) {
		sb.append("\tpublic void setFields(Object[] fieldValues) {\n");
		try {
			
			for(int i = 1; i <= metaData.getColumnCount(); i++) {
				
				sb.append("\t\tthis.");
				sb.append(metaData.getColumnName(i).toLowerCase());
				sb.append(" = (");
				try {
					sb.append(Class.forName(metaData.getColumnClassName(i)).getSimpleName());
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sb.append(") fieldValues[");
				sb.append((i - 1) + "");
				sb.append("];\n");
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb.append("\t}\n\n");
		
		sb.append("\tpublic void setFields(ResultSet rs) {\n");
		try {
			
			sb.append("\t\ttry {\n");
			for(int i = 1; i <= metaData.getColumnCount(); i++) {
				
				sb.append("\t\t\tthis.");
				sb.append(metaData.getColumnName(i).toLowerCase());
				sb.append(" = (");
				try {
					sb.append(Class.forName(metaData.getColumnClassName(i)).getSimpleName());
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sb.append(") rs.getObject(");
				sb.append(i + "");
				sb.append(");\n");
			}
			sb.append("\t\t} catch(SQLException ex) {\n");
			sb.append("\t\t\tLogger.getLogger(getClass().getName()).log(Level.SEVERE, \"Error al setear campos del dto" + className + "\");\n");
			sb.append("\t\t}\n");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb.append("\t}\n");
		
		
	}

	private void createFields(ResultSetMetaData metaData, StringBuilder sb) {
		try {
			for(int i = 1; i <= metaData.getColumnCount(); i++) {
				createField(sb, metaData.getColumnName(i), metaData.getColumnClassName(i));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createField(StringBuilder sb, String columnName, String columnClassName) {
		try {
			sb.append("\tprivate ");
			sb.append(Class.forName(columnClassName).getSimpleName());
			sb.append(" ");
			sb.append(columnName.toLowerCase());
			sb.append(";\n\n");
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void createClass(StringBuilder sb) {
		sb.append("package ");
		sb.append(packageName);
		sb.append(";\n\n");
		sb.append("import lombok.AllArgsConstructor;\n");
		sb.append("import lombok.Data;\n");
		sb.append("\n");
		sb.append("import java.sql.ResultSet;\n");
		sb.append("import java.sql.SQLException;\n");
		sb.append("import java.util.Date;\n");
		sb.append("\n");
		sb.append("import java.util.logging.Level;\n");
		sb.append("import java.util.logging.Logger;\n");
		sb.append("\n");
		sb.append("@Data\n");
		sb.append("@AllArgsConstructor\n");
		sb.append("public class");
		sb.append(" ");
		sb.append(className);
		sb.append(" {\n\n");
		sb.append("\tpublic static final String SQL_QUERY = \"");
		sb.append(this.sqlQuery.replace("\r", "").replace("\n", "\\n\"\n\t\t+ \""));
		sb.append("\";\n\n");
	}
}
