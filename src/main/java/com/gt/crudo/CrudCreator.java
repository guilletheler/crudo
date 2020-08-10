package com.gt.crudo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

	Class<?> entity;

	@Getter
	@Setter
	String basePackage;

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

	public CrudCreator(Class<?> entity) {
		super();
		this.entity = entity;
	}

	public String getProjectBase() {

		String ret = projectSourceFolder.getAbsoluteFile() + File.separator + "src" + File.separator + "main"
				+ File.separator + "java";

		for (String subFolder : basePackage.split("\\.")) {
			ret += File.separator + subFolder;
		}

		return ret;
	}

	public String getListControllerFileName() {
		return getProjectBase() + File.separator + "controller" + File.separator + getListControllerClassName() + ".java";
	}

	public String getEditControllerFileName() {
		return getProjectBase() + File.separator + "controller" + File.separator + getEditControllerClassName() + ".java";
	}

	public void generateListController(PrintStream print) {

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("ListControllerTemplate.java");
		
		String template = null;
		
		try {
			template = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
		} catch (IOException e) {
			log.log(Level.SEVERE, "error leyendo ListControllerTemplate.java", e);
		} 
		
		if(template == null) {
			return;
		}
		
		print.println(replaceVars(template));
	}
	
	private String replaceVars(String in) {
		String ret = in;
		ret = ret.replace("[$[CLASS_NAME]$]", getClassName());
		ret = ret.replace("[$[VAR_NAME]$]", getVarName());
		ret = ret.replace("[$[PLURAL_CLASS_NAME]$]", getPluralClassName());
		ret = ret.replace("[$[PLURAL_VAR_NAME]$]", getPluralVarName());
		ret = ret.replace("[$[REPO_CLASS_NAME]$]", getRepoClassName());
		ret = ret.replace("[$[REPO_VAR_NAME]$]", getRepoVarName());
		ret = ret.replace("[$[SERVICE_CLASS_NAME]$]", getServiceClassName());
		ret = ret.replace("[$[SERVICE_VAR_NAME]$]", getServiceVarName());
		
		ret = ret.replace("[$[LIST_CONTROLLER_CLASS_NAME]$]", getListControllerClassName());
		ret = ret.replace("[$[LIST_CONTROLLER_VAR_NAME]$]", getListControllerVarName());
		
		ret = ret.replace("[$[EDIT_CONTROLLER_CLASS_NAME]$]", getEditControllerClassName());
		ret = ret.replace("[$[EDIT_CONTROLLER_VAR_NAME]$]", getListControllerVarName());
		
		ret = ret.replace("[$[EDIT_PAGE_NAME]$]", getEditPageName());
		ret = ret.replace("[$[LIST_PAGE_NAME]$]", getListPageName());
		
		ret = ret.replace("[$[BASE_PACKAGE]$]", getBasePackage());
		
		return ret;
	}

	public void generateEditController(PrintStream print) {

InputStream inputStream = getClass().getClassLoader().getResourceAsStream("EditControllerTemplate.java");
		
		String template = null;
		
		try {
			template = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
		} catch (IOException e) {
			log.log(Level.SEVERE, "error leyendo EditControllerTemplate.java", e);
		} 
		
		if(template == null) {
			return;
		}
		
		print.println(replaceVars(template));
	}

	public void generateListPage(PrintStream print) {
		String lowerName = getClassName().substring(0, 1).toLowerCase() + getClassName().substring(1);

		String upperName = getClassName().toUpperCase();

		String controllerName = lowerName + "ListController";

		String pluralName = getClassName();

		if (pluralName.endsWith("a") || pluralName.endsWith("e") || pluralName.endsWith("i") || pluralName.endsWith("o")
				|| pluralName.endsWith("u")) {
			pluralName += "s";
		} else {
			pluralName += "es";
		}

		String pluralNameLower = pluralName.substring(0, 1).toLowerCase() + pluralName.substring(1);

		String pluralNameUpper = pluralNameLower.toUpperCase();

		List<Method> getteres = new ArrayList<>();

		for (Method m : entity.getMethods()) {
			if (m.getName().startsWith("get")) {
				getteres.add(m);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
				+
				"<ui:composition xmlns=\"http://www.w3.org/1999/xhtml\"\n" +
				"	xmlns:ui=\"http://java.sun.com/jsf/facelets\"\n" +
				"	xmlns:h=\"http://java.sun.com/jsf/html\"\n" +
				"	xmlns:f=\"http://xmlns.jcp.org/jsf/core\"\n" +
				"	xmlns:p=\"http://primefaces.org/ui\"\n" +
				"	xmlns:adm=\"http://github.com/adminfaces\"\n" +
				"	template=\"#" + "{layoutMB.template}\">\n" +
				"\n" +
				"	<ui:define name=\"metadata\">\n" +
				"		<ui:param name=\"title\" value=\"Lista de " + pluralName + "\" />\n" +
				"		<!-- Automatic create breadCrumb and page title when param 'title' is provided. -->\n" +
				"		<style type=\"text/css\">\n" +
				".ui-datatable .ui-datatable-header {\n" +
				"	text-align: right !important;\n" +
				"}\n" +
				"</style>\n" +
				"	</ui:define>\n" +
				"\n" +
				"	<ui:define name=\"description\">\n" +
				"        Lista de " + pluralName + "\n" +
				"    </ui:define>\n" +
				"\n" +
				"	<ui:define name=\"body\">\n" +
				"		<h:form>\n" +
				"\n" +
				"			<p:panel styleClass=\"card box-solid box-primary\"\n" +
				"				class=\"box box-solid box-primary\" header=\"LISTADO DE " + pluralNameUpper + "\">\n" +
				"\n" +
				"				<p:dataTable id=\"datatable\"\n" +
				"					value=\"#" +"{" + controllerName + ".lazyDataModel}\"\n" +
				"					widgetVar=\"" + getClassName() + "Table\" var=\"item\"\n" +
				"					paginatorTemplate=\"{CurrentPageReport}  {FirstPageLink} {PreviousPageLink} {PageLinks} "
				+ "{NextPageLink} {LastPageLink} {RowsPerPageDropdown} {Exportar}\"\n" +
				"					currentPageReportTemplate=\"{totalRecords} [ {currentPage} / {totalPages} ]\"\n" +
				"					emptyMessage=\"No se encontraron elementos\" paginator=\"true\"\n" +
				"					rows=\"50\" lazy=\"true\" reflow=\"true\"\n" +
				"					tableStyle=\"width: auto; white-space: nowrap; font-size: smaller\"\n" +
				"					rowsPerPageTemplate=\"20,50,100,200,300,400,500,1000,5000,10000\"" +
				"					style=\"width:100%\">\n" +
				"\n" +
				"					<f:facet name=\"{Exportar}\">\n" +
				"						<p:outputLabel value=\"&nbsp;&nbsp;EXPORTAR: \" />\n" +
				"						<h:commandLink styleClass=\"tableColumnHeader\"\n" +
				"							title=\"Exportar a XLS\">\n" +
				"							<i class=\"fa fa-fw fa-file-excel-o\" />\n" +
				"							<p:dataExporter type=\"xls\" target=\"datatable\" fileName=\"Lista"
				+ getClassName()
				+ "_#" + "{tableExporterUtilsMB.fechaExporta()}\" postProcessor=\"#" + "{tableExporterUtilsMB.acomodaXls}\"/>\n" +
				"						</h:commandLink>\n" +
				"					</f:facet>\n" +
				"					<f:facet name=\"header\">\n" +
				"						<div align=\"left\">\n" +
				"							<div class=\"row\">\n" +
				"								<div class=\"col-sm-12 col-md-3\">\n" +
				"									<p:commandButton action=\"" + getClassName()
				+ "Edit?faces-redirect=true\"\n" +
				"										icon=\"fa fa-plus\" iconPos=\"right\"\n" +
				"										styleClass=\"btn-primary btn-block\" id=\"createButton\"\n" +
				"										value=\"NUEVO " + upperName + "\" />\n" +
				"								</div>\n" +
				"							</div>\n" +
				"						</div>\n" +
				"					</f:facet>\n" +
				"\n" +
				"					<p:column headerText=\"Acción\" exportable=\"false\" width=\"5%\" >\n" +
				"						<p:button title=\"Editar\" class=\"btn btn-success btn-xs\"\n" +
				"							icon=\"fa fa-fw fa fa-edit\" outcome=\"" + getClassName()
				+ "Edit.xhtml\">\n" +
				"							<f:param name=\"id\" value=\"#" + "{item.id}\" />\n" +
				"						</p:button>\n" +
				"						<p:commandButton\n" +
				"							actionListener=\"#" + "{" + controllerName + ".borrar" + getClassName()
				+ "(item)}\"\n" +
				"							update=\"datatable\" class=\"btn btn-danger btn-xs\"\n" +
				"							icon=\"fa fa-fw fa fa-trash-o\" title=\"Eliminar\">\n" +
				"							<p:confirm header=\"Eliminar " + getClassName() + "\"\n" +
				"								message=\"¿Está seguro de eliminar la entidad?\"\n" +
				"								icon=\"ui-icon-alert\" />\n" +
				"						</p:commandButton>\n" +
				"					</p:column>\n" +
				"\n");

		for (Method m : getteres) {

			if (m.getName().equals("getClass")) {
				continue;
			}

			if (m.getName().equals("getEtiqueta")) {
				continue;
			}

			if (m.getName().equals("getCodigoNombre")) {
				continue;
			}

			if (m.getName().equals("getObservaciones")) {
				continue;
			}

			if (List.class.isAssignableFrom(m.getReturnType())) {
				continue;
			}
			if (Set.class.isAssignableFrom(m.getReturnType())) {
				continue;
			}
			if (Map.class.isAssignableFrom(m.getReturnType())) {
				continue;
			}

			// sb.append("\n\n\tClase de tipo " + m.getReturnType() + "\n\n");

			String propName = m.getName().substring(3, 4).toLowerCase() + m.getName().substring(4);
			String propValue = propName;

			if (m.getReturnType().isEnum()) {
				propValue += ".descripcion";
			}

			if (haveGetNombreMethod(m.getReturnType())) {
				propValue += ".nombre";
			}

			sb.append("					<p:column headerText=\"" + m.getName().substring(3).toUpperCase()
					+ "\" sortBy=\"#" + "{item." + propValue + "}\"\n"
					+
					"						filterBy=\"#" + "{item." + propValue
					+ "}\" filterMatchMode=\"contains\" >\n" +
					"						<p:outputLabel value=\"#" + "{item." + propValue);

			sb.append("}\" ");

			if (java.util.Date.class.isAssignableFrom(m.getReturnType())) {
				sb.append(">\n" +
						"							<f:convertDateTime pattern=\"dd/MM/yyyy HH:mm\" />\n" +
						"						</p:outputLabel");
			} else {
				sb.append("/");
			}

			sb.append(">\n" +
					"					</p:column>\n");
		}

		sb.append("				</p:dataTable>\n" +
				"			</p:panel>\n" +
				"			<p:confirmDialog global=\"true\" showEffect=\"fade\" hideEffect=\"fade\"\n" +
				"				styleClass=\"box-solid box-danger\">\n" +
				"				<p:commandButton value=\"Si\" type=\"button\"\n" +
				"					styleClass=\"btn-material btn-primary ui-confirmdialog-yes\"\n" +
				"					icon=\"fa fa-check\" />\n" +
				"				<p:commandButton value=\"No\" type=\"button\"\n" +
				"					styleClass=\"btn-material btn-danger ui-confirmdialog-no\"\n" +
				"					icon=\"fa fa-close\" />\n" +
				"			</p:confirmDialog>\n" +
				"		</h:form>\n" +
				"	</ui:define>\n" +
				"</ui:composition>");

		print.println(sb.toString());
	}

	private boolean haveGetNombreMethod(Class<?> returnType) {
		try {
			returnType.getMethod("getNombre");
		} catch (NoSuchMethodException ex) {
			return false;
		}
		return true;
	}

	public void generateEditPage(PrintStream print) {

		String entityName = getClassName();

		String lowerName = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);

		String upperName = entityName.toUpperCase();

		String controllerName = lowerName + "EditController";

		String pluralName = entityName;

		if (pluralName.endsWith("a") || pluralName.endsWith("e") || pluralName.endsWith("i") || pluralName.endsWith("o")
				|| pluralName.endsWith("u")) {
			pluralName += "s";
		} else {
			pluralName += "es";
		}

		List<Method> getteres = new ArrayList<>();

		for (Method m : entity.getMethods()) {
			if (m.getName().startsWith("set")) {
				getteres.add(m);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
				+
				"<ui:composition xmlns=\"http://www.w3.org/1999/xhtml\"\n" +
				"	xmlns:ui=\"http://java.sun.com/jsf/facelets\"\n" +
				"	xmlns:h=\"http://java.sun.com/jsf/html\"\n" +
				"	xmlns:f=\"http://xmlns.jcp.org/jsf/core\"\n" +
				"	xmlns:p=\"http://primefaces.org/ui\"\n" +
				"	xmlns:pe=\"http://primefaces.org/ui/extensions\"\n" +
				"	xmlns:adm=\"http://github.com/adminfaces\"\n" +
				"	template=\"#" + "{layoutMB.template}\">\n" +
				"\n" +
				"	<ui:define name=\"title\">\n" +
				"        Edición de " + entityName + "\n" +
				"    </ui:define>\n" +
				"\n" +
				"	<ui:define name=\"body\">\n" +
				"		<f:metadata>\n" +
				"			<f:viewParam name=\"id\" value=\"#" + "{" + controllerName + ".id}\"\n" +
				"				converter=\"javax.faces.Long\" />\n" +
				"			<!-- use view action if you are in a Java EE 7 server: <f:viewAction action=\"#" + "{"
				+ controllerName + ".init()}\"/>-->\n"
				+
				"			<f:event type=\"preRenderView\"\n" +
				"				listener=\"#" + "{" + controllerName + ".init}\" />\n" +
				"		</f:metadata>\n" +
				"\n" +
				"		<adm:breadcrumb\n" +
				"			title=\"#" + "{(" + controllerName + ".id == null) ? 'Nuevo " + entityName + "' : '" + entityName
				+ " '.concat(" + controllerName + ".id)}\"\n"
				+
				"			link=\"Edit" + entityName + ".xhtml?id=#" + "{" + controllerName + ".id}\" />\n" +
				"		<h:form id=\"form\" prependId=\"false\">\n" +
				"			<p:focus rendered=\"#" + "{" + controllerName + ".id == null}\" />\n" +
				"\n" +
				"			<p:panel class=\"box box-solid box-primary\"\n" +
				"				header=\"CREANDO/EDITANDO UN " + upperName + "\"\n" +
				"				style=\"width: 100%; height: 100%\">\n" +
				"\n" +
				"				<div class=\"ui-g ui-fluid\">\n");

		for (Method m : getteres) {

			if (m.getName().equalsIgnoreCase("setUltimaModificacion")) {
				continue;
			}

			if (m.getName().equalsIgnoreCase("setId")) {
				continue;
			}

			if (List.class.isAssignableFrom(m.getParameterTypes()[0])) {
				continue;
			}
			if (Set.class.isAssignableFrom(m.getParameterTypes()[0])) {
				continue;
			}
			if (Map.class.isAssignableFrom(m.getParameterTypes()[0])) {
				continue;
			}

			sb.append(genFieldInput(m, controllerName, lowerName));
		}

		sb.append("			</div>\n" +
				"				<div class=\"ui-g ui-fluid\">\n" +
				"					<div class=\"ui-g-12 ui-md-2\">\n" +
				"							<p:commandButton action=\"#" + "{" + controllerName + ".save}\"\n" +
				"								value=\"Guardar\" update=\"@form\" styleClass=\"btn btn-success\" />\n"
				+
				"					</div>\n" +
				"					<div class=\"ui-g-12 ui-md-2\">\n" +
				"							<p:commandButton value=\"Cancelar\" action=\"" + pluralName
				+ "List?faces-redirect=true\"\n" +
				"								styleClass=\"btn btn-danger\"\n" +
				"								process=\"@this\" immediate=\"true\" />\n" +
				"					</div>\n" +
				"				</div>\n" +
				"			</p:panel>\n" +
				"		</h:form>\n" +
				"	</ui:define>\n" +
				"</ui:composition>");

		print.println(sb.toString());

	}

	private String genFieldInput(Method m, String controllerName, String varName) {

		String propName = m.getName().substring(3, 4).toLowerCase() + m.getName().substring(4);
		String propNameUpper = propName.toUpperCase();
		String refClassName = m.getParameterTypes()[0].getSimpleName().substring(0, 1).toLowerCase()
				+ m.getParameterTypes()[0].getSimpleName().substring(1);

		if (propName.equals("observaciones")) {
			return "						<div class=\"ui-g-12\">\n" +
					"							<p:outputLabel for=\"obs\" value=\"OBSERVACIONES\" />\n" +
					"						</div>\n" +
					"						<div class=\"ui-g-12\">\n" +
					"							<p:inputTextarea id=\"obs\" rows=\"3\" cols=\"20\"\n" +
					"								value=\"#" + "{" + controllerName + "." + varName
					+ ".observaciones}\"\n" +
					"								autoResize=\"true\" />\n" +
					"						</div>\n" +
					"\n";
		}

		String ret = "			<div class=\"ui-g-12 ui-md-6\">\n" +
				"				<div class=\"ui-sm-12 ui-g-12\">\n" +
				"					<p:outputLabel for=\"" + propName + "\" value=\"" + propNameUpper + "\" />\n" +
				"				</div>\n" +
				"				<div class=\"ui-sm-12 ui-g-11\">\n";

		if (java.util.Date.class.isAssignableFrom(m.getParameterTypes()[0])) {
			// Dateproductavity
			ret += "				<p:calendar id=\"" + propName + "\"\n" +
					"					value=\"#" + "{" + controllerName + "." + varName + "." + propName
					+ "}\" locale=\"es\"\n" +
					"					navigator=\"true\" pattern=\"dd/MM/yyyy HH:mm\" showOn=\"button\">\n" +
					"				</p:calendar>\n";
		} else if (java.lang.Integer.class.isAssignableFrom(m.getParameterTypes()[0])
				|| java.lang.Long.class.isAssignableFrom(m.getParameterTypes()[0])) {
			// Numero entero
			ret += "				<p:inputNumber id=\"" + propName + "\"\n" +
					"					value=\"#" + "{" + controllerName + "." + varName + "." + propName
					+ "}\" thousandSeparator=\"\" decimalPlaces=\"0\" />\n";
		} else if (java.lang.Double.class.isAssignableFrom(m.getParameterTypes()[0])
				|| java.lang.Float.class.isAssignableFrom(m.getParameterTypes()[0])) {
			// Numero real
			ret += "				<p:inputNumber id=\"" + propName + "\"\n" +
					"					value=\"#" + "{" + controllerName + "." + varName + "." + propName
					+ "}\" thousandSeparator=\"\" decimalPlaces=\"2\" />\n";
		} else if (java.lang.String.class.isAssignableFrom(m.getParameterTypes()[0])) {
			// String
			ret += "							<p:inputText id=\"" + propName + "\"\n" +
					"								value=\"#" + "{" + controllerName + "." + varName + "." + propName
					+ "}\" />\n";
		} else if (java.lang.Boolean.class.isAssignableFrom(m.getParameterTypes()[0])) {
			ret += "				<p:selectBooleanButton id=\"" + propName + "\"\n" +
					"					value=\"#" + "{" + controllerName + "." + varName + "." + propName + "}\"\n" +
					"					onLabel=\"SI\" offLabel=\"NO\" onIcon=\"fa fa-check\"\n" +
					"					offIcon=\"fa fa-times\" />\n";
		} else {
			// Referencia a otra clase
			ret += "				<p:selectOneMenu id=\"" + propName + "\"\n" +
					"					value=\"#" + "{" + controllerName + "." + varName + "." + propName + "}\"\n" +
					"					style=\"width:100%\" filter=\"true\" filterMatchMode=\"contains\"\n" +
					"					converter=\"entityConverter\" styleClass=\"selectOneMenu\">\n" +
					"					<f:selectItem itemLabel=\"Seleccione una opción\"\n" +
					"						itemValue=\"#" + "{null}\" />\n";

			String valores;
			String label;

			if (m.getParameterTypes()[0].isEnum()) {
				valores = "enumsMB." + refClassName;
				label = "itemE.descripcion";
			} else {
				valores = refClassName + "Repo.findAll()";
				label = "itemE.etiqueta";
			}

			ret += "					<f:selectItems\n" +
					"						value=\"#" + "{" + valores + "}\"\n" +
					"						var=\"itemE\" itemLabel=\"#" + "{" + label + "}\"\n" +
					"						itemValue=\"#" + "{itemE}\" />\n" +
					"				</p:selectOneMenu>\n";
		}

		ret += "<p:tooltip for=\"" + propName + "\" value=\"" + propName + "\" position=\"top\" />";

		ret += "			</div>\n" +
				"		</div>\n";

		return ret;
	}

	public String getVarName() {
		if (varName == null) {
			varName = entity.getSimpleName().substring(0, 1).toLowerCase() + entity.getSimpleName().substring(1);
		}
		return varName;
	}

	public String getClassName() {
		return entity.getSimpleName();
	}

	public String getPluralVarName() {
		if (pluralVarName == null) {
			pluralVarName = getPluralClassName().substring(0,1).toLowerCase() + getPluralClassName().substring(1);
		}

		return pluralVarName;
	}

	public String getPluralClassName() {
		if (pluralClassName == null) {
			pluralClassName = getClassName();

			if (pluralClassName.endsWith("a") || pluralClassName.endsWith("e") || pluralClassName.endsWith("i")
					|| pluralClassName.endsWith("o")
					|| pluralClassName.endsWith("u")) {
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
		return getListControllerClassName().substring(0,1).toLowerCase() + getListControllerClassName().substring(1);
	}
	
	public String getEditControllerClassName() {
		return getClassName() + "EditController";
	}
	
	public String getEditControllerVarName() {
		return getEditControllerClassName().substring(0,1).toLowerCase() + getEditControllerClassName().substring(1);
	}
	
	public String getRepoClassName() {
		return getClassName() + "repo";
	}
	
	public String getRepoVarName() {
		return getRepoClassName().substring(0,1).toLowerCase() + getRepoClassName().substring(1);
	}
	
	public String getServiceClassName() {
		return getClassName() + "Service";
	}
	
	public String getServiceVarName() {
		return getServiceClassName().substring(0,1).toLowerCase() + getServiceClassName().substring(1);
	}
	
	public String getListPageName() {
		return getPluralClassName() + "List";
	}
	
	public String getEditPageName() {
		return getClassName() + "Edit";
	}
}
