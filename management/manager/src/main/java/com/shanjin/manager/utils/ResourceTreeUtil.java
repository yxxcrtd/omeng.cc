package com.shanjin.manager.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.shanjin.manager.Bean.SystemGroup;
import com.shanjin.manager.Bean.SystemResource;
import com.shanjin.manager.Bean.SystemRole;

/**
 * 生成资源菜单树
 * @author Huang yulai
 *
 */
public class ResourceTreeUtil {

	/**
	 * 资源树
	 * @param allResources
	 * @param resMap
	 * @return
	 */
		public static List<TreeNode> resourceTreeJson(List<SystemResource> allResources,Map<String,String> resMap){
			List<TreeNode> root = new ArrayList<TreeNode>();
			TreeNode tree = new TreeNode();
			tree.setId(0L);
			tree.setExpanded(true);
			tree.setLeaf(false);
			tree.setText("系统资源树");
			tree.setDescription("系统资源树");
			boolean rootChecked = false;
			List<TreeNode> resNodes = new ArrayList<TreeNode>();
			if(allResources!=null&&allResources.size()>0){
				for(SystemResource sr : allResources){
					TreeNode subNode = new TreeNode();
					subNode.setId(sr.getLong("id"));
					subNode.setText(sr.getStr("resName"));
					subNode.setDescription(sr.getStr("resName"));
					subNode.setExpanded(true);
                    subNode.setLeaf(true);
                    subNode.setChecked(false);
                    String checkVal = resMap.get(StringUtil.null2Str(sr.getLong("id")));
					boolean checked = "1".equals(checkVal)?true:false;
					if(checked){
						 rootChecked = true;
					}
					subNode.setChecked(checked);
					resNodes.add(subNode);
				}
			}
			tree.setChecked(rootChecked);	
			tree.setChildren(resNodes);
			root.add(tree);
			return root;
		}
	
		/**
		 * 资源树
		 * @param allResources
		 * @param resMap
		 * @return
		 */
			public static List<TreeNode> resourceTreeJson1(List<SystemResource> allResources,Map<String,String> resMap){
				List<TreeNode> root = new ArrayList<TreeNode>();
				List<TreeNode> resoureNodeList = new ArrayList<TreeNode>();
				if(allResources!=null&&allResources.size()>0){
					for(SystemResource sr : allResources){
						int isCommon = StringUtil.nullToInteger(sr.getInt("isCommon"));
						if(isCommon!=1){
							// 非公共资源
							TreeNode node = new TreeNode();
							node.setId(sr.getLong("id"));
							node.setText(sr.getStr("resName"));
							node.setDescription(sr.getStr("resName"));
							Long parentId = StringUtil.nullToLong(sr.getLong("fatherId"));
							node.setParentId(parentId);
							node.setRank(sr.getInt("rank"));
							node.setExpanded(false);
							node.setLeaf(StringUtil.nullToBoolean(sr.getInt("isLeaf")));
		                    String checkVal = resMap.get(StringUtil.null2Str(sr.getLong("id")));
							boolean checked = "1".equals(checkVal)?true:false;
							node.setChecked(checked);
							resoureNodeList.add(node);
							if(parentId.intValue()==0){
								//根节点
								root.add(node);
							}
						}
					}
				}
				if(root!=null&&root.size()>0){
					for(TreeNode tn:root){
					    initChild(tn,resoureNodeList);  
					}
				}
				return root;
			}
			
		    public static void initChild(TreeNode tn,List<TreeNode> list){  
		        Long parentId = tn.getId();  
		        for(TreeNode td : list){  
		            if(td.getParentId().equals(parentId)){  
		                initChild(td,list); 
		                tn.getChildren().add(td);  
		            }  
		        }  
		    } 
		/**
		 * 角色树
		 */
		public static List<TreeNode> roleTreeJson(List<SystemRole> selfRoles,List<SystemRole> otherRoles){
			TreeNode tree = new TreeNode();
			tree.setId(0L);
			tree.setExpanded(true);
			tree.setLeaf(false);
			tree.setText("角色树");
			tree.setDescription("角色树");
			List<TreeNode> resNodes = new ArrayList<TreeNode>();
			if(selfRoles!=null&&selfRoles.size()>0){
				
				for(SystemRole sr : selfRoles){
					TreeNode subNode = new TreeNode();
					subNode.setId(sr.getLong("id"));
					subNode.setText(sr.getStr("roleName"));
					subNode.setDescription(sr.getStr("remark"));
					subNode.setExpanded(true);
                    subNode.setLeaf(true);
                    subNode.setChecked(true);
                    resNodes.add(subNode);
				}
			}
			if(otherRoles!=null&&otherRoles.size()>0){
				
				for(SystemRole sr : otherRoles){
					TreeNode subNode = new TreeNode();
					subNode.setId(sr.getLong("id"));
					subNode.setText(sr.getStr("roleName"));
					subNode.setDescription(sr.getStr("remark"));
					subNode.setExpanded(true);
                    subNode.setLeaf(true);
                    subNode.setChecked(false);
                    resNodes.add(subNode);
				}
			}
			
			return resNodes;
		}
		/**
		 * 群组树
		 */
		public static List<TreeNode> groupTreeJson(List<SystemGroup> selfGroups,List<SystemGroup> otherGroups){
			TreeNode tree = new TreeNode();
			tree.setId(0L);
			tree.setExpanded(true);
			tree.setLeaf(false);
			tree.setText("群组树");
			tree.setDescription("群组树");
			List<TreeNode> groupNodes = new ArrayList<TreeNode>();
			if(selfGroups!=null&&selfGroups.size()>0){
				
				for(SystemGroup sr : selfGroups){
					TreeNode subNode = new TreeNode();
					subNode.setId(sr.getLong("id"));
					subNode.setText(sr.getStr("groupName"));
					subNode.setDescription(sr.getStr("remark"));
					subNode.setExpanded(true);
                    subNode.setLeaf(true);
                    subNode.setChecked(true);
                    groupNodes.add(subNode);
				}
			}
			if(otherGroups!=null&&otherGroups.size()>0){
				
				for(SystemGroup sr : otherGroups){
					TreeNode subNode = new TreeNode();
					subNode.setId(sr.getLong("id"));
					subNode.setText(sr.getStr("groupName"));
					subNode.setDescription(sr.getStr("remark"));
					subNode.setExpanded(true);
                    subNode.setLeaf(true);
                    subNode.setChecked(false);
                    groupNodes.add(subNode);
				}
			}
			
			return groupNodes;
		}
		
}
