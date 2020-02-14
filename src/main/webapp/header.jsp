<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<fmt:setLocale value="${lang}"/>
<fmt:setBundle basename="messages"/>

<!DOCTYPE html>
<html lang="${lang}">
<head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
	<meta name="description" content="Servlets Beauty Sheduler">
	<meta name="author" content="Victor Hilonenko">

	<title><fmt:message key="i18n.title" /></title>

    <link href="/favicon.ico" rel="icon" type="image/x-icon" />
    <link href="/css/toastr.css" rel="stylesheet" type="text/css" />
    <link href="/css/styles.css" rel="stylesheet">
    <link href="/css/jquery-ui.css" rel="stylesheet">
	<script type="text/javascript" src="/js/jquery.min.js"></script>
	<script type="text/javascript" src="/js/jquery-ui.js"></script>
</head>
<body onload="init()">
	<header>
		<nav id="navigation-bar" class="main_nav">
			<div id="nav-cont">
				<a href="/" class="navbar-brand"><span class="logo_black">Beauty</span><span class="logo_white"><fmt:message key="i18n.scheduler"/></span></a>
				<div id="lang">
					
					<a href="?lang=uk"><img src="/images/uk_flag.jpg" width="25px" border="0" alt="UKR"/></a>&nbsp;
					<a href="?lang=en"><img src="/images/us_flag.jpg" width="25px" border="0" alt="ENG"/></a>  
				</div>
				<div class="auth">
                    <c:choose>
                        <c:when test = "${!userPrincipal.isAuthenticated()}">
                            <div>
                                <a href="/login" class="auth_link"><fmt:message key="i18n.login"/></a>
                                <span style="color:#ffffff;"> | </span>
                                <a href="/registration" class="auth_link"><fmt:message key="i18n.register"/></a>
                            </div>
                        </c:when>

                        <c:otherwise>
                            <div>
                                <a href="/" class="auth_link"><strong>${userPrincipal.email}</strong></a>
                                <br>
                                <a href="/logout" class="auth_link"><fmt:message key="i18n.logout"/></a>
                            </div>
                        </c:otherwise>
                    </c:choose>
				</div>
				<div id="menu" class="menu">
					<ul>
						<li><a href="/"><fmt:message key="i18n.scheduler"/></a></li>

                        <c:if test = "${userPrincipal.hasRoleTag('authenticated')}">
                            <li><a href="/feedbacks"><fmt:message key="i18n.feedbacks"/></a></li>
                        </c:if>

                        <c:if test = "${userPrincipal.hasRoleTag('admin')}">
                            <li><a href="/admin"><fmt:message key="i18n.administration"/></a></li>
                        </c:if>
					</ul>
				</div>
			</div>
		</nav>	
	</header>