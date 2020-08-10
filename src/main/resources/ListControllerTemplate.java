package [$[BASE_PACKAGE]$].controller;

import static [$[BASE_PACKAGE]$].util.Utils.addDetailMessage;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.LazyDataModel;

import [$[BASE_PACKAGE]$].infra.model.GenericLazyDataModel;
import [$[BASE_PACKAGE]$].model.[$[VAR_NAME]$]s.[$[CLASS_NAME]$];
import [$[BASE_PACKAGE]$].repo.sysadmin.[$[REPO_CLASS_NAME]$];
import [$[BASE_PACKAGE]$].service.sysadmin.[$[SERVICE_CLASS_NAME]$];

import lombok.Getter;

@Named
@ViewScoped
public class [$[LIST_CONTROLLER_CLASS_NAME]$] implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	[$[REPO_CLASS_NAME]$] [$[REPO_VAR_NAME]$];
	
	@Inject
	[$[SERVICE_CLASS_NAME]$] [$[SERVICE_VAR_NAME]$];

	@Getter
	LazyDataModel<[$[CLASS_NAME]$]> lazyDataModel;
	
	@PostConstruct
	private void init() {
		lazyDataModel = new GenericLazyDataModel<>([$[SERVICE_VAR_NAME]$]);
	}

	public void borrar[$[CLASS_NAME]$]([$[CLASS_NAME]$] [$[VAR_NAME]$]) {
		[$[REPO_VAR_NAME]$].delete([$[VAR_NAME]$]);
		addDetailMessage("[$[CLASS_NAME]$] " + [$[VAR_NAME]$].getNombre() + " borrado exitosamente");
	}
}
