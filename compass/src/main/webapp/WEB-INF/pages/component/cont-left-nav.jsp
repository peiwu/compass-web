<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <ul class="parent-classifies-ul">
						<c:forEach items="${parentClassifies}" var="parentClassify" step="1">
							<li class="parent-class-item" data-parentid="${parentClassify.classifyId}" data-parentname="${parentClassify.classifyName}" data-parentdesc="${parentClassify.classifyDescription}">
								<span class="parent-class-icon"></span>
								<a href="javascript:void(null)" data-parentid="${parentClassify.classifyId}">${parentClassify.classifyName}</a>
								<span class="edit-parent-class" data-parentid="${parentClassify.classifyId}" data-parentname="${parentClassify.classifyName}" data-parentdesc="${parentClassify.classifyDescription}"></span>
								<span class="remove-parent-class" data-parentid="${parentClassify.classifyId}"></span>
							</li>	
							<hr class="parent-class-split" />							
						</c:forEach> 
	</ul>