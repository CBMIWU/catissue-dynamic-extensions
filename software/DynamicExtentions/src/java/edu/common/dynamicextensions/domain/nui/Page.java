package edu.common.dynamicextensions.domain.nui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page implements Serializable {
	private static final long serialVersionUID = 5552295077941247997L;

	private Long id;
	
	private List<Control> controls = new ArrayList<Control>();

	public Page() {
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public boolean isEmptyPage() {
		return controls.isEmpty();
	}

	public List<Control> getControls() {
		return controls;
	}

	public void setControls(List<Control> controls) {
		this.controls = controls;
	}
	
	public void addControl(Control ctrl) {
		this.controls.add(ctrl);
	}
}
