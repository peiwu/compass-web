<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page import="com.iflytek.voicecloud.compass.po.Classify"%>
<%@page import="com.iflytek.voicecloud.compass.po.Tag"%>
<!DOCTYPE html >
<head>
<%@include file="common/meta.jsp"%>
<link rel="stylesheet" type="text/css" href="${resourcePath}/css/pages/tagInsight.css">
<link rel="stylesheet" type="text/css" href="${resourcePath}/css/util/select2.css">

<title>tagInsight</title>
</head>
<body>	
<div class="container">
<input type="hidden" id="authority" value="${authority}"/> 
<input type="hidden" id="currenttag" value="${tag_id}"/> 
<input type="hidden" id="currentstep" value="2"/> 
   <!--  <%@include file="common/header.jsp" %>-->
   <div class="content-header">
    <div class="hint">
    <span>已选标签：</span>
    <span class="taglist">${tag_name}</span>
    <br>
    <span>当前覆盖用户：</span>
    <span class="popular">${tag_pul}</span>
    </div>
    <div class="export">
      <form id="export" action="tagInsight/Export" method="post">
       <input type="hidden" id="exp-tagpath" name="exp-tagpath">
       <input type="hidden" id="exp-num" name="exp-num">
        <input type="hidden" id="exp-tags" name="exp-tags">
       <input type="hidden" id="exp-filename" name="exp-filename">
	   <input type="hidden" id="exp-need" name="exp-need">
       <input type="button" id="export-submit" value="导出" style="height:30px;width:50px;"/>
      </form>
    </div>
    <div class="return">
      <a href="toTagView">返回</a>
    </div>
   </div>
   <div class="content">
     <div class="first" id="1">
         <div class="tag-item">
            <div class="part1">
            <div class="tag-name">
             ${tag_name}             
            </div>
             <div class="tag-popular">
                             用户数：<br>
             ${tag_pul} 
            </div>
            </div>
             <div class="arrow1">
            </div>
         </div>
      </div>	
       <div class="line" id="line-2">
       </div>
        <div class="parent" id="parent-2">
				    <c:forEach items="${parents}" var="parent">
					              <div class="parent-item"> 
					              <div class="part2">
					               <div class="parent-name"> 
								    <span class="p-name">${parent.classifyName} </span>
								    <span class="t-name"></span>
								    <img class="push1" src="css/img/push.png">
								   </div>
								   <c:set var="key4" value="${parent.classifyId}"></c:set>  
								     <div class="child-item">        
									 <ul class="child-ul">
									 <c:set var="key1" value="${parent.classifyId}"></c:set>  
								     <c:forEach items="${classify_relation[key1]}" var="child">
								      <li class="child-li">
								     <img class="push2" src="css/img/push1.png">
									 ${child.classifyName}
								    <c:set var="key5" value="${child.classifyId}"></c:set>  
									   ${child_population[key5]} &nbsp;
									   <span class="red">
									   <fmt:formatNumber type="percent" value="${child_population[key5]/tag_pul}" maxFractionDigits="2"/> 
									   </span>
								     <ul class="tag-ul" style="display:none">
								     <c:set var="key2" value="${child.classifyId}"></c:set>  
								     <c:forEach items="${tag_classify[key2]}" var="tag">
								      <li class="tag-li" data-tagid="${tag.tagId}">
								       <c:set var="key3" value="${tag.tagId}"></c:set>  
								        &nbsp;&nbsp;&nbsp;
								        <input type="radio" name="tag" value="${tag.tagId}-${tag.tagName}-${tag_population[key3]}-<fmt:formatNumber type="percent" value="${tag_population[key3]/tag_pul}" maxFractionDigits="0"/>" step="2"/>
									   ${tag.tagName}
									   ${tag_population[key3]} &nbsp;
									   <span class="red">
									   <fmt:formatNumber type="percent" value="${tag_population[key3]/tag_pul}" maxFractionDigits="2"/>
									   </span>
								       </li>	
							        </c:forEach> 	
							        </ul>
							        </li>
								</c:forEach>		
								</ul>	
								</div>
								 <div class="parent-popular">
								      <span class="parent-rate"> 
								                占比：<br>
								       <label class="p-rate"><fmt:formatNumber type="percent" value="${parent_population[key4]/tag_pul}" maxFractionDigits="2"/></label>
								        <label class="t-rate"></label>
								      </span>
								       <span class="parent-num">
								               用户数：<br>
								     <label class="p-num"> ${parent_population[key4]}</label>
								     <label class="t-num"></label>
								       </span>
							     </div>
							     </div>
							     <div class="arrow2">
                                 </div>
						</div>	
				</c:forEach> 	
			</div>
   </div>
   <div class="load"></div>
</div>
<script type="text/javascript" src="${resourcePath}/js/pages/tagInsight.js"></script>
<script type="text/javascript" src="${resourcePath}/js/util/select2.min.js"></script>	
</body>