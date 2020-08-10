package [$[BASE_PACKAGE]$].controller.sysadmin;

import static [$[BASE_PACKAGE]$].util.Utils.addDetailMessage;
import static com.github.adminfaces.template.util.Assert.has;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.util.Faces;

import [$[BASE_PACKAGE]$].model.[$[VAR_NAME]$]s.UserRol;
import [$[BASE_PACKAGE]$].model.[$[VAR_NAME]$]s.[$[CLASS_NAME]$];
import [$[BASE_PACKAGE]$].repo.sysadmin.[$[CLASS_NAME]$]Repo;
import [$[BASE_PACKAGE]$].util.Utils;
import com.github.adminfaces.template.exception.AccessDeniedException;

import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class [$[EDIT_CONTROLLER_CLASS_NAME]$] implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	[$[REPO_CLASS_NAME]$] [$[REPO_VAR_NAME]$];

	@Getter
	@Setter
	Integer id;

	@Getter
	@Setter
	[$[CLASS_NAME]$] [$[VAR_NAME]$];

	public void init() {
		if (Faces.isAjaxRequest()) {
			return;
		}
		if (has(id)) {
			[$[VAR_NAME]$] = [$[VAR_NAME]$]Repo.findById(id).orElse(new [$[CLASS_NAME]$]());
		} else {
			[$[VAR_NAME]$] = new [$[CLASS_NAME]$]();
		}
		if ([$[VAR_NAME]$].getId() == null) {
			setDefaultValues([$[VAR_NAME]$]);
		}
		cargarParametros();
	}
	
	private void setDefaultValues([$[CLASS_NAME]$] [$[VAR_NAME]$]) {
		
	}

	public String save() {
		String msg = "[$[CLASS_NAME]$] " + [$[VAR_NAME]$].getNombre();

		[$[REPO_VAR_NAME]$].save([$[VAR_NAME]$]);

		if ([$[VAR_NAME]$].getId() == null) {
			msg += " creado exitosamente";
		} else {
			msg += " editado exitosamente";
		}
		addDetailMessage(msg);

		return "[$[LIST_PAGE_NAME]$]?faces-redirect=true";
	}

	public void clear() {
		[$[VAR_NAME]$] = new [$[CLASS_NAME]$]();
		id = null;
	}

	public boolean isNew() {
		return [$[VAR_NAME]$] == null || [$[VAR_NAME]$].getId() == null;
	}

	public void setPassword(String password) {
		this.password = password;
		if (password != null && !password.isEmpty()) {
			[$[VAR_NAME]$].setAndEncryptPassword(password);
		}
	}


}
