package com.gt.crudo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Log
public class CrudCreator {

	Class<?> entityClass;

	@Setter
	String basePage;

	@Setter
	String basePackage;

	@Setter
	String subPackage;

	@Getter
	@Setter
	String roleRequired;

	@Getter
	@Setter
	String menuName;

	@Getter
	@Setter
	File projectSourceFolder;

	String varName;
	String pluralVarName;
	String pluralClassName;

	String listControllerVarName;
	String listControllerClassName;
	String editControllerVarName;
	String editControllerClassName;

	public CrudCreator(Class<?> entityClass) {
		super();
		this.entityClass = entityClass;
	}

	public String getProjectBase() {

		String ret = projectSourceFolder.getAbsoluteFile() + File.separator + "src" + File.separator + "main"
				+ File.separator + "java";

		for (String subFolder : getBasePackage().split("\\.")) {
			ret += File.separator + subFolder;
		}

		return ret;
	}

	public String getListControllerFileName() {
		return getJavaFileName("controller", getListControllerClassName());
	}

	public void generateListController(PrintStream print) {

		String template = loadTemplate("ListControllerTemplate.java");

		if (template == null) {
			return;
		}

		String ret = replaceVars(template);

		ret = ret.replace("[$[FILENAME]$]", getListControllerFileName());

		print.println(ret);
	}

	public String getEditControllerFileName() {
		return getJavaFileName("controller", getEditControllerClassName());
	}

	public void generateEditController(PrintStream print) {

		String template = loadTemplate("EditControllerTemplate.java");

		if (template == null) {
			return;
		}

		String ret = replaceVars(template);

		ret = ret.replace("[$[FILENAME]$]", getEditControllerFileName());

		String codigoDefault = "";

		if (getTieneCodigo()) {
			codigoDefault = replaceVars(loadTemplate("CodigoDefaultTemplate.java"));
		}

		ret = ret.replace("[$[CODIGO_DEFAULT]$]", codigoDefault);

		print.println(ret);
	}

	public String getRepoFileName() {
		return getJavaFileName("repo", getRepoClassName());
	}

	public void generateRepo(PrintStream print) {

		String template = loadTemplate("RepoTemplate.java");

		if (template == null) {
			return;
		}

		String ret = replaceVars(template);

		ret = ret.replace("[$[FILENAME]$]", getRepoFileName());

		String codigoQuery = "";

		if (getTieneCodigo()) {
			codigoQuery = replaceVars(loadTemplate("CodigoQueryTemplate.java"));
		}

		ret = ret.replace("[$[CODIGO_QUERY]$]", codigoQuery);

		print.println(ret);
	}

	private boolean getTieneCodigo() {
		boolean esta = false;

		try {
			entityClass.getMethod("getCodigo");
//            entityClass.getMethod("setCodigo");
			esta = true;
		} catch (NoSuchMethodException | SecurityException e) {

		}
		try {
			entityClass.getMethod("getCodigoNombre");
//            entityClass.getMethod("setCodigo");
			esta = true;
		} catch (NoSuchMethodException | SecurityException e) {

		}
		return esta;
	}

	public String getServiceFileName() {
		return getJavaFileName("service", getServiceClassName());
	}

	public void generateService(PrintStream print) {

		String template = loadTemplate("ServiceTemplate.java");

		if (template == null) {
			return;
		}

		String ret = replaceVars(template);

		ret = ret.replace("[$[FILENAME]$]", getServiceFileName());

		print.println(ret);
	}

	public String getListPageFileName(String subFolder) {
		return getXhtmlFileName(subFolder, getListPageName());
	}

	public void generateListPage(PrintStream print) {

		String template = this.loadTemplate("ListPageTemplate.xhtml");

		if (template == null) {
			return;
		}

		String columnTemplate = this.loadTemplate("DatatableColumnTemplate.xhtml");

		StringBuilder allColumns = new StringBuilder();

		for (Method m : entityClass.getMethods()) {
			if (notListableMethod(m)) {
				continue;
			}
			if (allColumns.length() > 0) {
				allColumns.append("\n");
			}
			allColumns.append(replaceColumnsVars(columnTemplate,
					m.getName().substring(3, 4).toLowerCase() + m.getName().substring(4), m.getReturnType()));

		}

		template = template.replace("[$[DATATABLE_COLUMNS]$]", allColumns.toString());

		print.println(replaceVars(template));
	}

	public void generateMenuDefinition(PrintStream print, String pagesSubFolder) {

		String ret = this.loadTemplate("MenuTemplate.xhtml");

		ret = ret.replace("[$[ROLE_REQUIRED]$]", getRoleRequired() == null ? "ROL_REQUIRED" : getRoleRequired());
		ret = ret.replace("[$[MENU_NAME]$]", getMenuName() == null ? "MENU" : getMenuName());
		ret = ret.replace("[$[PAGE_LIST_PATH]$]", getPagePath(pagesSubFolder, getListPageName()).replace("\\", "/"));
		ret = ret.replace("[$[OPTION_MENU_NAME]$]", getClassName());

		print.println(ret);
	}

	public void generateEnumsMB(PrintStream print) {
		print.println();
		print.println();
		print.println("Agregar a EnumMB");
		print.println();

		for (Method m : entityClass.getMethods()) {
			if (m.getName().startsWith("get") && Enum.class.isAssignableFrom(m.getReturnType())) {
				print.println("public " + m.getReturnType().getSimpleName() + "[] get"
						+ m.getReturnType().getSimpleName() + "() {\r\n" + "		return "
						+ m.getReturnType().getSimpleName() + ".values();\r\n" + "	}");
			}
		}
		print.println();
		print.println("Fin agregar a EnumMB");
		print.println();
	}

	public String getConverterFileName() {
		return getJavaFileName("infra" + File.separator + "converter", getConverterClassName());
	}

	public void generateConverter(PrintStream print) {
		String template = loadTemplate("ConverterTemplate.java");

		if (template == null) {
			return;
		}

		String ret = replaceVars(template);

		ret = ret.replace("[$[FILENAME]$]", getConverterFileName());

		print.println(ret);
	}

	public String getEditPageFileName(String subFolder) {
		return getXhtmlFileName(subFolder, getEditPageName());
	}

	public void generateEditPage(PrintStream print) {

		String template = this.loadTemplate("EditPageTemplate.xhtml");

		if (template == null) {
			return;
		}

		StringBuilder allColumns = new StringBuilder();

		for (Method m : entityClass.getMethods()) {

			String fieldName = getEditableFieldName(m);

			if (fieldName == null) {
				continue;
			}

			if (allColumns.length() > 0) {
				allColumns.append("\n");
			}

			if (fieldName.startsWith("_")) {
				// read only
				allColumns.append(buildReadOnlyField(fieldName.substring(1), m.getReturnType()));
			} else {
				allColumns.append(buildEditField(fieldName, m.getReturnType()));
			}

		}

		template = template.replace("[$[EDIT_FIELDS]$]", allColumns.toString());

		print.println(replaceVars(template));
	}

	private String replaceVars(String in) {
		String ret = in;

		// Ejemplo clase Usuario
		// Usuario
		ret = ret.replace("[$[CLASS_NAME]$]", getClassName());

		// usuario
		ret = ret.replace("[$[VAR_NAME]$]", getVarName());

		// USUARIO
		ret = ret.replace("[$[UPPER_NAME]$]", getVarName().toUpperCase());

		// Usuarios
		ret = ret.replace("[$[PLURAL_CLASS_NAME]$]", getPluralClassName());

		// usuarios
		ret = ret.replace("[$[PLURAL_VAR_NAME]$]", getPluralVarName());

		// USUARIOS
		ret = ret.replace("[$[PLURAL_UPPER_NAME]$]", getPluralVarName().toUpperCase());

		// UsuarioRepo
		ret = ret.replace("[$[REPO_CLASS_NAME]$]", getRepoClassName());

		// usuarioRepo
		ret = ret.replace("[$[REPO_VAR_NAME]$]", getRepoVarName());

		// UsuarioService
		ret = ret.replace("[$[SERVICE_CLASS_NAME]$]", getServiceClassName());

		// usuarioService
		ret = ret.replace("[$[SERVICE_VAR_NAME]$]", getServiceVarName());

		// UsuarioListController
		ret = ret.replace("[$[LIST_CONTROLLER_CLASS_NAME]$]", getListControllerClassName());

		// usuarioListController
		ret = ret.replace("[$[LIST_CONTROLLER_VAR_NAME]$]", getListControllerVarName());

		// UsuarioEditController
		ret = ret.replace("[$[EDIT_CONTROLLER_CLASS_NAME]$]", getEditControllerClassName());

		// UsuarioEditController
		ret = ret.replace("[$[EDIT_CONTROLLER_VAR_NAME]$]", getEditControllerVarName());

		// UsuarioEdit
		ret = ret.replace("[$[EDIT_PAGE_NAME]$]", getEditPageName());
		// UsuariosList
		ret = ret.replace("[$[LIST_PAGE_NAME]$]", getListPageName());

		ret = ret.replace("[$[BASE_PACKAGE]$]", getBasePackage());

		ret = ret.replace("[$[SUB_PACKAGE]$]", getSubPackage());

		ret = ret.replace("[$[ID_METHOD]$]", getLabelMethod());

		return ret;
	}

	private String getLabelMethod() {
		try {
			entityClass.getMethod("getNombre");
			return "getNombre()";
		} catch (NoSuchMethodException | SecurityException e) {

		}
		return "getId()";
	}

	private String replaceColumnsVars(String columnTemplate, String fieldName, Class<?> clazz) {
		return replaceColumnsVars(columnTemplate, fieldName, clazz, "", "");
	}

	private String replaceColumnsVars(String columnTemplate, String fieldName, Class<?> clazz, String refList,
			String refVar) {
		String ret = columnTemplate;

		ret = ret.replace("[$[UPPER_FIELD_NAME]$]", fieldName.toUpperCase());
		ret = ret.replace("[$[FIELD_VAR_NAME]$]", fieldName);

		ret = ret.replace("[$[REF_LIST]$]", refList);
		ret = ret.replace("[$[REF_VAR]$]", refVar);

		if (fieldName.equalsIgnoreCase("id")) {
			ret = ret.replace("[$[ID_ADMIN_VISIBLE]$]", "visible=\"#{sessionMB.inRole('SYSADMIN')}\"");
			ret = ret.replace("[$[DIV_ADMIN_VISIBLE]$]", "style=\"#{(sessionMB.inRole('SYSADMIN'))?'':'display:none'}\"");
		} else {
			ret = ret.replace("[$[ID_ADMIN_VISIBLE]$]", "");
			ret = ret.replace("[$[DIV_ADMIN_VISIBLE]$]", "");
		}

		String converter = "/";
		String decimalPlaces = "2";

		switch (clazz.getSimpleName()) {
		case "int":
		case "Integer":
		case "long":
		case "Long":
			decimalPlaces = "0";
			break;
		case "float":
		case "Float":
		case "double":
		case "Double":
			converter = "><f:convertNumber pattern=\"#0.00\" /></p:outputLabel";
			break;
		case "Date":
			converter = "><f:convertDateTime timeZone=\"America/Buenos_Aires\" type=\"date\" pattern=\"dd/MM/yyyy\"/></p:outputLabel";
			break;
		}

		ret = ret.replace("[$[DECIMAL_PLACES]$]", decimalPlaces);
		ret = ret.replace("[$[F:CONVERTER]$]", converter);
		return ret;
	}

	private boolean notListableMethod(Method m) {

		if (m.isBridge()) {
			return true;
		}

		if (m.getName().startsWith("get")) {
			try {
				entityClass.getMethod("set" + m.getName().substring(3), m.getReturnType());

				// Si es coleccion
				if (List.class.isAssignableFrom(m.getReturnType()) || Set.class.isAssignableFrom(m.getReturnType())
						|| Map.class.isAssignableFrom(m.getReturnType())
						|| Collection.class.isAssignableFrom(m.getReturnType())) {
					return true;
				}

				// el
				if (m.getName().equals("getObservaciones")) {
					return true;
				}

				return false;
			} catch (NoSuchMethodException | SecurityException e) {
				// el metodo no existe
			}

		}

		/**
		 * Campo boolean
		 */
		if (m.getName().startsWith("is")) {
			try {
				entityClass.getMethod("set" + m.getName().substring(3), m.getReturnType());

				return false;
			} catch (NoSuchMethodException | SecurityException e) {
				// el metodo no existe
			}

		}

		return true;
	}

	private String getEditableFieldName(Method m) {

		if (m.isBridge()) {
			return null;
		}

		if (m.getName().equals("getId")) {
			return "_id";
		}

		if (m.getName().startsWith("get")) {
			try {
				entityClass.getMethod("set" + m.getName().substring(3), m.getReturnType());

				if (Collections.class.isAssignableFrom(m.getReturnType())
						|| Iterable.class.isAssignableFrom(m.getReturnType())
						|| Map.class.isAssignableFrom(m.getReturnType())) {
					return null;
				}

				return m.getName().substring(3, 4).toLowerCase() + m.getName().substring(4);
			} catch (NoSuchMethodException | SecurityException e) {
				// el metodo no existe
			}

		}

		/**
		 * Campo boolean
		 */
		if (m.getName().startsWith("is")) {
			try {
				entityClass.getMethod("set" + m.getName().substring(3), m.getReturnType());

				return m.getName().substring(2, 3).toLowerCase() + m.getName().substring(3);
			} catch (NoSuchMethodException | SecurityException e) {
				// el metodo no existe
			}

		}

		return null;
	}

	private String loadTemplate(String templateFileName) {

		templateFileName += ".template";

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(templateFileName);

		String template = null;

		try {
			template = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
		} catch (IOException e) {
			log.log(Level.SEVERE, templateFileName, e);
		}

		return template;
	}

	private String buildReadOnlyField(String fieldName, Class<?> returnType) {
		String template = this.loadTemplate("ReadOnlyFieldTemplate.xhtml");

		return replaceColumnsVars(template, fieldName, returnType);
	}

	private String buildEditField(String fieldName, Class<?> returnType) {
		switch (returnType.getSimpleName()) {
		case "Date":
			return buildEdit("EditCalendarTemplate.xhtml", fieldName, Date.class);
		case "boolean":
		case "Boolean":
			return buildEdit("EditToggleTemplate.xhtml", fieldName, Boolean.class);
		case "int":
		case "Integer":
		case "long":
		case "Long":
			return buildEdit("EditNumberTemplate.xhtml", fieldName, Integer.class);
		case "float":
		case "Float":
		case "double":
		case "Double":
			return buildEdit("EditNumberTemplate.xhtml", fieldName, Double.class);
		case "String":
			if (fieldName.equals("observaciones")) {
				return buildEdit("EditObservacionesTemplate.xhtml", "observaciones", String.class);
			}
			return buildEdit("EditTextFieldTemplate.xhtml", fieldName, String.class);
		}

		if (Enum.class.isAssignableFrom(returnType)) {
			// es un enum;
			String enumDescription = "name()";
			try {
				returnType.getMethod("getDescripcion");
				enumDescription = "descripcion";
			} catch (NoSuchMethodException | SecurityException e) {
			}
			return buildEditCombo(fieldName, "enumsMB." + returnType.getSimpleName().substring(0, 1).toLowerCase()
					+ returnType.getSimpleName().substring(1), enumDescription);
		}

		// es un enum;
		return buildEditCombo(fieldName, returnType.getSimpleName().substring(0, 1).toLowerCase()
				+ returnType.getSimpleName().substring(1) + "Repo.findAll()", "etiqueta");

	}

	private String buildEdit(String templateName, String fieldName, Class<?> retClass) {
		String template = this.loadTemplate(templateName);

		return replaceColumnsVars(template, fieldName, retClass);
	}

	private String buildEditCombo(String fieldName, String refList, String refVar) {
		String template = this.loadTemplate("EditComboTemplate.xhtml");

		return replaceColumnsVars(template, fieldName, Date.class, refList, refVar);
	}

	public String getVarName() {
		if (varName == null) {
			varName = entityClass.getSimpleName().substring(0, 1).toLowerCase()
					+ entityClass.getSimpleName().substring(1);
		}
		return varName;
	}

	public String getClassName() {
		return entityClass.getSimpleName();
	}

	public String getPluralVarName() {
		if (pluralVarName == null) {
			pluralVarName = getPluralClassName().substring(0, 1).toLowerCase() + getPluralClassName().substring(1);
		}

		return pluralVarName;
	}

	public String getPluralClassName() {
		if (pluralClassName == null) {
			pluralClassName = getClassName();

			if (pluralClassName.endsWith("a") || pluralClassName.endsWith("e") || pluralClassName.endsWith("i")
					|| pluralClassName.endsWith("o") || pluralClassName.endsWith("u")) {
				pluralClassName += "s";
			} else {
				pluralClassName += "es";
			}
		}
		return pluralClassName;
	}

	public String getListControllerClassName() {
		return getClassName() + "ListController";
	}

	public String getListControllerVarName() {
		return getListControllerClassName().substring(0, 1).toLowerCase() + getListControllerClassName().substring(1);
	}

	public String getEditControllerClassName() {
		return getClassName() + "EditController";
	}

	public String getEditControllerVarName() {
		return getEditControllerClassName().substring(0, 1).toLowerCase() + getEditControllerClassName().substring(1);
	}

	public String getRepoClassName() {
		return getClassName() + "Repo";
	}

	public String getRepoVarName() {
		return getRepoClassName().substring(0, 1).toLowerCase() + getRepoClassName().substring(1);
	}

	public String getServiceClassName() {
		return getClassName() + "Service";
	}

	public String getConverterClassName() {
		return getClassName() + "Converter";
	}

	public String getServiceVarName() {
		return getServiceClassName().substring(0, 1).toLowerCase() + getServiceClassName().substring(1);
	}

	public String getListPageName() {
		return getPluralClassName() + "List";
	}

	public String getEditPageName() {
		return getClassName() + "Edit";
	}

	public String getBasePackage() {
		if (basePackage == null) {
			int pos = entityClass.getName().indexOf(".model");
			basePackage = entityClass.getName().substring(0, pos);
		}
		return basePackage;
	}

	public String getSubPackage() {
		if (subPackage == null) {
			int pos = entityClass.getName().indexOf(".model");
			subPackage = entityClass.getName().substring(pos + 6,
					entityClass.getName().length() - (entityClass.getSimpleName().length() + 1));
		}
		return subPackage;
	}

	private String getJavaFileName(String modulo, String className) {
		String fileName = getProjectSourceFolder().getAbsolutePath() + File.separator + "src" + File.separator + "main"
				+ File.separator + "java" + File.separator + getBasePackage().replace(".", File.separator)
				+ File.separator + modulo + getSubPackage().replace(".", File.separator) + File.separator + className
				+ ".java";

		return fileName.replace(File.separator + "." + File.separator, File.separator);
	}

	private String getXhtmlFileName(String subFolder, String pageName) {

		String pagePath = getPagePath(subFolder, pageName);

		String fileName = getProjectSourceFolder().getAbsolutePath() + File.separator + "src" + File.separator + "main"
				+ File.separator + "webapp" + pagePath + ".xhtml";

		String ret = fileName.replace(File.separator + "." + File.separator, File.separator);

		return ret;
	}

	private String getPagePath(String subFolder, String pageName) {
		return File.separator + "pages" + File.separator + subFolder.replace(".", File.separator) + File.separator
				+ pageName;
	}

}
