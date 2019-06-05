package com.shanjin.manager.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TreeNode implements Comparable<Object>, Serializable {

	private static final long serialVersionUID = 6373410249483047284L;
	private Long id = 0L;
	private String text;
	private Long parentId;
	private String idPath;
	private String namePath;
	private String description;
	private Boolean leaf = false;
	private String urlPath;
	private int rank;
	private List<TreeNode> children = new ArrayList<TreeNode>();
	private Boolean expanded = true;
	private Boolean checked = false; // 模板使用，判断是否选择模板

	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getLeaf() {
		return leaf;
	}

	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public Boolean getExpanded() {
		return expanded;
	}

	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getIdPath() {
		return idPath;
	}

	public void setIdPath(String idPath) {
		this.idPath = idPath;
	}

	public List<TreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TreeNode() {
		expanded = false;
		children = new ArrayList<TreeNode>();
	}

	public String getNamePath() {
		return namePath;
	}

	public void setNamePath(String namePath) {
		this.namePath = namePath;
	}

	public String getUrlPath() {
		return urlPath;
	}

	public void setUrlPath(String urlPath) {
		this.urlPath = urlPath;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int compareTo(Object o) {
		return this.getText().compareTo(((TreeNode) o).getText());
	}

	@Override
	public String toString() {
		return "TreeNode [id=" + id + ", text=" + text + ", parentId="
				+ parentId + ", idPath=" + idPath + ", namePath=" + namePath
				+ ", description=" + description + ", leaf=" + leaf
				+ ", urlPath=" + urlPath + ", rank=" + rank + ", children="
				+ children + ", expanded=" + expanded + ", checked=" + checked
				+ "]";
	}

}
